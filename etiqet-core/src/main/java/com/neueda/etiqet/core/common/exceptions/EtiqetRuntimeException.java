package com.neueda.etiqet.core.common.exceptions;

public class EtiqetRuntimeException extends RuntimeException {

    public EtiqetRuntimeException() {
        super();
    }

    public EtiqetRuntimeException(String message) {
        super(message);
    }

    public EtiqetRuntimeException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public EtiqetRuntimeException(Throwable throwable) {
        super(throwable);
    }

}
