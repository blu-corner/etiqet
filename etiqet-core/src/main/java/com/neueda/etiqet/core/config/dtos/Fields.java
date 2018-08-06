package com.neueda.etiqet.core.config.dtos;

import com.neueda.etiqet.core.common.EtiqetConstants;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@XmlRootElement(namespace = EtiqetConstants.NAMESPACE)
public class Fields implements Serializable {
	private Field[] field;

	@XmlElement(name = "field", namespace = EtiqetConstants.NAMESPACE)
	public Field[] getField() {
		return field;
	}

	public void setField(Field[] field) {
		this.field = field;
	}

	@Override
	public String toString() {
		return "Fields [field = " + Arrays.toString(field) + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Fields)) {
			return false;
		}

		List<Field> fieldList = Arrays.asList(this.field);
		for(Field otherField : ((Fields) obj).field) {
			if(!fieldList.contains(otherField)) {
				return false;
			}
		}

		return true;
	}

	@Override
    public int hashCode() {
	    return Objects.hash((Object[]) field);
    }

}
