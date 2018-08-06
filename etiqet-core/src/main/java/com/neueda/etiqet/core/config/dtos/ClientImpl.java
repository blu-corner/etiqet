package com.neueda.etiqet.core.config.dtos;

import com.neueda.etiqet.core.common.EtiqetConstants;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * XPath element: /etiqetConfiguration/clients/client
 */
@XmlRootElement(name = "client", namespace = EtiqetConstants.NAMESPACE)
public class ClientImpl implements Serializable {

    private String name;

    private String impl;

    private DictionaryDefinition dictionaryDefinition;

    private ClientConfig primaryConfig;

    private ClientConfig secondaryConfig;

    private String extensionsUrl;

    @XmlAttribute(required = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlAttribute(required = true)
    public String getImpl() {
        return impl;
    }

    public void setImpl(String impl) {
        this.impl = impl;
    }

    @XmlElement(name = "dictionaryDefinition", namespace = EtiqetConstants.NAMESPACE)
    public DictionaryDefinition getDictionaryDefinition() {
        return dictionaryDefinition;
    }

    public void setDictionaryDefinition(DictionaryDefinition dictionaryDefinition) {
        this.dictionaryDefinition = dictionaryDefinition;
    }

    @XmlElement(name = "primary", namespace = EtiqetConstants.NAMESPACE, required = true)
    public ClientConfig getPrimaryConfig() {
        return primaryConfig;
    }

    public void setPrimaryConfig(ClientConfig primaryConfig) {
        this.primaryConfig = primaryConfig;
    }

    @XmlElement(name = "secondary", namespace = EtiqetConstants.NAMESPACE)
    public ClientConfig getSecondaryConfig() {
        return secondaryConfig;
    }

    public void setSecondaryConfig(ClientConfig secondaryConfig) {
        this.secondaryConfig = secondaryConfig;
    }

    @XmlAttribute(name = "extensionsUrl")
    public String getExtensionsUrl() {
        return extensionsUrl;
    }

    public void setExtensionsUrl(String extensionsUrl) {
        this.extensionsUrl = extensionsUrl;
    }
}
