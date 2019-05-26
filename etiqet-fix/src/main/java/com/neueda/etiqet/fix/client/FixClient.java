package com.neueda.etiqet.fix.client;

import com.neueda.etiqet.core.client.Client;
import com.neueda.etiqet.core.client.delegate.ClientDelegate;
import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import com.neueda.etiqet.core.config.GlobalConfig;
import com.neueda.etiqet.core.message.cdr.Cdr;
import com.neueda.etiqet.core.transport.Codec;
import com.neueda.etiqet.core.transport.TransportDelegate;
import com.neueda.etiqet.fix.client.delegate.FixClientDelegate;
import com.neueda.etiqet.fix.config.FixConfigConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * {@inheritDoc}
 *
 * @author Neueda
 */
public class FixClient extends Client implements TransportDelegate<String, Cdr> {

    private static final String[] DEFAULT_CLIENT_DELEGATES = {"fix", "logger"};
    private static final Logger logger = LoggerFactory.getLogger(FixClient.class.getName());

    /**
     * Attribute sessionQueue.
     */
    private BlockingQueue<Cdr> sessionQueue;


    /**
     * Constructor.
     *
     * @param clientConfig the client's configuration.
     * @throws EtiqetException when an issue occurs setting up the FixClient
     */
    public FixClient(String clientConfig) throws EtiqetException {
        this(clientConfig, null);
    }

    /**
     * Constructor.
     *
     * @param primaryConfig   the client's configuration.
     * @param secondaryConfig the client's secondary configuration for failover.
     * @throws EtiqetException when an issue occurs setting up the FixClient
     */
    public FixClient(String primaryConfig, String secondaryConfig) throws EtiqetException {
        super(primaryConfig, secondaryConfig,
              GlobalConfig.getInstance().getProtocol(FixConfigConstants.PROTOCOL_NAME));
        sessionQueue = new LinkedBlockingQueue<>();
        setActions(DEFAULT_CLIENT_DELEGATES);
    }

    @Override
    public void setDelegate(ClientDelegate delegate) {
        super.setDelegate(
            (delegate instanceof FixClientDelegate) ? delegate : new FixClientDelegate(delegate));
    }

    @Override
    public void stop() {
        // Stops the transport
        transport.stop();

        // clear the msg queues
        msgQueue.clear();
        sessionQueue.clear();
    }

    @Override
    public void send(Cdr msg, String sessionId) throws EtiqetException {
        transport.send(msg, sessionId);
    }

    @Override
    public boolean isLoggedOn() {
        return (transport != null) && (transport.isLoggedOn());
    }

    @Override
    public void init(String config) throws EtiqetException {
        super.init(config);
        transport.setTransportDelegate(this);
        activeConfig = config;
    }

    @Override
    public void setCodec(Codec c) {
        transport.setCodec(c);
    }

    public boolean isAdmin(String msgType) {
        return getProtocolConfig().isAdmin(msgType);
    }

    @Override
    public String getMsgType(String messageName) {
        return getProtocolConfig().getMsgType(messageName);
    }

    @Override
    public String getMsgName(String messageType) {
        return getProtocolConfig().getMsgName(messageType);
    }

    /**
     * Method to check type of received message
     *
     * @param msgType       type of msg requested
     * @param timeoutMillis the maximum timeout to wait for the message in milliseconds
     */
    @Override
    public Cdr waitForMsgType(String msgType, Integer timeoutMillis) throws EtiqetException {
        return waitForMsg(isAdmin(msgType) ? sessionQueue : msgQueue, timeoutMillis);
    }

    @Override
    public String getDefaultSessionId() {
        return transport.getDefaultSessionId();
    }

    @Override
    public Cdr waitForNoMsgType(String msgType, Integer timeoutMillis) throws EtiqetException {
        return waitForNoMsg(isAdmin(msgType) ? sessionQueue : msgQueue, timeoutMillis);
    }

    // -----------------------------------------------------------------------------------------------
    // Application interface
    // -----------------------------------------------------------------------------------------------

    @Override
    public void start() {
        // Nothing to do here ...
    }

    @Override
    public void onCreate(String sessionId) {
        logger.info("Successfully called onCreate for sessionId : " + sessionId);
    }

    @Override
    public void fromApp(Cdr msg, String sessionID) {
        if (isAdmin(msg.getType())) {
            sessionQueue.add(msg);
        } else {
            msgQueue.add(msg);
        }
    }

    @Override
    public void onLogon(String sessionID) {
        logger.info("session logged on : " + sessionID);
        synchronized (logonEvent) {
            logonEvent.completeEvent();
            logonEvent.notifyAll();
        }
    }

    @Override
    public void onLogout(String sessionID) {
        logger.info("session logged out : " + sessionID);
    }

    @Override
    public void toApp(Cdr msg, String sessionID) {
        // Nothing to do here
    }

}
