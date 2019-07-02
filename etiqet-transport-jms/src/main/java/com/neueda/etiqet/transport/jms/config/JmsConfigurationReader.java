package com.neueda.etiqet.transport.jms.config;

import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import com.neueda.etiqet.transport.jms.JmsConfiguration;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.net.URL;

import static com.neueda.etiqet.transport.jms.config.JmsConfigConstants.JMS_CONFIG_SCHEMA;

public class JmsConfigurationReader {

    public JmsConfiguration getJmsConfiguration(final String configPath) throws EtiqetException {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(JmsConfiguration.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            URL protocolSchema = getClass().getClassLoader().getResource(JMS_CONFIG_SCHEMA);
            if (protocolSchema == null) {
                throw new EtiqetException("Unable to find configuration schema " + JMS_CONFIG_SCHEMA);
            }
            Schema schema = sf.newSchema(protocolSchema);
            unmarshaller.setSchema(schema);
            return (JmsConfiguration) unmarshaller.unmarshal(new File(configPath));
        } catch (Exception e) {
            throw new EtiqetException("Error retrieving Jms configuration from " + configPath, e);
        }
    }
}
