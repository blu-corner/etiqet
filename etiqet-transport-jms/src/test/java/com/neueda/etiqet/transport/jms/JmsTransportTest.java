package com.neueda.etiqet.transport.jms;

import com.example.tutorial.AddressBookProtos;
import com.google.protobuf.Message;
import com.neueda.etiqet.core.client.delegate.ClientDelegate;
import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import com.neueda.etiqet.core.message.cdr.Cdr;
import com.neueda.etiqet.core.transport.Codec;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQObjectMessage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import javax.jms.*;
import java.io.Serializable;
import java.time.Duration;

import static java.util.Optional.empty;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

public class JmsTransportTest {

    private JmsTransport transport;

    @Before
    public void setUp() {
        transport = new JmsTransport();
    }

    @After
    public void tearDown() {
        transport.stop();
        transport = null;
    }

    @Test
    public void testInit() throws EtiqetException {
        transport.init("src/test/resources/config/jmsConfig.xml");
        ActiveMQConnectionFactory connectionFactory = (ActiveMQConnectionFactory) transport.getConnectionFactory();
        assertNotNull("Connection Factory should exist after transport is initialised", connectionFactory);
        assertEquals("vm://localhost?broker.persistent=false", connectionFactory.getBrokerURL());
        assertEquals("USERNAME", connectionFactory.getUserName());
        assertEquals("PASSWORD", connectionFactory.getPassword());
        assertEquals("testTopic", transport.getDefaultSessionId());
    }

    @Test
    public void testInit_PropertiesNotFound() {
        String configPath = "src/test/resources/config/jms_config_not_found.xml";
        try {
            transport.init(configPath);
            fail("Should have failed because the above config file shouldn't exist");
        } catch (Exception e) {
            assertTrue(e instanceof EtiqetException);
            assertEquals("Exception thrown while trying to read " + configPath, e.getMessage());
        }
    }

    @Test
    public void testInit_invalidXmlConfiguration() {
        String configPath = "src/test/resources/config/jmsConfig_invalid.xml";
        try {
            transport.init(configPath);
            fail("Should have failed because the above config file is not valid");
        } catch (Exception e) {
            assertTrue(e instanceof EtiqetException);
            assertEquals("Exception thrown while parsing " + configPath + " as com.neueda.etiqet.transport.jms.JmsConfiguration" , e.getMessage());
        }
    }

    @Test
    public void testInit_propertiesMissingInConfiguration() throws EtiqetException{
        String configPath = "src/test/resources/config/jmsConfig_missingProperties.xml";

        transport.init(configPath);

        ActiveMQConnectionFactory connectionFactory = (ActiveMQConnectionFactory) transport.getConnectionFactory();
        assertNull(connectionFactory.getUserName());
        assertNull(connectionFactory.getPassword());
        assertEquals("tcp://localhost:61616", connectionFactory.getBrokerURL());
    }

    @Test
    public void testInit_constructorArgsMissingInConfiguration() throws EtiqetException{
        String configPath = "src/test/resources/config/jmsConfig_missingConstructorArgs.xml";

        transport.init(configPath);

        ActiveMQConnectionFactory connectionFactory = (ActiveMQConnectionFactory) transport.getConnectionFactory();
        assertEquals("USERNAME", connectionFactory.getUserName());
        assertEquals("PASSWORD", connectionFactory.getPassword());
        assertEquals("tcp://localhost:61616", connectionFactory.getBrokerURL());
    }

    @Test
    public void testStart() throws JMSException, EtiqetException {
        ConnectionFactory connectionFactory = mock(ConnectionFactory.class);
        Connection connection = mock(Connection.class);
        Session session = mock(Session.class);
        when(connection.createSession(anyBoolean(), anyInt())).thenReturn(session);
        when(connectionFactory.createConnection()).thenReturn(connection);

        transport.setConnectionFactory(connectionFactory);

        assertNull(transport.getConnection());
        assertFalse(transport.isLoggedOn());
        assertNull(transport.getSession());
        transport.start();
        assertEquals(connectionFactory, transport.getConnectionFactory());
        assertEquals(connection, transport.getConnection());
        assertTrue(transport.isLoggedOn());
        assertEquals(session, transport.getSession());

        verify(connectionFactory, times(1)).createConnection();
        verify(connection, times(1)).start();
        verify(connection, times(1)).createSession(false, Session.CLIENT_ACKNOWLEDGE);
        verifyNoMoreInteractions(connectionFactory, connection);
    }

