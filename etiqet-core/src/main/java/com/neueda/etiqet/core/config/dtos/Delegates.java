package com.neueda.etiqet.core.config.dtos;

import com.neueda.etiqet.core.common.EtiqetConstants;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement(namespace = EtiqetConstants.NAMESPACE)
public class Delegates implements Serializable {
	private Delegate[] delegate;

	@XmlElement(name = "delegate", namespace = EtiqetConstants.NAMESPACE)
	public Delegate[] getDelegate() {
		return delegate;
	}

	public void setDelegate(Delegate[] delegates) {
		this.delegate = delegates;
	}

	@Override
	public String toString() {
		return "Delegates [delegates = " + delegate + "]";
	}
}
