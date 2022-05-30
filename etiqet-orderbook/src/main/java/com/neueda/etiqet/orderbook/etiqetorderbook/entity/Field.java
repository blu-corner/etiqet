package com.neueda.etiqet.orderbook.etiqetorderbook.entity;

/**
 * Used by OrderDetailsController
 * TODO: check if it is completely necessary
 */
public class Field {
    private String field;
    private String value;

    public Field(String field, String value) {
        this.field = field;
        this.value = value;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
