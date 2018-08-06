package com.neueda.etiqet.rest.message;

import com.neueda.etiqet.core.common.cdr.Cdr;
import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import com.neueda.etiqet.core.common.exceptions.SerializeException;
import com.neueda.etiqet.core.config.dtos.Message;
import com.neueda.etiqet.rest.message.impl.HttpRequestMsg;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RestMsgTest {

    @Test
    public void testSerializeHttpVerb() throws EtiqetException {
        String verb = "POST";
        String endpoint = "/api/test";
        String authHeader = "Bearer 12345";

        Cdr cdr = new Cdr(verb);
        cdr.set("$httpVerb", verb);
        cdr.set("$httpEndpoint", endpoint);
        cdr.set("$header.authorization", authHeader);
        cdr.set("test", "value");
        cdr.set("test2", "value2");

        RestMsg restMsg = new RestMsg(verb);
        HttpRequestMsg requestMsg = restMsg.serialize(cdr);
        assertEquals(verb, requestMsg.getVerb());
        assertEquals(endpoint, requestMsg.getUrl());
        assertTrue(requestMsg.getHeaders().containsKey("authorization"));
        assertEquals(authHeader, requestMsg.getHeaders().get("authorization"));
        assertEquals("{\"test\":\"value\",\"test2\":\"value2\"}", requestMsg.getPayload());
    }

    @Test
    public void testSerializeNotHttpVerbType() throws EtiqetException {
        RestMsg msg = new RestMsg("404") {
            @Override
            Message getMessage() throws EtiqetException {
                Message message = mock(Message.class);
                when(message.getImplementation()).thenReturn("com.neueda.etiqet.rest.message.impl.HttpRequestMsg");
                return message;
            }
        };

        try {
            msg.serialize(new Cdr("404"));
            fail("Serialise should have failed for msgType 404");
        } catch (Exception e) {
            assertTrue(e instanceof EtiqetException);
            assertEquals("Message type 404 not recognised", e.getMessage());
        }
    }

    @Test
    public void testSerializeNullMessage() throws EtiqetException {
        RestMsg msg = new RestMsg("OPTIONS") {
            @Override
            Message getMessage() throws EtiqetException {
                return null;
            }
        };

        try {
            msg.serialize(new Cdr("OPTIONS"));
            fail("Serialise should have failed for msgType OPTIONS");
        } catch (Exception e) {
            assertTrue(e instanceof EtiqetException);
            assertEquals("Message type OPTIONS not recognised", e.getMessage());
        }
    }

    @Test
    public void testSerializeClassNotFound() throws EtiqetException {
        String classImpl = "com.neueda.etiqet.rest.message.impl.ClassNotFound";
        RestMsg msg = new RestMsg("404") {
            @Override
            Message getMessage() throws EtiqetException {
                Message message = mock(Message.class);
                when(message.getImplementation()).thenReturn(classImpl);
                return message;
            }
        };

        try {
            msg.serialize(new Cdr("404"));
            fail("Serialise should have failed for msgType 404");
        } catch (Exception e) {
            assertTrue(e instanceof SerializeException);
            assertEquals("java.lang.ClassNotFoundException: " + classImpl, e.getMessage());
        }
    }

}