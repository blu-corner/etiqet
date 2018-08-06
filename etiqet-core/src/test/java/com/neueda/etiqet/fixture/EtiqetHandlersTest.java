package com.neueda.etiqet.fixture;

import com.neueda.etiqet.core.client.Client;
import com.neueda.etiqet.core.client.ClientFactory;
import com.neueda.etiqet.core.common.cdr.Cdr;
import com.neueda.etiqet.core.common.cdr.CdrItem;
import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import com.neueda.etiqet.core.config.GlobalConfig;
import com.neueda.etiqet.core.testing.client.TestClient;
import com.neueda.etiqet.core.testing.server.TestServer;
import com.neueda.etiqet.core.util.Separators;
import org.awaitility.Duration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import javax.net.ssl.HttpsURLConnection;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.Assert.*;

public class EtiqetHandlersTest {

    private EtiqetHandlers handlers;

    @Before
    public void setUp() throws Exception {
        handlers = new EtiqetHandlers() {
            @Override
            public Client createClient(String impl, String clientName) throws EtiqetException {
                if (clientName.contains("ThrowError")) {
                    // stubbed exception for testing the catch statement in EtiqetHandlers
                    throw new EtiqetException("Error creating client " + clientName);
                }

                String configDir = System.getProperty("user.dir") + "/src/test/resources";
                String etiqetConfig = configDir + "/properties/test.properties";
                String clientConfig = configDir + "/properties/testConfig.properties";
                Client client = ClientFactory.create("testProtocol", etiqetConfig, clientConfig);
                client.setProtocolConfig(GlobalConfig.getInstance().getProtocol("testProtocol"));
                addClient(clientName, client);
                return client;
            }
        };
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testStartServer() {
        EtiqetHandlers handlers = new EtiqetHandlers();
        String serverName = "testServer";

        handlers.createServer(serverName, "com.neueda.etiqet.core.testing.server.TestServer");
        handlers.startServer(serverName);
        assertTrue(handlers.getServer(serverName) instanceof TestServer);
        assertTrue(((TestServer) handlers.getServer(serverName)).isStarted());

        handlers.closeServer(serverName);
        assertFalse(((TestServer) handlers.getServer(serverName)).isStarted());
    }

    @Test
    public void testStartServerInvalidServerClass() {
        EtiqetHandlers handlers = new EtiqetHandlers();
        String serverName = "testServer";
        String serverClass = "com.neueda.etiqet.core.testing.server.ServerNotFound";

        try {
            handlers.createServer(serverName, serverClass);
            fail("Server class " + serverClass + " should not exists so starting it should fail");
        } catch (AssertionError e) {
            assertEquals("Error creating server type " + serverClass, e.getMessage());
        }
    }

    @Test
    public void testCloseServerNotStarted() {
        EtiqetHandlers handlers = new EtiqetHandlers();
        String serverName = "testCaseServer";
        assertNull("Server should be null before and after calling closeServer in testCloseServerNotStarted",
                handlers.getServer(serverName));
        handlers.closeServer(serverName);
        assertNull("Server should be null before and after calling closeServer in testCloseServerNotStarted",
                handlers.getServer(serverName));
    }

    @Test
    public void testEqualityNull() throws EtiqetException {

        String clientName = "testClient";
        Client client = handlers.createClient("testProtocol", clientName);
        handlers.startClient(clientName);

        handlers.createMessageForClient("TestMsg", clientName,
                "message1", "test=null,messageId=123");
        handlers.sendMessage("message1", clientName);
        handlers.waitForResponseOfType("response1", clientName, "testResponse");
        handlers.compareTimestampEqualsCukeVar("test", "response1", "");
    }

    @Test
    public void testClientFailover() throws EtiqetException {
        String clientName = "testClient3";
        String pconfig = System.getProperty("user.dir") + "/src/test/resources/properties/testConfig.properties";
        String sconfig = System.getProperty("user.dir") + "/src/test/resources/properties/testConfig.properties";

        EtiqetHandlers handler = new EtiqetHandlers();
        assertNull(clientName + ": shouldn\'t exist, but does!", handler.getClient(clientName));

        handler.createClientWithFailover("testProtocol", clientName, pconfig, sconfig);
        handler.startClient(clientName);
        Client client = handler.getClient(clientName);
        assertEquals("testValue", client.getConfig().getString("testProperty"));
        handler.createMessageForClient("TestMsg", clientName,
                "message1", "test=null,messageId=123");

        assertNotNull(clientName + " should exist but doesn\'t", client);
        assertTrue(clientName + " should be able to failover but cannot", client.canFailover());
        handler.sendMessage("message1", clientName);
        handler.waitForResponseOfType("response1", clientName, "testResponse");

        handler.failover(clientName);
        client = handler.getClient(clientName);
        assertNotNull(clientName + " should still exist but doesn\'t", client);
    }

    @Test
    public void testCantClientFailoverWithNoSecondary() throws EtiqetException {
        String clientName = "testClient1";
        String pconfig = System.getProperty("user.dir") + "/src/test/resources/properties/testConfig.properties";
        EtiqetHandlers handler = new EtiqetHandlers();
        boolean failedToFailover = true;
        String message = null;
        try {
            handler.createClientWithFailover("testProtocol", clientName, pconfig, null);
        } catch (EtiqetException e) {
            failedToFailover = false;
            message = e.getMessage();
        }
        assertTrue("Incorrect error message: " + message, message == "Secondary Config must be provided when trying to create a client with failover capabilities");
        assertFalse("Failover should have failed", failedToFailover);

    }

    @Test
    public void testCantClientFailoverWithRegularClient() throws EtiqetException {
        String clientName = "testClient1";
        EtiqetHandlers handler = new EtiqetHandlers();
        handler.createClient("testProtocol", clientName);
        handler.startClient(clientName);

        assertNotNull(handler.getClient(clientName));
        Client client = handler.getClient(clientName);

        assertFalse("Client should not be able to failover", client.canFailover());

        boolean failedToFailover = true;
        String message = null;
        try {
            handler.failover(clientName);
        } catch (EtiqetException e) {
            failedToFailover = false;
            message = e.getMessage();
        }
        assertFalse("Client should not be able to failover", failedToFailover);
        assertTrue("Client should have failed however message is: " + message, message.equalsIgnoreCase("Client: " + clientName + " not enabled for failover"));

    }

    @Test
    public void testCompareTimestamps() {
        String firstMessageAlias = "ER";
        String secondMessageAlias = "NOS";
        EtiqetHandlers mock = Mockito.spy(EtiqetHandlers.class);

        String sFirstField = "OrderQty";
        String sSecondField = "OrderQty";

        Mockito.doReturn("=200").when(mock).preTreatParams("=" + firstMessageAlias + "->" + sFirstField);
        Mockito.doReturn("=50").when(mock).preTreatParams("=" + secondMessageAlias + "->" + sSecondField);

        mock.compareValues(sFirstField, firstMessageAlias, sSecondField, secondMessageAlias, null);

        String firstField = "TransactTime";
        String secondField = "TransactTime";

        Mockito.doReturn("=20180101-01:01:02.000").when(mock).preTreatParams("=" + firstMessageAlias + "->" + firstField);
        Mockito.doReturn(10006000L).when(mock).dateToNanos("20180101-01:01:02.000");

        Mockito.doReturn("=20180101-01:01:01.000").when(mock).preTreatParams("=" + secondMessageAlias + "->" + secondField);
        Mockito.doReturn(10005000L).when(mock).dateToNanos("20180101-01:01:01.000");

        mock.compareValues(firstField, firstMessageAlias, secondField, secondMessageAlias, 5000L);

        Mockito.doReturn("=20180101-01:01:01.000").when(mock).preTreatParams("=" + firstMessageAlias + "->" + firstField);
        Mockito.doReturn(10005000L).when(mock).dateToNanos("20180101-01:01:01.000");

        Mockito.doReturn("=20180101-01:01:02.000").when(mock).preTreatParams("=" + secondMessageAlias + "->" + secondField);
        Mockito.doReturn(10006000L).when(mock).dateToNanos("20180101-01:01:02.000");

        try {
            mock.compareValues(firstField, firstMessageAlias, secondField, secondMessageAlias, 5000L);
            fail("Reversing timestamps should have raised an AssertionError");
        } catch (AssertionError e) {
            assertEquals("Expected " + firstField + " in " + firstMessageAlias
                    + " to be greater than or equal to " + secondField + " in " + secondMessageAlias, e.getMessage());
        }
    }

    @Test
    public void testCompareTimestampsMillis() {
        String firstField = "TransactTime";
        String secondField = "TransactTime";
        String firstMessageAlias = "ER";
        String secondMessageAlias = "NOS";

        EtiqetHandlers mock = Mockito.spy(EtiqetHandlers.class);

        Mockito.doReturn("=20180101-01:01:02.000").when(mock).preTreatParams("=" + firstMessageAlias + "->" + firstField);
        Mockito.doReturn(10006000L).when(mock).dateToNanos("20180101-01:01:02.000");

        Mockito.doReturn("=20180101-01:01:01.000").when(mock).preTreatParams("=" + secondMessageAlias + "->" + secondField);
        Mockito.doReturn(10005000L).when(mock).dateToNanos("20180101-01:01:01.000");

        mock.compareValues(firstField, firstMessageAlias, secondField, secondMessageAlias, null);

        Mockito.doReturn("=20180101-01:01:01.100").when(mock).preTreatParams("=" + firstMessageAlias + "->" + firstField);
        Mockito.doReturn(10006000L).when(mock).dateToNanos("20180101-01:01:02.000");

        Mockito.doReturn("=20180101-01:01:01.000").when(mock).preTreatParams("=" + secondMessageAlias + "->" + secondField);
        Mockito.doReturn(10005000L).when(mock).dateToNanos("20180101-01:01:01.000");

        try {
            mock.compareValues(firstField, firstMessageAlias, secondField, secondMessageAlias, 50L);
        } catch (AssertionError e) {
            assertEquals("Expected " + firstField + " in " + firstMessageAlias + " to be greater than or equal to " +
                    secondField + " in " + secondMessageAlias + " by no more than 50ms", e.getMessage());
        }
    }

    @Test
    public void testNanoSeconds() {
        String t = "20180101-01:01:02.000111000";
        String t0 = "20180101-01:01:02.000111000";
        String t00 = "20180101-01:01:02.000111001";
        EtiqetHandlers eh = new EtiqetHandlers();
        long t1 = eh.dateToNanos(t);
        long t2 = eh.dateToNanos(t0);
        long t3 = eh.dateToNanos(t00);
        System.out.println(String.format("nano %d", eh.dateToNanos(t)));
        assertTrue(t1 == t2);
        assertTrue(t3 > t2);
        assertTrue(t1 < t3);
    }

    @Test
    public void testCompareTimestampsIncorrectValues() {
        String firstField = "TransactTime";
        String secondField = "TransactTime";
        String firstMessageAlias = "ER";
        String secondMessageAlias = "NOS";

        EtiqetHandlers mock = Mockito.spy(EtiqetHandlers.class);

        Mockito.doReturn("=99999999-01:01:02.000").when(mock).preTreatParams("=" + firstMessageAlias + "->" + firstField);
        Mockito.doReturn(-1L).when(mock).dateToNanos("99999999-01:01:02.000");
        Mockito.doReturn("=20180101-01:01:01.000").when(mock).preTreatParams("=" + secondMessageAlias + "->" + secondField);
        Mockito.doReturn(10005000L).when(mock).dateToNanos("20180101-01:01:01.000");

        try {
            mock.compareValues(firstField, firstMessageAlias, secondField, secondMessageAlias, null);
            fail("Comparing timestamps -1L and 10005000L should have thrown an assertion error");
        } catch (AssertionError e) {
            assertEquals("Could not read " + firstField + " in " + firstMessageAlias, e.getMessage());
        }

        Mockito.doReturn("=20180101-01:01:01.000").when(mock).preTreatParams("=" + firstMessageAlias + "->" + firstField);
        Mockito.doReturn(10005000L).when(mock).dateToNanos("20180101-01:01:01.000");
        Mockito.doReturn("=99999999-01:01:02.000").when(mock).preTreatParams("=" + secondMessageAlias + "->" + secondField);
        Mockito.doReturn(-1L).when(mock).dateToNanos("99999999-01:01:02.000");

        try {
            mock.compareValues(firstField, firstMessageAlias, secondField, secondMessageAlias, null);
            fail("Comparing timestamps -1L and 10005000L should have thrown an assertion error");
        } catch (AssertionError e) {
            assertEquals("Could not read " + secondField + " in " + secondMessageAlias, e.getMessage());
        }
    }

    @Test
    public void testSendMessageWrongMessageName() throws EtiqetException {
        EtiqetHandlers handlers = new EtiqetHandlers();
        try {
            handlers.sendMessage("messageNotFound", "clientNotFound");
            fail("Message name 'messageNotFound' should not exist so should throw an AssertionError");
        } catch (AssertionError e) {
            assertEquals("sendMessage:Message 'messageNotFound' must exist", e.getMessage());
        }
    }

    @Test
    public void testSendMessageWrongClientName() throws EtiqetException {
        EtiqetHandlers handlers = new EtiqetHandlers();
        handlers.addMessage("testMsg", new Cdr("TestMsg"));
        try {
            handlers.sendMessage("testMsg", "clientNotFound");
            fail("Client name 'clientNotFound' should not exist so should throw an AssertionError");
        } catch (AssertionError e) {
            assertEquals("Client clientNotFound must exist", e.getMessage());
        }
    }

    @Test
    public void testSendMessageWithSessionName() throws EtiqetException {
        EtiqetHandlers handlers = new EtiqetHandlers();
        handlers.addMessage("testMsg", new Cdr("TestMsg"));
        TestClient client = new TestClient();
        handlers.addClient("testClient", client);
        handlers.sendMessage("testMsg", "testClient", "randomSessionID");
        assertEquals(1, client.getMessagesSent().size());
        assertEquals("TestMsg", client.getMessagesSent().peek().getType());
        assertEquals("randomSessionID", client.getSessionId());
    }

    @Test
    public void testWaitForResponseClientNotFound() throws EtiqetException {
        EtiqetHandlers handlers = new EtiqetHandlers();
        String clientName = "clientNotFound";
        try {
            handlers.waitForResponseOfType("default", clientName, "MsgType", 100);
        } catch (AssertionError e) {
            assertEquals("Client " + clientName + " must exist", e.getMessage());
        }
    }

    @Test
    public void testPreTreatParamsNullEmpty() {
        EtiqetHandlers handlers = new EtiqetHandlers();
        assertEquals("Empty string parameters should return an empty string",
                "", handlers.preTreatParams(""));
        assertEquals("Null parameters should return null", "", handlers.preTreatParams(null));
        assertEquals("Parameters less than 3 characters should return null",
                "", handlers.preTreatParams("="));
        assertEquals("Parameters less than 3 characters should return null",
                "", handlers.preTreatParams("x="));
        assertEquals("Parameters less than 3 characters should return null",
                "", handlers.preTreatParams("=y"));
    }

    private Cdr createNestedCdr() {
        Cdr parent = new Cdr("TestMsg");
        Cdr child1 = new Cdr("child1");
        Cdr child2 = new Cdr("child2");
        child2.set("nestedChild", "testValue");

        CdrItem child1Items = new CdrItem(CdrItem.CdrItemType.CDR_ARRAY);
        child1Items.setCdrs(Collections.singletonList(child2));
        child1.setItem("child", child1Items);

        CdrItem parentItems = new CdrItem(CdrItem.CdrItemType.CDR_ARRAY);
        parentItems.setCdrs(Collections.singletonList(child1));
        parent.setItem("parent", parentItems);

        return parent;
    }

    @Test
    public void testPreTreatParams() {
        EtiqetHandlers handlers = new EtiqetHandlers();
        assertEquals("value", handlers.preTreatParams("value"));

        Cdr inputMsg = new Cdr("inputMsg");
        inputMsg.set("testField", "testValue");

        handlers.addMessage("inputMsg", inputMsg);

        assertEquals("value=testValue,value2=test",
                handlers.preTreatParams("value=inputMsg->testField,value2=test"));

        Cdr responseMsg = new Cdr("responseMsg");
        responseMsg.set("testResponseField", "testValue2");
        handlers.addResponse("testResponse", responseMsg);

        assertEquals("value=testValue2", handlers.preTreatParams("value=testResponse->testResponseField"));

        Cdr nestedCdr = createNestedCdr();
        handlers.addMessage(EtiqetHandlers.DEFAULT_MESSAGE_NAME, nestedCdr);

        assertEquals("value=testValue", handlers.preTreatParams("value=parent->child->nestedChild"));

        handlers.removeSentMessage(EtiqetHandlers.DEFAULT_MESSAGE_NAME);
        handlers.addResponse(EtiqetHandlers.DEFAULT_MESSAGE_NAME, nestedCdr);

        assertEquals("value=testValue", handlers.preTreatParams("value=parent->child->nestedChild"));

        SimpleDateFormat f = new SimpleDateFormat("yyyyMMdd-HH:mm:ss.SSS");
        f.setTimeZone(TimeZone.getTimeZone("UTC"));

        String params = handlers.preTreatParams("TransactTime=now");
        assertFalse("TransactTime=now should replace the value now with current timestamp",
                params.contains("now"));
        String timestamp = params.split(Separators.KEY_VALUE_SEPARATOR)[1];
        try {
            // Can't ocmpare the exact time because it's down to milliseconds, so best result is to test format matches
            f.parse(timestamp);
        } catch (ParseException e) {
            fail("Unable to parse resulting timestamp (" + timestamp + ") matching " + f.toPattern());
        }
    }

    @Test
    public void testGetValueFromTree() {
        EtiqetHandlers handlers = new EtiqetHandlers();

        Cdr parent = createNestedCdr();

        assertEquals("testValue", handlers.getValueFromTree("parent->child->nestedChild", parent));
        assertNull(handlers.getValueFromTree("parent->child->childNotFound", parent));

        parent.clear();
        parent.set("parent", "testValue");
        assertNull(handlers.getValueFromTree("parent->child->childNotFound", parent));
        assertNull(handlers.getValueFromTree("parent2", parent));
    }

    @Test
    public void testExtractTimestampAndCukeVar() throws EtiqetException, InterruptedException {
        String field = "TransactTime";
        String firstTimestamp = "20180101-01:01:01.000";
        String sSecondTimestamp = "20180101-01:01:02.000";
        String clientName = "testClient";

        EtiqetHandlers mock = Mockito.spy(EtiqetHandlers.class);
        Client client = mock.createClient("testProtocol", clientName);
        assertTrue(client instanceof TestClient);

        long firstTimestampLong = 10005000L;
        long secondTimestampLong = 10005555L;
        Mockito.doReturn(firstTimestampLong).when(mock).dateToNanos(firstTimestamp);
        Mockito.doReturn(secondTimestampLong).when(mock).dateToNanos(sSecondTimestamp);

        String messageName = "testMessage";
        mock.createMessage("TestMsg", "testProtocol", messageName,
                "test=value,TransactTime=" + firstTimestamp);
        mock.sendMessage(messageName, clientName);

        mock.waitForResponseOfType("testResponse", clientName, "testResponse");

        Long[] result = mock.extractTimestampAndCukeVar(field, null, sSecondTimestamp);
        assertArrayEquals(new Long[]{firstTimestampLong, secondTimestampLong}, result);

        String messageAlias = "ER";
        Mockito.doReturn("=" + firstTimestamp).when(mock).preTreatParams("=" + messageAlias + "->" + field);

        result = mock.extractTimestampAndCukeVar(field, messageAlias, sSecondTimestamp);
        assertArrayEquals(new Long[]{firstTimestampLong, secondTimestampLong}, result);
    }

    @Test
    public void testValueMoreThanValue() {

        String firstMessageAlias = "ER";
        String secondMessageAlias = "NOS";
        EtiqetHandlers mock = Mockito.spy(EtiqetHandlers.class);

        String sFirstField = "OrderQty";
        String sSecondField = "OrderQty";

        Mockito.doReturn("=50").when(mock).preTreatParams("=" + firstMessageAlias + "->" + sFirstField);
        Mockito.doReturn("=200").when(mock).preTreatParams("=" + secondMessageAlias + "->" + sSecondField);

        mock.compareValues(sSecondField, secondMessageAlias, sFirstField, firstMessageAlias, null);
    }

    @Test
    public void testValueEquality() {

        String firstMessageAlias = "ER";
        String secondMessageAlias = "NOS";
        EtiqetHandlers mock = Mockito.spy(EtiqetHandlers.class);

        String sFirstField = "OrderQty";
        String sSecondField = "OrderQty";

        Mockito.doReturn("=200").when(mock).preTreatParams("=" + firstMessageAlias + "->" + sFirstField);
        Mockito.doReturn("=200").when(mock).preTreatParams("=" + secondMessageAlias + "->" + sSecondField);

        mock.compareValuesEqual(sSecondField, secondMessageAlias, sFirstField, firstMessageAlias);
    }

    @Test
    public void testCompareTimestampGreaterCukeVar() {
        String field = "TransactTime";
        String messageAlias = "ER";
        String sSecondTimestamp = "20180101-01:01:02.000";

        EtiqetHandlers mock = Mockito.spy(EtiqetHandlers.class);

        Mockito.doReturn(new Long[]{1234567L, 1234566L}).when(mock).extractTimestampAndCukeVar(field, messageAlias, sSecondTimestamp);

        mock.compareTimestampGreaterCukeVar(field, messageAlias, sSecondTimestamp);
    }

    @Test(expected = AssertionError.class)
    public void testCompareTimestampGreaterCukeVarIncorrectValues() {
        String field = "TransactTime";
        String messageAlias = "ER";
        String sSecondTimestamp = "20180101-01:01:02.000";

        EtiqetHandlers mock = Mockito.spy(EtiqetHandlers.class);

        Mockito.doReturn(new Long[]{1234566L, 1234567L}).when(mock).extractTimestampAndCukeVar(field, messageAlias, sSecondTimestamp);

        mock.compareTimestampGreaterCukeVar(field, messageAlias, sSecondTimestamp);
    }

    @Test
    public void testCompareTimestampLesserCukeVar() {
        String field = "TransactTime";
        String messageAlias = "ER";
        String sSecondTimestamp = "20180101-01:01:02.000";

        EtiqetHandlers mock = Mockito.spy(EtiqetHandlers.class);

        Mockito.doReturn(new Long[]{1234566L, 1234567L}).when(mock).extractTimestampAndCukeVar(field, messageAlias, sSecondTimestamp);

        mock.compareTimestampLesserCukeVar(field, messageAlias, sSecondTimestamp);
    }

    @Test(expected = AssertionError.class)
    public void testCompareTimestampLesserCukeVarIncorrectValues() {
        String field = "TransactTime";
        String messageAlias = "ER";
        String sSecondTimestamp = "20180101-01:01:02.000";

        EtiqetHandlers mock = Mockito.spy(EtiqetHandlers.class);

        Mockito.doReturn(new Long[]{1234567L, 1234566L}).when(mock).extractTimestampAndCukeVar(field, messageAlias, sSecondTimestamp);

        mock.compareTimestampLesserCukeVar(field, messageAlias, sSecondTimestamp);
    }

    @Test
    public void testCompareTimestampEqualsCukeVar() {
        String field = "TransactTime";
        String messageAlias = "ER";
        String sSecondTimestamp = "20180101-01:01:02.000";

        EtiqetHandlers mock = Mockito.spy(EtiqetHandlers.class);

        Mockito.doReturn("=20180101-01:01:02.000").when(mock).preTreatParams("=" + messageAlias + "->" + field);

        mock.compareTimestampEqualsCukeVar(field, messageAlias, sSecondTimestamp);
    }

    @Test(expected = AssertionError.class)
    public void testCompareTimestampEqualsCukeVarIncorrectValues() {
        String field = "TransactTime";
        String messageAlias = "ER";
        String sSecondTimestamp = "20180101-01:01:01.000";

        EtiqetHandlers mock = Mockito.spy(EtiqetHandlers.class);

        Mockito.doReturn("=20180101-01:01:02.000").when(mock).preTreatParams("=" + messageAlias + "->" + field);

        mock.compareTimestampEqualsCukeVar(field, messageAlias, sSecondTimestamp);
    }

    @Test
    public void testValidateTimestampAgainstFormatParam() {
        String formatParam = "yyyymmdd-HH:mm:ss.SSS";
        String messageAlias = "ER";
        String field = "TransactTime";

        EtiqetHandlers mock = Mockito.spy(EtiqetHandlers.class);

        Mockito.doReturn("=20181212-01:01:01.000").when(mock).preTreatParams("=" + messageAlias + "->" + field);
        mock.validateTimestampAgainstFormatParam(formatParam, messageAlias, field);
    }

    @Test
    public void testValidateTimestampFails() {
        String formatParam = "yyyymmdd-HH:mm:ss.SSS";
        String messageAlias = "ER";
        String field = "TransactTime";

        EtiqetHandlers mock = Mockito.spy(EtiqetHandlers.class);

        Mockito.doReturn("=99999999-01:01:01.000").when(mock).preTreatParams("=" + messageAlias + "->" + field);
        try {
            mock.validateTimestampAgainstFormatParam(formatParam, messageAlias, field);
            fail("Parsing date 99999999-01:01:01.000 should have resulted in an AssertionError");
        } catch (AssertionError e) {
            assertEquals("Error parsing date from field " + field + " in message " + messageAlias + ".",
                    e.getMessage());
        }
    }

    @Test
    public void testAddCukeVariable() {
        String value1 = "currentTimestamp";
        String value2 = "worng";
        String alias = "test";

        handlers.addCukeVariable(alias, value1);
        handlers.addCukeVariable(alias, value2);
    }

    @Test
    public void testAddMessage() {
        String messageName = "test";
        Cdr message = new Cdr("MsgType=D");

        handlers.addMessage(messageName, message);
    }

    @Test
    public void testStartStopClient() throws EtiqetException {
        String clientName = "testClient";
        Client client = handlers.createClient("testProtocol", clientName);
        assertTrue(client instanceof TestClient);

        TestClient testClient = (TestClient) client;
        assertFalse(testClient.isStarted());

        handlers.startClient(clientName);
        await().atMost(2, TimeUnit.SECONDS).until(testClient::isStarted);

        handlers.stopClient(clientName);
        await().atMost(2, TimeUnit.SECONDS).until(() -> !testClient.isStarted());
    }

    @Test
    public void testStartClient() {
        String clientName = "testClient3";
        String extraConfig = System.getProperty("user.dir") + "/src/test/resources/properties/testConfig.properties";
        handlers.startClient("test", clientName, extraConfig);
        await().atMost(1L, TimeUnit.SECONDS).until(() -> ((TestClient) handlers.getClient(clientName)).isStarted());

        Client client = handlers.getClient(clientName);
        assertTrue(client instanceof TestClient);

        TestClient testClient = (TestClient) client;
        assertTrue(testClient.isStarted());

        handlers.stopClient(clientName);
        assertFalse(testClient.isStarted());
    }

    @Test
    public void testStartClientExtraConfig() throws EtiqetException {
        String clientName = "testClient2";
        handlers.startClient("test", clientName);
        await().atMost(1L, TimeUnit.SECONDS).until(() -> ((TestClient) handlers.getClient(clientName)).isStarted());

        Client client = handlers.getClient(clientName);
        assertTrue(client instanceof TestClient);

        TestClient testClient = (TestClient) client;
        assertTrue(testClient.isStarted());

        handlers.stopClient(clientName);
        assertFalse(testClient.isStarted());
    }

    @Test
    public void testStartClientDoesNotExist() {
        String clientName = "testThrowError";

        assertNull(handlers.getClient(clientName));

        String extraConfig = System.getProperty("user.dir") + "/src/test/resources/properties/testConfig.properties";
        handlers.startClient("test", clientName, extraConfig);

        assertNull(handlers.getClient(clientName));
    }

    @Test
    public void testAssertFoundOrExists(){

        String messageCheck = "Test";
        String noMatch = "DidnotMatch";
        String messageList = "msg1,msg2";
        try {
            handlers.assertFoundOrExists(true, false, messageCheck, noMatch, messageList);
        }catch (AssertionError e){
            assertEquals("No Exist", "Messages 'Test' do not exist ", e.getMessage());
        }
        try {
            handlers.assertFoundOrExists(true, true, messageCheck, noMatch, messageList);
        }catch (AssertionError e){
            assertEquals("neither matching", "Messages 'Test' do not exist and Params 'DidnotMatch' do not match in messages 'msg1,msg2'", e.getMessage());
        }
        try {
            handlers.assertFoundOrExists(false, false, messageCheck, noMatch, messageList);
        }catch (AssertionError e){
            assertEquals("Params shouldn't match","Params 'DidnotMatch' do not match in messages 'msg1,msg2", e.getMessage());
        }
        try {
            handlers.assertFoundOrExists(false, true, messageCheck, noMatch, messageList);
        }catch (AssertionError e){
            assertEquals("params not equals?","Params 'DidnotMatch' do not match in messages 'msg1,msg2'", e.getMessage());
        }

    }

    @Test
    public void testSendMessage() throws EtiqetException, InterruptedException {
        String clientName = "testClient";
        Client client = handlers.createClient("testProtocol", clientName);
        assertTrue(client instanceof TestClient);

        TestClient testClient = (TestClient) client;

        handlers.startClient(clientName);
        handlers.createMessage("TestMsg", "testProtocol", "testMessage", "test=value");
        await().atLeast(Duration.ONE_HUNDRED_MILLISECONDS);
        handlers.sendMessage("testMessage", clientName);

        assertFalse(testClient.getMessagesSent().isEmpty());
        assertEquals(1, testClient.getMessagesSent().size());
        Cdr message = testClient.getMessagesSent().poll();
        assertEquals("TestMsg", message.getType());
        assertEquals("value", message.getAsString("test"));
    }

    @Test
    public void testLoggedOn() throws EtiqetException, InterruptedException {
        String clientName = "testClient";
        Client client = handlers.createClient("testProtocol", clientName);
        assertTrue(client instanceof TestClient);

        TestClient testClient = (TestClient) client;
        assertFalse(testClient.isLoggedOn());

        handlers.startClient(clientName);
        handlers.isClientLoggedOff(clientName);

        handlers.createMessage("TestMsg", "testProtocol", "testMessage", "test=value");
        await().atLeast(Duration.ONE_HUNDRED_MILLISECONDS);
        handlers.sendMessage("testMessage", clientName);

        handlers.isClientLoggedOn(clientName);

        handlers.stopClient(clientName);
        handlers.isClientLoggedOff(clientName);
    }
    @Test
    public void testResponsesIncorrect() throws EtiqetException {
        String clientName = "testClient";
        Client client = handlers.createClient("testProtocol", clientName);
        assertTrue(client instanceof TestClient);

        String messageName = "testMessage";
        handlers.createMessage("TestMsg", "testProtocol", messageName, "test=value");
        await().atLeast(Duration.ONE_HUNDRED_MILLISECONDS);
        handlers.sendMessage(messageName, clientName);

        handlers.waitForResponseOfType("testResponse", clientName, "testResponse");
        handlers.checkFieldPresence("testResponse", "sent,test");

        String responseParams = "test=value2";

        //this should throw an assertion error
        try {
            handlers.checkResponseKeyPresenceAndValue("testResponse", responseParams);
            fail("Should have thrown an AssertionError");
        } catch(AssertionError e) {
            assertEquals("checkResponseKeyPresenceAndValue: testResponse Msg: 'test=value' found, expected: 'test=value2'", e.getMessage());
        }

    }

    @Test
    public void testResponses() throws EtiqetException {
        String clientName = "testClient";
        Client client = handlers.createClient("testProtocol", clientName);
        assertTrue(client instanceof TestClient);

        String messageName = "testMessage";
        handlers.createMessage("TestMsg", "testProtocol", messageName, "test=value");
        await().atLeast(Duration.ONE_HUNDRED_MILLISECONDS);
        handlers.sendMessage(messageName, clientName);

        handlers.waitForResponseOfType("testResponse", clientName, "testResponse");
        handlers.checkFieldPresence("testResponse", "sent,test");

        String responseParams = "sent=" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + ",test=value";
        handlers.checkResponseKeyPresenceAndValue("testResponse", responseParams);
    }

    @Test(expected = AssertionError.class)
    public void testResponsesIncorrectValue() throws EtiqetException {
        String clientName = "testClient";
        Client client = handlers.createClient("testProtocol", clientName);
        assertTrue(client instanceof TestClient);

        String messageName = "testMessage";
        handlers.createMessage("TestMsg", "test", messageName, "test=value");
        await().atLeast(Duration.ONE_HUNDRED_MILLISECONDS);
        handlers.sendMessage(messageName, clientName);

        handlers.waitForResponseOfType("testResponse", clientName, "testResponse");

        // this will throw an AssertionError (expected) because the values are incorrect
        String responseParams = "sent=20180101,test=value2";
        handlers.checkResponseKeyPresenceAndValue("testResponse", responseParams);
    }

    @Test(expected = AssertionError.class)
    public void testResponseFieldNotFound() throws EtiqetException {
        String clientName = "testClient";
        Client client = handlers.createClient("testProtocol", clientName);
        assertTrue(client instanceof TestClient);

        String messageName = "testMessage";
        handlers.createMessage("TestMsg", "test", messageName, "test=value");
        await().atLeast(Duration.ONE_HUNDRED_MILLISECONDS);
        handlers.sendMessage(messageName, clientName);

        handlers.waitForResponseOfType("testResponse", clientName, "testResponse");

        // this should throw an assertion error (expected) because the two fields aren't present in the response
        handlers.checkFieldPresence("testResponse", "fieldNotPresent,otherField");
    }

    @Test
    public void testWaitForResponse() throws EtiqetException {
        String clientName = "testClient";
        Client client = handlers.createClient("testProtocol", clientName);
        assertTrue(client instanceof TestClient);

        String messageName = "testMessage";
        handlers.createMessage("TestMsg", "testProtocol", messageName, "test=value");
        await().atLeast(Duration.ONE_HUNDRED_MILLISECONDS);
        handlers.sendMessage(messageName, clientName);

        handlers.waitForResponse(EtiqetHandlers.DEFAULT_MESSAGE_NAME,  clientName);
        handlers.checkFieldPresence(EtiqetHandlers.DEFAULT_MESSAGE_NAME, "test,sent");
    }

    @Test
    public void testMessageFilters() throws EtiqetException {
        String clientName = "testClient";
        Client client = handlers.createClient("testProtocol", clientName);
        handlers.filterMessage("toFilter");
        assertTrue(client instanceof TestClient);

        String messageName = "testMessage";
        handlers.createMessage("addFilter", "testProtocol", messageName, "test=value");
        await().atLeast(Duration.ONE_HUNDRED_MILLISECONDS);
        handlers.sendMessage(messageName, clientName);

        handlers.waitForResponseOfType("testResponse", clientName, "testResponse");
        handlers.checkFieldPresence("testResponse", "sent,test");

        handlers.removeFromFiltered("toFilter");
        handlers.sendMessage(messageName, clientName);

        handlers.waitForResponseOfType("testResponse", clientName, "toFilter");

        handlers.filterMessage("toFilter");
        handlers.sendMessage(messageName, clientName);

        handlers.waitForResponseOfType("testResponse", clientName, "testResponse");

        handlers.cleanFilteredMsgs();
        handlers.sendMessage(messageName, clientName);

        handlers.waitForResponseOfType("testResponse", clientName, "toFilter");
    }

    @Test(expected = AssertionError.class)
    public void testMessageFiltersDoesntReturn() throws EtiqetException {
        String clientName = "testClient";
        Client client = handlers.createClient("testProtocol", clientName);
        handlers.filterMessage("toFilter");
        assertTrue(client instanceof TestClient);

        String messageName = "testMessage";
        handlers.createMessage("addFilter", "test", messageName, "test=value");
        await().atLeast(Duration.ONE_HUNDRED_MILLISECONDS);
        handlers.sendMessage(messageName, clientName);

        // This should throw an assertion error because the toFilter message should have been filtered out
        handlers.waitForResponseOfType("testResponse", clientName, "toFilter");
    }

    @Test
    public void testCreateMessageForClient() throws EtiqetException {
        String clientName = "testClient";
        Client client = handlers.createClient("testProtocol", clientName);

        handlers.createMessageForClient("TestMsg", clientName, "testMsg", "test=value");
        await().atLeast(Duration.ONE_HUNDRED_MILLISECONDS);

        handlers.sendMessage("testMsg", clientName);

        handlers.waitForResponse("testResponse", clientName, 1000);
    }

    @Test
    public void testWaitForLogon() throws EtiqetException {
        String clientName = "testClient";
        Client client = handlers.createClient("testProtocol", clientName);

        handlers.waitForClientLogon(clientName);
        handlers.isClientLoggedOn(clientName);
    }

    @Test
    public void testGetResponseToMessageFromListByField() throws EtiqetException {
        String clientName = "testClient";
        Client client = handlers.createClient("testProtocol", clientName);
        handlers.startClient(clientName);

        handlers.createMessageForClient("TestMsg", clientName,
                                            "message1", "test=value,messageId=123");
        handlers.sendMessage("message1", clientName);
        handlers.waitForResponseOfType("response1", clientName, "testResponse");

        handlers.createMessageForClient("TestMsg", clientName, "message2",
                                            "test=otherValue,messageId=234");
        handlers.sendMessage("message2", clientName);
        handlers.waitForResponseOfType("response2", clientName, "testResponse");

        handlers.createMessageForClient("TestMsg", clientName, "message3",
                "test=thirdValue,messageId=123");
        handlers.getResponseToMessageFromListByField
                ("response3", "message3", "response2,response1", "messageId");

        String paramsToCheck = "test=value,messageId=123,sent="
                                + new SimpleDateFormat("yyyyMMdd").format(new Date());
        handlers.checkResponseKeyPresenceAndValue("response3", paramsToCheck);
    }

    @Test
    public void testResponseFunctionsEmptyParameters() {
        EtiqetHandlers handlers = new EtiqetHandlers();
        try {
            handlers.checkResponseKeyPresenceAndValue("testMsg", "");
            fail("checkResponseKeyPresenceAndValue with empty string parameters should throw an AssertionError");
        } catch (AssertionError e) {
            assertEquals("checkResponseKeyPresenceAndValue: Nothing to match", e.getMessage());
        }
        try {
            handlers.checkResponseKeyPresenceAndValue("testMsg", null);
            fail("checkResponseKeyPresenceAndValue with null parameters should throw an AssertionError");
        } catch (AssertionError e) {
            assertEquals("checkResponseKeyPresenceAndValue: Nothing to match", e.getMessage());
        }
        try {
            handlers.checkFieldPresence("testMsg", "");
            fail("checkFieldPresence with empty string parameters should throw an AssertionError");
        } catch (AssertionError e) {
            assertEquals("checkFieldPresence: Nothing to match", e.getMessage());
        }
        try {
            handlers.checkFieldPresence("testMsg", null);
            fail("checkFieldPresence with null parameters should throw an AssertionError");
        } catch (AssertionError e) {
            assertEquals("checkFieldPresence: Nothing to match", e.getMessage());
        }
    }

    @Test
    public void testCheckThatListOfParamsMatchInListOfMessages() {
        EtiqetHandlers handlers = new EtiqetHandlers();

        Cdr msg1 = new Cdr("TestMsg");
        msg1.set("field1", "testValue");
        msg1.set("field2", "otherValue");

        Cdr msg2 = new Cdr("TestMsg");
        msg2.set("field1", "testValue");
        msg2.set("field2", "otherValue");
        msg2.set("field3", "anotherValue");

        handlers.addMessage("msg1", msg1);
        handlers.addMessage("msg2", msg2);
        handlers.checkThatListOfParamsMatchInListOfMessages("field1,field2", "msg1,msg2");

        handlers.removeSentMessage("msg2");

        handlers.addResponse("msg2", msg2);
        handlers.checkThatListOfParamsMatchInListOfMessages("field1,field2", "msg1,msg2");
    }

    @Test
    public void testCheckThatListOfParamsMatchInListOfMessagesParamMessageNotFound() {
        EtiqetHandlers handlers = new EtiqetHandlers();

        Cdr msg1 = new Cdr("TestMsg");
        msg1.set("field1", "testValue");
        msg1.set("field2", "otherValue");

        handlers.addMessage("msg1", msg1);
        try {
            handlers.checkThatListOfParamsMatchInListOfMessages("field1,field2", "msg1,msg2,msg3");
            fail("msg2 and msg3 should not exist, causing an AssertionError");
        } catch(AssertionError e) {
            assertEquals("Messages 'msg2,msg3' do not exist ", e.getMessage());
        }

        Cdr msg2 = new Cdr("TestMsg");
        msg2.set("field1", "otherValue");
        msg2.set("field2", "testValue");

        handlers.addResponse("msg2", msg2);
        try {
            handlers.checkThatListOfParamsMatchInListOfMessages("field1,field2", "msg1,msg2");
            fail("field1 and field2 do not match, this should cause an AssertionError");
        } catch(AssertionError e) {
            assertEquals("Params 'field1,field2' do not match in messages 'msg1,msg2'", e.getMessage());
        }

        try {
            handlers.checkThatListOfParamsMatchInListOfMessages("field1,field2", "msg1,msg2,msg3");
            fail("field1 and field2 do not match, this should cause an AssertionError");
        } catch(AssertionError e) {
            assertEquals("Messages 'msg3' do not exist and Params 'field1,field2' do not match in messages 'msg1,msg2,msg3'",
                            e.getMessage());
        }
    }

    @Test
    public void testCheckThatListOfParamsMatchInListOfMessagesEmptyParameters() {
        EtiqetHandlers handlers = new EtiqetHandlers();
        try {
            handlers.checkThatListOfParamsMatchInListOfMessages("", "TestMsg,TestMsg2");
            fail("Empty paramList in checkThatListOfParamsMatchInListOfMessages should have thrown AssertionError");
        } catch (AssertionError e) {
            assertEquals("checkThatListOfParamsMatchInListOfMessages: No params to check", e.getMessage());
        }
        try {
            handlers.checkThatListOfParamsMatchInListOfMessages("MsgType,Field1", "");
            fail("Empty messageList in checkThatListOfParamsMatchInListOfMessages should have thrown AssertionError");
        } catch (AssertionError e) {
            assertEquals("checkThatListOfParamsMatchInListOfMessages: No messages where to find matches",
                            e.getMessage());
        }
    }

    @Test
    public void testCheckParamsMatch() {
        EtiqetHandlers handlers = new EtiqetHandlers();

        Cdr msg1 = new Cdr("TestMsg");
        msg1.set("field1", "testValue");
        msg1.set("field2", "otherValue");

        Cdr msg2 = new Cdr("TestMsg");
        msg2.set("field1", "testValue");
        msg2.set("field2", "otherValue");
        msg2.set("field3", "anotherValue");

        handlers.addMessage("msg1", msg1);
        handlers.addMessage("msg2", msg2);

        handlers.checkThatMessageParamsMatch("msg1->field1=msg2->field1,msg2->field2=msg1->field2");
    }

    @Test
    public void testCheckParamsMatchWrongValues() {
        EtiqetHandlers handlers = new EtiqetHandlers();

        Cdr msg1 = new Cdr("TestMsg");
        msg1.set("field1", "testValue");
        msg1.set("field2", "otherValue");

        Cdr msg2 = new Cdr("TestMsg");
        msg2.set("field1", "testValue");
        msg2.set("field2", "otherValue");
        msg2.set("field3", "anotherValue");

        handlers.addMessage("msg1", msg1);
        handlers.addMessage("msg2", msg2);

        try {
            handlers.checkThatMessageParamsMatch("msg1->field1=field2");
            fail("No message on RHS should have caused an AssertionError");
        } catch (AssertionError e) {
            assertEquals("No matches found in 'msg1->field1=field2'", e.getMessage());
        }

        try {
            handlers.checkThatMessageParamsMatch("field1=msg2->field2");
            fail("No message on LHS should have caused an AssertionError");
        } catch (AssertionError e) {
            assertEquals("No matches found in 'field1=msg2->field2'", e.getMessage());
        }

        try {
            handlers.checkThatMessageParamsMatch("msg1->field1=msg3->field2");
            fail("msg3 doesn't exist so should have caused an AssertionError");
        } catch (AssertionError e) {
            assertEquals("No matches found in 'msg1->field1=msg3->field2'", e.getMessage());
        }

        try {
            handlers.checkThatMessageParamsMatch("msg4->field1=msg1->field2");
            fail("msg4 doesn't exist so should have caused an AssertionError");
        } catch (AssertionError e) {
            assertEquals("No matches found in 'msg4->field1=msg1->field2'", e.getMessage());
        }

        try {
            handlers.checkThatMessageParamsMatch("msg1->field1=msg2->field4");
            fail("field4 doesn't exist in msg2 so should have caused an AssertionError");
        } catch (AssertionError e) {
            assertEquals("No matches found in 'msg1->field1=msg2->field4'", e.getMessage());
        }

        try {
            handlers.checkThatMessageParamsMatch("msg1->field3=msg2->field3");
            fail("field3 doesn't exist in msg1 so should have caused an AssertionError");
        } catch (AssertionError e) {
            assertEquals("No matches found in 'msg1->field3=msg2->field3'", e.getMessage());
        }

        try {
            handlers.checkThatMessageParamsMatch("msg1->field1=msg2->field2,msg1->field2=msg2->field1");
            fail("field1 in msg1 doesn't match field2 in msg2 so should have caused an AssertionError");
        } catch (AssertionError e) {
            assertEquals("No matches found in 'msg1->field1=msg2->field2,msg1->field2=msg2->field1'",
                e.getMessage());
        }

        try {
            handlers.checkThatMessageParamsMatch("msg2->field2");
            fail("No = sign in the parameters so should have caused an AssertionError");
        } catch (AssertionError e) {
            assertEquals("No matches found in 'msg2->field2'", e.getMessage());
        }
    }

    @Test
    public void testCheckParamsMatchEmptyParams() {
        EtiqetHandlers handlers = new EtiqetHandlers();
        try {
            handlers.checkThatMessageParamsMatch("");
            fail("Empty parameters should throw an AssertionError");
        } catch (AssertionError e) {
            assertEquals("checkThatMessageParamsMatch: Nothing to check", e.getMessage());
        }
    }

    @Test
    public void testConsumeNamedResponse() {
        EtiqetHandlers handlers = new EtiqetHandlers();

        String messageName = "testResponse";
        handlers.addResponse(messageName, new Cdr("TestMsg"));
        assertNotNull(handlers.getResponse(messageName));
        handlers.consumeNamedResponse(messageName);
        assertNull(handlers.getResponse(messageName));
    }

    @Test(expected = AssertionError.class)
    public void testGetResponseToMessageFromListByFieldWithError() throws EtiqetException {
        String clientName = "testClient";
        Client client = handlers.createClient("testProtocol", clientName);
        handlers.startClient(clientName);

        handlers.createMessageForClient("TestMsg", clientName,
                "message1", "test=value,messageId=123");
        handlers.sendMessage("message1", clientName);
        handlers.waitForResponseOfType("response1", clientName, "testResponse");

        handlers.createMessageForClient("TestMsg", clientName, "message2",
                "test=otherValue,messageId=234");
        handlers.sendMessage("message2", clientName);
        handlers.waitForResponseOfType("response2", clientName, "testResponse");

        handlers.createMessageForClient("TestMsg", clientName, "message3",
                "test=thirdValue,messageId=456");

        // this should thrown an assertion error because neither response1 or response2 will have the same messageId
        handlers.getResponseToMessageFromListByField
                ("response3", "message3", "response2,response1", "messageId");
    }

    @Test
    public void testdateToNanos() {
        String validTimestamp = "20180101-01:01:01.000";
        String wrongTimestamp = "wrong";

        handlers.dateToNanos(validTimestamp);
        handlers.dateToNanos(wrongTimestamp);
    }

    @Test
    public void testWaitForNoResponse() throws EtiqetException {
        String messageName = "test";
        String clientName = "testClient";
        String messageType = "Logout";

        Client client = handlers.createClient("testProtocol", clientName);
        handlers.startClient(clientName);

        handlers.waitForNoResponse(messageName, clientName, messageType);
    }

    @Test
    public void testValidateMessageTypeExistInResponseMap() throws EtiqetException {
        String clientName = "testClient";
        Client client = handlers.createClient("testProtocol", clientName);
        handlers.startClient(clientName);

        handlers.createMessageForClient("TestMsg", clientName,
                "message1", "test=value,messageId=123");
        handlers.sendMessage("message1", clientName);
        handlers.waitForResponseOfType("response1", clientName, "testResponse");
        handlers.validateMessageTypeExistInResponseMap("response1");
    }

    @Test
    public void testValidateMessageTypeDoesNotExistInResponseMap() {
        handlers.validateMessageTypeDoesNotExistInResponseMap("test");
    }

    @Test
    public void testExpectException() {
        assertFalse("EtiqetHandlers should not already expect exceptions", handlers.expectException);
        handlers.expectException();
        assertTrue("EtiqetHandlers should now expect exceptions", handlers.expectException);
        handlers.resetExpectException();
        assertFalse("EtiqetHandlers should not expect exceptions anymore", handlers.expectException);
    }

    @Test
    public void testCheckForExceptionsEmpty() {
        try {
            handlers.checkForExceptions();
        } catch (AssertionError e) {
            assertEquals("There should be exceptions to check for, none were found", e.getMessage());
        }
    }

    @Test
    public void testCheckPrecision() {
        handlers.checkPrecision(3, "20180709-16:30:00.000");
        handlers.checkPrecision(0, "20180709-16:30:00");
        handlers.checkPrecision(0, "20180709-16:30:00.");

        try {
            handlers.checkPrecision(6, "20180709-16:30:00.000");
        } catch (AssertionError e) {
            assertEquals("Precision doesn't match - expected:<6> but was:<3>", e.getMessage());
        }
        try {
            handlers.checkPrecision(0, "20180709-16:30:00.000");
        } catch (AssertionError e) {
            assertEquals("Precision doesn't match, expected 0 precision after seconds", e.getMessage());
        }
    }

    @Test
    public void testCheckTimestampPrecision() throws EtiqetException {
        String clientName = "testClient";
        handlers.createClient("testProtocol", clientName);
        handlers.startClient(clientName);

        handlers.createMessageForClient("TestMsg", clientName,
            "message1", "test=value,messageId=123");
        handlers.sendMessage("message1", clientName);
        handlers.waitForResponseOfType("response1", clientName, "testResponse");

        handlers.checkTimeStampPrecision("sentTime", "response1", "3");
        try {
            handlers.checkTimeStampPrecision("sentTime", "response1", "6");
        } catch (AssertionError e) {
            assertEquals("Precision doesn't match - expected:<6> but was:<3>", e.getMessage());
        }
    }

    @Test
    public void testCheckTimestampPrecisionNullEmpty() throws EtiqetException {
        String clientName = "testClient";
        handlers.createClient("testProtocol", clientName);
        handlers.startClient(clientName);

        handlers.createMessageForClient("TestMsg", clientName,
            "message1", "test=value,messageId=123");
        handlers.sendMessage("message1", clientName);
        handlers.waitForResponseOfType("response1", clientName, "testResponse");

        try {
            handlers.checkTimeStampPrecision("sentTime", "response1", null);
        } catch (AssertionError e) {
            assertEquals("Level of time precision must be provided e.g 0,3,6,9,second,milli,micro or nano", e.getMessage());
        }

        try {
            handlers.checkTimeStampPrecision("sentTime", "response1", "");
        } catch (AssertionError e) {
            assertEquals("Level of time precision must be provided e.g 0,3,6,9,second,milli,micro or nano", e.getMessage());
        }
    }

    @Test
    public void testCheckTimestampPrecisionEnumValues() throws EtiqetException {
        String clientName = "testClient";
        handlers.createClient("testProtocol", clientName);
        handlers.startClient(clientName);

        handlers.createMessageForClient("TestMsg", clientName,
            "message1", "test=value,messageId=123");
        handlers.sendMessage("message1", clientName);
        handlers.waitForResponseOfType("response1", clientName, "testResponse");

        handlers.checkTimeStampPrecision("sentTime", "response1", EtiqetHandlers.MILLI_TIME_NAME);
        try {
            handlers.checkTimeStampPrecision("sentTime", "response1", EtiqetHandlers.MICRO_TIME_NAME);
        } catch (AssertionError e) {
            assertEquals("Precision doesn't match - expected:<6> but was:<3>", e.getMessage());
        }
        try {
            handlers.checkTimeStampPrecision("sentTime", "response1", EtiqetHandlers.NANO_TIME_NAME);
        } catch (AssertionError e) {
            assertEquals("Precision doesn't match - expected:<9> but was:<3>", e.getMessage());
        }
        try {
            handlers.checkTimeStampPrecision("sentTime", "response1", "mini");
        } catch (AssertionError e) {
            assertEquals("Level of time precision must be provided e.g 0,3,6,9,second,milli,micro or nano", e.getMessage());
        }
    }

    private EtiqetHandlers createMockHandlersForNeuedaExtensions(Integer responseCode, String response) {
        return new EtiqetHandlers() {
            @Override
            URL getFullExtensionsUrl(String extensionsUrl, String endpoint) throws MalformedURLException {
                String spec = String.format("%s%s", extensionsUrl, endpoint);
                URL mockURL = new URL(spec);
                return new URL(mockURL, spec, new URLStreamHandler() {
                    @Override
                    protected URLConnection openConnection(URL u) throws IOException {
                        HttpURLConnection connection;
                        if(u.toString().startsWith("https://"))
                            connection = Mockito.mock(HttpsURLConnection.class);
                        else
                            connection = Mockito.mock(HttpURLConnection.class);

                        Mockito.when(connection.getResponseCode()).thenReturn(responseCode);
                        Mockito.when(connection.getResponseMessage()).thenReturn(response);
                        Mockito.when(connection.getOutputStream()).thenReturn(new ByteArrayOutputStream());
                        return connection;
                    }
                });
            }
        };
    }

    @Test
    public void testSendNamedRestMessageWithPayloadHeaders() throws EtiqetException, IOException {
        EtiqetHandlers mockHandlers = createMockHandlersForNeuedaExtensions(200, "Success");
        String clientName = "testClient";
        mockHandlers.createClient("testProtocol", clientName);
        mockHandlers.getClient(clientName).setExtensionsUrl("http://localhost:5000");
        mockHandlers.startClient(clientName);

        mockHandlers.sendNamedRestMessageWithPayloadHeaders(
            EtiqetHandlers.HTTP_POST,
            new HashMap<>(),
            "testPayload",
            "/testEndpoint",
            clientName
        );
    }

    @Test
    public void testSendNamedRestMessageWithPayloadHeadersBadResponse() throws EtiqetException, IOException {
        String response = "Unauthorised";
        EtiqetHandlers mockHandlers = createMockHandlersForNeuedaExtensions(403, response);
        String clientName = "testClient";
        mockHandlers.createClient("testProtocol", clientName);
        mockHandlers.getClient(clientName).setExtensionsUrl("https://localhost:5000");
        mockHandlers.startClient(clientName);

        try {
            HashMap<String, String> headers = new HashMap<>();
            headers.put("Authorization", "Bearer sdfasdfgs-sd245sdf-45aaasd");
            mockHandlers.sendNamedRestMessageWithPayloadHeaders(
                EtiqetHandlers.HTTP_POST,
                headers,
                "testPayload",
                "/testEndpoint",
                clientName
            );
            fail("sendNamedRestMessageWithPayloadHeaders should have thrown an exception due to the 403 error code");
        } catch (Exception e) {
            assertTrue(e instanceof EtiqetException);
            assertEquals("Did not receive 200 (OK) response. Response from server: " + response, e.getMessage());
        }
    }

    @Test
    public void testSendNamedResponseMessageWithPayloadHeadersNoConnection() throws EtiqetException {
        EtiqetHandlers mockHandlers = new EtiqetHandlers() {
            @Override
            URL getFullExtensionsUrl(String extensionsUrl, String endpoint) throws MalformedURLException {
                String spec = String.format("%s%s", extensionsUrl, endpoint);
                URL mockURL = new URL(spec);
                return new URL(mockURL, spec, new URLStreamHandler() {
                    @Override
                    protected URLConnection openConnection(URL u) throws IOException {
                        return null;
                    }
                });
            }
        };

        String clientName = "testClient";
        mockHandlers.createClient("testProtocol", clientName);
        mockHandlers.getClient(clientName).setExtensionsUrl("https://localhost:5000");
        mockHandlers.startClient(clientName);

        try {
            mockHandlers.sendNamedRestMessageWithPayloadHeaders(
                EtiqetHandlers.HTTP_POST,
                new HashMap<>(),
                "testPayload",
                "/testEndpoint",
                clientName
            );
            fail("sendNamedRestMessageWithPayloadHeaders should have failed because of a null HTTP connection");
        } catch (AssertionError e) {
            assertEquals("Couldn't open an HTTP connection", e.getMessage());
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e.getMessage());
        }
    }

