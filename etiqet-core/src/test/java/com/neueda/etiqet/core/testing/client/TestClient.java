package com.neueda.etiqet.core.testing.client;

import com.neueda.etiqet.core.client.Client;
import com.neueda.etiqet.core.common.cdr.Cdr;
import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import com.neueda.etiqet.core.message.config.ProtocolConfig;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;

public class TestClient extends Client<Cdr, String> {

    public static final String DEFAULT_CLIENT_CONFIG
            = "${etiqet.directory}/etiqet-core/src/test/resources/properties/test.properties";

    private boolean isStarted = false;
    private boolean isLoggedOn = false;

    private boolean usingPrimary = true;
    private boolean usingSecondary = false;

    private String sessionId = "";

    private BlockingQueue<Cdr> messagesSent = new LinkedBlockingQueue<>();

    public TestClient() throws EtiqetException {
        this(DEFAULT_CLIENT_CONFIG);
    }

    public TestClient(String clientConfig) throws EtiqetException {
        super(clientConfig);
    }

    public TestClient(String primaryConfig, String secondaryConfig) throws EtiqetException {
        super(primaryConfig, secondaryConfig);
    }

    public TestClient(String primaryClientConfig, String secondaryClientConfig, ProtocolConfig protocolConfig)
    throws EtiqetException {
        super(primaryClientConfig, secondaryClientConfig, protocolConfig);
    }

    @Override
    public boolean isAdmin(String msgType) {
        return false;
    }

    @Override
    public void launchClient() throws EtiqetException {
        isStarted = true;
    }

    @Override
    public void launchClient(String configPath) throws EtiqetException {
        launchClient();
    }

    @Override
    public String getDefaultSessionId() {
        return null;
    }

    @Override
    public void send(Cdr msg) throws EtiqetException {
        isLoggedOn = true;
        messagesSent.add(msg);

        msg.set("sent", new SimpleDateFormat("yyyyMMdd").format(new Date()));
        msg.set("sentTime", new SimpleDateFormat("yyyyMMdd-HH:mm:ss.SSS").format(new Date()));

        if("addFilter".equals(msg.getType())) {
            msgQueue.add(new Cdr("toFilter"));
        }
        Cdr response = new Cdr("testResponse");
        response.update(msg);
        msgQueue.add(response);
    }

    @Override
    public void send(Cdr msg, String sessionId) throws EtiqetException {
        setSessionId(sessionId);
        send(msg);
    }

    @Override
    public void stop() {
        isStarted = false;
        isLoggedOn = false;
    }

    @Override
    public boolean isLoggedOn() {
        return isLoggedOn;
    }

    @Override
    public String getMsgType(String messageName) {
        return messageName;
    }

    @Override
    public String getMsgName(String messageType) {
        return messageType;
    }

    public boolean isStarted() {
        return isStarted;
    }

    public Queue<Cdr> getMessagesSent() {
        return messagesSent;
    }

    @Override
    public Cdr waitForAppMsg() throws EtiqetException {
        return waitForMsg(msgQueue, 5000);
    }

    @Override
    public Cdr waitForMsgType(String msgType, Integer timeoutMillis) throws EtiqetException {
        return waitForMsg(msgQueue, timeoutMillis);
    }

    @Override
    public Cdr waitForNoMsgType(String msgType, Integer timeoutMillis) throws EtiqetException {
        return waitForNoMsg(msgQueue, timeoutMillis);
    }

    @Override
    public Boolean waitForLogon(Integer timeoutMillis) {
        try {
            await().atMost(timeoutMillis, TimeUnit.MILLISECONDS);
            launchClient();
            this.isLoggedOn = true;
        } catch (EtiqetException ignored) {}

        return isStarted;
    }

    @Override
    public Cdr encode(Cdr message) {
        return message;
    }

    @Override
    public Cdr decode(Cdr message) {
        return message;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public void failover() {
        if(usingPrimary) {
            usingPrimary = false;
            usingSecondary = true;
        } else {
            usingPrimary = true;
            usingSecondary = false;
        }
    }

    @Override
    public String getProtocolName() {
        return "testProtocol";
    }

    public boolean isUsingPrimary() {
        return usingPrimary;
    }

    public boolean isUsingSecondary() {
        return usingSecondary;
    }
}
