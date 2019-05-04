package com.neueda.etiqet.core.client.event;

import com.neueda.etiqet.core.client.Client;
import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StopEvent extends Event<StopObserver> {

    private static final Logger logger = LoggerFactory.getLogger(StopEvent.class.getName());

    public StopEvent(Client client) {
        super(client);
    }

    public void publishStop() {
        for (StopObserver observer : mObservers) {
            try {
                observer.handleStop(client);
            } catch (EtiqetException e) {
                logger.warn(
                    String.format("StopObserver '%s' threw Exception:\n\t%s", observer.getClass(), e.getMessage()));
            }
        }
    }
}
