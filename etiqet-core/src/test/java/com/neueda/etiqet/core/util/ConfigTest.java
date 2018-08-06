package com.neueda.etiqet.core.util;

import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import org.junit.Test;

import static org.junit.Assert.*;

public class ConfigTest {

    @Test
    public void testConfig() throws EtiqetException {
        Config config = new Config();
        assertTrue(config.isEmpty());

        String configPath = System.getProperty("user.dir") + "/src/test/resources/properties/test.properties";
        config = PropertiesFileReader.loadPropertiesFile(configPath);
        assertFalse(config.isEmpty());

        // test properties that are set in the file
        assertEquals(12, config.getKeySet().size());
        assertEquals("config.properties", config.getString("fix.config.file.path"));
        assertEquals("server.cfg", config.getString("fix.server.config.file.path"));
        assertEquals("database.properties", config.getString("shell.database.properties.path"));
        assertEquals("remoteshell.properties", config.getString("shell.remoteshell.properties.path"));
        assertEquals("protocolConfig.properties", config.getString("config.protocol.file.path"));
        assertTrue(config.getBoolean("test.boolean.true"));
        assertTrue(config.getBoolean("test.boolean.convert.true"));
        assertFalse(config.getBoolean("test.boolean.false"));
        assertFalse(config.getBoolean("test.boolean.convert.false"));
        assertEquals(Integer.valueOf(20), config.getInteger("test.int"));
        assertEquals(Long.valueOf(20L), config.getLong("test.int"));
        assertEquals(Double.valueOf(25.3), config.getDouble("test.double"));
        assertEquals("etiqet-core/src/test/resources/properties/testConfig.properties",
                                config.getString("config.client.file.path"));

        // test default values are set for properties that don't exist
        assertEquals(Config.DEFAULT_STRING, config.getString("property.does.not.exist"));
        assertEquals(Config.DEFAULT_DOUBLE, config.getDouble("property.does.not.exist"));
        assertEquals(Config.DEFAULT_INTEGER, config.getInteger("property.does.not.exist"));
        assertEquals(Config.DEFAULT_LONG, config.getLong("property.does.not.exist"));

        // test default numeric values are set for properties that aren't numeric
        assertEquals(Config.DEFAULT_DOUBLE, config.getDouble("fix.config.file.path"));
        assertEquals(Config.DEFAULT_INTEGER, config.getInteger("fix.config.file.path"));
        assertEquals(Config.DEFAULT_LONG, config.getLong("fix.config.file.path"));
    }

}
