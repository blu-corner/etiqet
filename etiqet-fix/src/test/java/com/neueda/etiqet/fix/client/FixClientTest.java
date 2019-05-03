package com.neueda.etiqet.fix.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.neueda.etiqet.core.client.delegate.ClientDelegate;
import com.neueda.etiqet.core.common.Environment;
import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import com.neueda.etiqet.core.util.PropertiesFileReader;
import org.junit.Test;
import org.mockito.Mockito;
import quickfix.ConfigError;
import quickfix.SocketInitiator;

public class FixClientTest {

    private final String PRIMARY_CONFIG = "${etiqet.directory}/etiqet-fix/src/test/resources/config/client.cfg";
    private final String SECONDARY_CONFIG = "${etiqet.directory}/etiqet-fix/src/test/resources/config/secondary_client.cfg";

    private FixClient client;
    private String activeConfig = "";

    private void createClient(boolean withSecondary) throws EtiqetException, ConfigError {
        String secondary = null;
        if (withSecondary) {
            secondary = SECONDARY_CONFIG;
        }

        client = new FixClient(PRIMARY_CONFIG, secondary);
    }

    @Test
    public void testLaunchClient() throws EtiqetException, ConfigError, InterruptedException {
        createClient(true);
        client.launchClient();
        Thread.sleep(5000);
        assertTrue("FixClient should have been started", client.waitForLogon());
        assertEquals("Should have started FixClient with the primary config",
            PropertiesFileReader.loadPropertiesFile(Environment.resolveEnvVars(PRIMARY_CONFIG)), this.client.getConfig());
        client.stop();
        assertFalse("FixClient should have been stopped", client.isLoggedOn());
    }

    @Test
    public void testFailover() throws EtiqetException, ConfigError {
        createClient(true);
        client.launchClient();
        assertTrue("FixClient should have been started", client.waitForLogon());
        assertEquals("Should have started FixClient with the primary config",
            PropertiesFileReader.loadPropertiesFile(Environment.resolveEnvVars(PRIMARY_CONFIG)), this.client.getConfig());

        client.failover();
        assertTrue("FixClient should have been started", client.waitForLogon());
        assertEquals("Should be started with secondary config",
            PropertiesFileReader.loadPropertiesFile(Environment.resolveEnvVars(SECONDARY_CONFIG)), this.activeConfig);

        client.failover();
        assertTrue("FixClient should have been started", client.waitForLogon());
        assertEquals("Should have started FixClient with the primary config",
            PropertiesFileReader.loadPropertiesFile(Environment.resolveEnvVars(PRIMARY_CONFIG)), this.client.getConfig());
    }

    @Test
    public void testFailoverFails() throws EtiqetException, ConfigError {
        createClient(false);
        client.launchClient();
        try {
            client.failover();
            fail("No secondary config should have been specified and should throw an EtiqetException");
        } catch (Exception e) {
            assertTrue(e instanceof EtiqetException);
            assertEquals("No secondary config to failover", e.getMessage());
        }
    }

}
