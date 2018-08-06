package com.neueda.etiqet.core.message;

import com.neueda.etiqet.core.common.cdr.Cdr;
import com.neueda.etiqet.core.common.exceptions.EtiqetException;

/**
 * This interface is responsible for providing the encoding / decoding methods from specific messages from / to CDR.
 * @param <T> the specific message to encode / decode from /to CDR.
 */
public interface Codec<T> {
    /**
     * The encoding of a CDR into the specific message.
     * @param message the CDR.
     * @return the specific message.
     */
    T encode(Cdr message) throws EtiqetException;

    /**
     * The decoding of a specific message into a CDR.
     * @param message the specific message.
     * @return the CDR.
     */
    Cdr decode(T message) throws EtiqetException;
}
