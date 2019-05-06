package com.neueda.etiqet.core.config.dtos;

import com.neueda.etiqet.core.common.EtiqetConstants;
import java.io.Serializable;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlValue;

@XmlRootElement(namespace = EtiqetConstants.NAMESPACE)
public class Dictionary implements Serializable {

    private String handler;

    private String value;
    private Class<?> handlerClass;

    public Dictionary() {
    }

    public Dictionary(String handler) {
        this.handler = handler;
    }

    public Dictionary(Class<?> handlerClass) {
        this.handlerClass = handlerClass;
        this.handler = handlerClass.getName();
    }

    @XmlAttribute
    public String getHandler() {
        return handler;
    }

    public void setHandler(String handler) {
        this.handler = handler;
    }

    public void setHandler(Class<?> handler) {
        this.handler = handler.getName();
        this.handlerClass = handler;
    }

    @XmlTransient
    public Class<?> getHandlerClass() {
        return this.handlerClass;
    }

    @XmlValue
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String toString() {
        return "Dictionary [ handler = " + handler + " ]";
    }

}