    @Test
    public void testStart_CannotCreateSession() throws JMSException {
        ConnectionFactory connectionFactory = mock(ConnectionFactory.class);
        Connection connection = mock(Connection.class);
        when(connection.createSession(anyBoolean(), anyInt())).thenThrow(new JMSException("Unable to create session"));
        when(connectionFactory.createConnection()).thenReturn(connection);

        transport.setConnectionFactory(connectionFactory);

        try {
            assertNull(transport.getConnection());
            assertFalse(transport.isLoggedOn());
            assertNull(transport.getSession());
            transport.start();
            fail("Should have thrown an exception because we couldn't create a session");
        } catch (Exception e) {
            assertTrue(e instanceof EtiqetException);
            assertEquals("Couldn't create Jms connection", e.getMessage());
        }
    }

    @Test
    public void testStop() throws JMSException {
        Connection connection = mock(Connection.class);
        Session session = mock(Session.class);

        transport.setConnection(connection);
        transport.setSession(session);
        transport.stop();
        verify(connection, times(1)).stop();
        verify(session, times(1)).close();
        verifyNoMoreInteractions(connection, session);
    }

    @Test
    public void testStop_NoSessionNoConnection() {
        assertNull(transport.getConnection());
        assertFalse(transport.isLoggedOn());
        assertNull(transport.getSession());
        transport.stop();
        assertNull(transport.getConnection());
        assertFalse(transport.isLoggedOn());
        assertNull(transport.getSession());
    }

    @Test
    public void testStop_SessionCloseException() throws JMSException {
        Connection connection = mock(Connection.class);
        Session session = mock(Session.class);
        doThrow(new JMSException("Unable to close session")).when(session).close();

        transport.setConnection(connection);
        transport.setSession(session);
        transport.stop();
        assertNull(transport.getConnection());
        assertFalse(transport.isLoggedOn());
        assertNull(transport.getSession());

        verify(connection, times(1)).stop();
        verify(session, times(1)).close();
        verifyNoMoreInteractions(connection, session);
    }

    @Test
    public void testStop_ConnectionCloseException() throws JMSException {
        Connection connection = mock(Connection.class);
        Session session = mock(Session.class);
        doThrow(new JMSException("Unable to close session")).when(connection).stop();

        transport.setConnection(connection);
        transport.setSession(session);
        transport.stop();
        assertNull(transport.getConnection());
        assertFalse(transport.isLoggedOn());
        assertNull(transport.getSession());

        verify(connection, times(1)).stop();
        verify(session, times(1)).close();
        verifyNoMoreInteractions(connection, session);
    }

    @Test
    public void testSend_WithTopic() throws EtiqetException, JMSException {
        String testText = "TEST";
        Cdr testCdr = new Cdr("TEST");
        Codec<Cdr, String> codec = mock(Codec.class);
        when(codec.encode(any(Cdr.class))).thenReturn(testText);
        Session session = mock(Session.class);
        Topic topic = mock(Topic.class);
        when(session.createTopic(anyString())).thenReturn(topic);
        MessageProducer producer = mock(MessageProducer.class);
        when(session.createProducer(eq(topic))).thenReturn(producer);
        TextMessage message = mock(TextMessage.class);
        when(session.createTextMessage(eq(testText))).thenReturn(message);

        transport.setCodec(codec);
        transport.setSession(session);
        transport.send(testCdr, "TestTopic");
        verify(session, times(1)).createTopic(eq("TestTopic"));
        verify(session, times(1)).createProducer(eq(topic));
        verify(session, times(1)).createTextMessage(eq(testText));
        verify(producer, times(1)).send(eq(message));
        verifyNoMoreInteractions(session, producer);
    }

    @Test
    public void testSend_WithDefaultTopic() throws EtiqetException, JMSException {
        String testText = "TEST";
        Cdr testCdr = new Cdr("TEST");
        Codec<Cdr, String> codec = mock(Codec.class);
        when(codec.encode(any(Cdr.class))).thenReturn(testText);
        Session session = mock(Session.class);
        Topic topic = mock(Topic.class);
        when(session.createTopic(anyString())).thenReturn(topic);
        MessageProducer producer = mock(MessageProducer.class);
        when(session.createProducer(eq(topic))).thenReturn(producer);
        TextMessage message = mock(TextMessage.class);
        when(session.createTextMessage(eq(testText))).thenReturn(message);

        transport.setCodec(codec);
        transport.setSession(session);
        transport.setDefaultTopic("TestTopic");
        transport.send(testCdr);
        verify(session, times(1)).createTopic(eq("TestTopic"));
        verify(session, times(1)).createProducer(eq(topic));
        verify(session, times(1)).createTextMessage(eq(testText));
        verify(producer, times(1)).send(eq(message));
        verifyNoMoreInteractions(session, producer);
    }

