package com.neueda.etiqet.core.config.xml;

import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import com.neueda.etiqet.core.config.dtos.EtiqetConfiguration;
import org.junit.Test;

import static org.junit.Assert.*;

public class XmlParserTest {

    @Test
    public void testParse() throws Exception {
        String configPath = "${etiqet.directory}/etiqet-core/src/test/resources/config/etiqet.config.xml";
        XmlParser parser = new XmlParser();
        Object genericResult = parser.parse(configPath, EtiqetConfiguration.class);
        assertNotNull(genericResult);
        assertTrue(genericResult instanceof EtiqetConfiguration);

        EtiqetConfiguration etiqetConfig = (EtiqetConfiguration) genericResult;

        assertEquals(2, etiqetConfig.getProtocols().size());
        assertTrue(etiqetConfig.getProtocols().stream().anyMatch(p -> p.getName().equals("testProtocol")));
        assertTrue(etiqetConfig.getProtocols().stream().anyMatch(p -> p.getName().equals("otherTestProtocol")));

        assertEquals(2, etiqetConfig.getClients().size());
        assertTrue(etiqetConfig.getClients().stream().anyMatch(c -> c.getName().equals("testClient1")));
        assertTrue(etiqetConfig.getClients().stream().anyMatch(c -> c.getName().equals("testClient2")));

        assertEquals(1, etiqetConfig.getServers().size());
        assertTrue(etiqetConfig.getServers().stream().anyMatch(s -> s.getName().equals("testServer")));
    }

    @Test
    public void testParseBadProtocol() {
        String badSchemaFile = "file.not.found.xsd";
        XmlParser parser = new XmlParser() {
            @Override
            String getSchemaFile() {
                return badSchemaFile;
            }
        };

        try {
            parser.parse("file/not/relevant/for/test.xml", EtiqetConfiguration.class);
            fail("Using " + badSchemaFile + ", parsing should have thrown an EtiqetException");
        } catch (Exception e) {
            assertTrue("Using " + badSchemaFile + ", parsing should have thrown an EtiqetException",
                            e instanceof EtiqetException);
            assertEquals("Could not read " + badSchemaFile + ", please check your classpath configuration"
                            , e.getMessage());
        }
    }
}