package com.neueda.etiqet.fix.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.neueda.etiqet.core.common.Environment;
import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import org.junit.Test;
import org.mockito.Mockito;
import quickfix.ConfigError;
import quickfix.SocketInitiator;

public class FixClientTest {

    private final String PRIMARY_CONFIG = "${etiqet.directory}/etiqet-fix/src/test/resources/config/client.cfg";
    private final String SECONDARY_CONFIG = "${etiqet.directory}/etiqet-fix/src/test/resources/config/secondary_client.cfg";
    private FixClient client;
    private boolean socketInitiatorStarted = false;

    private String activeConfig = "";

    private void createClient(boolean withSecondary) throws EtiqetException, ConfigError {
        SocketInitiator mockSocketInitiator = Mockito.mock(SocketInitiator.class);
        Mockito.doAnswer(invocationOnMock -> {
            socketInitiatorStarted = true;
            return null;
        })
            .when(mockSocketInitiator).start();
        Mockito.doAnswer(invocationOnMock -> {
            socketInitiatorStarted = false;
            return null;
        })
            .when(mockSocketInitiator).stop();

        String secondary = null;
        if (withSecondary) {
            secondary = SECONDARY_CONFIG;
        }

        client = new FixClient(PRIMARY_CONFIG, secondary);
    }

    @Test
    public void testLaunchClient() throws EtiqetException, ConfigError {
        createClient(true);
        client.launchClient();
        assertTrue("FixClient should have been started", socketInitiatorStarted);
        assertEquals("Should have started FixClient with the primary config",
            Environment.resolveEnvVars(PRIMARY_CONFIG), this.activeConfig);
        client.stop();
        assertFalse("FixClient should have been stopped", socketInitiatorStarted);
    }

    @Test
    public void testFailover() throws EtiqetException, ConfigError {
        createClient(true);
        client.launchClient();
        assertTrue("FixClient should have been started", socketInitiatorStarted);
        assertEquals("Should be started FixClient with the primary config",
            Environment.resolveEnvVars(PRIMARY_CONFIG), this.activeConfig);

        client.failover();
        assertTrue("FixClient should still be started", socketInitiatorStarted);
        assertEquals("Should be started with secondary config",
            Environment.resolveEnvVars(SECONDARY_CONFIG), this.activeConfig);

        client.failover();
        assertTrue("FixClient should still be started", socketInitiatorStarted);
        assertEquals("Should be started FixClient with the primary config",
            Environment.resolveEnvVars(PRIMARY_CONFIG), this.activeConfig);
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
