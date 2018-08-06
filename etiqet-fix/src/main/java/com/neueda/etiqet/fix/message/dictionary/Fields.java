package com.neueda.etiqet.fix.message.dictionary;

public class Fields {
	private Field[] field;

	public Field[] getField() {
		return field;
	}

	public void setField(Field[] field) {
		this.field = field;
	}

	@Override
	public String toString() {
		return "Fields [field = " + field + "]";
	}
}
