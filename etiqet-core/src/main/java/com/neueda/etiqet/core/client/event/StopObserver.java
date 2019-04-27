package com.neueda.etiqet.core.client.event;

import com.neueda.etiqet.core.client.Client;
import com.neueda.etiqet.core.common.exceptions.EtiqetException;

public abstract class StopObserver {

    public abstract void handleStop(Client client) throws EtiqetException;

}
