package com.neueda.etiqet.core.client.delegate;

import com.neueda.etiqet.core.message.cdr.Cdr;

/**
 * This interface delegates the actions to be customised by the handler at application layer.
 */
public interface ClientDelegate {

    /**
     * Set the next delegate in the chain to process the messages.
     *
     * @param next next to delegate in the chain to process the messages.
     */
    void setNextDelegate(ClientDelegate next);

    /**
     * Iterates on the chain of delegates and returns the first matching the given class.
     *
     * @param delegateClass the delegate to find in the chain.
     * @return the delegate matching the given class or null if not found.
     */
    ClientDelegate findDelegate(Class<? extends ClientDelegate> delegateClass);

    /**
     * This must be invoked in order to transform before the messages is sent to the server.
     *
     * @param msg the generic message to be transformed.
     * @return the transformed message.
     */
    Cdr processMessage(Cdr msg);
}
