package com.neueda.etiqet.transport.jms.config.model;

import com.neueda.etiqet.core.common.exceptions.EtiqetRuntimeException;

public enum ArgumentType {
    STRING (String.class),
    BOOLEAN (Boolean.class);

    private Class clazz;

    ArgumentType(final Class clazz)  {
        this.clazz = clazz;
    }

    public Class getClazz() {
        return clazz;
    }

   public static ArgumentType from(final String propertyType) {
        switch (propertyType.toLowerCase()) {
            case "string": return STRING;
            case "boolean": return BOOLEAN;
            default: throw new EtiqetRuntimeException("Invalid constructor argument property type " + propertyType);
        }
    }
}
