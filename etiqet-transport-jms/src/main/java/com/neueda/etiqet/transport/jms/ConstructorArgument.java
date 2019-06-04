package com.neueda.etiqet.transport.jms;

public class ConstructorArgument {
    private ArgumentType argumentType;
    private Object value;

    public ConstructorArgument(ArgumentType argumentType, Object value) {
        this.argumentType = argumentType;
        this.value = value;
    }

    public ArgumentType getArgumentType() {
        return argumentType;
    }

    public void setArgumentType(ArgumentType argumentType) {
        this.argumentType = argumentType;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
