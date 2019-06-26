package com.neueda.etiqet.transport.jms;

import com.neueda.etiqet.core.client.delegate.SinkClientDelegate;
import com.neueda.etiqet.core.json.JsonCodec;
import com.neueda.etiqet.core.message.CdrBuilder;
import com.neueda.etiqet.core.message.cdr.Cdr;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.junit.EmbeddedActiveMQBroker;
import org.junit.*;

import javax.jms.ConnectionFactory;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static java.util.stream.Collectors.toList;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class JmsTransportIntegrationTest {
    private JmsTransport jmsTransport;
    @Rule
    public EmbeddedActiveMQBroker broker;

    @Before
    public void setup() throws Exception {
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("vm://localhost?broker.persistent=false");
        jmsTransport = new JmsTransport();
        jmsTransport.setCodec(new JsonCodec());
        jmsTransport.setConnectionFactory(connectionFactory);
        jmsTransport.setDelegate(new SinkClientDelegate());
        jmsTransport.start();
    }

    @After
    public void tearDown() throws Exception {
        jmsTransport.stop();
    }

    @Test
    public void testSubscribeToTopicAndReceiveMessages() throws Exception {
        final String topicName = "topicTest01";
        BlockingQueue<Cdr> receivedMessages = new LinkedBlockingQueue<>();
        Cdr cdr = new Cdr("NONE");
        cdr.set("field", "value01");

        jmsTransport.subscribeToTopic(Optional.of(topicName), message -> receivedMessages.add(message));
        jmsTransport.sendToTopic(CdrBuilder.aCdr("NONE").withField("field", "value01").build(), Optional.of(topicName));
        jmsTransport.sendToTopic(CdrBuilder.aCdr("NONE").withField("field", "value02").build(), Optional.of(topicName));
        Thread.sleep(200);

        assertEquals(2, receivedMessages.size());
        List<String> values = receivedMessages.stream().map(m -> m.getAsString("field")).sorted(String::compareTo).collect(toList());
        assertEquals("value01", values.get(0));
        assertEquals("value02", values.get(1));
    }

    @Test
    public void testSubscribeToTopic_noMessagesReceived() throws Exception {
        final String topicName = "topicTest01";
        BlockingQueue<Cdr> receivedMessages = new LinkedBlockingQueue<>();

        jmsTransport.subscribeToTopic(Optional.of(topicName), message -> receivedMessages.add(message));
        Thread.sleep(200);

        assertTrue(receivedMessages.isEmpty());
    }

    @Test
    public void testSubscribeToQueueAndReceiveMessages() throws Exception {
        final String queueName = "queue01";
        BlockingQueue<Cdr> receivedMessages = new LinkedBlockingQueue<>();
        Cdr cdr = new Cdr("NONE");
        cdr.set("field", "value01");

        jmsTransport.subscribeToQueue(queueName, message -> receivedMessages.add(message));
        jmsTransport.sendToQueue(CdrBuilder.aCdr("NONE").withField("field", "value01").build(), queueName);
        jmsTransport.sendToQueue(CdrBuilder.aCdr("NONE").withField("field", "value02").build(), queueName);
        Thread.sleep(200);

        assertEquals(2, receivedMessages.size());
        List<String> values = receivedMessages.stream().map(m -> m.getAsString("field")).sorted(String::compareTo).collect(toList());
        assertEquals("value01", values.get(0));
        assertEquals("value02", values.get(1));
    }

}
