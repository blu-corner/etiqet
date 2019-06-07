package com.neueda.etiqet.transport.jms;

import com.neueda.etiqet.core.common.exceptions.EtiqetRuntimeException;

public enum ArgumentType {
    STRING (String.class);

    private Class clazz;

    ArgumentType(final Class clazz)  {
        this.clazz = clazz;
    }

    public Class getClazz() {
        return clazz;
    }

   public static ArgumentType from(final String propertyType) {
        switch (propertyType) {
            case "string": return STRING;
            default: throw new EtiqetRuntimeException("Invalid constructor argument property type " + propertyType);
        }
    }
}
