package com.neueda.etiqet.core.config;

import static com.neueda.etiqet.core.common.ConfigConstants.DEFAULT_CONFIG_VARIABLE;

import com.neueda.etiqet.core.client.Client;
import com.neueda.etiqet.core.client.ClientFactory;
import com.neueda.etiqet.core.common.ConfigConstants;
import com.neueda.etiqet.core.common.Environment;
import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import com.neueda.etiqet.core.config.annotations.Configuration;
import com.neueda.etiqet.core.config.annotations.EtiqetProtocol;
import com.neueda.etiqet.core.config.annotations.NamedClient;
import com.neueda.etiqet.core.config.annotations.NamedServer;
import com.neueda.etiqet.core.config.dtos.ClientConfig;
import com.neueda.etiqet.core.config.dtos.ClientImpl;
import com.neueda.etiqet.core.config.dtos.Dictionary;
import com.neueda.etiqet.core.config.dtos.EtiqetConfiguration;
import com.neueda.etiqet.core.config.dtos.Message;
import com.neueda.etiqet.core.config.dtos.Messages;
import com.neueda.etiqet.core.config.dtos.Protocol;
import com.neueda.etiqet.core.config.dtos.ServerImpl;
import com.neueda.etiqet.core.config.xml.XmlParser;
import com.neueda.etiqet.core.message.config.ProtocolConfig;
import com.neueda.etiqet.core.server.Server;
import com.neueda.etiqet.core.server.ServerFactory;
import com.neueda.etiqet.core.util.StringUtils;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Singleton instance of the Etiqet global configuration. Once initialised by one of the {@link #getInstance()}, {@link
 * #getInstance(String)}, or {@link #getInstance(Class)} methods, can be called from various points within Etiqet simply
 * using the no-argument {@linkplain #getInstance()}
 */
public class GlobalConfig {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalConfig.class);

    static GlobalConfig instance;

    private EtiqetConfiguration config;

    private String configPath;
    private Class<?> configClass;

    private Map<String, ProtocolConfig> protocols = new HashMap<>();

    private Map<String, Client> clients = new HashMap<>();

    private Map<String, Server> servers = new HashMap<>();

    private GlobalConfig(final String path) throws EtiqetException {
        configPath = path;
        try {
            setConfig(new XmlParser().parse(path, EtiqetConfiguration.class));

            for (Protocol protocol : config.getProtocols()) {
                addProtocol(protocol);
            }

            for (ClientImpl clientImpl : config.getClients()) {
                addClient(clientImpl);
            }

            for (ServerImpl serverDetail : config.getServers()) {
                addServer(serverDetail);
            }
        } catch (Exception e) {
            String msg = "Error reading global configuration file " + getConfigPath();
            LOG.error(msg, e);
            throw new EtiqetException(msg, e);
        }
    }

    private GlobalConfig(final Class<?> config) throws EtiqetException {
        this.configClass = config;
        if (!config.isAnnotationPresent(Configuration.class)) {
            throw new EtiqetException("Could not initialise Etiqet Configuration from class " + config.getName());
        }

        try {
            this.config = new EtiqetConfiguration();
            // Process the protocols first, then the clients, then the servers
            Method[] methods = configClass.getMethods();
            if (Arrays.stream(methods).noneMatch(m -> m.isAnnotationPresent(EtiqetProtocol.class))) {
                throw new EtiqetException("Could not find any protocols defined in " + configClass.getName());
            }

            for (Method method : methods) {
                if (method.isAnnotationPresent(EtiqetProtocol.class)) {
                    Protocol protocol = getProtocol(method);
                    addProtocol(protocol);
                    this.config.getProtocols().add(protocol);
                }
            }

            for (Method method : methods) {
                if (method.isAnnotationPresent(NamedClient.class)) {
                    ClientImpl clientImpl = getClientImpl(method);
                    addClient(clientImpl);
                    this.config.getClients().add(clientImpl);
                }
            }
            for (Method method : methods) {
                if (method.isAnnotationPresent(NamedServer.class)) {
                    ServerImpl server = getServer(method);
                    addServer(server);
                    this.config.getServers().add(server);
                }
            }

            new XmlParser().validate(this.config, configClass);
        } catch (EtiqetException e) {
            LOG.error("EtiqetException was thrown during setup of configuration class {}", config, e);
            throw e;
        } catch (Exception e) {
            throw new EtiqetException("Unexpected exception occurred while configuring Etiqet.", e);
        }
    }

    /**
     * <p>Gets the GlobalConfig for Etiqet.</p>
     *
     * <p>If not instantiated already, this will instantiate global config by looking for
     * a file specified under the system property {@value ConfigConstants#DEFAULT_CONFIG_VARIABLE}</p>
     *
     * @return instance of GlobalConfig
     * @throws EtiqetException when the GlobalConfig could not be instantiated via the file specified via the system
     * property {@value ConfigConstants#DEFAULT_CONFIG_VARIABLE}
     */
    public static GlobalConfig getInstance() throws EtiqetException {
        if (instance == null) {
            if (!Environment.isEnvVarSet(DEFAULT_CONFIG_VARIABLE)) {
                throw new EtiqetException(
                    "Could not find system property " + DEFAULT_CONFIG_VARIABLE + " to create global"
                        + " configuration with");
            }
            LOG.debug("Creating default GlobalConfig from file {}", System.getProperty(DEFAULT_CONFIG_VARIABLE));
            instance = new GlobalConfig(Environment.resolveEnvVars(System.getProperty(DEFAULT_CONFIG_VARIABLE)));
        }
        return instance;
    }

    /**
     * <p>Gets the GlobalConfig for Etiqet.</p>
     *
     * <p>If not instantiated already, this will instantiate global config by parsing the <code>configPath</code>
     * specified</p>
     *
     * @param configPath path to an Etiqet configuration file
     * @return instance of GlobalConfig
     * @throws EtiqetException when the GlobalConfig could not be instantiated via the file specified
     */
    public static GlobalConfig getInstance(String configPath) throws EtiqetException {
        if (instance == null) {
            LOG.debug("Creating default GlobalConfig from file {}", configPath);
            instance = new GlobalConfig(Environment.resolveEnvVars(configPath));
        }
        return instance;
    }

    /**
     * <p>Gets the GlobalConfig for Etiqet.</p>
     *
     * <p>If not instantiated already, this will instantiate global config by reading methods in the class that use the
     * {@link EtiqetProtocol}, {@link NamedClient}, and {@link NamedServer} annotations.</p>
     *
     * <p>The <code>configurationClass</code> provided must be annotated with {@link Configuration}, and must provide
     * at least one protocol. Once the configuration class has been processed, it is validated against the Etiqet XML
     * schema to ensure that all required fields are available</p>
     *
     * @param configurationClass class that configures Etiqet
     * @return instance of GlobalConfig
     * @throws EtiqetException when the GlobalConfig could not be instantiated via the file specified via the system
     * property {@value ConfigConstants#DEFAULT_CONFIG_VARIABLE}
     */
    public static GlobalConfig getInstance(Class<?> configurationClass) throws EtiqetException {
        if (instance == null) {
            LOG.debug("Creating default GlobalConfig from file {}", configurationClass);
            instance = new GlobalConfig(configurationClass);
        }
        return instance;
    }

    private ServerImpl getServer(Method method) throws EtiqetException {
        NamedServer serverAnnotation = method.getAnnotation(NamedServer.class);
        String serverName = serverAnnotation.name();
        Class<? extends Server> serverImpl = serverAnnotation.impl();
        String serverConfig = serverAnnotation.config();

        if (!method.getReturnType().equals(ServerImpl.class)) {
            String error = String
                .format("NamedServer %s does not return type com.neueda.etiqet.core.config.dtos.ServerImpl",
                    serverName);
            throw new EtiqetException(error);
        }

        ServerImpl server;
        try {
            server = (ServerImpl) method.invoke(configClass.newInstance());
        } catch (Exception e) {
            throw new EtiqetException(String.format("Error creating NamedServer %s", serverName), e);
        }

        server.setName(serverName);
        server.setImplementationClass(serverImpl);
        server.setConfigPath(serverConfig);
        return server;
    }

    private ClientImpl getClientImpl(Method method) throws EtiqetException {
        NamedClient clientAnnotation = method.getAnnotation(NamedClient.class);
        String clientName = clientAnnotation.name();
        if (!method.getReturnType().equals(ClientImpl.class)) {
            String error = String.format("Client %s does not return type com.neueda.etiqet.core.config.dtos.ClientImpl",
                clientName);
            throw new EtiqetException(error);
        }

        if (!protocols.containsKey(clientAnnotation.impl())) {
            String error = String
                .format("Client %s references unknown protocol %s", clientName, clientAnnotation.impl());
            throw new EtiqetException(error);
        }

        ClientImpl client;
        try {
            client = (ClientImpl) method.invoke(configClass.newInstance());
        } catch (Exception e) {
            throw new EtiqetException(String.format("Error creating Client %s", clientName), e);
        }
        if (client.getPrimaryConfig() == null || StringUtils.isNullOrEmpty(client.getPrimaryConfig().getConfigPath())) {
            throw new EtiqetException(
                String.format("Client %s does not have a primary configuration file defined", clientName)
            );
        }

        client.setName(clientName);
        client.setImpl(clientAnnotation.impl());
        return client;
    }

    private Protocol getProtocol(Method method) throws EtiqetException {
        EtiqetProtocol protocolAnnotation = method.getAnnotation(EtiqetProtocol.class);
        String protocolName = protocolAnnotation.value();
        if (!method.getReturnType().equals(Protocol.class)) {
            String error = String.format("Protocol %s does not return type com.neueda.etiqet.core.config.dtos.Protocol",
                protocolName);
            throw new EtiqetException(error);
        }

        Protocol protocol;
        try {
            protocol = (Protocol) method.invoke(configClass.newInstance());
        } catch (Exception e) {
            throw new EtiqetException(String.format("Error creating Protocol %s", protocolName), e);
        }

        validateProtocol(protocol, protocolName);

        protocol.setName(protocolName);
        return protocol;
    }

    /**
     * Validates that a protocol is valid for use in test cases
     *
     * @param protocol protocol parsed from a configuration class
     * @param protocolName name of the protocol
     * @throws EtiqetException when the protocol is invalid
     */
    private void validateProtocol(Protocol protocol, String protocolName) throws EtiqetException {
        com.neueda.etiqet.core.config.dtos.Client client = protocol.getClient();
        if (client == null) {
            throw new EtiqetException(String.format("Protocol %s does not have a client specified", protocolName));
        }
        if (StringUtils.isNullOrEmpty(client.getDefaultConfig())) {
            throw new EtiqetException("Client for protocol " + protocolName + " doesn't have default configuration");
        }
        if (StringUtils.isNullOrEmpty(client.getImpl())) {
            throw new EtiqetException(
                "Client for protocol " + protocolName + " doesn't specify an implementation class");
        }
        Dictionary dictionary = protocol.getDictionary();
        if (dictionary != null && StringUtils.isNullOrEmpty(dictionary.getHandler())) {
            throw new EtiqetException("Protocol " + protocolName + " does not specify a dictionary handler class");
        }

        Messages messages = protocol.getMessages();
        if (messages != null) {
            for (Message message : messages.getMessage()) {
                if (StringUtils.isNullOrEmpty(message.getName())) {
                    throw new EtiqetException("A message for protocol " + protocolName + " was passed without a name");
                }
                if (StringUtils.isNullOrEmpty(message.getImplementation())) {
                    throw new EtiqetException(
                        "A message for protocol " + protocolName + " was passed without an implementation class");
                }
            }
        }
    }

    private void addProtocol(Protocol protocol) throws EtiqetException {
        protocols.put(protocol.getName(), new ProtocolConfig(protocol));
    }

    private void addClient(ClientImpl clientImpl) throws EtiqetException {
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

        clients.put(clientImpl.getName(), client);
    }

    private void addServer(ServerImpl serverDetail) throws EtiqetException {
        String serverImpl = serverDetail.getImpl();
        Server server = ServerFactory.create(serverImpl, serverDetail.getConfigPath());
        servers.put(serverDetail.getName(), server);
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

    public Class<?> getConfigClass() {
        return configClass;
    }
}
