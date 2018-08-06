package com.neueda.etiqet.fix.message.dictionary;

import javax.xml.bind.annotation.XmlAttribute;

public class Group {
	private Field[] field;

	private String name;

	private String required;

	public Field[] getField() {
		return field;
	}

	public void setField(Field[] field) {
		this.field = field;
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

	@Override
	public String toString() {
		return "Group [field = " + field + ", name = " + name + ", required = " + required + "]";
	}
}
