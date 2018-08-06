package com.neueda.etiqet.core.util;

import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

public class PropertiesFileReaderTest {

    @Test
    public void testLoadPropertiesFile() throws EtiqetException {
        String configPath = System.getProperty("user.dir") + "/src/test/resources/properties/test.properties";
        Config properties = PropertiesFileReader.loadPropertiesFile(configPath);

        assertFalse(properties.isEmpty());
        assertEquals(12, properties.getKeySet().size());
        assertEquals("config.properties", properties.getString("fix.config.file.path"));
        assertEquals("server.cfg", properties.getString("fix.server.config.file.path"));
        assertEquals("database.properties", properties.getString("shell.database.properties.path"));
        assertEquals("remoteshell.properties", properties.getString("shell.remoteshell.properties.path"));
        assertEquals("protocolConfig.properties", properties.getString("config.protocol.file.path"));
        assertTrue(properties.getBoolean("test.boolean.true"));
        assertTrue(properties.getBoolean("test.boolean.convert.true"));
        assertFalse(properties.getBoolean("test.boolean.false"));
        assertFalse(properties.getBoolean("test.boolean.convert.false"));
        assertEquals(Integer.valueOf(20), properties.getInteger("test.int"));
        assertEquals(Long.valueOf(20L), properties.getLong("test.int"));
        assertEquals(Double.valueOf(25.3), properties.getDouble("test.double"));
        assertEquals("etiqet-core/src/test/resources/properties/testConfig.properties",
                                    properties.getString("config.client.file.path"));
    }

    @Test
    public void testLoadFromClasspath() throws EtiqetException {
        String configPath = "/properties/test.properties";
        Map<String, String> properties = PropertiesFileReader.loadFromClasspath(configPath);

        assertFalse(properties.isEmpty());
        assertEquals(12, properties.size());
        assertEquals("config.properties", properties.get("fix.config.file.path"));
        assertEquals("server.cfg", properties.get("fix.server.config.file.path"));
        assertEquals("database.properties", properties.get("shell.database.properties.path"));
        assertEquals("remoteshell.properties", properties.get("shell.remoteshell.properties.path"));
        assertEquals("protocolConfig.properties", properties.get("config.protocol.file.path"));
        assertEquals("true", properties.get("test.boolean.true"));
        assertEquals("1", properties.get("test.boolean.convert.true"));
        assertEquals("false", properties.get("test.boolean.false"));
        assertEquals("0", properties.get("test.boolean.convert.false"));
        assertEquals("20", properties.get("test.int"));
        assertEquals("25.3", properties.get("test.double"));
        assertEquals("etiqet-core/src/test/resources/properties/testConfig.properties",
                                    properties.get("config.client.file.path"));
    }

    @Test
    public void testLoadPropertiesException() {
        String configPath = System.getProperty("user.dir") + "/src/test/resources/does/not/exist.properties";
        try {
            Config properties = PropertiesFileReader.loadPropertiesFile(configPath);
            fail("Exception should be thrown because the test file shouldn't exist");
        } catch (Exception e) {
            assertEquals(EtiqetException.class, e.getClass());
            assertTrue(e.getMessage().startsWith("java.io.FileNotFoundException"));
        }
    }

    @Test
    public void testLoadFromClasspathException() {
        String configPath = "/properties/does/not/exist.properties";
        try {
            Map<String, String> properties = PropertiesFileReader.loadFromClasspath(configPath);
            fail("Exception should be thrown because the test file shouldn't exist");
        } catch (Exception e) {
            assertEquals(EtiqetException.class, e.getClass());
            assertEquals("Could not read classpath file " + configPath, e.getMessage());
        }
    }

}