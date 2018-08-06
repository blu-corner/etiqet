package com.neueda.etiqet.fix.message.dictionary;

public class Trailer {
	private Field[] field;

	public Field[] getField() {
		return field;
	}

	public void setField(Field[] field) {
		this.field = field;
	}

	@Override
	public String toString() {
		return "Trailer [field = " + field + "]";
	}
}
