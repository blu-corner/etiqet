package com.neueda.etiqet.core.config.annotations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.neueda.etiqet.core.client.Client;
import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import com.neueda.etiqet.core.config.GlobalConfig;
import com.neueda.etiqet.core.config.annotations.impl.EmptyConfiguration;
import com.neueda.etiqet.core.config.annotations.impl.ExampleConfiguration;
import com.neueda.etiqet.core.config.annotations.impl.IncompleteConfiguration;
import com.neueda.etiqet.core.message.config.ProtocolConfig;
import com.neueda.etiqet.core.server.Server;
import com.neueda.etiqet.core.testing.message.TestDictionary;
import com.neueda.etiqet.core.testing.server.TestServer;
import java.lang.reflect.Field;
import org.junit.Test;

public class ConfigurationTest {

    @Test
    public void testIncompleteConfiguration() {
        try {
            GlobalConfig.getInstance(IncompleteConfiguration.class);
        } catch (Exception e) {
            assertTrue(e instanceof EtiqetException);
            assertEquals("Protocol testProtocol does not have a client specified", e.getMessage());
        }
    }

    @Test
    public void testEmptyConfiguration() {
        try {
            GlobalConfig.getInstance(EmptyConfiguration.class);
            fail("Empty Configuration should not contain anything to allow successful configuration");
        } catch (Exception e) {
            assertTrue(e instanceof EtiqetException);
            assertEquals("Could not find any protocols defined in " + EmptyConfiguration.class.getName(),
                e.getMessage());
        }
    }

    @Test
    public void testExampleConfiguration() throws Exception {
        GlobalConfig config = GlobalConfig.getInstance(ExampleConfiguration.class);
        assertNotNull(config);

        assertEquals(2, config.getClients().size());
        Client testClient1 = config.getClient("testClient1");
        assertNotNull(testClient1);
        assertEquals("testProtocol", testClient1.getProtocolName());

        ProtocolConfig testProtocol = config.getProtocol("testProtocol");
        assertNotNull(testProtocol);
        assertEquals(testProtocol, testClient1.getProtocolConfig());
        assertEquals("${user.dir}/src/test/resources/properties/test.properties",
            testProtocol.getClient().getDefaultConfig());

        assertTrue(testProtocol.getDictionary() instanceof TestDictionary);

        assertEquals(1, config.getServers().size());
        Server testServer = config.getServer("testServer");
        assertNotNull(testServer);
        assertTrue(testServer instanceof TestServer);
        TestServer server = (TestServer) testServer;
        assertNotNull(server.getConfigPath());
        assertEquals("${user.dir}/src/test/resources/properties/testConfig.properties", server.getConfigPath());

        // because GlobalConfig is a singleton, we need to ensure that the instance is reset after each of these
        // tests. Without exposing the instance, this involves using reflection to set the instance to null
        Field field = GlobalConfig.class.getDeclaredField("instance");
        field.setAccessible(true);
        field.set(config, null);
    }

}
