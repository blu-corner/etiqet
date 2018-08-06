package com.neueda.etiqet.core.client.delegate;

import com.neueda.etiqet.core.common.cdr.Cdr;

/**
 * Last delegate in the chain - does nothing
 * @param <U> unmarshalled native message format
 * @param <M> marshalled format of message for Etiqet to handle
 */
public class SinkClientDelegate<U, M> implements ClientDelegate<U, M> {

    @Override
    public void setNextDelegate(ClientDelegate<U, M> next) {
        // Nothing to be done since the sink is the last step of the chain
    }

    @Override
    public ClientDelegate<U, M> findDelegate(Class<? extends ClientDelegate<U, M>> delegateClass) {
        return null;
    }

    @Override
    public Cdr transformBeforeSendMessage(Cdr msg) {
        return msg;
    }

    @Override
    public Cdr transformAfterReceiveMessage(Cdr msg) {
        return msg;
    }

    @Override
    public U transformBeforeEncoding(U msg) {
        return msg;
    }

    @Override
    public M transformAfterEncoding(M msg) {
        return msg;
    }

    @Override
    public M transformBeforeDecoding(M msg) {
        return msg;
    }

    @Override
    public U transformAfterDecoding(U msg) {
        return msg;
    }
}
