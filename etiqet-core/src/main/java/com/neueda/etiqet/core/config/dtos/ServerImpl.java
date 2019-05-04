package com.neueda.etiqet.core.config.dtos;

import com.neueda.etiqet.core.common.EtiqetConstants;
import java.io.Serializable;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * XPath: /etiqetConfiguration/servers/server
 */
@XmlRootElement(name = "server", namespace = EtiqetConstants.NAMESPACE)
public class ServerImpl implements Serializable {

    private String name;

    private String impl;

    private Class<? extends com.neueda.etiqet.core.server.Server> implementationClass;

    private String configPath;

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

    @XmlTransient
    public Class<? extends com.neueda.etiqet.core.server.Server> getImplementationClass() {
        return this.implementationClass;
    }

    public void setImplementationClass(Class<? extends com.neueda.etiqet.core.server.Server> implementationClass) {
        this.impl = implementationClass.getName();
        this.implementationClass = implementationClass;
    }

    @XmlAttribute(name = "config", required = true)
    public String getConfigPath() {
        return configPath;
    }

    public void setConfigPath(String configPath) {
        this.configPath = configPath;
    }
}
