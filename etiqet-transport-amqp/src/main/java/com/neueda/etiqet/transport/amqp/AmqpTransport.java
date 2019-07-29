package com.neueda.etiqet.transport.amqp;

import com.neueda.etiqet.core.client.delegate.ClientDelegate;
import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import com.neueda.etiqet.core.common.exceptions.EtiqetRuntimeException;
import com.neueda.etiqet.core.message.cdr.Cdr;
import com.neueda.etiqet.core.message.config.AbstractDictionary;
import com.neueda.etiqet.core.transport.Codec;
import com.neueda.etiqet.core.transport.ExchangeTransport;
import com.neueda.etiqet.core.transport.TransportDelegate;
import com.neueda.etiqet.core.transport.delegate.BinaryMessageConverterDelegate;
import com.neueda.etiqet.transport.amqp.config.AmqpConfigExtractor;
import com.neueda.etiqet.transport.amqp.config.model.AmqpConfig;
import com.neueda.etiqet.transport.amqp.config.model.ExchangeConfig;
import com.neueda.etiqet.transport.amqp.config.model.QueueConfig;
import com.rabbitmq.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Optional.empty;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class AmqpTransport<T> implements ExchangeTransport {

    private Connection connection;
    private Map<String, Channel> channelsByExchange;
    private Codec<Cdr, T> codec;
    private ClientDelegate delegate;
    private BinaryMessageConverterDelegate<T> binaryMessageConverterDelegate;
    private AmqpConfigExtractor configExtractor;

    private final static Logger logger = LoggerFactory.getLogger(AmqpTransport.class);
    private final static String DEFAULT_EXCHANGE = "";

    public AmqpTransport() {
        this(new AmqpConfigExtractor());
    }

    AmqpTransport(AmqpConfigExtractor configExtractor) {
        super();
        this.configExtractor = configExtractor;
        channelsByExchange = new HashMap<>();
    }

    @Override
    public void init(String configPath) throws EtiqetException {
        AmqpConfig configuration = configExtractor.retrieveConfiguration(configPath);
        this.binaryMessageConverterDelegate = instantiateBinaryMessageConverterDelegate(configuration);
        final ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(configuration.getHost());
        configuration.getPort().ifPresent(connectionFactory::setPort);
        try {
            connection = connectionFactory.newConnection();
        } catch (Exception e) {
            throw new EtiqetException(e);
        }

        configuration.getExchangeConfigs().forEach(
            exchangeConfig -> {
                try {
                    createExchange(exchangeConfig, exchangeConfig.getExchangeType());
                } catch (IOException e) {
                    throw new EtiqetRuntimeException(e);
                }
            }
        );
    }

    private AMQP.Exchange.DeclareOk createExchange(final ExchangeConfig exchangeConfig, final BuiltinExchangeType exchangeType) throws IOException {
        final Channel channel = connection.createChannel();
        final String exchangeName = exchangeConfig.getName();
        channelsByExchange.put(exchangeName, channel);

        AMQP.Exchange.DeclareOk exchangeDeclareOk = channel.exchangeDeclare(exchangeName, exchangeType);

        for (QueueConfig queueConfig : exchangeConfig.getQueueConfigs()) {
            final String queueName = queueConfig.getName();
            channel.queueDeclare(queueName, queueConfig.isDurable(), queueConfig.isExclusive(), queueConfig.isAutodelete(), null);
            channel.queueBind(queueName, exchangeName, queueConfig.getBindingKey().orElse(""));
        }
        return exchangeDeclareOk;
    }


    @Override
    public void start() throws EtiqetException {
        try {
            for (String exchangeId : channelsByExchange.keySet()) {
                send(new Cdr("Logon"), exchangeId);
            }
        } catch (Exception e) {
            throw new EtiqetException("Could not start RabbitMQ. Reason " + e.getMessage(), e);
        }
    }

    @Override
    public void stop() {
        channelsByExchange.forEach(
            (exchangeName, channel) -> {
                try {
                    channel.exchangeDeleteNoWait(exchangeName, false);
                } catch (IOException e) {
                    logger.error("Could not delete exchange " + exchangeName);
                }
            }
        );
        try {
            connection.close();
        } catch (IOException e) {
            logger.error("Could not close connection");
        }
    }


    @Override
    public void send(Cdr msg) throws EtiqetException {
        send(msg, getDefaultSessionId());
    }

    @Override
    public void send(Cdr msg, String sessionId) throws EtiqetException {
        sendToExchange(msg, sessionId);
    }

    @Override
    public Cdr subscribeAndConsumeFromQueue(String queueName, Duration timeout) throws EtiqetException {
        CompletableFuture<Cdr> eventualCdr = new CompletableFuture<>();
        Consumer<Cdr> consumer = eventualCdr::complete;
        subscribeToQueue(queueName, consumer);
        try {
            return eventualCdr.get(timeout.toMillis(), MILLISECONDS);
        } catch (Exception e) {
            throw new EtiqetException(e);
        }
    }

    @Override
    public void subscribeToQueue(String queueName, Consumer<Cdr> cdrListener) throws EtiqetException {
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            final Cdr decodedMessage = decodeMessageBytes(delivery.getBody());
            cdrListener.accept(processMessageUsingDelegate(decodedMessage));
        };
        try {
            final Channel channel = connection.createChannel();
            channel.basicConsume(queueName, false, deliverCallback, consumerTag -> {});
        } catch (IOException e) {
            throw new EtiqetException(e);
        }
    }

    private Cdr decodeMessageBytes(byte[] messageBytes) {
        try {
            T message = binaryMessageConverterDelegate.fromByteArray(messageBytes);
            return codec.decode(message);
        } catch (EtiqetException e) {
            logger.error("Unable to decode message bytes " + new String(messageBytes, UTF_8));
            throw new EtiqetRuntimeException(e);
        }
    }

    @Override
    public void sendToExchange(Cdr cdr, String exchangeName) throws EtiqetException {
        sendToExchange(cdr, exchangeName, empty());
    }

    @Override
    public void sendToExchange(Cdr cdr, String exchangeName, String routingKey) throws EtiqetException {
        sendToExchange(cdr, exchangeName, Optional.of(routingKey));
    }

    public void sendToExchange(Cdr cdr, String exchangeName, Optional<String> routingKey) throws EtiqetException {
        final Channel channel = getChannelByExchangeName(exchangeName);
        final T payload = codec.encode(processMessageUsingDelegate(cdr));
        try {
            if(payload instanceof byte[]) {
                channel.basicPublish(exchangeName, routingKey.orElse(""), null, (byte[]) payload);
            } else {
                final byte[] binaryMessage = binaryMessageConverterDelegate.toByteArray(payload);
                channel.basicPublish(exchangeName, routingKey.orElse(""), null, binaryMessage);
            }
        } catch (IOException e) {
            throw new EtiqetException(e);
        }
    }

    private Channel getChannelByExchangeName(final String exchangeName) throws EtiqetException {
        final Channel channel = channelsByExchange.get(exchangeName);
        if (channel == null) {
            throw new EtiqetException("Exchange " + exchangeName + " hasn't been created for current client");
        }
        return channel;
    }

    private Cdr processMessageUsingDelegate(Cdr cdr) {
        if (delegate == null) {
            return cdr;
        }
        return delegate.processMessage(cdr);
    }

    private BinaryMessageConverterDelegate<T> instantiateBinaryMessageConverterDelegate(AmqpConfig config) throws EtiqetException {
        Class delegateClass = config.getBinaryMessageConverterDelegateClass();
        try {
           return (BinaryMessageConverterDelegate<T>) delegateClass.newInstance();
        } catch (ReflectiveOperationException e) {
            throw new EtiqetException("Unable to instantiate BinaryMessageConverterDelegate " + delegateClass.getName());
        }
    }

    @Override
    public boolean isLoggedOn() {
        return connection.isOpen();
    }

    @Override
    public String getDefaultSessionId() {
        return DEFAULT_EXCHANGE;
    }

    @Override
    public void setTransportDelegate(TransportDelegate<String, Cdr> transDel) {
        logger.warn("Trying to set trasnport delegate which won't be used");
    }

    @Override
    public Codec getCodec() {
        return codec;
    }

    @Override
    public void setCodec(Codec c) {
        codec = c;
    }

    @Override
    public ClientDelegate getDelegate() {
        return delegate;
    }

    @Override
    public void setDelegate(ClientDelegate delegate) {
        this.delegate = delegate;
    }

}
