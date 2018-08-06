package com.neueda.etiqet.core.config.dtos;

import com.neueda.etiqet.core.common.EtiqetConstants;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Objects;

@XmlRootElement(namespace = EtiqetConstants.NAMESPACE)
public class Message implements Serializable {
	private String admin;
	
	private String implementation;

	private String name;
	
	private String msgtype;
	
	private Fields fields;

	@XmlAttribute
	public String getAdmin() {
		return admin;
	}

	public void setAdmin(String admin) {
		this.admin = admin;
	}

	@XmlElement(name = "implementation", namespace = EtiqetConstants.NAMESPACE)
	public String getImplementation() {
		return implementation;
	}

	public void setImplementation(String implementation) {
		this.implementation = implementation;
	}

	@XmlAttribute
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}	

	@XmlAttribute
	public String getMsgtype() {
		return msgtype;
	}

	public void setMsgtype(String msgtype) {
		this.msgtype = msgtype;
	}

	@XmlElement(name = "fields", namespace = EtiqetConstants.NAMESPACE)
	public Fields getFields() {
		return fields;
	}

	public void setFields(Fields fields) {
		this.fields = fields;
	}

	@Override
	public String toString() {
		StringBuilder out = new StringBuilder();
		out.append( "Message [admin = " + admin + ", implementation = " + implementation
				+ ", name = " + name + ", msgtype = " + msgtype + ", fields = {");
		if (getFields() != null && getFields().getField() != null && getFields().getField().length > 0) {
			for (Field field: getFields().getField()) {			
				out.append( "\r\n" + field.toString());
			}
		}
		out.append("}");
		out.append("]");
		return out.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Message)) {
			return false;
		}

		Message otherMsg = (Message) obj;

		return otherMsg.admin.equals(this.admin)
				&& otherMsg.implementation.equals(this.implementation)
				&& otherMsg.msgtype.equals(this.msgtype)
				&& otherMsg.name.equals(this.name)
				&& otherMsg.fields.equals(this.fields);
	}

	@Override
	public int hashCode() {
		return Objects.hash(admin, implementation, msgtype, name, fields);
	}

}
