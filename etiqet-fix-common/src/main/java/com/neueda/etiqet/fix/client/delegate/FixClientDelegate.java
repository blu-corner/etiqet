package com.neueda.etiqet.fix.client.delegate;

import com.neueda.etiqet.core.client.delegate.BaseClientDelegate;
import com.neueda.etiqet.core.client.delegate.ClientDelegate;
import com.neueda.etiqet.core.message.cdr.Cdr;
import com.neueda.etiqet.core.util.StringUtils;

/**
 * Delegate for QuickFix client that fills some necessary parameters when sending messages to the server.
 */
public class FixClientDelegate extends BaseClientDelegate {

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
     *
     * @param next the next delegate on the chain to process the message.
     */
    public FixClientDelegate(ClientDelegate next) {
        super(next);
    }

    /**
     * Initialise the delegate with some message required by quickfix.
     *
     * @param targetSubID the target identifier
     * @param senderSubID the sender identifier
     */
    public void init(String targetSubID, String senderSubID) {
        init(targetSubID, senderSubID, null);
    }

    /**
     * Initialise the delegate with some message required by quickfix.
     *
     * @param targetSubID the target identifier
     * @param senderSubID the sender identifier
     * @param password    the password for the client to connect
     */
    public void init(String targetSubID, String senderSubID, String password) {
        this.targetSubID = targetSubID;
        this.senderSubID = senderSubID;
        this.password = password;
    }

    @Override
    public Cdr processMessage(Cdr msg) {
        if (!StringUtils.isNullOrEmpty(targetSubID)) {
            msg.set("TargetSubID", targetSubID);
        }
        if (!StringUtils.isNullOrEmpty(senderSubID)) {
            msg.set("SenderSubID", senderSubID);
        }
        if ("Logon".equals(msg.getType()) && !StringUtils.isNullOrEmpty(password)) {
            msg.set("Password", password);
        }
        return (next != null) ? next.processMessage(msg) : msg;
    }
}
