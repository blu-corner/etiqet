package com.neueda.etiqet.transport.jms.config.model;

import com.neueda.etiqet.transport.jms.*;

public class JmsConfigurationBuilder {
    private JmsConfiguration jmsConfiguration;

    private JmsConfigurationBuilder() {
        jmsConfiguration = new JmsConfiguration();
        jmsConfiguration.setConstructorArgs(new ConstructorArgs());
        jmsConfiguration.setProperties(new Properties());
    }

    public static JmsConfigurationBuilder aJmsConfigurationBuilder() {
        return new JmsConfigurationBuilder();
    }

    public JmsConfigurationBuilder withImplementation(final String implementation) {
        this.jmsConfiguration.setImplementation(implementation);
        return this;
    }

    public JmsConfigurationBuilder addConstructorArg(final ConstructorArg constructorArg) {
        this.jmsConfiguration.getConstructorArgs().getArg().add(constructorArg);
        return this;
    }

    public JmsConfigurationBuilder addProperty(final SetterProperty property) {
        this.jmsConfiguration.getProperties().getProperty().add(property);
        return this;
    }

    public JmsConfiguration build() {
        return jmsConfiguration;
    }
}
