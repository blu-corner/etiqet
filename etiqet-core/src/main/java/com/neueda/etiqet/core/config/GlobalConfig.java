package com.neueda.etiqet.core.config;

import com.neueda.etiqet.core.client.Client;
import com.neueda.etiqet.core.client.ClientFactory;
import com.neueda.etiqet.core.common.Environment;
import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import com.neueda.etiqet.core.config.dtos.ClientConfig;
import com.neueda.etiqet.core.config.dtos.ClientImpl;
import com.neueda.etiqet.core.config.dtos.EtiqetConfiguration;
import com.neueda.etiqet.core.config.dtos.Protocol;
import com.neueda.etiqet.core.config.xml.XmlParser;
import com.neueda.etiqet.core.message.config.ProtocolConfig;
import com.neueda.etiqet.core.server.Server;
import com.neueda.etiqet.core.server.ServerFactory;
import com.neueda.etiqet.core.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static com.neueda.etiqet.core.common.ConfigConstants.DEFAULT_CONFIG_VARIABLE;

public class GlobalConfig {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalConfig.class);

    static GlobalConfig instance;

    private EtiqetConfiguration config;

    private final String configPath;

    private Map<String, ProtocolConfig> protocols = new HashMap<>();

    private Map<String, Client> clients = new HashMap<>();

    private Map<String, Server> servers = new HashMap<>();

    public static GlobalConfig getInstance() throws EtiqetException {
        if(!Environment.isEnvVarSet(DEFAULT_CONFIG_VARIABLE)) {
            throw new EtiqetException("Could not find system property " + DEFAULT_CONFIG_VARIABLE + " to create global"
                                        + " configuration with");
        }
        if(instance == null) {
            if(LOG.isDebugEnabled()) {
                LOG.debug(String.format("Creating default GlobalConfig from file %s",
                            System.getProperty(DEFAULT_CONFIG_VARIABLE)));
            }
            instance = new GlobalConfig(Environment.resolveEnvVars(System.getProperty(DEFAULT_CONFIG_VARIABLE)));
        }
        return instance;
    }

    private GlobalConfig(final String path) throws EtiqetException {
        configPath = path;
        try {
            setConfig(new XmlParser().parse(getConfigPath(), EtiqetConfiguration.class));

            for(Protocol protocol : config.getProtocols()) {
                protocols.put(protocol.getName(), new ProtocolConfig(protocol));
            }

            for (ClientImpl clientImpl : config.getClients()) {
                String impl = clientImpl.getImpl();
                ProtocolConfig protocolConfig = protocols.get(impl);
                String clientClass = protocolConfig.getClient().getImpl();

                ClientConfig secondaryConfig = clientImpl.getSecondaryConfig();
                Client client = ClientFactory.create(
                    clientClass,
                    clientImpl.getPrimaryConfig().getConfigPath(),
                    secondaryConfig != null ? secondaryConfig.getConfigPath() : null,
                    protocolConfig
                );

                if(!StringUtils.isNullOrEmpty(clientImpl.getExtensionsUrl())) {
                    client.setExtensionsUrl(clientImpl.getExtensionsUrl());
                }

                clients.put(clientImpl.getName(), client);
            }

            for(com.neueda.etiqet.core.config.dtos.Server serverDetail : config.getServers()) {
                String serverImpl = serverDetail.getImpl();
                Server server = ServerFactory.create(serverImpl, serverDetail.getConfigPath());
                servers.put(serverDetail.getName(), server);
            }
        } catch (Exception e) {
            String msg = "Error reading global configuration file " + getConfigPath();
            LOG.error(msg, e);
            throw new EtiqetException(msg, e);
        }
    }

    public ProtocolConfig getProtocol(String name) {
        return protocols.get(name);
    }

    public Map<String, ProtocolConfig> getProtocols() {
        return protocols;
    }

    public Client getClient(String name) {
        return clients.get(name);
    }

    public Map<String, Client> getClients() {
        return clients;
    }

    public Server getServer(String name) {
        return servers.get(name);
    }

    public Map<String, Server> getServers() {
        return servers;
    }

    public EtiqetConfiguration getConfig() {
        return config;
    }

    public void setConfig(EtiqetConfiguration config) {
        this.config = config;
    }

    public String getConfigPath() {
        return configPath;
    }
}
