package com.neueda.etiqet.transport.rabbitmq;

import com.neueda.etiqet.core.client.delegate.ClientDelegate;
import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import com.neueda.etiqet.core.common.exceptions.EtiqetRuntimeException;
import com.neueda.etiqet.core.message.cdr.Cdr;
import com.neueda.etiqet.core.transport.Codec;
import com.neueda.etiqet.core.transport.ExchangeTransport;
import com.neueda.etiqet.core.transport.TransportDelegate;
import com.neueda.etiqet.transport.rabbitmq.config.AmqpConfigExtractor;
import com.neueda.etiqet.transport.rabbitmq.config.model.AmqpConfig;
import com.neueda.etiqet.transport.rabbitmq.config.model.ExchangeConfig;
import com.neueda.etiqet.transport.rabbitmq.config.model.QueueConfig;
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

public class RabbitMqTransport implements ExchangeTransport {

    private final static Logger logger = LoggerFactory.getLogger(RabbitMqTransport.class);
    private Connection connection;
    private Map<String, Channel> channelsByExchange;
    private Codec<Cdr, Object> codec;
    private TransportDelegate<String, Cdr> transDel;
    private ClientDelegate delegate;
    private ConnectionFactory connectionFactory;
    private AmqpConfigExtractor configExtractor;


    public RabbitMqTransport() {
        this(new AmqpConfigExtractor());
    }

    RabbitMqTransport(AmqpConfigExtractor configExtractor) {
        super();
        this.configExtractor = configExtractor;
        channelsByExchange = new HashMap<>();
    }

    @Override
    public void init(String configPath) throws EtiqetException {
        AmqpConfig configuration = configExtractor.retrieveConfiguration(configPath);
        connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(configuration.getHost());
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
        Consumer<Cdr> consumer = cdr -> eventualCdr.complete(cdr);
        subscribeToQueue(queueName, consumer);
        try {
            return eventualCdr.get(timeout.toMillis(), MILLISECONDS);
        } catch (Exception e) {
            throw new EtiqetException(e);
        }
    }

    @Override
    public void subscribeToQueue(String queueName, Consumer<Cdr> cdrListener) throws EtiqetException {
        DeliverCallback deliverCallback = (consumerTag, delivery) ->
            cdrListener.accept(decodeMessageBytes(delivery.getBody()));
        try {
            final Channel channel = connection.createChannel();
            channel.basicConsume(queueName, false, deliverCallback, consumerTag -> {});
        } catch (IOException e) {
            throw new EtiqetException(e);
        }
    }

    private Cdr decodeMessageBytes(byte[] messageBytes) {
        String strMessage = new String(messageBytes, UTF_8);
        try {
            return codec.decode(strMessage);
        } catch (EtiqetException e) {
            logger.error("Unable to decode message bytes " + strMessage);
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
        final Object payload = codec.encode(cdr);
        final byte[] payloadBytes;
        if (payload instanceof String) {
            payloadBytes = ((String) payload).getBytes(UTF_8);
        } else {
            throw new EtiqetException("Unable to encode cdr");
        }
        try {
            channel.basicPublish(exchangeName, routingKey.orElse(""), null, payloadBytes);
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

    @Override
    public boolean isLoggedOn() {
        return connection.isOpen();
    }

    @Override
    public String getDefaultSessionId() {
        return channelsByExchange.keySet().stream().findFirst().orElse("EXCHANGE");// DEF_CONNECTION + SESSION_SEPARATOR + DEF_CHANNEL;
    }

    @Override
    public void setTransportDelegate(TransportDelegate<String, Cdr> transDel) {
        this.transDel = transDel;
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
