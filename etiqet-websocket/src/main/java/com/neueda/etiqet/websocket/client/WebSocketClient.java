package com.neueda.etiqet.websocket.client;

import com.neueda.etiqet.core.client.Client;
import com.neueda.etiqet.core.client.delegate.SinkClientDelegate;
import com.neueda.etiqet.core.common.Environment;
import com.neueda.etiqet.core.common.cdr.Cdr;
import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import com.neueda.etiqet.core.config.GlobalConfig;
import com.neueda.etiqet.core.config.dtos.Message;
import com.neueda.etiqet.core.message.config.ProtocolConfig;
import com.neueda.etiqet.core.util.Config;
import com.neueda.etiqet.core.util.PropertiesFileReader;
import com.neueda.etiqet.core.util.StringUtils;
import com.neueda.etiqet.websocket.config.WebSocketConfigConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;

import java.net.URI;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class WebSocketClient extends Client<Message, String> {

    private static final Logger LOG = LogManager.getLogger(WebSocketClient.class);

    private String activeConfig;

    private org.eclipse.jetty.websocket.client.WebSocketClient client;

    public WebSocketClient(String clientConfig) throws EtiqetException {
        this(clientConfig, null);
    }

    public WebSocketClient(String primaryConfig, String secondaryConfig) throws EtiqetException {
        this(primaryConfig, secondaryConfig, GlobalConfig.getInstance().getProtocol(WebSocketConfigConstants.DEFAULT_PROTOCOL_NAME));
    }

    public WebSocketClient(String primaryClientConfig, String secondaryClientConfig, ProtocolConfig protocol)
    throws EtiqetException {
        super(primaryClientConfig, secondaryClientConfig, protocol);
        // Delegate isn't needed for the websocket client to pre-process the messages
        this.delegate = new SinkClientDelegate<>();
        this.activeConfig = primaryClientConfig;
    }

    @Override
    public boolean isAdmin(String msgType) { return false; }

    @Override
    public void launchClient() throws EtiqetException {
        launchClient(primaryConfig);
    }

    @Override
    public void launchClient(String configPath) throws EtiqetException {
        this.activeConfig = configPath;
        this.config = PropertiesFileReader.loadPropertiesFile(Environment.resolveEnvVars(activeConfig));

        try
        {
            this.client = new org.eclipse.jetty.websocket.client.WebSocketClient();
            WebSocketSession instance = new WebSocketSession(msgQueue);
            client.start();

            String socketUrl = this.config.getString("socketUrl");
            String messageFilter = this.config.getString("messageFilter");
            if(!StringUtils.isNullOrEmpty(messageFilter)) {
                socketUrl += "?filter=" + messageFilter;
            }
            ClientUpgradeRequest request = new ClientUpgradeRequest();
            LOG.info("Starting websocket client: " + socketUrl);
            Future<Session> fut = client.connect(instance, new URI(socketUrl),request);
            fut.get(5, TimeUnit.SECONDS);
        }
        catch (Throwable e)
        {
            e.printStackTrace();
            throw new EtiqetException("Failed to connect to websocket");
        }
    }

    @Override
    public void failover() throws EtiqetException {
        if(this.activeConfig.equals(primaryConfig)) {
            launchClient(secondaryConfig);
        } else {
            launchClient();
        }
    }

    @Override
    public String getDefaultSessionId() { return ""; }

    @Override
    public void send(Cdr msg) throws EtiqetException {

    }

    /**
     * Sends a message based on the client / message configuation
     * @param msg message to be sent
     * @param sessionId Not used by RestClient
     * @throws EtiqetException when an error is thrown sending the message
     */
    @Override
    public void send(Cdr msg, String sessionId) throws EtiqetException {
        send(msg);
    }

    /**
     * Gets the client configuration from the {@link #activeConfig} path
     * @return client configuration
     */
    Config getClientConfig() {
        return this.config;
    }

    @Override
    public Cdr waitForMsgType(String msgType, Integer timeoutMillis) throws EtiqetException {
        return waitForMsg(msgQueue, timeoutMillis);
    }

    @Override
    public void stop() {
        try {
            if (this.client != null) {
                client.stop();
            }
        } catch (Exception ex)
        {
            // Ignore shutdown exception
        }
    }

    @Override
    public boolean isLoggedOn() { return false; }

    @Override
    public String getMsgType(String messageType) {
        return getMsgName(messageType);
    }

    @Override
    public String getMsgName(String messageName) {
        String msgName;
        Message message = getProtocolConfig().getMessage(messageName);
        if (message != null) {
            msgName = message.getName();
        } else {
            msgName = messageName;
        }
        return msgName;
    }

    @Override
    public Cdr waitForNoMsgType(String msgType, Integer timeoutMillis) throws EtiqetException {
        return waitForNoMsg(msgQueue, timeoutMillis);
    }

    @Override
    public Message encode(Cdr message) throws EtiqetException {
        // Not implemented
        return null;
    }

    @Override
    public Cdr decode(Message message) {
        return msgQueue.iterator().next();
    }

    public String getPrimaryConfig() {
        return primaryConfig;
    }

    public String getSecondaryConfig() {
        return secondaryConfig;
    }
}
