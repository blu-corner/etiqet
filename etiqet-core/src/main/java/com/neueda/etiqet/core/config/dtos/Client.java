package com.neueda.etiqet.core.config.dtos;

import com.neueda.etiqet.core.common.EtiqetConstants;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * XPath element: /etiqetConfiguration/protocols/protocol/client
 */
@XmlRootElement(namespace = EtiqetConstants.NAMESPACE)
public class Client implements Serializable {

    private String impl;
    private String transportImpl;
    private String codecImpl;

    private Class<?> implementationClass;

    private String defaultConfig;

    private List<UrlExtension> urlExtensions = new ArrayList<>();

    private List<Delegate> delegates = new ArrayList<>();

    private StopEvent stopEvent;

    @XmlElementWrapper(name = "delegates", namespace = EtiqetConstants.NAMESPACE)
    @XmlElement(name = "delegate", namespace = EtiqetConstants.NAMESPACE)
    public List<Delegate> getDelegates() {
        return delegates;
    }

    public void setDelegates(List<Delegate> delegates) {
        this.delegates = delegates;
    }

    @XmlElement(name = "stopEvent", namespace = EtiqetConstants.NAMESPACE)
    public StopEvent getStopEvent() {
        return stopEvent;
    }

    public void setStopEvent(StopEvent stopEvent) {
        this.stopEvent = stopEvent;
    }

    @XmlAttribute(required = true)
    public String getImpl() {
        return impl;
    }

    public void setImpl(String impl) {
        this.impl = impl;
    }

    @XmlAttribute(required = true)
    public String getCodecImpl() {
        return codecImpl;
    }

    public void setCodecImpl(String codecImpl) {
        this.codecImpl = codecImpl;
    }

    @XmlAttribute(required = true)
    public String getTransportImpl() {
        return transportImpl;
    }

    public void setTransportImpl(String transportImpl) {
        this.transportImpl = transportImpl;
    }

    @XmlTransient
    public Class<?> getImplementationClass() {
        return this.implementationClass;
    }

    public void setImplementationClass(Class<?> implementationClass) {
        this.impl = implementationClass.getName();
        this.implementationClass = implementationClass;
    }

    @XmlAttribute(required = true)
    public String getDefaultConfig() {
        return defaultConfig;
    }

    public void setDefaultConfig(String defaultConfig) {
        this.defaultConfig = defaultConfig;
    }

    @XmlElementWrapper(name = "urlExtensions", namespace = EtiqetConstants.NAMESPACE, required = false)
    @XmlElement(name = "urlExtension", namespace = EtiqetConstants.NAMESPACE, required = false)
    public List<UrlExtension> getUrlExtensions() {
        return this.urlExtensions;
    }

    public void setUrlExtensions(List<UrlExtension> urlExtensions) {
        this.urlExtensions = urlExtensions;
    }
}
