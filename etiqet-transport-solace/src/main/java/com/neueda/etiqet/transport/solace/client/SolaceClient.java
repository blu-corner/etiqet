package com.neueda.etiqet.transport.solace.client;

import com.neueda.etiqet.core.client.Client;
import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import com.neueda.etiqet.core.json.JsonUtils;
import com.neueda.etiqet.core.message.cdr.Cdr;
import com.neueda.etiqet.core.message.config.ProtocolConfig;
import com.neueda.etiqet.transport.solace.SolaceUtils;
import com.solacesystems.jms.SolMessageConsumer;
import com.solacesystems.jms.SolXAConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;


public class SolaceClient extends Client {

    private static final Logger LOG = LoggerFactory.getLogger(SolaceClient.class);

    private SolXAConnectionFactory connectionFactory;
    private Connection connection;
    private Session session;
    private String topic;

    public SolaceClient(String clientConfig) throws EtiqetException {
        this(clientConfig, null);
    }

    public SolaceClient(String primaryClientConfig, String secondaryClientConfig) throws EtiqetException {
        this(primaryClientConfig, secondaryClientConfig, null);
    }

    public SolaceClient(String primaryClientConfig,
                        String secondaryClientConfig,
                        ProtocolConfig protocol) throws EtiqetException {
        super(primaryClientConfig, secondaryClientConfig, protocol);
        topic = getConfig().getString("solaceDefaultTopic");
        connectionFactory = SolaceUtils.getConnectionFactory(getConfig().getString("solaceHost"),
                                                             getConfig().getString("solaceVpn"),
                                                             getConfig().getString("solaceUser"),
                                                             getConfig().getString("solacePassword"));
        try {
            connection = connectionFactory.createConnection();
            session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
        } catch (Exception e) {
            LOG.error("Couldn't create Solace session", e);
            throw new EtiqetException("Error creating Solace Client", e);
        }
    }

    public Cdr waitForMsgOnQueue(String queueName, Integer timeoutMillis) throws EtiqetException {
        try {
            return waitForMsgOnDestination(session.createQueue(queueName), timeoutMillis);
        } catch (JMSException e) {
            LOG.debug("No message received on Queue " + queueName);
            throw new EtiqetException(e);
        }
    }

    public Cdr waitForMsgOnTopic(String topicName, Integer timeoutMillis) throws EtiqetException {
        try {
            return waitForMsgOnDestination(session.createTopic(topicName), timeoutMillis);
        } catch (JMSException e) {
            LOG.debug("No message received on Topic " + topicName);
            throw new EtiqetException(e);
        }
    }

    public Cdr waitForMsgOnDestination(Destination destination, Integer timeoutMillis) throws EtiqetException {
        try {
            SolMessageConsumer consumer = (SolMessageConsumer) session.createConsumer(destination);

            consumer.setMessageListener(message -> {
                if (message instanceof TextMessage) {
                    try {
                        TextMessage txt = (TextMessage) message;
                        Cdr cdr = delegate.processMessage((Cdr) getCodec().decode(txt.getText()));
                        LOG.info("Received on destination {}: {}", destination, cdr);
                        msgQueue.add(cdr);
                        message.acknowledge();
                    } catch (Exception e) {
                        LOG.error("Error while getting message from Solace bus", e);
                    }
                } else if(message instanceof BytesMessage) {
                    try {
                        BytesMessage bytesXMLMessage = ((BytesMessage) message);
                        byte[] b = new byte[(int) bytesXMLMessage.getBodyLength()];
                        bytesXMLMessage.readBytes(b);

                        Cdr cdr = delegate.processMessage((Cdr) getCodec().decode(new String(b)));
                        LOG.info("Received on destination {}: {}", destination, cdr);

                        msgQueue.add(cdr);
                        message.acknowledge();
                    } catch (Exception e) {
                        LOG.error("Error while getting message from Solace bus", e);
                    }
                }
            });

            if (timeoutMillis == null) {
                timeoutMillis = 5000; // 5 seconds
            }


            connection.start();
            consumer.start();

            Cdr msg = msgQueue.poll(timeoutMillis, TimeUnit.MILLISECONDS);
            consumer.stop();
            return msg;

        } catch (JMSException e) {
            throw new EtiqetException(e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new EtiqetException(e);
        }
    }

    @Override
    public Cdr waitForMsg(BlockingQueue<Cdr> queue, Integer timeoutMillis) throws EtiqetException {
        return waitForMsgOnTopic(topic, timeoutMillis);
    }

    @Override
    public boolean isLoggedOn() {
        return session != null;
    }

    @Override
    public void stop() {
        super.stop();
        try {
            session.close();
            connection.close();
        } catch (JMSException e) {
            LOG.error(  "Error closing Solace connection", e);
        } finally {
            session = null;
            connection = null;
        }
    }
}
