package com.neueda.etiqet.fix.message.dictionary;

import javax.xml.bind.annotation.XmlAttribute;

public class Field {
	
	private Integer number;
	
	private String name;

	private String required;
	
	private Value[] value;

        private String allowedValues;

	@XmlAttribute
	public Integer getNumber() {
		return number;
	}
	
	public void setNumber(Integer number) {
		this.number = number;
	}

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
	
        @XmlAttribute
	public String getAllowedValues() {
		return allowedValues;
	}

	public void setAllowedValues(String allowedValues) {
		this.allowedValues = allowedValues;
	}

	public Value[] getValue() {
		return value;
	}

	public void setValue(Value[] value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "Field [name = " + name + ", required = " + required + "]";
	}
}
