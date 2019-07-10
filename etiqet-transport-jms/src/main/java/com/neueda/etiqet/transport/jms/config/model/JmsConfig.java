package com.neueda.etiqet.transport.jms.config.model;

import java.util.List;
import java.util.Optional;

public class JmsConfig {
    private Class implementation;
    private List<ConstructorArgument> constructorArgs;
    private List<SetterArgument> setterArgs;
    private String defaultTopic;
    private Class binaryMessageConverterDelegate;

    public JmsConfig(Class implementation, List<ConstructorArgument> constructorArgs, List<SetterArgument> setterArgs, String defaultTopic, Class binaryMessageConverterDelegate) {
        this.implementation = implementation;
        this.constructorArgs = constructorArgs;
        this.setterArgs = setterArgs;
        this.defaultTopic = defaultTopic;
        this.binaryMessageConverterDelegate = binaryMessageConverterDelegate;
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

    public Optional<Class> getBinaryMessageConverterDelegateClass() {
        return Optional.ofNullable(binaryMessageConverterDelegate);
    }
}
