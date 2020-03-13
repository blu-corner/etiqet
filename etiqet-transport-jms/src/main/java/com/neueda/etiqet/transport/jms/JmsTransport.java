package com.neueda.etiqet.transport.jms;

import com.neueda.etiqet.core.client.delegate.ClientDelegate;
import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import com.neueda.etiqet.core.common.exceptions.EtiqetRuntimeException;
import com.neueda.etiqet.core.message.cdr.Cdr;
import com.neueda.etiqet.core.transport.BrokerTransport;
import com.neueda.etiqet.core.transport.Codec;
import com.neueda.etiqet.core.transport.TransportDelegate;
import com.neueda.etiqet.core.transport.delegate.BinaryMessageConverterDelegate;
import com.neueda.etiqet.transport.jms.config.JmsConfigExtractor;
import com.neueda.etiqet.transport.jms.config.JmsConfigXmlParser;
import com.neueda.etiqet.transport.jms.config.model.ConstructorArgument;
import com.neueda.etiqet.transport.jms.config.model.JmsConfig;
import com.neueda.etiqet.transport.jms.config.model.SetterArgument;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Class used to interact with a jms bus
 */
public class JmsTransport implements BrokerTransport {

    private final static Logger logger = LoggerFactory.getLogger(JmsTransport.class);

    private ConnectionFactory connectionFactory;
    private Connection connection;
    private Session session;
    private Codec<Cdr, ?> codec;
    private String defaultTopic;
    private ClientDelegate delegate;
    private BinaryMessageConverterDelegate binaryMessageConverterDelegate;

    /**
     * Instantiates a Jms connection factory and determines the default topic to publish messages to
     *
     * @param configPath Path to the jms configuration
     * @throws EtiqetException when we're unable to read the configuration file
     */
    @Override
    public void init(String configPath) throws EtiqetException {
        JmsConfigExtractor jmsConfigExtractor = new JmsConfigExtractor(new JmsConfigXmlParser());
        JmsConfig configuration = jmsConfigExtractor.retrieveConfiguration(configPath);
        binaryMessageConverterDelegate = instantiateBinaryMessageConverterDelegate(configuration);
        connectionFactory = createConnectionFactory(configuration);
        defaultTopic = configuration.getDefaultTopic();
    }

    private ConnectionFactory createConnectionFactory(final JmsConfig configuration) throws EtiqetException {
        List<ConstructorArgument> constructorArguments = configuration.getConstructorArgs();
        final Class[] argumentClasses = constructorArguments.stream()
            .map(arg -> arg.getArgumentType().getClazz())
            .toArray(Class[]::new);
        final Object[] argumentValues = constructorArguments.stream()
            .map(ConstructorArgument::getValue)
            .toArray(Object[]::new);

        try {
            final Class<?> constructorClass = configuration.getImplementation();
            final ConnectionFactory cf = (ConnectionFactory) constructorClass.getConstructor(argumentClasses).newInstance(argumentValues);
            configuration.getSetterArgs().forEach(
                setterArgument -> setArgument(setterArgument, constructorClass, cf)
            );
            return cf;
        } catch (ReflectiveOperationException e) {
            throw new EtiqetException(e.getMessage());
        }

    }

    private void setArgument(final SetterArgument setterArgument, final Class<?> clazz, final ConnectionFactory connectionFactory) {
        try {
            Method method = clazz.getMethod("set" + StringUtils.capitalize(setterArgument.getName()), setterArgument.getArgumentType().getClazz());
            method.invoke(connectionFactory, setterArgument.getValue());
        } catch (ReflectiveOperationException e) {
            throw new EtiqetRuntimeException("Invalid setter property for connection factory with name " + setterArgument.getName());
        }
    }

    private BinaryMessageConverterDelegate instantiateBinaryMessageConverterDelegate(JmsConfig config) throws EtiqetException {
        Optional<Class> delegateClass = config.getBinaryMessageConverterDelegateClass();
        if (delegateClass.isPresent()) {
            try {
                return (BinaryMessageConverterDelegate) delegateClass.get().newInstance();
            } catch (ReflectiveOperationException e) {
                throw new EtiqetException("Unable to instantiate BinaryMessageConverterDelegate " + delegateClass.get().getName());
            }
        } else {
            return null;
        }
    }

