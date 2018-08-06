package com.neueda.etiqet.core.config.dtos;

import com.neueda.etiqet.core.common.EtiqetConstants;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * XPath: /etiqetConfiguration
 */
@XmlRootElement(name = "etiqetConfiguration", namespace = EtiqetConstants.NAMESPACE)
public class EtiqetConfiguration implements Serializable {

    private List<Protocol> protocols;

    private List<ClientImpl> clients = new ArrayList<>();

    private List<Server> servers = new ArrayList<>();

    /**
     * Gets all defined protocols in the configuration.
     *
     * XPath: /etiqetConfiguration/protocols
     *
     * @return list of protocol DTOs to be prepared for use
     */
    @XmlElementWrapper(name = "protocols", namespace = EtiqetConstants.NAMESPACE)
    @XmlElement(name = "protocol", namespace = EtiqetConstants.NAMESPACE)
    public List<Protocol> getProtocols() {
        return protocols;
    }

    /**
     * Sets the protocols in the configuration
     * @param protocols list of protocol DTOs to be prepared for use
     */
    public void setProtocols(List<Protocol> protocols) {
        this.protocols = protocols;
    }

    /**
     * Gets all clients pre-defined for use in test steps
     *
     * XPath: /etiqetConfiguration/clients
     *
     * @return all client names and implementations
     */
    @XmlElementWrapper(name = "clients", namespace = EtiqetConstants.NAMESPACE, required = false)
    @XmlElement(name = "client", namespace = EtiqetConstants.NAMESPACE, required = false)
    public List<ClientImpl> getClients() {
        return clients;
    }

    /**
     * Sets pre-defined clients for use in test steps
     * @param clients list of client names / implementations
     */
    public void setClients(List<ClientImpl> clients) {
        this.clients = clients;
    }

    /**
     * Gets all servers pre-defined for use in test steps
     *
     * XPath: /etiqetConfiguration/servers
     *
     * @return all server names, implementations and configurations
     */
    @XmlElementWrapper(name = "servers", namespace = EtiqetConstants.NAMESPACE, required = false)
    @XmlElement(name = "server", namespace = EtiqetConstants.NAMESPACE, required = false)
    public List<Server> getServers() {
        return servers;
    }

    /**
     * Sets pre-defined servers to be used in test steps
     * @param servers list of server names, implementations and configurations
     */
    public void setServers(List<Server> servers) {
        this.servers = servers;
    }
}
