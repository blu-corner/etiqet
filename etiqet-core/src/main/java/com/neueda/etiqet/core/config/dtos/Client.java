package com.neueda.etiqet.core.config.dtos;

import com.neueda.etiqet.core.common.EtiqetConstants;

import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * XPath element: /etiqetConfiguration/protocols/protocol/client
 */
@XmlRootElement(namespace = EtiqetConstants.NAMESPACE)
public class Client implements Serializable {

	private String impl;

    private Class<?> implementationClass;

	private String defaultConfig;

    private List<UrlExtension> urlExtensions = new ArrayList<>();

	private Delegates delegates;

	private StopEvent stopEvent;

	@XmlElement(name = "delegates", namespace = EtiqetConstants.NAMESPACE)
	public Delegates getDelegates() {
		return delegates;
	}

	public void setDelegates(Delegates delegates) {
		this.delegates = delegates;
	}

    @XmlTransient
    public List<Delegate> getDelegateList() {
        return Arrays.asList(delegates.getDelegate());
    }

    public void setDelegates(List<Delegate> delegates) {
        this.delegates = new Delegates();
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
