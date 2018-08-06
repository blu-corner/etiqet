package com.neueda.etiqet.core.client.delegate;

import com.neueda.etiqet.core.common.cdr.Cdr;
import com.neueda.etiqet.core.common.exceptions.StopEncodingException;
import com.neueda.etiqet.core.common.exceptions.StopStringDecodingException;

/**
 * @param <U> unmarshalled native message format
 * @param <M> marshalled format of message for Etiqet to handle
 */
public class BaseClientDelegate<U, M> implements ClientDelegate<U, M> {

    /* the next delegate on the chain to process the message */
    protected ClientDelegate<U, M> next;

    /**
     * Constructor.
     */
    public BaseClientDelegate() {
        this(new SinkClientDelegate<>());
    }

    /**
     * Constructor.
     * @param next the next delegate on the chain to process the message.
     */
    public BaseClientDelegate(ClientDelegate<U, M> next) {
        setNextDelegate(next);
    }

    @Override
    public void setNextDelegate(ClientDelegate<U, M> next) {
        this.next = next;
    }

    @Override
    public ClientDelegate<U, M> findDelegate(Class<? extends ClientDelegate<U, M>> delegateClass) {
        if (getClass().getCanonicalName().equals(delegateClass.getCanonicalName())){
            return this;
        } else {
            return (next != null) ? next.findDelegate(delegateClass) : null;
        }
    }

    @Override
    public Cdr transformBeforeSendMessage(Cdr msg) {
        return (next != null)? next.transformBeforeSendMessage(msg): msg;
    }

    @Override
    public Cdr transformAfterReceiveMessage(Cdr msg) {
        return (next != null)? next.transformAfterReceiveMessage(msg): msg;
    }

    @Override
    public U transformBeforeEncoding(U msg) throws StopEncodingException {
        return (next != null)? next.transformBeforeEncoding(msg): msg;
    }

    @Override
    public M transformAfterEncoding(M msg) throws StopEncodingException {
        return (next != null)? next.transformAfterEncoding(msg): msg;
    }

    @Override
    public M transformBeforeDecoding(M msg) throws StopStringDecodingException {
        return (next != null)? next.transformBeforeDecoding(msg): msg;
    }

    @Override
    public U transformAfterDecoding(U msg) throws StopStringDecodingException {
        return (next != null)? next.transformAfterDecoding(msg): msg;
    }
}
