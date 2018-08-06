package com.neueda.etiqet.core.config.dtos;

import com.neueda.etiqet.core.common.EtiqetConstants;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * XPath element: /etiqetConfiguration/protocols/protocol/client
 */
@XmlRootElement(namespace = EtiqetConstants.NAMESPACE)
public class Client implements Serializable {

	private String impl;

	private String defaultConfig;

	private String extensionsUrl;

	private Delegates delegates;

	@XmlElement(name = "delegates", namespace = EtiqetConstants.NAMESPACE)
	public Delegates getDelegates() {
		return delegates;
	}

	public void setDelegates(Delegates delegates) {
		this.delegates = delegates;
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

	@XmlAttribute
	public String getExtensionsUrl() {
		return extensionsUrl;
	}

	public void setExtensionsUrl(String extensionsUrl) {
		this.extensionsUrl = extensionsUrl;
	}
}
