package com.neueda.etiqet.core.transport;

import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import com.neueda.etiqet.core.message.cdr.Cdr;
import com.neueda.etiqet.core.util.ParserUtils;

/**
 * Default codec to string.
 */
public class ToStringCodec implements Codec<Cdr, String> {

    @Override
    public String encode(Cdr message) throws EtiqetException {
        return message.toString();
    }

    @Override
    public Cdr decode(String message) throws EtiqetException {
        return ParserUtils.stringToCdr("None", message);
    }

}
