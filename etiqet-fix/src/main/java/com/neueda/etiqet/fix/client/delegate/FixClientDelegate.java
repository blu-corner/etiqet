package com.neueda.etiqet.fix.client.delegate;

import com.neueda.etiqet.core.client.delegate.BaseClientDelegate;
import com.neueda.etiqet.core.client.delegate.ClientDelegate;
import com.neueda.etiqet.core.common.exceptions.StopEncodingException;
import com.neueda.etiqet.core.util.StringUtils;
import quickfix.Message;
import quickfix.field.Password;
import quickfix.field.SenderSubID;
import quickfix.field.TargetSubID;

/**
 * Delegate for QuickFix client that fills some necessary parameters when sending messages to the server.
 */
public class FixClientDelegate extends BaseClientDelegate<Message, String> {

    private String targetSubID;
    private String senderSubID;
    private String password;

    /**
     * Constructor.
     */
    public FixClientDelegate() {
        super();
    }

    /**
     * Constructor.
     * @param next the next delegate on the chain to process the message.
     */
    public FixClientDelegate(ClientDelegate<Message, String> next) {
        super(next);
    }

    /**
     * Initialise the delegate with some message required by quickfix.
     * @param targetSubID the target identifier
     * @param senderSubID the sender identifier
     */
    public void init(String targetSubID, String senderSubID)
    {
        init(targetSubID, senderSubID, null);
    }

    /**
     * Initialise the delegate with some message required by quickfix.
     * @param targetSubID the target identifier
     * @param senderSubID the sender identifier
     * @param password the password for the client to connect
     */
    public void init(String targetSubID, String senderSubID, String password)
    {
        this.targetSubID = targetSubID;
        this.senderSubID = senderSubID;
        this.password = password;
    }

    @Override
    public Message transformBeforeEncoding(Message msg) throws StopEncodingException {
        if(!StringUtils.isNullOrEmpty(targetSubID)) {
            msg.getHeader().setField(new TargetSubID(targetSubID));
        }
        if(!StringUtils.isNullOrEmpty(senderSubID)) {
            msg.getHeader().setField(new SenderSubID(senderSubID));
        }
        if(msg.isAdmin() && !StringUtils.isNullOrEmpty(password)) {
            msg.setField(new Password(password));
        }
        return (next != null)? next.transformBeforeEncoding(msg): msg;
    }
}
