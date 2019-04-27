package com.neueda.etiqet.core.client.delegate;

import com.neueda.etiqet.core.message.cdr.Cdr;

/**
 * Last delegate in the chain - does nothing
 */
public class SinkClientDelegate implements ClientDelegate {

    @Override
    public void setNextDelegate(ClientDelegate next) {
        // Nothing to be done since the sink is the last step of the chain
    }

    @Override
    public ClientDelegate findDelegate(Class<? extends ClientDelegate> delegateClass) {
        return null;
    }

    @Override
    public Cdr processMessage(Cdr msg) {
        return msg;
    }
}
