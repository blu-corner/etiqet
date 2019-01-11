package com.neueda.etiqet.core.client.delegate;

import com.neueda.etiqet.core.message.cdr.Cdr;
import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class SinkClientDelegateTest {

    private final SinkClientDelegate sinkClientDelegate = new SinkClientDelegate();

    @Test
    public void testTransformBeforeSendMessage() throws EtiqetException {
        Cdr testMessage = new Cdr("None");
        testMessage.set("test", "value");
        assertEquals(testMessage, sinkClientDelegate.processMessage(testMessage));
        assertNull(sinkClientDelegate.processMessage(null));
    }

}