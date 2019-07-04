package com.neueda.etiqet.transport.jms.config.model;

public class SetterArgument {
    private ArgumentType argumentType;
    private String name;
    private Object value;

    public SetterArgument(ArgumentType argumentType, String name, Object value) {
        this.argumentType = argumentType;
        this.name = name;
        this.value = value;
    }

    public ArgumentType getArgumentType() {
        return argumentType;
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }

}
