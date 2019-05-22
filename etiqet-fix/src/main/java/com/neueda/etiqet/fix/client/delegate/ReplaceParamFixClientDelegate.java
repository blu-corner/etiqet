package com.neueda.etiqet.fix.client.delegate;

import com.neueda.etiqet.core.client.delegate.ClientDelegate;
import com.neueda.etiqet.core.message.cdr.Cdr;
import com.neueda.etiqet.core.message.cdr.CdrItem;

import java.util.Map;

/**
 * Delegate for QuickFix client that fills some necessary parameters when sending messages to the server.
 */
public class ReplaceParamFixClientDelegate extends MessageFixClientDelegate {

    /**
     * Constructor.
     */
    public ReplaceParamFixClientDelegate() {
        super();
    }

    /**
     * Constructor.
     *
     * @param next the next delegate on the chain to process the message.
     */
    public ReplaceParamFixClientDelegate(ClientDelegate next) {
        super(next);
    }

    @Override
    public Cdr processMessage(Cdr msg) {
        // Copy original message
        Cdr transformed = new Cdr(msg.getType());
        Map<String, CdrItem> transformedItems = msg.getItems();
        transformedItems.forEach(transformed::setItem);

        // Replace parameters from configured message
        if (message != null) {
            transformedItems.forEach((k, v) -> {
                if (message.getItems().containsKey(k)) {
                    transformedItems.put(k, message.getItem(k));
                }
            });
        }

        return super.processMessage(transformed);
    }
}
