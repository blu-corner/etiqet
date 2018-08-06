package com.neueda.etiqet.core.common;

import com.neueda.etiqet.core.common.exceptions.EnvironmentVariableNotFoundException;
import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import static com.neueda.etiqet.core.common.Environment.*;
import static org.junit.Assert.*;

public class EnvironmentTest {

    @Test
    public void testIsEnvVarSet() {
        assertTrue(isEnvVarSet("etiqet.directory"));
        assertFalse(isEnvVarSet("MADE_UP_ENV_VAR"));
        assertTrue(isEnvVarSet("PATH"));
    }

    @Test
    public void testResolveEnvVars() throws EtiqetException {
        String testString = "$etiqet.directory/extended/path/to/file.ext";
        String result = resolveEnvVars(testString);
        assertFalse(result.contains("${etiqet.directory}"));

        String etiqetDirectory = System.getProperty("etiqet.directory");
        String suffix = "/extended/path/to/file.ext".replaceAll("/", Matcher.quoteReplacement(File.separator));
        assertEquals(etiqetDirectory + suffix, result);

        testString = "${etiqet.directory}";
        result = resolveEnvVars(testString);
        assertFalse(result.contains("${etiqet.directory}"));
        assertEquals(etiqetDirectory, result);
    }

    @Test
    public void testResolveEnvVarsException() {
        String testString = "${MADE_UP_ENV_VAR}/extended/path/to/file.ext";
        try {
            resolveEnvVars(testString);
        } catch (Exception e) {
            assertTrue(e instanceof EnvironmentVariableNotFoundException);
            assertEquals("Environment variable not found: MADE_UP_ENV_VAR", e.getMessage());
        }
    }

   @Test
   public void testFileResolveEnvVars() throws EtiqetException {
       String testFilePath = "${etiqet.directory}/etiqet-core/src/test/resources/properties/test.properties";
       InputStream inputStream = fileResolveEnvVars(testFilePath);
       assertNotNull(inputStream);

       BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
       final Map<String, String> properties =
               reader.lines()
                       .collect(Collectors.toMap(s -> s.split("=")[0], s -> s.split("=")[1]));
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

       String expected = "etiqet-core/src/test/resources/properties/testConfig.properties"
               .replaceAll("/", Matcher.quoteReplacement(File.separator));
       assertEquals(expected, properties.get("config.client.file.path"));
   }

   @Test
   public void testFileResolveEnvVarsBadPath() throws EtiqetException {
       String testFilePath = "${etiqet.directory}/path/does/not/exist/test.properties";
       InputStream inputStream = fileResolveEnvVars(testFilePath);
       assertNull(inputStream);

       inputStream = fileResolveEnvVars("");
       assertNull(inputStream);

       inputStream = fileResolveEnvVars(null);
       assertNull(inputStream);
   }

}
