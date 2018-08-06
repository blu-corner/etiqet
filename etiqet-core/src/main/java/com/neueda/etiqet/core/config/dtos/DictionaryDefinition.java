package com.neueda.etiqet.core.config.dtos;

import com.neueda.etiqet.core.common.EtiqetConstants;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * XPath: /etiqetConfiguration/clients/client/dictionaryDefinition
 */
@XmlRootElement(name = "dictionaryDefinition", namespace = EtiqetConstants.NAMESPACE)
public class DictionaryDefinition implements Serializable {

    private String path;

    @XmlAttribute(name = "path")
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