    /**
     * Starts a connection to the configured Jms bus
     *
     * @throws EtiqetException when unable to create a connection or session on the Jms bus
     */
    @Override
    public void start() throws EtiqetException {
        try {
            connection = connectionFactory.createConnection();
            connection.start();
            session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
        } catch (JMSException e) {
            throw new EtiqetException("Couldn't create Jms connection", e);
        }
    }

    /**
     * Stops the Jms bus connection and session
     */
    @Override
    public void stop() {
        if (session != null) {
            try {
                session.close();
            } catch (JMSException e) {
                logger.error("Couldn't safely stop Jms session", e);
            } finally {
                session = null;
            }
        }

        if (connection != null) {
            try {
                connection.stop();
            } catch (JMSException e) {
                logger.error("Couldn't safely stop Jms connection", e);
            } finally {
                connection = null;
            }
        }
    }

    /**
     * Sends a message to the Jms bus on the default topic provided in the configuration file
     *
     * @param msg message to be sent
     * @throws EtiqetException When an error occurs sending the message
     */
    @Override
    public void send(Cdr msg) throws EtiqetException {
        send(msg, getDefaultSessionId());
    }

    /**
     * Sends a message to the Jms bus on the default topic provided
     *
     * @param msg       message to be sent
     * @param topicName String containing the topic
     * @throws EtiqetException When an error occurs sending the message
     */
    @Override
    public void send(Cdr msg, String topicName) throws EtiqetException {
        sendToTopic(msg, Optional.ofNullable(topicName));
    }

