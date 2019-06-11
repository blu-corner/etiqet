package com.neueda.etiqet.transport.jms.config;

import java.util.List;

public class JmsConfig {
    private Class implementation;
    private List<ConstructorArgument> constructorArgs;
    private List<SetterArgument> setterArgs;
    private String defaultTopic;

    public JmsConfig(Class implementation, List<ConstructorArgument> constructorArgs, List<SetterArgument> setterArgs, String defaultTopic) {
        this.implementation = implementation;
        this.constructorArgs = constructorArgs;
        this.setterArgs = setterArgs;
        this.defaultTopic = defaultTopic;
    }

    public Class getImplementation() {
        return implementation;
    }

    public List<ConstructorArgument> getConstructorArgs() {
        return constructorArgs;
    }

    public List<SetterArgument> getSetterArgs() {
        return setterArgs;
    }

    public String getDefaultTopic() {
        return defaultTopic;
    }
}
