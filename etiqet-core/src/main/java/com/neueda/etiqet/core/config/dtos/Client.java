package com.neueda.etiqet.core.config.dtos;

import com.neueda.etiqet.core.common.EtiqetConstants;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.List;

/**
 * XPath element: /etiqetConfiguration/protocols/protocol/client
 */
@XmlRootElement(namespace = EtiqetConstants.NAMESPACE)
public class Client implements Serializable {

	private String impl;

	private String defaultConfig;

	private List<UrlExtension> urlExtensions;

	private Delegates delegates;

	private StopEvent stopEvent;

	@XmlElement(name = "delegates", namespace = EtiqetConstants.NAMESPACE)
	public Delegates getDelegates() {
		return delegates;
	}

	public void setDelegates(Delegates delegates) {
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
