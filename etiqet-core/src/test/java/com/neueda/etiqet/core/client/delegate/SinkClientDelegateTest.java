package com.neueda.etiqet.core.client.delegate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import com.neueda.etiqet.core.message.cdr.Cdr;
import org.junit.Test;

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
