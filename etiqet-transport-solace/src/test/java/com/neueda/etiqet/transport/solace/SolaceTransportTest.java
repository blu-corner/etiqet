package com.neueda.etiqet.transport.solace;

import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import com.neueda.etiqet.core.message.cdr.Cdr;
import com.neueda.etiqet.core.transport.Codec;
import com.solacesystems.jms.SolXAConnectionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.jms.*;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

public class SolaceTransportTest {

    private SolaceTransport transport;

    @Before
    public void setUp() {
        transport = new SolaceTransport();
    }

    @After
    public void tearDown() {
        transport = null;
    }

    @Test
    public void testInit() throws EtiqetException {
        transport.init("src/test/resources/config/solace.cfg");
        SolXAConnectionFactory connectionFactory = transport.getConnectionFactory();
        assertNotNull("Connection Factory should exist after transport is initialised", connectionFactory);
        assertEquals("tcp://localhost", connectionFactory.getHost());
        assertEquals("default", connectionFactory.getVPN());
        assertEquals("USERNAME", connectionFactory.getUsername());
        assertEquals("PASSWORD", connectionFactory.getPassword());
        assertEquals("testTopic", transport.getDefaultSessionId());
    }

    @Test
    public void testInit_PropertiesNotFound() {
        String configPath = "src/test/resources/config/solace_config_not_found.cfg";
        try {
            transport.init(configPath);
            fail("Should have failed because the above config file shouldn't exist");
        } catch (Exception e) {
            assertTrue(e instanceof EtiqetException);
            assertEquals("Could not init Solace with config [" + configPath + "]", e.getMessage());
        }
    }

    @Test
    public void testStart() throws JMSException, EtiqetException {
        SolXAConnectionFactory connectionFactory = mock(SolXAConnectionFactory.class);
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
        verify(connection, times(1)).createSession(eq(true), eq(Session.AUTO_ACKNOWLEDGE));
        verifyNoMoreInteractions(connectionFactory, connection);
    }

    @Test
    public void testStart_CannotCreateSession() throws JMSException {
        SolXAConnectionFactory connectionFactory = mock(SolXAConnectionFactory.class);
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
            assertEquals("Couldn't create Solace connection", e.getMessage());
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
        when(session.createProducer(eq(topic))).thenThrow(new JMSException("Unable to create Solace Producer"));
        try {
            transport.setSession(session);
            transport.send(new Cdr("TEST"), "TestTopic");
            fail("Should have failed because and exception should have been thrown while creating the producer");
        } catch (Exception e) {
            assertTrue(e instanceof EtiqetException);
            assertEquals("javax.jms.JMSException: Unable to create Solace Producer", e.getMessage());
        }
    }

}
