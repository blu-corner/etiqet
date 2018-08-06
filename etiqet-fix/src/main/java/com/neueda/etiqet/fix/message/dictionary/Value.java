package com.neueda.etiqet.fix.message.dictionary;

import javax.xml.bind.annotation.XmlAttribute;

public class Value {
	
	private String enume;

	private String description;	
	

	@XmlAttribute(name = "enum")
	public String getEnume() {
		return enume;
	}

	public void setEnume(String enume) {
		this.enume = enume;
	}

	@XmlAttribute
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "Field [enum = " + enume + ", description = " + description + "]";
	}
}
