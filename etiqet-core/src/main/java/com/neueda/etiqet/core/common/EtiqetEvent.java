package com.neueda.etiqet.core.common;

public class EtiqetEvent {
    private Boolean event;

    public EtiqetEvent() {
        setEvent(false);
    }

    public Boolean completeEvent() {
        setEvent(true);
        return getEvent();
    }

    public Boolean resetEvent() {
        setEvent(false);
        return getEvent();
    }

    public Boolean getEvent() {
        return event;
    }

    public void setEvent(Boolean event) {
        this.event = event;
    }
}
