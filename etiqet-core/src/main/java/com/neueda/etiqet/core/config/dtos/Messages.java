package com.neueda.etiqet.core.config.dtos;

import com.neueda.etiqet.core.common.EtiqetConstants;
import com.neueda.etiqet.core.config.xml.MessagesAdapter;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@XmlJavaTypeAdapter(MessagesAdapter.class)
@XmlRootElement(namespace = EtiqetConstants.NAMESPACE)
public class Messages implements Serializable {

    private String reference;

	private Message[] message;

	@XmlAttribute(name = "ref")
    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

	@XmlElement(name = "message", namespace = EtiqetConstants.NAMESPACE)
	public Message[] getMessage() {
		return message;
	}

	public void setMessage(Message[] message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "ClassPojo [message = " + Arrays.toString(message) + "]";
	}

    @Override
    public boolean equals(Object obj) {
	    if(!(obj instanceof Messages)) {
	        return false;
        }

        Messages other = (Messages) obj;
        List<Message> messages = Arrays.asList(this.message);
        for(Message otherMsg : other.getMessage()) {
            if(!messages.contains(otherMsg)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash((Object[]) message);
    }
}
