package com.neueda.etiqet.fix.client.delegate;

import com.neueda.etiqet.core.client.delegate.ClientDelegate;
import com.neueda.etiqet.core.message.cdr.Cdr;

/**
 * Delegate for QuickFix client that fills some necessary parameters when sending messages to the server.
 */
public class OrderParamFixClientDelegate extends MessageFixClientDelegate {

    /**
     * Constructor.
     */
    public OrderParamFixClientDelegate() {
        super();
    }

    /**
     * Constructor.
     *
     * @param next the next delegate on the chain to process the message.
     */
    public OrderParamFixClientDelegate(ClientDelegate next) {
        super(next);
    }

    @Override
    public Cdr processMessage(Cdr msg) {
        // Copy original message into transform
        Cdr transformed = new Cdr(msg.getType());
        msg.getItems().forEach(transformed::setItem);

        // If message with params has been defined then replace with message's values.
        if (message != null) {
            // Copy the cdr
            message.getItems().forEach(transformed::setItem);
        }
        return super.processMessage(transformed);
    }
}
