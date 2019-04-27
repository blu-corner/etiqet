package com.neueda.etiqet.core.config.dtos;

import com.neueda.etiqet.core.common.EtiqetConstants;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = {"implementation", "headers", "fields"}, namespace = EtiqetConstants.NAMESPACE)
@XmlRootElement(namespace = EtiqetConstants.NAMESPACE)
public class Message implements Serializable {

    private String admin;

    private String implementation;
    private Class<?> implementationClass;

    private String name;

    private String msgtype;

    private List<Field> headers = new ArrayList<>();

    private List<Field> fields = new ArrayList<>();

    public Message() {
    }

    public Message(String name, String implementation) {
        this.name = name;
        this.implementation = implementation;
    }

    public Message(String name, Class<?> implementation) {
        this.name = name;
        this.implementation = implementation.getName();
        this.implementationClass = implementation;
    }

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

    public void setImplementation(Class<?> implementationClass) {
        this.implementation = implementationClass.getName();
        this.implementationClass = implementationClass;
    }

    @XmlTransient
    public Class<?> getImplementationClass() {
        return this.implementationClass;
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

    @XmlElementWrapper(name = "headers", namespace = EtiqetConstants.NAMESPACE)
    @XmlElement(name = "field", namespace = EtiqetConstants.NAMESPACE)
    public List<Field> getHeaders() {
        return headers;
    }

    public void setHeaders(List<Field> headers) {
        this.headers = headers;
    }

    @XmlElementWrapper(name = "fields", namespace = EtiqetConstants.NAMESPACE)
    @XmlElement(name = "field", namespace = EtiqetConstants.NAMESPACE)
    public List<Field> getFields() {
        return fields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        out.append("Message [admin = ")
            .append(admin)
            .append(", implementation = ")
            .append(implementation)
            .append(", name = ")
            .append(name)
            .append(", msgtype = ")
            .append(msgtype)
            .append(", fields = {");
        for (Field field : getFields()) {
            out.append("\r\n").append(field.toString());
        }
        out.append("}");
        out.append("]");
        return out.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Message)) {
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
