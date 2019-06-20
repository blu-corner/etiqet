package com.neueda.etiqet.core.message;

import com.neueda.etiqet.core.message.cdr.Cdr;

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

    public Cdr build() {
        return cdr;
    }
}
