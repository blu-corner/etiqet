package com.neueda.etiqet.transport.jms.config;

import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import com.neueda.etiqet.core.common.exceptions.EtiqetRuntimeException;
import com.neueda.etiqet.transport.jms.*;
import com.neueda.etiqet.transport.jms.config.model.ArgumentType;
import com.neueda.etiqet.transport.jms.config.model.ConstructorArgument;
import com.neueda.etiqet.transport.jms.config.model.JmsConfig;
import com.neueda.etiqet.transport.jms.config.model.SetterArgument;

import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class JmsConfigExtractor {
    private final JmsConfigXmlParser jmsConfigXmlParser;

    public JmsConfigExtractor(final JmsConfigXmlParser jmsConfigXmlParser) {
        this.jmsConfigXmlParser = jmsConfigXmlParser;
    }

    public JmsConfig retrieveConfiguration(final String configPath) throws EtiqetException {

        final JmsConfiguration jmsConfiguration = jmsConfigXmlParser.parse(configPath, JmsConfiguration.class);
        final Class<?> constructorClass;
        try {
            constructorClass = Class.forName(jmsConfiguration.getImplementation());
        } catch (ReflectiveOperationException e) {
            throw new EtiqetException(e);
        }
        return new JmsConfig(
            constructorClass,
            constructorArguments(jmsConfiguration.getConstructorArgs()),
            setterArguments(jmsConfiguration.getProperties()),
            jmsConfiguration.getDefaultTopic(),
            binaryMessageConverterDelegateClass(jmsConfiguration)
        );
    }

    private List<ConstructorArgument> constructorArguments(final ConstructorArgs constructorArgs) {
        if (constructorArgs == null) {
            return Collections.emptyList();
        }
        return constructorArgs.getArg().stream()
            .map(this::mapArgument)
            .collect(toList());
    }

    private ConstructorArgument mapArgument(final ConstructorArg xmlArg) {
        final ArgumentType argumentType = ArgumentType.from(xmlArg.getArgType().value());
        return new ConstructorArgument(
            argumentType,
            getArgumentValue(argumentType, xmlArg.getArgValue())
        );
    }

    private Object getArgumentValue(final ArgumentType argumentType, final String stringValue) {
        switch (argumentType) {
            case BOOLEAN:
            case BOOLEAN_BOXED:
                return stringValue.toLowerCase().equals("true");
            case STRING:
                return stringValue;
            default: throw new EtiqetRuntimeException("Unable to process argument value from config. Argument type: " + argumentType.name() + ", value: " + stringValue);
        }
    }


    private List<SetterArgument> setterArguments(final Properties properties) {
        if (properties == null) {
            return Collections.emptyList();
        }
        return properties.getProperty().stream()
            .map(this::mapProperty)
            .collect(toList());
    }

    private SetterArgument mapProperty(SetterProperty prop) {
        final ArgumentType argumentType = ArgumentType.from(prop.getArgType().value());
        return new SetterArgument(
            argumentType,
            prop.getArgName(),
            getArgumentValue(argumentType, prop.getArgValue())
        );
    }

    private Class binaryMessageConverterDelegateClass(JmsConfiguration xmlConfiguration) throws EtiqetException{
        try {
            String binaryMessageConverterDelegateClassName = xmlConfiguration.getBinaryMessageConverterDelegate();
            if (binaryMessageConverterDelegateClassName == null) {
                return null;
            }
            return Class.forName(binaryMessageConverterDelegateClassName);
        } catch (ReflectiveOperationException e) {
            throw new EtiqetException("Unable to find BinaryMessageConverterDelegate class " + xmlConfiguration.getBinaryMessageConverterDelegate());
        }
    }

}
