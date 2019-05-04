package com.neueda.etiqet.rest.message;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import com.neueda.etiqet.core.common.exceptions.SerializeException;
import com.neueda.etiqet.core.config.GlobalConfig;
import com.neueda.etiqet.core.config.dtos.Message;
import com.neueda.etiqet.core.message.cdr.Cdr;
import com.neueda.etiqet.rest.RestConfig;
import com.neueda.etiqet.rest.message.impl.HttpRequestMsg;
import java.lang.reflect.Field;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class RestMsgTest {

    private GlobalConfig globalConfig;

    @Before
    public void setUp() throws EtiqetException {
        globalConfig = GlobalConfig.getInstance(RestConfig.class);
    }

    @After
    public void tearDown() throws Exception {
        Field field = GlobalConfig.class.getDeclaredField("instance");
        field.setAccessible(true);
        field.set(globalConfig, null);
    }

    @Test
    public void testSerializeMessage() throws EtiqetException {
        String verb = "POST";
        String endpoint = "/api/test";
        String authHeader = "Bearer 12345";
        String messageName = "test_01";

        Cdr cdr = new Cdr(verb);
        cdr.set("$httpVerb", verb);
        cdr.set("$httpEndpoint", endpoint);
        cdr.set("$header.authorization", authHeader);
        cdr.set("test", "value");
        cdr.set("test2", "value2");

        RestMsg restMsg = new RestMsg(messageName);
        assertEquals(messageName, restMsg.getType());
        HttpRequestMsg requestMsg = restMsg.serialize(cdr);
        assertEquals(verb, requestMsg.getVerb());
        assertEquals(endpoint, requestMsg.getUrl());
        assertTrue(requestMsg.getHeaders().containsKey("authorization"));
        assertEquals(authHeader, requestMsg.getHeaders().get("authorization"));
        assertEquals("{\"test\":\"value\",\"test2\":\"value2\"}", requestMsg.getPayload());
    }

    @Test
    public void testSerializeNotValidMessage() throws EtiqetException {
        RestMsg msg = new RestMsg("invalid");

        try {
            msg.serialize(new Cdr("invalid"));
            fail("Serialise should have failed for msgType \"invalid\"");
        } catch (Exception e) {
            assertTrue(e instanceof EtiqetException);
            assertEquals("Message type invalid not recognised", e.getMessage());
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
