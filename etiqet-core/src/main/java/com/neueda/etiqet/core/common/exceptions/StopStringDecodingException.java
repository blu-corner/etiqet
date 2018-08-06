package com.neueda.etiqet.core.common.exceptions;

/**
 * Exception thrown to stop the encoder for a specific message
 */
public class StopStringDecodingException extends EtiqetException {

    private static final String STOP_DECODING_MESSAGE = "Stop decoding the message with reason :";

    /**
     * Constructor from error message.
     * @param msg the cdr message containing the data.
     * @param reason the message describing the exception.
     */
    public StopStringDecodingException(String msg, String reason) {
        super(STOP_DECODING_MESSAGE + reason + ". Message details: " + msg);
    }

    /**
     * Constructor from throwable exception.
     * @param msg the cdr message containing the data.
     * @param e the throwable exception.
     */
    public StopStringDecodingException(String msg, Throwable e) {
        this(msg, e.getMessage(), e);
    }

    /**
     * Constructor from error message and exception.
     * @param msg the cdr message containing the data.
     * @param reason the message describing the exception.
     * @param e the throwable exception.
     */
    public StopStringDecodingException(String msg, String reason, Throwable e) {
        super(STOP_DECODING_MESSAGE + reason + ". Message details: " + msg, e);
    }
}
