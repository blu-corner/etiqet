package com.neueda.etiqet.core.config.dtos;

import com.neueda.etiqet.core.common.EtiqetConstants;
import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import com.neueda.etiqet.core.config.xml.ProtocolAdapter;
import com.neueda.etiqet.core.config.xml.XmlParser;
import com.neueda.etiqet.core.message.config.AbstractDictionary;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * XPath: /protocol or /etiqetConfiguration/protocols/protocol
 */
@XmlRootElement(namespace = EtiqetConstants.NAMESPACE)
@XmlJavaTypeAdapter(ProtocolAdapter.class)
public class Protocol implements Serializable {

    private String name;

    private String ref;

    private Client client;

    private Dictionary dictionary;

    private String componentsPackage;

    private Messages messages;
    private String messagesFile;

    @XmlAttribute
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlAttribute(name = "ref")
    public String getReference() {
        return ref;
    }

    public void setReference(String reference) {
        this.ref = reference;
    }

    @XmlElement(name = "dictionary", namespace = EtiqetConstants.NAMESPACE)
    public Dictionary getDictionary() {
        return dictionary;
    }

    public void setDictionary(Dictionary dictionary) {
        this.dictionary = dictionary;
    }

    public void setDictionary(Class<? extends AbstractDictionary> dictionaryClass) {
        this.dictionary = new Dictionary(dictionaryClass);
    }

    @XmlElement(name = "components_package", namespace = EtiqetConstants.NAMESPACE)
    public String getComponentsPackage() {
        return componentsPackage;
    }

    public void setComponentsPackage(String componentsPackage) {
        this.componentsPackage = componentsPackage;
    }

    @XmlElement(name = "messages", namespace = EtiqetConstants.NAMESPACE)
    public Messages getMessages() {
        return messages;
    }

    public void setMessages(Messages messages) {
        this.messages = messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = new Messages(messages);
    }

    public void setMessages(String messagesFile) throws EtiqetException {
        this.messages = new XmlParser().parse(messagesFile, Messages.class);
        this.messagesFile = messagesFile;
    }

    @XmlTransient
    public List<Message> getMessageList() {
        return Arrays.asList(messages.getMessage());
    }

    @XmlElement(name = "client", namespace = EtiqetConstants.NAMESPACE)
    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        out.append("Protocol [name = " + name + ", messages = ");

        if (messages != null && messages.getMessage() != null && messages.getMessage().length > 0) {
            for (Message message : messages.getMessage()) {
                out.append("\r\n" + message);
            }
        }
        out.append("]");
        return out.toString();

    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Protocol)) {
            return false;
        }
        Protocol other = (Protocol) obj;
        return other.client.equals(this.client)
            && other.componentsPackage.equals(this.componentsPackage)
            && other.dictionary.equals(this.dictionary)
            && other.messages.equals(this.messages)
            && other.name.equals(this.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(client, componentsPackage, dictionary, messages, name);
    }
}
