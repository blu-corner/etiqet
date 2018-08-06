package com.neueda.etiqet.core.config.dtos;

import javax.xml.bind.annotation.XmlAttribute;
import java.io.Serializable;

public class ClientConfig implements Serializable {

    private String configPath;

    @XmlAttribute(name = "configPath")
    public String getConfigPath() {
        return configPath;
    }

    public void setConfigPath(String configPath) {
        this.configPath = configPath;
    }
}
