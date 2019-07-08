package com.neueda.etiqet.transport.jms;

import com.example.tutorial.AddressBookProtos;
import com.neueda.etiqet.core.client.delegate.SinkClientDelegate;
import com.neueda.etiqet.core.message.cdr.Cdr;
import com.neueda.etiqet.core.message.cdr.CdrItem;
import com.neueda.etiqet.core.message.dictionary.ProtobufDictionary;
import com.neueda.etiqet.core.transport.Codec;
import com.neueda.etiqet.core.transport.ProtobufCodec;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.junit.EmbeddedActiveMQBroker;
import org.junit.*;

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

    @Before
    public void setup() throws Exception {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("vm://localhost?broker.persistent=false");
        connectionFactory.setTrustAllPackages(true);
        jmsTransport = new JmsTransport();
        Codec codec = new ProtobufCodec();
        codec.setDictionary(new ProtobufDictionary("config/dictionary/addressbook.desc"));
        jmsTransport.setCodec(codec);
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

}
