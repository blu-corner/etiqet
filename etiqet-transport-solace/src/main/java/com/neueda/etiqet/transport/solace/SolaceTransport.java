package com.neueda.etiqet.transport.solace;

import com.neueda.etiqet.core.client.delegate.ClientDelegate;
import com.neueda.etiqet.core.common.Environment;
import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import com.neueda.etiqet.core.message.cdr.Cdr;
import com.neueda.etiqet.core.transport.Codec;
import com.neueda.etiqet.core.transport.Transport;
import com.neueda.etiqet.core.transport.TransportDelegate;

import com.solacesystems.jms.SolXAConnectionFactory;
import com.solacesystems.jms.SolXAConnectionFactoryImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;
import java.util.Properties;

public class SolaceTransport implements Transport {

    private final static Logger logger = LoggerFactory.getLogger(SolaceTransport.class);

    private SolXAConnectionFactory connectionFactory;
    private Connection connection;
    private Session session;
    private Codec<Cdr, String> codec;
    private String defaultTopic;
    private ClientDelegate delegate;
    private TransportDelegate<String, Cdr> transDel;

    @Override
    public void init(String configPath) throws EtiqetException {
        try {
            // Load configuration
            Properties props = new Properties();
            props.load(Environment.fileResolveEnvVars(configPath));

            // Create connection and channels
            connectionFactory = new SolXAConnectionFactoryImpl();
            connectionFactory.setHost(props.getProperty("solaceHost"));
            connectionFactory.setVPN(props.getProperty("solaceVpn"));
            connectionFactory.setUsername(props.getProperty("solaceUser"));
            connectionFactory.setPassword(props.getProperty("solacePassword"));

            defaultTopic = props.getProperty("solaceDefaultTopic");
        } catch (Exception e) {
            throw new EtiqetException("Could not init Solace with config [" + configPath + "]", e);
        }
    }

    @Override
    public void start() throws EtiqetException {
        try {
            connection = connectionFactory.createConnection();
            connection.start();
            session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
        } catch (JMSException e) {
            throw new EtiqetException("Couldn't create Solace connection", e);
        }
    }

    @Override
    public void stop() {
        if(connection != null) {
            try {
                connection.stop();
                connection = null;
            } catch (JMSException e) {
                logger.error("Couldn't stop Solace connection", e);
            }
        }
    }

    @Override
    public void send(Cdr msg) throws EtiqetException {
        send(msg, defaultTopic);
    }

    @Override
    public void send(Cdr msg, String sessionId) throws EtiqetException {
        try {
            MessageProducer producer = session.createProducer(session.createTopic(sessionId));
            producer.send(session.createTextMessage(codec.encode(msg)));
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isLoggedOn() {
        return connection != null;
    }

    @Override
    public String getDefaultSessionId() {
        return "";
    }

    @Override
    public void setTransportDelegate(TransportDelegate<String, Cdr> transDel) {
        this.transDel = transDel;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setCodec(Codec c) {
        this.codec = c;
    }

    @Override
    public Codec getCodec() {
        return codec;
    }

    @Override
    public void setDelegate(ClientDelegate delegate) {
        this.delegate = delegate;
    }

    @Override
    public ClientDelegate getDelegate() {
        return delegate;
    }

}
