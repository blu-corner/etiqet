package com.neueda.etiqet.rest.transport;

import com.google.api.client.http.HttpResponse;
import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import com.neueda.etiqet.core.json.JsonUtils;
import com.neueda.etiqet.core.message.cdr.Cdr;
import com.neueda.etiqet.core.transport.Codec;
import com.neueda.etiqet.rest.message.RestMsg;
import com.neueda.etiqet.rest.message.impl.HttpRequestMsg;

import java.io.IOException;
import java.util.Map;

public class RestCodec implements Codec<Cdr, HttpRequestMsg> {

    @Override
    public HttpRequestMsg encode(Cdr message) throws EtiqetException {
        return new RestMsg(message.getType()).serialize(message);
    }

    @Override
    public Cdr decode(HttpRequestMsg message) throws EtiqetException {
        HttpResponse httpResponse = message.getResponse();
        Cdr responseData = null;
        if (httpResponse.getStatusCode() != 404) {
            try {
                responseData = JsonUtils.jsonToCdr(httpResponse.parseAsString());
            } catch (IOException e) {
                throw new EtiqetException("Could not decode message [" + message.toString() + "]", e);
            }
        }
        if (responseData == null) {
            responseData = new Cdr(Cdr.class.getName());
        }

        for (Map.Entry<String, Object> header : httpResponse.getHeaders().entrySet()) {
            responseData.set("$header." + header.getKey(), String.valueOf(header.getValue()));
        }

        return new RestMsg(String.valueOf(httpResponse.getStatusCode())).update(responseData);
    }

}
