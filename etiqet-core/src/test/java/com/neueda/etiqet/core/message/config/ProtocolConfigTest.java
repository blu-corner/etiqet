package com.neueda.etiqet.core.message.config;

import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import com.neueda.etiqet.core.config.dtos.Delegate;
import com.neueda.etiqet.core.config.dtos.Message;
import com.neueda.etiqet.core.config.dtos.Protocol;
import org.junit.Test;

import static org.junit.Assert.*;

public class ProtocolConfigTest {

    @Test
    public void testGoodProtocol() throws EtiqetException {
        String protocolPath = "${etiqet.directory}/etiqet-core/src/test/resources/protocols/testProtocol.xml";
        ProtocolConfig protocolConfig = new ProtocolConfig(protocolPath);
        testCommonFields(protocolConfig);
        assertEquals("com.neueda.etiqet.core.testing.message.TestDictionary",
                protocolConfig.getProtocol().getDictionary().getHandler());
        assertEquals("quickfix.fix44.component.",
                protocolConfig.getComponentPackage());
        assertEquals("quickfix.fix44.component",
                protocolConfig.getProtocol().getComponentsPackage());

        Delegate[] delegates = protocolConfig.getClientDelegates().getDelegate();
        assertEquals(2, delegates.length);
        assertEquals("logger", delegates[0].getKey());
        assertEquals("com.neueda.etiqet.core.client.delegate.LoggerClientDelegate", delegates[0].getImpl());
        assertEquals("sink", delegates[1].getKey());
        assertEquals("com.neueda.etiqet.core.client.delegate.SinkClientDelegate", delegates[1].getImpl());
    }

    @Test
    public void testBadProtocol() {
        String protocolPath = "${etiqet.directory}/etiqet-core/src/test/resources/protocols/testBadProtocol.xml";
        try {
            ProtocolConfig protocolConfig = new ProtocolConfig(protocolPath);
            fail("Creation of ProtocolConfig with testBadProtocol.xml should throw an exception");
        } catch (Exception e) {
            assertTrue(e instanceof EtiqetException);
            assertEquals("Unable to load messages configuration: " + protocolPath, e.getMessage());
            assertEquals("Error loading dictionaryHandler: com.neueda.etiqet.core.TestDictionary",
                            e.getCause().getMessage());
        }
    }

    @Test
    public void testInvalidProtocol() {
        String protocolPath = "${etiqet.directory}/etiqet-core/src/test/resources/protocols/testInvalidProtocol.xml";
        try {
            ProtocolConfig protocolConfig = new ProtocolConfig(protocolPath);
            fail("Creation of ProtocolConfig with testInvalidProtocol.xml should throw an exception");
        } catch (Exception e) {
            assertTrue(e instanceof EtiqetException);
            assertEquals("Unable to load messages configuration: " + protocolPath, e.getMessage());
        }
    }

    @Test
    public void testNoDictionaryProtocol() throws EtiqetException {
        String protocolPath = "${etiqet.directory}/etiqet-core/src/test/resources/protocols/testNoDictionaryProtocol.xml";
        try {
            ProtocolConfig protocolConfig = new ProtocolConfig(protocolPath);
        } catch (Exception e) {
            assertTrue(e instanceof EtiqetException);
            assertEquals("Unable to load messages configuration: " + protocolPath, e.getMessage());
        }
    }

    @Test
    public void testNoDictionaryHandlerProtocol() throws EtiqetException {
        String protocolPath = "${etiqet.directory}/etiqet-core/src/test/" +
                                "resources/protocols/testNoDictionaryHandlerProtocol.xml";
        try {
            ProtocolConfig protocolConfig = new ProtocolConfig(protocolPath);
        } catch (Exception e) {
            assertTrue(e instanceof EtiqetException);
            assertEquals("Unable to load messages configuration: " + protocolPath, e.getMessage());
        }

    }

    @Test
    public void testComponentsPackageTrailingDot() throws EtiqetException {
        String protocolPath = "${etiqet.directory}/etiqet-core/src/test/" +
                "resources/protocols/testComponentsPackageTrailingDotProtocol.xml";
        ProtocolConfig protocolConfig = new ProtocolConfig(protocolPath);
        assertNotNull(protocolConfig);
        testCommonFields(protocolConfig);
        assertEquals("quickfix.fix44.component.",
                protocolConfig.getComponentPackage());
        assertEquals("quickfix.fix44.component.",
                protocolConfig.getProtocol().getComponentsPackage());
    }

    private void testCommonFields(ProtocolConfig protocolConfig) {
        Protocol protocol = protocolConfig.getProtocol();
        assertNotNull(protocol);
        assertEquals("test", protocolConfig.getProtocol().getName());

        assertEquals("com.neueda.etiqet.core.testing.client.TestClient",
                protocolConfig.getClient().getImpl());

        assertEquals("com.neueda.etiqet.core.testing.message.TestMessage",
                protocol.getMessageClass());

        Message message = protocolConfig.getMessages()[0];
        assertNotNull(message);
        assertEquals("TestMsg", message.getName());
        assertEquals("java.lang.String", message.getImplementation());
    }

    @Test
    public void testDictionaryFields() throws EtiqetException {
        String protocolPath = "${etiqet.directory}/etiqet-core/src/test/resources/protocols/testProtocol.xml";
        ProtocolConfig protocolConfig = new ProtocolConfig(protocolPath);
        assertNotNull(protocolConfig);

        assertEquals("TestMsg", protocolConfig.getMsgType("TestMsg"));
        assertEquals("OtherMsg", protocolConfig.getMsgType("OtherMsg"));

        assertEquals("TestMsg", protocolConfig.getMsgType("TestMsg"));
        assertEquals("OtherMsg", protocolConfig.getMsgName("OtherMsg"));
        assertEquals("2", protocolConfig.getNameForTag(2));
        assertEquals(Integer.valueOf(0), protocolConfig.getTagForName("field"));
        assertFalse(protocolConfig.tagContains(2));
        assertEquals("msgType", protocolConfig.getMsgType("msgType"));
        assertFalse(protocolConfig.isAdmin("test"));
        assertFalse(protocolConfig.isHeaderField("test"));
        assertFalse(protocolConfig.isHeaderField(2));
    }

    @Test
    public void testDictionaryFieldsNoDictionary() throws EtiqetException {
        String protocolPath = "${etiqet.directory}/etiqet-core/src/test/resources/protocols/testNoDictionaryProtocol.xml";
        try {
            ProtocolConfig protocolConfig = new ProtocolConfig(protocolPath);
        } catch (Exception e) {
            assertTrue(e instanceof EtiqetException);
            assertEquals("Unable to load messages configuration: " + protocolPath, e.getMessage());
        }
    }
}
