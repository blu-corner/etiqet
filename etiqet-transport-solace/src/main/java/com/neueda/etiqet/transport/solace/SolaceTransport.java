package com.neueda.etiqet.transport.solace;

import com.neueda.etiqet.core.client.delegate.ClientDelegate;
import com.neueda.etiqet.core.common.Environment;
import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import com.neueda.etiqet.core.message.cdr.Cdr;
import com.neueda.etiqet.core.transport.Codec;
import com.neueda.etiqet.core.transport.Transport;
import com.neueda.etiqet.core.transport.TransportDelegate;

import com.neueda.etiqet.core.util.StringUtils;
import com.solacesystems.jms.SolXAConnectionFactory;
import com.solacesystems.jms.SolXAConnectionFactoryImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;
import java.util.Properties;

/**
 * Class used to interact with a solace bus
 */
public class SolaceTransport implements Transport {

    private final static Logger logger = LoggerFactory.getLogger(SolaceTransport.class);

    private SolXAConnectionFactory connectionFactory;
    private Connection connection;
    private Session session;
    private Codec<Cdr, String> codec;
    private String defaultTopic;
    private ClientDelegate delegate;
    private TransportDelegate<String, Cdr> transDel;

    /**
     * Instantiates a Solace connection factory and determines the default topic to publish messages to
     *
     * @param configPath       Path to the solace configuration
     * @throws EtiqetException when we're unable to read the configuration file
     */
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

    /**
     * Starts a connection to the configured Solace bus
     *
     * @throws EtiqetException when unable to create a connection or session on the Solace bus
     */
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

    /**
     * Stops the Solace bus connection and session
     */
    @Override
    public void stop() {
        if(session != null) {
            try {
                session.close();
            } catch (JMSException e) {
                logger.error("Couldn't safely stop Solace session", e);
            } finally {
                session = null;
            }
        }

        if(connection != null) {
            try {
                connection.stop();
            } catch (JMSException e) {
                logger.error("Couldn't safely stop Solace connection", e);
            } finally {
                connection = null;
            }
        }
    }

    /**
     * Sends a message to the Solace bus on the default topic provided in the configuration file
     *
     * @param msg              message to be sent
     * @throws EtiqetException When an error occurs sending the message
     */
    @Override
    public void send(Cdr msg) throws EtiqetException {
        send(msg, getDefaultSessionId());
    }

    /**
     * Sends a message to the Solace bus on the default topic provided
     *
     * @param msg message to be sent
     * @param topicName String containing the topic
     * @throws EtiqetException When an error occurs sending the message
     */
    @Override
    public void send(Cdr msg, String topicName) throws EtiqetException {
        if(StringUtils.isNullOrEmpty(topicName)) {
            logger.info("Empty topic name passed for sending to Solace, using default topic from config: {}", defaultTopic);
            topicName = getDefaultSessionId();
        }
        if(StringUtils.isNullOrEmpty(topicName)) {
            throw new EtiqetException("Unable to send message without a defined topic");
        }
        try {
            MessageProducer producer = session.createProducer(session.createTopic(topicName));
            producer.send(session.createTextMessage(codec.encode(msg)));
        } catch (JMSException e) {
            logger.error("Exception sending message to Solace bus.", e);
            throw new EtiqetException(e);
        }
    }

    /**
     * @return whether a connection is established to the solace bus
     */
    @Override
    public boolean isLoggedOn() {
        return connection != null;
    }

    /**
     * @return the default topic to send messages to
     */
    @Override
    public String getDefaultSessionId() {
        return defaultTopic;
    }

    /**
     * @param transDel the transport delegate class
     */
    @Override
    public void setTransportDelegate(TransportDelegate<String, Cdr> transDel) {
        this.transDel = transDel;
    }

    /**
     * @param c the codec used by the transport.
     */
    @Override
    @SuppressWarnings("unchecked")
    public void setCodec(Codec c) {
        this.codec = c;
    }

    /**
     * @return the codec used by the transport.
     */
    @Override
    public Codec getCodec() {
        return codec;
    }

    /**
     * @param delegate the first client delegate of a chain to use (if any)
     */
    @Override
    public void setDelegate(ClientDelegate delegate) {
        this.delegate = delegate;
    }

    /**
     * @return the first client delegate of a chain to be used (if any)
     */
    @Override
    public ClientDelegate getDelegate() {
        return delegate;
    }

    SolXAConnectionFactory getConnectionFactory() {
        return connectionFactory;
    }

    void setConnectionFactory(SolXAConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    Connection getConnection() {
        return connection;
    }

    void setConnection(Connection connection) {
        this.connection = connection;
    }

    Session getSession() {
        return session;
    }

    void setSession(Session session) {
        this.session = session;
    }

    void setDefaultTopic(String defaultTopic) {
        this.defaultTopic = defaultTopic;
    }

}
