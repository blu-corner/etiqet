package com.neueda.etiqet.transport.jms;

import com.neueda.etiqet.core.client.delegate.SinkClientDelegate;
import com.neueda.etiqet.core.message.cdr.Cdr;
import com.neueda.etiqet.core.message.cdr.CdrItem;
import com.neueda.etiqet.core.message.config.AbstractDictionary;
import com.neueda.etiqet.core.message.dictionary.ProtobufDictionary;
import com.neueda.etiqet.core.transport.Codec;
import com.neueda.etiqet.core.transport.ProtobufCodec;
import com.neueda.etiqet.core.transport.delegate.BinaryMessageConverterDelegate;
import com.neueda.etiqet.core.transport.delegate.ProtobufBinaryMessageConverterDelegate;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.junit.EmbeddedActiveMQBroker;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static com.neueda.etiqet.core.message.CdrBuilder.aCdr;
import static junit.framework.TestCase.assertEquals;

public class JmsTransportProtobufIntegrationTest {
    private JmsTransport jmsTransport;

    @Rule
    public EmbeddedActiveMQBroker broker;

    @After
    public void tearDown() throws Exception {
        jmsTransport.stop();
    }

    @Test
    public void testSubscribeToTopicAndReceiveMessages() throws Exception {
        initTransport(null);
        final String topicName = "topicTest01";
        BlockingQueue<Cdr> receivedMessages = new LinkedBlockingQueue<>();
        Cdr cdr = aCdr("Person")
            .withField("name", "PersonName")
            .withField("id", 34)
            .withField("email", "aaa@aaa.aaa")
            .build();

        jmsTransport.subscribeToTopic(Optional.of(topicName), message -> receivedMessages.add(message));
        jmsTransport.sendToTopic(cdr, Optional.of(topicName));
        Thread.sleep(200);

        assertEquals(1, receivedMessages.size());
        Map<String, CdrItem> values = receivedMessages.take().getItems();
        assertEquals("PersonName", values.get("name").getStrval());
        assertEquals(34, Math.round(values.get("id").getIntval()));
        assertEquals("aaa@aaa.aaa", values.get("email").getStrval());

    }

    @Test
    public void testSubscribeToTopicAndReceiveBinaryMessages() throws Exception {
        initTransport(new ProtobufBinaryMessageConverterDelegate());
        final String topicName = "topicTest01";
        BlockingQueue<Cdr> receivedMessages = new LinkedBlockingQueue<>();
        Cdr cdr = aCdr("Person")
            .withField("name", "PersonName")
            .withField("id", 34)
            .withField("email", "aaa@aaa.aaa")
            .build();

        jmsTransport.subscribeToTopic(Optional.of(topicName), message -> receivedMessages.add(message));
        jmsTransport.sendToTopic(cdr, Optional.of(topicName));
        Thread.sleep(200);

        assertEquals(1, receivedMessages.size());
        Map<String, CdrItem> values = receivedMessages.take().getItems();
        assertEquals("PersonName", values.get("name").getStrval());
        assertEquals(34, Math.round(values.get("id").getIntval()));
        assertEquals("aaa@aaa.aaa", values.get("email").getStrval());
    }

    private void initTransport(BinaryMessageConverterDelegate binaryMessageConverterDelegate) throws Exception {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("vm://localhost?broker.persistent=false");
        connectionFactory.setTrustAllPackages(true);
        AbstractDictionary dictionary = new ProtobufDictionary("config/dictionary/addressbook.desc");
        jmsTransport = new JmsTransport();
        jmsTransport.setDictionary(dictionary);
        Codec codec = new ProtobufCodec();
        codec.setDictionary(dictionary);
        jmsTransport.setCodec(codec);
        jmsTransport.setConnectionFactory(connectionFactory);
        if (binaryMessageConverterDelegate != null) {
            binaryMessageConverterDelegate.setDictionary(dictionary);
        }
        jmsTransport.setBinaryMessageConverterDelegate(binaryMessageConverterDelegate);
        jmsTransport.setDelegate(new SinkClientDelegate());
        jmsTransport.start();
    }

}
