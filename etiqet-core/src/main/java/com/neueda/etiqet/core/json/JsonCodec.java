package com.neueda.etiqet.core.json;

import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import com.neueda.etiqet.core.message.cdr.Cdr;
import com.neueda.etiqet.core.transport.Codec;

public class JsonCodec implements Codec<Cdr, String> {
    @Override
    public String encode(Cdr message) throws EtiqetException {
        return JsonUtils.cdrToJson(message);
    }

    @Override
    public Cdr decode(String message) throws EtiqetException {
        return JsonUtils.jsonToCdr(message);
    }

}
