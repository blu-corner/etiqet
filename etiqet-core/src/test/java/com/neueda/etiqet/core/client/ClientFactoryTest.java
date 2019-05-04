package com.neueda.etiqet.core.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import com.neueda.etiqet.core.client.delegate.LoggerClientDelegate;
import com.neueda.etiqet.core.common.Environment;
import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import com.neueda.etiqet.core.config.GlobalConfig;
import com.neueda.etiqet.core.message.config.ProtocolConfig;
import com.neueda.etiqet.core.testing.client.TestClient;
import java.lang.reflect.Field;
import java.net.URL;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ClientFactoryTest {

    private GlobalConfig globalConfig;

    @Before
    public void setUp() throws Exception {
        URL resource = this.getClass().getClassLoader().getResource("config/etiqet.config.xml");
        assertNotNull("Cannot find test Etiqet configuration file in classpath:config/etiqet.config.xml", resource);
        globalConfig = GlobalConfig.getInstance(resource.getPath());
    }

    @After
    public void tearDown() throws Exception {
        Field field = GlobalConfig.class.getDeclaredField("instance");
        field.setAccessible(true);
        field.set(globalConfig, null);
    }

    @Test
    public void testCreate() throws EtiqetException {
        String clientType = "testProtocol";
        Client client = ClientFactory.create(clientType);
        assertNotNull(client);
        assertTrue(client instanceof TestClient);
        assertFalse(client.getConfig().isEmpty());
        assertFalse(client.canFailover());
        assertTrue(client.getDelegate() instanceof LoggerClientDelegate);
    }

    @Test
    public void testCreateException() {
        String clientType = "badProtocol";
        try {
            Client client = ClientFactory.create(clientType);
            fail("'badProtocol' should not exist for this test.");
        } catch (Exception e) {
            assertTrue(e instanceof EtiqetException);
            assertEquals(String.format(ClientFactory.PROTOCOL_ERROR, clientType), e.getMessage());
        }

    }

    @Test
    public void testCreateWithConfig() throws EtiqetException {
        String clientType = "testProtocol";
        String clientConfig = "${etiqet.directory}/etiqet-core/src/test/resources/properties/testConfig.properties";
        Client client = ClientFactory.create(clientType, clientConfig);
        assertNotNull(client);
        assertTrue(client instanceof TestClient);
        assertFalse(client.getConfig().isEmpty());
        assertEquals(1, client.getConfig().getKeySet().size());
        assertEquals("testValue", client.getConfig().getString("testProperty"));
        assertFalse(client.canFailover());
    }

    @Test
    public void testCreateConfigException() {
        String clientType = "badProtocol";
        String clientConfig = "${etiqet.directory}/etiqet-core/src/test/resources/properties/testConfig.properties";
        try {
            Client client = ClientFactory.create(clientType, clientConfig);
            fail("'badProtocol' should not exist for this test.");
        } catch (Exception e) {
            assertTrue(e instanceof EtiqetException);
            assertEquals(String.format(ClientFactory.CLIENT_CREATION_ERROR, clientType), e.getMessage());
        }

    }

    @Test
    public void testCreateWithExtraConfig() throws EtiqetException {
        String clientType = "testProtocol";
        String primaryConfig = "${etiqet.directory}/etiqet-core/src/test/resources/properties/test.properties";
        String secondaryConfig = "${etiqet.directory}/etiqet-core/src/test/resources/properties/testConfig.properties";
        Client client = ClientFactory.create(clientType, primaryConfig, secondaryConfig);
        assertNotNull(client);
        assertTrue(client instanceof TestClient);
        assertFalse(client.getConfig().isEmpty());
        assertEquals(Environment.resolveEnvVars(primaryConfig), client.primaryConfig);
        assertEquals(Environment.resolveEnvVars(secondaryConfig), client.secondaryConfig);
        assertTrue(client.canFailover());
    }

    @Test
    public void testCreateWithExtraConfigException() {
        String clientType = "badProtocol";
        String primaryConfig = "${etiqet.directory}/etiqet-core/src/test/resources/properties/test.properties";
        String secondaryConfig = "${etiqet.directory}/etiqet-core/src/test/resources/properties/testConfig.properties";
        try {
            Client client = ClientFactory.create(clientType, primaryConfig, secondaryConfig);
            fail("'badProtocol' should not exist for this test.");
        } catch (Exception e) {
            assertTrue(e instanceof EtiqetException);
            assertEquals(String.format(ClientFactory.CLIENT_CREATION_ERROR, clientType), e.getMessage());
        }
    }

    @Test
    public void testCreateWithSecondaryConfigAndProtocolConfig() throws EtiqetException {
        String clientType = "testProtocol";
        ProtocolConfig protocolConfig = GlobalConfig.getInstance().getProtocol(clientType);
        String clientClass = protocolConfig.getClient().getImpl();
        String primaryConfig = "${etiqet.directory}/etiqet-core/src/test/resources/properties/testConfig.properties";
        String secondaryConfig = "${etiqet.directory}/etiqet-core/src/test/resources/properties/testConfig2.properties";

        Client client = ClientFactory.create(clientClass, primaryConfig, secondaryConfig, protocolConfig);

        assertNotNull(client);
        assertTrue(client instanceof TestClient);
        assertFalse(client.getConfig().isEmpty());
        assertEquals(Environment.resolveEnvVars(primaryConfig), client.primaryConfig);
        assertEquals(Environment.resolveEnvVars(secondaryConfig), client.secondaryConfig);
        assertTrue(client.canFailover());
    }

    @Test
    public void testCreateSecondaryConfigAndProtocolConfigException() {
        String clientType = "badProtocol";
        String primaryConfig = "${etiqet.directory}/etiqet-core/src/test/resources/properties/testConfig.properties";
        String secondaryConfig = "${etiqet.directory}/etiqet-core/src/test/resources/properties/testConfig2.properties";

        try {
            ClientFactory.create(clientType, primaryConfig, secondaryConfig, mock(ProtocolConfig.class));
            fail("'badProtocol' should not exist for this test.");
        } catch (Exception e) {
            assertTrue(e instanceof EtiqetException);
            assertEquals(String.format(ClientFactory.CLIENT_CREATION_ERROR, clientType), e.getMessage());
        }
    }
}
