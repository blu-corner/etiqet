package com.neueda.etiqet.core.client.delegate;

import com.neueda.etiqet.core.common.cdr.Cdr;
import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class SinkClientDelegateTest {

    private final SinkClientDelegate<Cdr, String> sinkClientDelegate = new SinkClientDelegate<>();

    @Test
    public void testTransformBeforeSendMessage() throws EtiqetException {
        Cdr testMessage = new Cdr("None");
        testMessage.set("test", "value");
        assertEquals(testMessage, sinkClientDelegate.transformBeforeSendMessage(testMessage));
        assertNull(sinkClientDelegate.transformBeforeSendMessage(null));
    }

    @Test
    public void testTransformAfterReceiveMessage() throws EtiqetException {
        Cdr testMessage = new Cdr("None");
        testMessage.set("test", "value");
        assertEquals(testMessage, sinkClientDelegate.transformAfterReceiveMessage(testMessage));
        assertNull(sinkClientDelegate.transformAfterReceiveMessage(null));
    }

    @Test
    public void testTransformBeforeEncoding() throws EtiqetException {
        Cdr message = new Cdr("TestMessage");
        assertEquals(message, sinkClientDelegate.transformBeforeEncoding(message));
        assertNull(sinkClientDelegate.transformBeforeEncoding(null));
    }

    @Test
    public void testTransformAfterEncoding() throws EtiqetException {
        String message = "TestMessage";
        assertEquals(message, sinkClientDelegate.transformAfterEncoding(message));
        assertNull(sinkClientDelegate.transformAfterEncoding(null));
    }

    @Test
    public void testTransformBeforeDecoding() throws EtiqetException {
        String message = "TestMessage";
        assertEquals(message, sinkClientDelegate.transformBeforeDecoding(message));
        assertNull(sinkClientDelegate.transformBeforeDecoding(null));
    }

    @Test
    public void testTransformAfterDecoding() throws EtiqetException {
        Cdr message = new Cdr("TestMessage");
        assertEquals(message, sinkClientDelegate.transformAfterDecoding(message));
        assertNull(sinkClientDelegate.transformAfterDecoding(null));
    }
}