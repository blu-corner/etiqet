package com.neueda.etiqet.core.client.event;

import com.neueda.etiqet.core.client.Client;
import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StopEvent extends Event<StopObserver> {
    private static final Logger LOG = LogManager.getLogger(StopEvent.class.getName());

    public StopEvent(Client client) {
        super(client);
    }

    public void publishStop(){
        for(StopObserver observer: mObservers){
            try {
                observer.handleStop(client);
            } catch (EtiqetException e) {
                LOG.warn(String.format("StopObserver '%s' threw Exception:\n\t%s", observer.getClass(), e.getMessage()));
            }
        }
    }
}
