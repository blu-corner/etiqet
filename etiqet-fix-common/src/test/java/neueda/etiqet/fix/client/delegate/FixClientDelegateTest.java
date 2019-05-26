package neueda.etiqet.fix.client.delegate;

import com.neueda.etiqet.core.client.delegate.BaseClientDelegate;
import com.neueda.etiqet.core.message.cdr.Cdr;
import com.neueda.etiqet.fix.client.delegate.FixClientDelegate;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class FixClientDelegateTest {

    @Test
    public void testTransformBeforEncodingEmptyValues() {
        FixClientDelegate delegate = new FixClientDelegate();
        delegate.init(null, null);
        Cdr msg = new Cdr("0");
        delegate.processMessage(msg);
        testEmptySubIDs(msg);

        delegate = new FixClientDelegate();
        delegate.init("", "");
        delegate.processMessage(msg);
        testEmptySubIDs(msg);
    }

    private void testEmptySubIDs(Cdr msg) {
        assertNull("TargetSubID shouldn't have been set, so this should cause a FieldNotFound error",
                   msg.getItem("TargetSubID"));
        assertNull("SenderSubID shouldn't have been set, so this should cause a FieldNotFound error",
                   msg.getItem("SenderSubID"));
    }

    @Test
    public void testTransformBeforeEncodingNoLogon() {
        final String targetSub = "targetSub";
        final String senderSub = "senderSub";

        FixClientDelegate delegate = new FixClientDelegate();
        delegate.init(targetSub, senderSub);
        Cdr msg = new Cdr("0");

        delegate.processMessage(msg);
        assertEquals(targetSub, msg.getItem("TargetSubID").getStrval());
        assertEquals(senderSub, msg.getItem("SenderSubID").getStrval());

        delegate.processMessage(msg);
        testSubs(msg, targetSub, senderSub);
    }

    @Test
    public void testTransformBeforeEncodingWithLogon() {
        final String targetSub = "targetSub";
        final String senderSub = "senderSub";
        final String password = "password";

        FixClientDelegate delegate = new FixClientDelegate();
        delegate.init(targetSub, senderSub);
        Cdr msg = new Cdr("Logon");

        delegate.processMessage(msg);
        assertEquals(targetSub, msg.getItem("TargetSubID").getStrval());
        assertEquals(senderSub, msg.getItem("SenderSubID").getStrval());
        delegate.init(targetSub, senderSub, password);

        delegate.processMessage(msg);
        testSubs(msg, targetSub, senderSub);
        assertEquals(
            "FieldNotFound exception was thrown while checking for TargetSubID / SenderSubID / Password: ",
            password, msg.getItem("Password").getStrval());
    }

    @Test
    public void testTransformBeforeEncodingChainDelegate() {
        final String targetSub = "targetSub";
        final String senderSub = "senderSub";

        Cdr msg = new Cdr("0");

        FixClientDelegate delegate = new FixClientDelegate(null);
        delegate.init(targetSub, senderSub);

        delegate.processMessage(msg);

        testSubs(msg, targetSub, senderSub);

        BaseClientDelegate nextDelegate = Mockito.spy(BaseClientDelegate.class);
        delegate = new FixClientDelegate(nextDelegate);
        delegate.init(targetSub, senderSub);

        delegate.processMessage(msg);
        testSubs(msg, targetSub, senderSub);
        Mockito.verify(nextDelegate, Mockito.times(1)).processMessage(msg);
    }

    private void testSubs(Cdr msg, String targetSub, String senderSub) {
        assertEquals("TargetSubID not found in [" + msg.toString() + "]", targetSub,
                     msg.getItem("TargetSubID").getStrval());
        assertEquals("SenderSubID not found in [" + msg.toString() + "]", senderSub,
                     msg.getItem("SenderSubID").getStrval());
    }

}
