package com.neueda.etiqet.core.server;

import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import com.neueda.etiqet.core.testing.server.TestServer;
import org.junit.Test;

import static org.junit.Assert.*;

public class ServerFactoryTest {

    @Test
    public void testBasicCreate() throws EtiqetException {
        String serverClassName = "com.neueda.etiqet.core.testing.server.TestServer";
        Server server = ServerFactory.create(serverClassName);
        assertTrue(server instanceof TestServer);
        assertEquals(TestServer.DEFAULT_CONFIG, server.serverConfig);
    }

    @Test
    public void testBasicCreateException() {
        String serverClassName = "com.neueda.etiqet.core.testing.FakeTestServer";
        try {
            Server server = ServerFactory.create(serverClassName);
            fail("This should have thrown an exception because FakeTestServer shouldn't exist");
        } catch (Exception e) {
            assertTrue(e instanceof EtiqetException);
            assertEquals("Error creating server of type: " + serverClassName, e.getMessage());
        }
    }

    @Test
    public void testCreateWithConfig() throws EtiqetException {
        String serverClassName = "com.neueda.etiqet.core.testing.server.TestServer";
        String configFile = "etiqet-core/src/test/resources/properties/testDefaultServer.properties";
        Server server = ServerFactory.create(serverClassName, configFile);
        assertTrue(server instanceof TestServer);
        assertEquals(configFile, server.serverConfig);
    }

    @Test
    public void testCreateWithConfigException() {
        String serverClassName = "com.neueda.etiqet.core.testing.FakeTestServer";
        String configFile = "etiqet-core/src/test/resources/properties/testDefaultServer.properties";
        try {
            Server server = ServerFactory.create(serverClassName, configFile);
            fail("This should have thrown an exception because FakeTestServer shouldn't exist");
        } catch (Exception e) {
            assertTrue(e instanceof EtiqetException);
            assertEquals("Error creating server of type: " + serverClassName, e.getMessage());
        }
    }

    @Test
    public void testCreateWithExtraConfig() throws EtiqetException {
        String serverClassName = "com.neueda.etiqet.core.testing.server.TestServer";
        String configFile = "etiqet-core/src/test/resources/properties/testDefaultServer.properties";
        String extraConfig = "src/test/resources/properties/test.properties";

        Server server = ServerFactory.create(serverClassName, configFile, extraConfig);
        assertTrue(server instanceof TestServer);

        assertEquals(configFile, server.serverConfig);

        assertEquals(12, server.getConfig().getKeySet().size());
        assertEquals("config.properties", server.getConfig().getString("fix.config.file.path"));
        assertEquals("server.cfg", server.getConfig().getString("fix.server.config.file.path"));
        assertEquals("database.properties", server.getConfig().getString("shell.database.properties.path"));
        assertEquals("remoteshell.properties", server.getConfig().getString("shell.remoteshell.properties.path"));
        assertEquals("protocolConfig.properties", server.getConfig().getString("config.protocol.file.path"));
        assertTrue(server.getConfig().getBoolean("test.boolean.true"));
        assertTrue(server.getConfig().getBoolean("test.boolean.convert.true"));
        assertFalse(server.getConfig().getBoolean("test.boolean.false"));
        assertFalse(server.getConfig().getBoolean("test.boolean.convert.false"));
        assertEquals(Integer.valueOf(20), server.getConfig().getInteger("test.int"));
        assertEquals(Long.valueOf(20), server.getConfig().getLong("test.int"));
        assertEquals(Double.valueOf(25.3), server.getConfig().getDouble("test.double"));
        assertEquals("etiqet-core/src/test/resources/properties/testConfig.properties",
                        server.getConfig().getString("config.client.file.path"));
    }

    @Test
    public void testCreateWithExtraConfigFileNotFound() throws EtiqetException {
        String serverClassName = "com.neueda.etiqet.core.testing.server.TestServer";
        String configFile = "etiqet-core/src/test/resources/properties/testDefaultServer.properties";
        String extraConfig = "these/properties/do/not/exist.properties";

        Server server = ServerFactory.create(serverClassName, configFile, extraConfig);
        assertTrue(server instanceof TestServer);

        assertEquals(configFile, server.serverConfig);
        assertTrue(server.getConfig().isEmpty());
    }

    @Test
    public void testCreateWithExtraConfigException() {
        String serverClassName = "com.neueda.etiqet.core.testing.FakeTestServer";
        String configFile = "etiqet-core/src/test/resources/properties/testDefaultServer.properties";
        String extraConfig = "src/test/resources/properties/test.properties";
        try {
            Server server = ServerFactory.create(serverClassName, configFile, extraConfig);
            fail("This should have thrown an exception because FakeTestServer shouldn't exist");
        } catch (Exception e) {
            assertTrue(e instanceof EtiqetException);
            assertEquals("Error creating server of type: " + serverClassName, e.getMessage());
        }
    }

}