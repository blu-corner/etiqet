package com.neueda.etiqet.core.transport;

import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import com.neueda.etiqet.core.message.config.ProtocolConfig;

/**
 * This interface is responsible for providing the encoding / decoding methods from specific messages from / to CDR.
 *
 * @param <U> unmarshalled native message format
 * @param <M> marshalled format of message for Etiqet to handle
 */
public interface Codec<U, M> {

    /**
     * The encoding of a CDR into the specific message.
     *
     * @param message the CDR.
     * @return the specific message.
     */
    M encode(U message) throws EtiqetException;

    /**
     * The decoding of a specific message into a CDR.
     *
     * @param message the specific message.
     * @return the CDR.
     */
    U decode(M message) throws EtiqetException;

    default void setProtocolConfig(ProtocolConfig protocolConfig) {
    }

}