    @Test
    public void testSend_NoTopicProvided() {
        Cdr testCdr = new Cdr("TEST");
        transport.setDefaultTopic("");
        try {
            transport.send(testCdr, null);
            fail("Should have failed because no viable topic was provided");
        } catch (Exception e) {
            assertTrue(e instanceof EtiqetException);
            assertEquals("Unable to send message without a defined topic", e.getMessage());
        }
    }

    @Test
    public void testSend_CannotCreateProducer() throws JMSException {
        Session session = mock(Session.class);
        Topic topic = mock(Topic.class);
        when(session.createTopic(anyString())).thenReturn(topic);
        when(session.createProducer(eq(topic))).thenThrow(new JMSException("Unable to create Jms Producer"));
        try {
            transport.setSession(session);
            transport.send(new Cdr("TEST"), "TestTopic");
            fail("Should have failed because and exception should have been thrown while creating the producer");
        } catch (Exception e) {
            assertTrue(e instanceof EtiqetException);
            assertEquals("Unable to send message to Jms topic TestTopic", e.getMessage());
        }
    }

    @Test
    public void testSubscribeAndConsumeFromTopic_protobufMessage() throws Exception{
        Session session = mock(Session.class);
        Topic topic = mock(Topic.class);
        ClientDelegate clientDelegate = mock(ClientDelegate.class);
        Codec<Cdr, Message> codec = mock(Codec.class);
        when(session.createTopic(anyString())).thenReturn(topic);
        MessageConsumer messageConsumer = mock(MessageConsumer.class);
        Message message = AddressBookProtos.Person.newBuilder().setId(23).setName("name").build();
        doAnswer(
            call -> {
                MessageListener ml = call.getArgument(0);
                ActiveMQObjectMessage activeMQMessage = new ActiveMQObjectMessage();
                activeMQMessage.setObject((Serializable) message);
                ml.onMessage(activeMQMessage);
                return null;
            }
        ).when(messageConsumer).setMessageListener(any(MessageListener.class));
        when(session.createConsumer(null)).thenReturn(messageConsumer);

        transport.setSession(session);
        transport.setCodec(codec);
        transport.setDelegate(clientDelegate);
        transport.subscribeAndConsumeFromTopic(empty(), Duration.ofMillis(700));

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(codec, times(1)).decode(messageCaptor.capture());
        Message receivedMessage = messageCaptor.getValue();
        assertEquals(receivedMessage.getClass(), message.getClass());
        AddressBookProtos.Person receivedPerson = (AddressBookProtos.Person) receivedMessage;
        assertEquals("name", receivedPerson.getName());
        assertEquals(23, receivedPerson.getId());
    }

    @Test
    public void testClearQueue() throws JMSException, EtiqetException {
        Queue queue = mock(Queue.class);
        Session session = mock(Session.class);
        when(session.createQueue(anyString())).thenReturn(queue);
        MessageConsumer consumer = mock(MessageConsumer.class);
        when(session.createConsumer(queue)).thenReturn(consumer);

        javax.jms.Message msg1 = mock(javax.jms.Message.class);
        javax.jms.Message msg2 = mock(javax.jms.Message.class);
        javax.jms.Message msg3 = mock(javax.jms.Message.class);
        javax.jms.Message msg4 = mock(javax.jms.Message.class);
        javax.jms.Message msg5 = mock(javax.jms.Message.class);

        when(consumer.receive(anyLong())).thenReturn(msg1, msg2, msg3, msg4, msg5, null);

        transport.setSession(session);
        transport.clearQueue("testQueue");
        verify(msg1, times(1)).acknowledge();
        verify(msg2, times(1)).acknowledge();
        verify(msg3, times(1)).acknowledge();
        verify(msg4, times(1)).acknowledge();
        verify(msg5, times(1)).acknowledge();
    }

}