    @Test
    public void testSendNamedRestMessageWithPayloadHeadersExceptions() throws EtiqetException {
        String clientName = "testClient";
        handlers.createClient("testProtocol", clientName);
        handlers.startClient(clientName);

        try {
            handlers.sendNamedRestMessageWithPayloadHeaders(
                EtiqetHandlers.HTTP_POST,
                new HashMap<>(),
                "testPayload",
                null,
                clientName
            );
        } catch (Exception e) {
            assertTrue(e instanceof EtiqetException);
            assertEquals("Cannot send REST request without an endpoint", e.getMessage());
        }

        try {
            handlers.sendNamedRestMessageWithPayloadHeaders(
                EtiqetHandlers.HTTP_POST,
                new HashMap<>(),
                "testPayload",
                "/testEndpoint",
                clientName
            );
        } catch (AssertionError e) {
            assertEquals("No Extensions URL found in client " + clientName, e.getMessage());
        } catch (Exception e) {
            fail("Unexpected Exception thrown: " + e.getMessage());
        }
    }

    @Test
    public void testExtensionsEnabled() throws EtiqetException {
        String clientName = "testClient";
        handlers.createClient("testProtocol", clientName);
        handlers.startClient(clientName);

        try {
            handlers.checkExtensionsEnabled(clientName);
        } catch (Exception e) {
            assertTrue(e instanceof EtiqetException);
            assertEquals("Extensions are not enabled - please enable to use this function", e.getMessage());
        }

        handlers.getClient(clientName).setExtensionsUrl("https://localhost:5000");
        handlers.checkExtensionsEnabled(clientName);
    }

}
