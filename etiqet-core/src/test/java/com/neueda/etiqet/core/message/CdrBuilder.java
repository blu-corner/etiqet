package com.neueda.etiqet.core.message;

import com.neueda.etiqet.core.message.cdr.Cdr;
import com.neueda.etiqet.core.message.cdr.CdrItem;

public class CdrBuilder {
    private Cdr cdr;

    private CdrBuilder(String typeId) {
        cdr = new Cdr(typeId);
    }

    public static CdrBuilder aCdr(String typeId) {
        return new CdrBuilder(typeId);
    }

    public CdrBuilder withField(String field, String value) {
        cdr.set(field, value);
        return this;
    }

    public CdrBuilder withField(String field, int value) {
        cdr.set(field, value);
        return this;
    }

    public CdrBuilder withField(String field, long value) {
        cdr.set(field, value);
        return this;
    }

    public CdrBuilder withField(String field, boolean value) {
        cdr.set(field, value);
        return this;
    }

    public CdrBuilder withField(String field, double value) {
        cdr.set(field, value);
        return this;
    }

    public CdrBuilder withCdrItem(String field, CdrItem item) {
        cdr.setItem(field, item);
        return this;
    }

    public Cdr build() {
        return cdr;
    }
}
