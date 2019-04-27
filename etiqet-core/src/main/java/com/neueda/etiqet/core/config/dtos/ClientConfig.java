package com.neueda.etiqet.core.config.dtos;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAttribute;

public class ClientConfig implements Serializable {

    private String configPath;

    public ClientConfig() {
    }

    public ClientConfig(String configPath) {
        this.configPath = configPath;
    }

    @XmlAttribute(name = "configPath")
    public String getConfigPath() {
        return configPath;
    }

    public void setConfigPath(String configPath) {
        this.configPath = configPath;
    }
}
