package com.neueda.etiqet.core.config;

import com.neueda.etiqet.core.client.Client;
import com.neueda.etiqet.core.common.ConfigConstants;
import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import com.neueda.etiqet.core.config.dtos.EtiqetConfiguration;
import com.neueda.etiqet.core.server.Server;
import com.neueda.etiqet.core.testing.client.TestClient;
import com.neueda.etiqet.core.testing.server.TestServer;
import org.junit.Test;

import static com.neueda.etiqet.core.common.ConfigConstants.DEFAULT_CONFIG_VARIABLE;
import static org.junit.Assert.*;

public class GlobalConfigTest {

    @Test
    public void testCreateGoodConfig() throws EtiqetException {
        String configPath = "${etiqet.directory}/etiqet-core/src/test/resources/config/etiqet.config.xml";
        System.setProperty(ConfigConstants.DEFAULT_CONFIG_VARIABLE, configPath);
        GlobalConfig.instance = null; // reset this
        GlobalConfig config = GlobalConfig.getInstance();

        assertNotNull(config);
        EtiqetConfiguration etiqetConfig = config.getConfig();
        assertNotNull(etiqetConfig);

        assertEquals(2, etiqetConfig.getProtocols().size());
        assertEquals(2, etiqetConfig.getClients().size());
        assertEquals(1, etiqetConfig.getServers().size());

        assertNotNull(config.getProtocol("testProtocol"));
        assertNotNull(config.getProtocol("otherTestProtocol"));

        Client testClient1 = config.getClient("testClient1");
        assertNotNull(testClient1);
        assertTrue(testClient1 instanceof TestClient);

        Client testClient2 = config.getClient("testClient2");
        assertNotNull(testClient2);
        assertTrue(testClient2 instanceof TestClient);

        Server testServer = config.getServer("testServer");
        assertNotNull(testServer);
        assertTrue(testServer instanceof TestServer);
    }

    @Test
    public void testGlobalConfigDefaultLocationNotSet() {
        System.clearProperty(DEFAULT_CONFIG_VARIABLE);
        GlobalConfig.instance = null; // reset this
        try {
            GlobalConfig config = GlobalConfig.getInstance();
            fail(DEFAULT_CONFIG_VARIABLE + " is not set so should throw an exception");
        } catch (Exception e) {
            assertTrue(e instanceof EtiqetException);
            assertEquals("Could not find system property " + DEFAULT_CONFIG_VARIABLE + " to create global"
                + " configuration with", e.getMessage());
        }
    }

}