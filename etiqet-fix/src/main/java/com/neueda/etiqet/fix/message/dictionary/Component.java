package com.neueda.etiqet.fix.message.dictionary;

import javax.xml.bind.annotation.XmlAttribute;

public class Component {
	private String name;

	private String required;

	@XmlAttribute
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlAttribute
	public String getRequired() {
		return required;
	}

	public void setRequired(String required) {
		this.required = required;
	}

	@Override
	public String toString() {
		return "Component [name = " + name + ", required = " + required + "]";
	}
}