    /**
     * @return whether a connection is established to the jms bus
     */
    @Override
    public boolean isLoggedOn() {
        return session != null;
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

    @Override
    public void subscribeToTopic(Optional<String> topicName, final Consumer<Cdr> cdrListener) throws EtiqetException {
        try {
            final Topic topic = session.createTopic(topicName.orElse(defaultTopic));
            subscribe(topic, cdrListener);
        } catch (JMSException e) {
            throw new EtiqetException(e);
        }
    }

    @Override
    public void subscribeToQueue(String queueName, Consumer<Cdr> cdrListener) throws EtiqetException {
        try {
            final Queue queue = session.createQueue(queueName);
            subscribe(queue, cdrListener);
        } catch (JMSException e) {
            throw new EtiqetException(e);
        }
    }

    private void subscribe(final Destination destination, final Consumer<Cdr> cdrListener) throws JMSException {
        MessageListener messageListener = message -> cdrListener.accept(jmsMessageToCdr(message));
        final MessageConsumer consumer = session.createConsumer(destination);
        consumer.setMessageListener(messageListener);
    }

    @Override
    public void sendToTopic(Cdr cdr, Optional<String> maybeTopicName) throws EtiqetException {
        final String topicName = maybeTopicName.orElseGet(() -> {
            logger.info("Empty topic name passed for sending to Jms, using default topic from config: {}", defaultTopic);
            return defaultTopic;
        });

        if (StringUtils.isEmpty(topicName)) {
            throw new EtiqetException("Unable to send message without a defined topic");
        }
        try {
            sendToDestination(cdr, session.createTopic(topicName));
        } catch (JMSException e) {
            throw new EtiqetException("Unable to send message to Jms topic " + topicName, e);
        }
    }

    @Override
    public void sendToQueue(Cdr cdr, String queueName) throws EtiqetException {
        if (StringUtils.isEmpty(queueName)) {
            throw new EtiqetException("Unable to send message without a defined queue");
        }
        try {
            sendToDestination(cdr, session.createQueue(queueName));
        } catch (JMSException e) {
            throw new EtiqetException("Exception sending message to Jms queue " + queueName, e);
        }
    }

    @Override
    public void clearQueue(String queueName) throws EtiqetException {
        try {
            Queue queue = session.createQueue(queueName);
            int i = 0;
            MessageConsumer consumer = session.createConsumer(queue);
            Message consumed;
            while ((consumed = consumer.receive(5000)) != null) {
                i++;
                consumed.acknowledge();
                logger.debug("Consumed message {} from queue {}", consumed, queueName);
            }

            consumer.close();
            logger.info("Removed {} messages from queue {}", i, queueName);
        } catch (JMSException e) {
            throw new EtiqetException("JMS Exception thrown trying to clear queue " + queueName, e);
        }
    }

    private void sendToDestination(final Cdr cdr, final Destination destination) throws JMSException, EtiqetException {
        MessageProducer producer = session.createProducer(destination);
        final Cdr processedMessage = processMessageWithDelegate(cdr);
        Object payload = codec.encode(processedMessage);
        final Message message;
	if (binaryMessageConverterDelegate != null) {
	    BytesMessage bytesMessage = session.createBytesMessage();
	    bytesMessage.writeBytes(binaryMessageConverterDelegate.toByteArray(payload));
	    message = bytesMessage;
	} else if (payload instanceof String) {
            message = session.createTextMessage((String) payload);
        } else {
            message = session.createObjectMessage((Serializable) payload);
        }
        producer.send(message);
    }

    @Override
    public Cdr subscribeAndConsumeFromTopic(final Optional<String> topicName, final Duration timeout) throws EtiqetException {
        try {
            final Topic topic = session.createTopic(topicName.orElse(defaultTopic));
            return jmsMessageToCdr(consumeFromDestination(topic).get(timeout.getSeconds(), TimeUnit.SECONDS));
        } catch (Exception e) {
            throw new EtiqetException(e);
        }
    }

    @Override
    public Cdr subscribeAndConsumeFromQueue(final String queueName, final Duration timeout) throws EtiqetException {
        try {
            final Queue queue = session.createQueue(queueName);
            return jmsMessageToCdr(consumeFromDestination(queue).get(timeout.getSeconds(), TimeUnit.SECONDS));
        } catch (Exception e) {
            throw new EtiqetException(e);
        }
    }

    private CompletableFuture<Message> consumeFromDestination(final Destination destination) throws EtiqetException {
        CompletableFuture<Message> eventualCdr = new CompletableFuture<>();
        try {
            final MessageConsumer consumer = session.createConsumer(destination);
            consumer.setMessageListener(message -> eventualCdr.complete(message));
        } catch (JMSException e) {
            throw new EtiqetException(e);
        }
        return eventualCdr;
    }

    private Cdr jmsMessageToCdr(final Message message) {
        final Cdr decodedMessage;
        try {
            decodedMessage = getDecodedMessage(message);
            if (decodedMessage != null) {
                logger.info("Decoded message: " + decodedMessage.toString());
            }
            message.acknowledge();
        } catch (JMSException | EtiqetException e) {
            throw new EtiqetRuntimeException("Unable to convert message to Cdr:" + e.getMessage(), e);
        }
        return processMessageWithDelegate(decodedMessage);
    }

    private Cdr getDecodedMessage(final Message message) throws EtiqetException, JMSException{
        final Object messageContent;

        if (message instanceof TextMessage) {
            TextMessage txt = (TextMessage) message;
            messageContent = txt.getText();
        } else if (message instanceof BytesMessage) {
            BytesMessage bytesXMLMessage = ((BytesMessage) message);
            messageContent = new byte[(int) bytesXMLMessage.getBodyLength()];
            bytesXMLMessage.readBytes((byte[]) messageContent);
        } else if (message instanceof ObjectMessage) {
            messageContent = ((ObjectMessage) message).getObject();
        } else {
            throw new EtiqetException("Unable to extract content from message type " + message.getClass().getName());
        }

        return (Cdr) getCodec().decode(messageContent);
    }

    private Cdr processMessageWithDelegate(final Cdr cdr) {
        if (delegate == null) {
            return cdr;
        }
        return delegate.processMessage(cdr);
    }

    ConnectionFactory getConnectionFactory() {
        return connectionFactory;
    }

    void setConnectionFactory(ConnectionFactory connectionFactory) {
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
