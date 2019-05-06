package com.neueda.etiqet.transport.rabbitmq;

import com.neueda.etiqet.core.client.delegate.ClientDelegate;
import com.neueda.etiqet.core.common.Environment;
import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import com.neueda.etiqet.core.message.cdr.Cdr;
import com.neueda.etiqet.core.transport.Codec;
import com.neueda.etiqet.core.transport.Transport;
import com.neueda.etiqet.core.transport.TransportDelegate;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.ShutdownNotifier;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RabbitMqTransport implements Transport {

  private static final String DEF_QUEUE = "DEF_QUEUE";
  private final static Logger logger = LoggerFactory.getLogger(RabbitMqTransport.class);
  private final static String DEF_CONNECTION = "DEF_CONNECTION";
  private final static String DEF_CHANNEL = "DEF_CHANNEL";
  private final static String SESSION_SEPARATOR = "\\.";
  private Map<String, Connection> connections = new HashMap<>();
  private Map<String, Channel> channels = new HashMap<>();
  private Codec<Cdr, byte[]> codec;
  private TransportDelegate<String, Cdr> transDel;
  private ClientDelegate delegate;

  @Override
  public void init(String configPath) throws EtiqetException {
    try {
      // Load configuration
      Properties props = new Properties();
      props.load(Environment.fileResolveEnvVars(configPath));

      // Create connection and channels
      ConnectionFactory factory = new ConnectionFactory();
      factory.setHost("localhost");
      Connection conn = factory.newConnection();
      Channel ch = conn.createChannel();
      String channelName = props.getProperty("connection.channel", DEF_CHANNEL);
      String queueName = props.getProperty("connection.channel.queue.name", DEF_QUEUE);
      ch.queueDeclare(
          queueName,
          Boolean.parseBoolean(props.getProperty("connection.channel.queue.durable", "false")),
          Boolean.parseBoolean(props.getProperty("connection.channel.queue.exclusive", "false")),
          Boolean.parseBoolean(props.getProperty("connection.channel.queue.autodelete", "false")),
          null);
      ch.basicConsume(queueName, new DefaultConsumer(ch) {
        @Override
        public void handleDelivery(String consumerTag,
            Envelope envelope,
            AMQP.BasicProperties properties,
            byte[] body)
            throws IOException {
          String routingKey = envelope.getRoutingKey();
          String contentType = properties.getContentType();
          long deliveryTag = envelope.getDeliveryTag();

          try {
            codec.decode(body);
          } catch (EtiqetException e) {
            logger.error("Error decoding message with routing key [" + routingKey +
                "] and content type [" + contentType + "]");
          }

          ch.basicAck(deliveryTag, false);
        }
      });
      connections.put(DEF_CONNECTION, conn);
      channels.put(channelName, ch);
    } catch (Exception e) {
      throw new EtiqetException("Could not init RabbitMQ with config [" + configPath + "]", e);
    }
  }

  @Override
  public void start() throws EtiqetException {
    try {
      for(String channelId: channels.keySet()) {
        send(new Cdr("Logon"), channelId);
      }
    } catch (Exception e) {
      throw new EtiqetException("Could not start RabbitMQ. Reason " + e.getMessage(), e);
    }
  }

  @Override
  public void stop() {
    connections.forEach((k, c) -> {
      try {
        c.close();
      } catch (IOException e) {
        logger.error("Could not close connection " + c.getClientProvidedName());
      }
    });
  }

  /**
   * Sends a message through the given channel.
   *
   * @param msg the message to be sent.
   * @param sessionId the session identifier to address the message to.
   * @param channelName the name of the channel.
   */
  private void send(Cdr msg, String sessionId, String channelName) throws EtiqetException {
    try {
      channels.get(sessionId).basicPublish("", channelName, null, codec.encode(msg));
    } catch (IOException e) {
      throw new EtiqetException("Could not send message [" + msg.toString() + "]", e);
    }
  }

  @Override
  public void send(Cdr msg) throws EtiqetException {
    send(msg, getDefaultSessionId(), DEF_CHANNEL);
  }

  @Override
  public void send(Cdr msg, String sessionId) throws EtiqetException {
    send(msg, sessionId, sessionId.split(SESSION_SEPARATOR)[1]);
  }

  @Override
  public boolean isLoggedOn() {
    return connections.values().stream().anyMatch(ShutdownNotifier::isOpen);
  }

  @Override
  public String getDefaultSessionId() {
    return DEF_CONNECTION + SESSION_SEPARATOR + DEF_CHANNEL;
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
