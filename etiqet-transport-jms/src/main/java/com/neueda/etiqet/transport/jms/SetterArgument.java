package com.neueda.etiqet.transport.jms;

public class SetterArgument {
    private ArgumentType argumentType;
    private String name;
    private String value;

    public SetterArgument(ArgumentType argumentType, String name, String value) {
        this.argumentType = argumentType;
        this.name = name;
        this.value = value;
    }

    public ArgumentType getArgumentType() {
        return argumentType;
    }

    public void setArgumentType(ArgumentType argumentType) {
        this.argumentType = argumentType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
