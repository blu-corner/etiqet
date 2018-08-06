package com.neueda.etiqet.core.config.dtos;

import com.neueda.etiqet.core.common.EtiqetConstants;
import com.neueda.etiqet.core.config.xml.ProtocolAdapter;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.util.Objects;

/**
 * XPath: /protocol or /etiqetConfiguration/protocols/protocol
 */
@XmlRootElement(namespace = EtiqetConstants.NAMESPACE)
@XmlJavaTypeAdapter(ProtocolAdapter.class)
public class Protocol implements Serializable {

	private String name;

	private String ref;

	private String messageClass;

	private Client client;

	private Dictionary dictionary;

	private String componentsPackage;

	private Messages messages;

	@XmlAttribute
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }

	@XmlAttribute(name = "ref")
	public String getReference() { return ref; }
	public void setReference(String reference) { this.ref = reference; }

	@XmlElement(name = "dictionary", namespace = EtiqetConstants.NAMESPACE)
	public Dictionary getDictionary() { return dictionary; }
	public void setDictionary(Dictionary dictionary) { this.dictionary = dictionary; }

	@XmlElement(name = "components_package", namespace = EtiqetConstants.NAMESPACE)
	public String getComponentsPackage() { return componentsPackage; }
	public void setComponentsPackage(String componentsPackage) { this.componentsPackage = componentsPackage; }

	@XmlElement(name = "messages", namespace = EtiqetConstants.NAMESPACE)
	public Messages getMessages() { return messages; }
	public void setMessages(Messages messages) { this.messages = messages; }

	@XmlElement(name = "messageClass", namespace = EtiqetConstants.NAMESPACE)
	public String getMessageClass() { return messageClass; }
	public void setMessageClass(String messageClass) { this.messageClass = messageClass; }

	@XmlElement(name = "client", namespace = EtiqetConstants.NAMESPACE)
	public Client getClient() {return client;}
	public void setClient(Client client) {this.client = client;}

	@Override
	public String toString() {
		StringBuilder out = new StringBuilder();
		out.append("Protocol [name = " + name + ", com.neueda.etiqet.quickfix.dictionary = " + dictionary.toString() + ", components_package = " + componentsPackage + ", messages = ");

		if (messages != null && messages.getMessage() != null && messages.getMessage().length > 0) {
			for (Message message: messages.getMessage()) {
				out.append("\r\n" + message);
			}
		}
		out.append("]");
		return out.toString();

	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Protocol)) {
			return false;
		}
		Protocol other = (Protocol) obj;
		return other.client.equals(this.client)
				&& other.componentsPackage.equals(this.componentsPackage)
				&& other.dictionary.equals(this.dictionary)
				&& other.messageClass.equals(this.messageClass)
				&& other.messages.equals(this.messages)
				&& other.name.equals(this.name);
	}

    @Override
    public int hashCode() {
        return Objects.hash(client, componentsPackage, dictionary, messageClass, messages, name);
    }
}
