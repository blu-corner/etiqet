package com.neueda.etiqet.fix.client;

import com.neueda.etiqet.core.common.Environment;
import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import com.neueda.etiqet.core.util.PropertiesFileReader;
import org.junit.Test;
import quickfix.ConfigError;

import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.*;

public class FixClientTest {

    private final String PRIMARY_CONFIG = "${etiqet.directory}/etiqet-fix/src/test/resources/config/client.cfg";
    private final String SECONDARY_CONFIG = "${etiqet.directory}/etiqet-fix/src/test/resources/config/secondary_client.cfg";

    private FixClient client;

    @Test
    public void testLaunchClient() throws EtiqetException {
        client = new FixClient(PRIMARY_CONFIG, SECONDARY_CONFIG);
        assertNull("FixClient should have been started", client.getTransport());
        client.launchClient();
        assertNotNull("FixClient should have been started", client.getTransport());
        client.stop();
    }

    @Test
    public void testFailover() throws EtiqetException {
        client = new FixClient(PRIMARY_CONFIG, SECONDARY_CONFIG);
        assertEquals("Should have started FixClient with the primary config",
                     PropertiesFileReader.loadPropertiesFile(Environment.resolveEnvVars(PRIMARY_CONFIG)),
                     this.client.getConfig());

        client.failover();
        assertEquals("Should be started with secondary config",
                     PropertiesFileReader.loadPropertiesFile(Environment.resolveEnvVars(SECONDARY_CONFIG)),
                     this.client.getConfig());

        client.failover();
        assertEquals("Should have started FixClient with the primary config",
                     PropertiesFileReader.loadPropertiesFile(Environment.resolveEnvVars(PRIMARY_CONFIG)),
                     this.client.getConfig());
    }

    @Test
    public void testFailoverFails() throws EtiqetException, ConfigError {
        client = new FixClient(PRIMARY_CONFIG);
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
