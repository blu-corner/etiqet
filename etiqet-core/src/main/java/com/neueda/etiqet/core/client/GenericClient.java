package com.neueda.etiqet.core.client;

import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import com.neueda.etiqet.core.message.cdr.Cdr;
import com.neueda.etiqet.core.transport.TransportDelegate;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.fail;

public class GenericClient extends Client implements TransportDelegate<String, Cdr> {

    private static final Logger logger = LoggerFactory.getLogger(Client.class);

    public Set<String> logged = new HashSet<>();

    public GenericClient(String clientConfig, String secondaryConfig) throws EtiqetException {
        super(clientConfig, secondaryConfig);
    }

    @Override
    public String getDefaultSessionId() {
        return transport.getDefaultSessionId();
    }

    @Override
    public void stop() {
        transport.stop();
    }

    @Override
    public boolean isLoggedOn() {
        return !logged.isEmpty();
    }

    @Override
    public String getMsgType(String messageType) {
        return getProtocolConfig().getMsgType(messageType);
    }

    @Override
    public String getMsgName(String messageName) {
        return getProtocolConfig().getMsgName(messageName);
    }

    @Override
    public Cdr waitForNoMsgType(String msgType, Integer timeoutMillis) throws EtiqetException {
        return waitForNoMsg(msgQueue, timeoutMillis);
    }


    @Override
    public void onCreate(String sid) {
        logger.info("Created session with id [" + sid + "]");
    }

    @Override
    public void onLogon(String sid) {
        logger.info("Logged in session id [" + sid + "]");
        logged.add(sid);
    }

    @Override
    public void onLogout(String sid) {
        logger.info("Logged out from session id [" + sid + "]");
        logged.remove(sid);
    }

    @Override
    public void toApp(Cdr msg, String sid) {
        logger.info(">>> Sending to session id [" + sid + "] message [" + msg.toString() + "]");
    }

    @Override
    public void fromApp(Cdr msg, String sid) {
        logger.info("<<< Received from session id [" + sid + "] message [" + msg.toString() + "]");
        try {
            msgQueue.put(msg);
        } catch (InterruptedException e) {
            fail("Unable to add message to application queue");
        }
    }
}
