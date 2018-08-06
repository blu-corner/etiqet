package com.neueda.etiqet.core.common.exceptions;

import com.neueda.etiqet.core.common.cdr.Cdr;

/**
 * Exception thrown to stop the encoder for a specific message
 */
@SuppressWarnings("serial")
public class StopEncodingException extends EtiqetException {

    private static final String STOP_ENCODING_REASON = "Stop encoding the message with reason :";

    /**
     * Constructor from error message.
     * @param msg the message describing the exception.
     * @param reason the message describing the exception.
     */
    public StopEncodingException(Cdr msg, String reason) {
        super(STOP_ENCODING_REASON + reason + ". Message details: " + msg.toString());
    }

    /**
     * Constructor from throwable exception.
     * @param e the throwable exception.
     */
    public StopEncodingException(Throwable e) {
        super(e);
    }

    /**
     * Constructor from throwable exception.
     * @param e the throwable exception.
     */
    public StopEncodingException(Cdr msg, Throwable e) {
        this(msg, e.getMessage(), e);
    }

    /**
     * Constructor from error message and exception.
     * @param msg the message describing the exception.
     * @param reason the message describing the exception.
     */
    public StopEncodingException(Cdr msg, String reason, Throwable e) {
        super(STOP_ENCODING_REASON + reason + ". Message details: " + msg.toString(), e);
    }
}
