package com.neueda.etiqet.core.config.annotations;

import com.neueda.etiqet.core.client.Client;
import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import com.neueda.etiqet.core.config.GlobalConfig;
import com.neueda.etiqet.core.config.annotations.impl.ExampleConfiguration;
import com.neueda.etiqet.core.message.config.ProtocolConfig;
import com.neueda.etiqet.core.server.Server;
import com.neueda.etiqet.core.testing.message.TestDictionary;
import com.neueda.etiqet.core.testing.server.TestServer;
import org.junit.Test;

import static org.junit.Assert.*;

public class ConfigurationTest {

    @Test
    public void testExampleConfiguration() throws EtiqetException {
        GlobalConfig config = GlobalConfig.getInstance(ExampleConfiguration.class);
        assertNotNull(config);

        assertEquals(2, config.getClients().size());
        Client testClient1 = config.getClient("testClient1");
        assertNotNull(testClient1);
        assertEquals("testProtocol", testClient1.getProtocolName());

        ProtocolConfig testProtocol = config.getProtocol("testProtocol");
        assertNotNull(testProtocol);
        assertEquals(testProtocol, testClient1.getProtocolConfig());
        assertEquals("${etiqet.directory}/etiqet-core/src/test/resources/properties/test.properties",
            testProtocol.getClient().getDefaultConfig());

        assertTrue(testProtocol.getDictionary() instanceof TestDictionary);

        assertEquals(1, config.getServers().size());
        Server testServer = config.getServer("testServer");
        assertNotNull(testServer);
        assertTrue(testServer instanceof TestServer);
        TestServer server = (TestServer) testServer;
        assertNotNull(server.getConfigPath());
        assertEquals("${user.dir}/src/test/resources/fix-config/testServer.cfg", server.getConfigPath());
    }

}
