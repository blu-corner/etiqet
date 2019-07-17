package com.neueda.etiqet.transport.jms;

import com.neueda.etiqet.core.client.delegate.SinkClientDelegate;
import com.neueda.etiqet.core.config.dtos.Protocol;
import com.neueda.etiqet.core.message.cdr.Cdr;
import com.neueda.etiqet.core.message.cdr.CdrItem;
import com.neueda.etiqet.core.message.config.ProtocolConfig;
import com.neueda.etiqet.core.transport.ProtobufCodec;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.junit.EmbeddedActiveMQBroker;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import javax.xml.bind.JAXBContext;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static com.neueda.etiqet.core.message.CdrBuilder.aCdr;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class JmsTransportProtobufIntegrationTest {
    private JmsTransport jmsTransport;
    private ProtobufCodec codec;

    @Rule
    public EmbeddedActiveMQBroker broker;

    @Before
    public void setup() throws Exception {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("vm://localhost?broker.persistent=false");
        connectionFactory.setTrustAllPackages(true);
        jmsTransport = new JmsTransport();

        codec = new ProtobufCodec();

        // Had issues reading the URL when running from Maven command line (couldn't find the test protocol in the JAR),
        // so we're annoyingly parsing the protocol config manually
        InputStream protocolIS = getClass().getClassLoader()
                                           .getResourceAsStream("config/protobuf/testProtobufProtocol.xml");
        assertNotNull("Unable to find test protocol config", protocolIS);
        Protocol protocol = (Protocol) JAXBContext.newInstance(Protocol.class)
                                                  .createUnmarshaller()
                                                  .unmarshal(protocolIS);

        codec.setProtocolConfig(new ProtocolConfig(protocol));
        jmsTransport.setCodec(codec);
        jmsTransport.setConnectionFactory(connectionFactory);
        jmsTransport.setDelegate(new SinkClientDelegate());
        jmsTransport.start();
    }

    @After
    public void tearDown() {
        try {
            jmsTransport.stop();
        } catch (Exception ignored) {
        }
    }

    @Test
    public void testSubscribeToTopicAndReceiveMessages() throws Exception {
        initTransport();
        final String topicName = "topicTest01";
        BlockingQueue<Cdr> receivedMessages = new LinkedBlockingQueue<>();
        Cdr cdr = aCdr("Person")
            .withField("name", "PersonName")
            .withField("id", 34)
            .withField("email", "aaa@aaa.aaa")
            .build();

        jmsTransport.subscribeToTopic(Optional.of(topicName), receivedMessages::add);
        jmsTransport.sendToTopic(cdr, Optional.of(topicName));
        Thread.sleep(200);

        Cdr received = receivedMessages.poll(5, TimeUnit.SECONDS);
        assertNotNull(received);
        Map<String, CdrItem> values = received.getItems();
        assertEquals("PersonName", values.get("name").getStrval());
        assertEquals(34, Math.round(values.get("id").getIntval()));
        assertEquals("aaa@aaa.aaa", values.get("email").getStrval());

    }

    @Test
    public void testSubscribeToTopicAndReceiveBinaryMessages() throws Exception {
        initTransport();
        final String topicName = "topicTest01";
        BlockingQueue<Cdr> receivedMessages = new LinkedBlockingQueue<>();
        Cdr cdr = aCdr("Person")
            .withField("name", "PersonName")
            .withField("id", 34)
            .withField("email", "aaa@aaa.aaa")
            .build();

        jmsTransport.subscribeToTopic(Optional.of(topicName), receivedMessages::add);
        jmsTransport.sendToTopic(cdr, Optional.of(topicName));

        Cdr received = receivedMessages.poll(5, TimeUnit.SECONDS);
        assertNotNull(received);
        Map<String, CdrItem> values = received.getItems();
        assertEquals("PersonName", values.get("name").getStrval());
        assertEquals(34, Math.round(values.get("id").getIntval()));
        assertEquals("aaa@aaa.aaa", values.get("email").getStrval());
    }

    private void initTransport() throws Exception {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("vm://localhost?broker.persistent=false");
        connectionFactory.setTrustAllPackages(true);
        jmsTransport = new JmsTransport();
        jmsTransport.setCodec(codec);
        jmsTransport.setConnectionFactory(connectionFactory);
        jmsTransport.setDelegate(new SinkClientDelegate());
        jmsTransport.start();
    }

}
