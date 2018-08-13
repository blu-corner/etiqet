package com.neueda.etiqet.fix.client.delegate;

import com.neueda.etiqet.core.client.delegate.BaseClientDelegate;
import com.neueda.etiqet.core.common.exceptions.StopEncodingException;
import org.junit.Test;
import org.mockito.Mockito;
import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.DefaultCstmApplVerID;
import quickfix.field.Password;
import quickfix.field.SenderSubID;
import quickfix.field.TargetSubID;

import static org.junit.Assert.*;

public class FixClientDelegateTest {

    @Test
    public void testTransformBeforeEncoding() {
        final String targetSub = "targetSub";
        final String senderSub = "senderSub";
        final String password = "password";

        FixClientDelegate delegate = new FixClientDelegate();
        delegate.init(targetSub, senderSub);
        Message msg = Mockito.spy(Message.class);

        // Need to mock this using a Message class rather than concrete implementation
        Mockito.when(msg.isAdmin()).thenReturn(Boolean.TRUE);

        try {
            delegate.transformBeforeEncoding(msg);
        } catch (StopEncodingException e) {
            fail("StopEncodingException was thrown during transformation: " + e.getMessage());
        }

        assertTrue(msg.isAdmin());
        try {
            assertEquals(targetSub, msg.getHeader().getField(new TargetSubID()).getValue());
            assertEquals(senderSub, msg.getHeader().getField(new SenderSubID()).getValue());
            assertEquals("T4.0", msg.getField(new DefaultCstmApplVerID()).getValue());
        } catch (FieldNotFound e) {
            fail("FieldNotFound exception was thrown while checking for TargetSubID / SenderSubID / Password: "
                    + e.getMessage());
        }

        delegate.init(targetSub, senderSub, password);

        try {
            delegate.transformBeforeEncoding(msg);
        } catch (StopEncodingException e) {
            fail("StopEncodingException was thrown during transformation: " + e.getMessage());
        }

        assertTrue(msg.isAdmin());
        testSubs(msg, targetSub, senderSub);
        try {
            assertEquals(password, msg.getField(new Password()).getValue());
            assertEquals("T4.0", msg.getField(new DefaultCstmApplVerID()).getValue());
        } catch (FieldNotFound e) {
            fail("FieldNotFound exception was thrown while checking for TargetSubID / SenderSubID / Password: "
                + e.getMessage());
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testTransformBeforeEncodingChainDelegate() {
        final String targetSub = "targetSub";
        final String senderSub = "senderSub";

        Message msg = new Message();

        FixClientDelegate delegate = new FixClientDelegate(null);
        delegate.init(targetSub, senderSub);

        try {
            delegate.transformBeforeEncoding(msg);
        } catch (StopEncodingException e) {
            fail("StopEncodingException was thrown during transformation: " + e.getMessage());
        }

        assertFalse(msg.isAdmin());
        testSubs(msg, targetSub, senderSub);

        BaseClientDelegate nextDelegate = Mockito.spy(BaseClientDelegate.class);
        delegate = new FixClientDelegate(nextDelegate);
        delegate.init(targetSub, senderSub);

        try {
            delegate.transformBeforeEncoding(msg);
            testSubs(msg, targetSub, senderSub);
            Mockito.verify(nextDelegate, Mockito.times(1)).transformBeforeEncoding(msg);
        } catch (StopEncodingException e) {
            fail("StopEncodingException was thrown during transformation: " + e.getMessage());
        }
    }

    private void testSubs(Message msg, String targetSub, String senderSub) {
        try {
            assertEquals(targetSub, msg.getHeader().getField(new TargetSubID()).getValue());
            assertEquals(senderSub, msg.getHeader().getField(new SenderSubID()).getValue());
        } catch (FieldNotFound e) {
            fail("FieldNotFound exception was thrown while checking for TargetSubID / SenderSubID: " + e.getMessage());
        }
    }

}
