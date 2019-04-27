package com.neueda.etiqet.core.client.delegate;

import com.neueda.etiqet.core.message.cdr.Cdr;

/**
 * Base client delegate implementing the chain of responsibility pattern.
 */
public class BaseClientDelegate implements ClientDelegate {

    /* the next delegate on the chain to process the message */
    protected ClientDelegate next;

    /**
     * Constructor.
     */
    public BaseClientDelegate() {
        this(new SinkClientDelegate());
    }

    /**
     * Constructor.
     *
     * @param next the next delegate on the chain to process the message.
     */
    public BaseClientDelegate(ClientDelegate next) {
        setNextDelegate(next);
    }

    @Override
    public void setNextDelegate(ClientDelegate next) {
        this.next = next;
    }

    @Override
    public ClientDelegate findDelegate(Class<? extends ClientDelegate> delegateClass) {
        if (getClass().getCanonicalName().equals(delegateClass.getCanonicalName())) {
            return this;
        } else {
            return (next != null) ? next.findDelegate(delegateClass) : null;
        }
    }

    @Override
    public Cdr processMessage(Cdr msg) {
        return (next != null) ? next.processMessage(msg) : msg;
    }
}
