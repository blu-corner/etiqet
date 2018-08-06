package com.neueda.etiqet.core.config.dtos;

import com.neueda.etiqet.core.common.EtiqetConstants;
import com.neueda.etiqet.core.util.StringUtils;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;
import java.io.Serializable;
import java.util.Objects;

@XmlRootElement(namespace = EtiqetConstants.NAMESPACE)
public class Field implements Serializable {
	private String name;

	private String utilclass;

	private String method;

	private String type;
	
	private String value;

    private String required;

    private String allowedValues;

	@XmlAttribute
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlAttribute
	public String getUtilclass() {
		return utilclass;
	}

	public void setUtilclass(String utilclass) {
		this.utilclass = utilclass;
	}

	@XmlAttribute
	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	@XmlAttribute
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@XmlValue
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@XmlAttribute
	public String getAllowedValues() {
		return allowedValues;
	}

	public void setAllowedValues(String allowedValues) {
		this.allowedValues = allowedValues;
	}

	@XmlAttribute
	public String getRequired() {
		return required;
	}

	public void setRequired(String required) {
		this.required = required;
	}

	public boolean hasStaticMethod() {
	    return !StringUtils.isNullOrEmpty(utilclass) && !StringUtils.isNullOrEmpty(method);
    }

	@Override
    public String toString()
    {
        return "Field [name = "+name+", utilclass = "+utilclass+", method = "+method+", type = "+type+", value= " + value+ ", required= " + required+ ", allowedValues= " + allowedValues+ "]";
    }

	@Override
	public boolean equals(Object obj) {
		return obj instanceof Field
                && obj.toString().equals(this.toString());
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, utilclass, method, type, value, required, allowedValues);
	}
}
