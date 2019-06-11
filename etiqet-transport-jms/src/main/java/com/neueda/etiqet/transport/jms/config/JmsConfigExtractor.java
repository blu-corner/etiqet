package com.neueda.etiqet.transport.jms.config;

import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import com.neueda.etiqet.transport.jms.*;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.net.URL;
import java.util.Collections;
import java.util.List;

import static com.neueda.etiqet.transport.jms.config.JmsConfigConstants.JMS_CONFIG_SCHEMA;
import static java.util.stream.Collectors.toList;

public class JmsConfigExtractor {

    public JmsConfig retrieveConfiguration(final String configPath) throws EtiqetException {
        final JmsConfiguration jmsConfiguration = getJmsConfiguration(configPath);
        final Class<?> constructorClass;
        try {
            constructorClass = Class.forName(jmsConfiguration.getImplementation());
        } catch (ReflectiveOperationException e) {
            throw new EtiqetException(e.getMessage());
        }
        return new JmsConfig(
            constructorClass,
            getConstructorArguments(jmsConfiguration.getConstructorArgs()),
            getSetterArguments(jmsConfiguration.getProperties()),
            jmsConfiguration.getDefaultTopic()
        );
    }

    private JmsConfiguration getJmsConfiguration(final String configPath) throws EtiqetException {
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
        } catch (JAXBException | SAXException e) {
            throw new EtiqetException("Error retrieving Jms configuration from " + configPath, e);
        }
    }

    private List<ConstructorArgument> getConstructorArguments(final ConstructorArgs constructorArgs) {
        if (constructorArgs == null) {
            return Collections.emptyList();
        }
        return constructorArgs.getArg().stream()
            .map(this::mapArgument)
            .collect(toList());
    }

    private ConstructorArgument mapArgument(final ConstructorArg xmlArg) {
        return new ConstructorArgument(
            ArgumentType.from(xmlArg.getArgType().value()),
            xmlArg.getArgValue()
        );
    }


    private List<SetterArgument> getSetterArguments(final Properties properties) {
        if (properties == null) {
            return Collections.emptyList();
        }
        return properties.getProperty().stream()
            .map(this::mapProperty)
            .collect(toList());
    }

    private SetterArgument mapProperty(SetterProperty prop) {
        return new SetterArgument(
            ArgumentType.from(prop.getArgType().value()),
            prop.getArgName(),
            prop.getArgValue()
        );
    }
}
