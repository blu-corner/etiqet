package com.neueda.etiqet.core.config.dtos;

import com.neueda.etiqet.core.common.EtiqetConstants;
import java.io.Serializable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(namespace = EtiqetConstants.NAMESPACE)
public class StopEvent implements Serializable {

    private Observer[] observers;

    @XmlElement(name = "observer", namespace = EtiqetConstants.NAMESPACE)
    public Observer[] getObservers() {
        return observers;
    }

    public void setObservers(Observer[] observers) {
        this.observers = observers;
    }

    @Override
    public String toString() {
        return "StopEvent [observers = " + observers + "]";
    }
}
