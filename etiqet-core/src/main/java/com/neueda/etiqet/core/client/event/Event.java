package com.neueda.etiqet.core.client.event;

import com.neueda.etiqet.core.client.Client;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public abstract class Event<Observer> {

    protected final Set<Observer> mObservers = Collections.newSetFromMap(new ConcurrentHashMap<>(0));
    protected Client client;

    public Event(Client client) {
        this.client = client;
    }

    public void registerObserver(Observer observer) {
        if (observer == null) {
            return;
        }
        mObservers.add(observer);
    }

    public void unregisterObserver(Observer observer) {
        if (observer != null) {
            mObservers.remove(observer);
        }
    }
}
