package com.neueda.etiqet.transport.jms.config.model;

import com.neueda.etiqet.transport.jms.ArgType;
import com.neueda.etiqet.transport.jms.ConstructorArg;

public class ConstructorArgBuilder {
    private ConstructorArg constructorArg;

    private ConstructorArgBuilder() {
        this.constructorArg = new ConstructorArg();
    }

    public static ConstructorArgBuilder aConstructorArg() {
        return new ConstructorArgBuilder();
    }

    public ConstructorArgBuilder argType(final ArgType argType) {
        constructorArg.setArgType(argType);
        return this;
    }

    public ConstructorArgBuilder argValue(final String argValue) {
        constructorArg.setArgValue(argValue);
        return this;
    }

    public ConstructorArg build() {
        return constructorArg;
    }
}
