package com.neueda.etiqet.core.client;

import com.neueda.etiqet.core.client.delegate.ClientDelegate;
import com.neueda.etiqet.core.client.delegate.SinkClientDelegate;
import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import com.neueda.etiqet.core.common.exceptions.UnhandledClientException;
import com.neueda.etiqet.core.common.exceptions.UnhandledProtocolException;
import com.neueda.etiqet.core.config.GlobalConfig;
import com.neueda.etiqet.core.config.dtos.Delegate;
import com.neueda.etiqet.core.config.dtos.Delegates;
import com.neueda.etiqet.core.message.config.ProtocolConfig;
import com.neueda.etiqet.core.util.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Class implementing the factory method pattern to create Clients.
 */
public class ClientFactory {

	private static final Logger LOG = LogManager.getLogger(ClientFactory.class);

	static final String CLIENT_CREATION_ERROR = "Error creating client type %s";
	static final String PROTOCOL_ERROR = "Could not find protocol for %s";

	private ClientFactory() {}

	/**
	 * Instantiates the client specified in parameters
	 * @param clientType Type of client required
	 * @return An instance of Client
	 * @throws EtiqetException if client type is not found.
	 */
	public static Client create(String clientType) throws EtiqetException {
        try {
			ProtocolConfig protocolConfig = GlobalConfig.getInstance().getProtocol(clientType);
			if(protocolConfig == null) {
			    throw new UnhandledProtocolException(String.format(PROTOCOL_ERROR, clientType));
            }
			String classUri = protocolConfig.getClient().getImpl();

			Client client = (Client) Class.forName(classUri)
                                          .getConstructor(String.class)
                                          .newInstance(protocolConfig.getClient().getDefaultConfig());

			client.setProtocolConfig(protocolConfig);
			client.setProtocolName(clientType);
			if(!StringUtils.isNullOrEmpty(protocolConfig.getClient().getExtensionsUrl())) {
			    client.setExtensionsUrl(protocolConfig.getClient().getExtensionsUrl());
            }
            setClientDelegates(client, protocolConfig);
			return client;
        } catch (Exception e) {
            LOG.error(String.format(CLIENT_CREATION_ERROR, clientType), e);
            throw new UnhandledClientException(String.format(CLIENT_CREATION_ERROR, clientType), e);
        }
	}

	/**
	 * Instantiates the client specified in parameters
	 * @param clientType Type of client required
	 * @param config the path to the configuration file.
	 * @return An instance of Client
	 * @throws EtiqetException if client type is not found.
	 */
	public static Client create(String clientType, String config) throws EtiqetException {
        try {
			ProtocolConfig protocolConfig = GlobalConfig.getInstance().getProtocol(clientType);
            if(protocolConfig == null) {
				throw new UnhandledProtocolException(String.format(PROTOCOL_ERROR, clientType));
            }
			String classUri = protocolConfig.getClient().getImpl();
			Client client = (Client) Class.forName(classUri).getConstructor(String.class).newInstance(config);
            client.setProtocolConfig(protocolConfig);
            client.setProtocolName(clientType);

            if(!StringUtils.isNullOrEmpty(protocolConfig.getClient().getExtensionsUrl())) {
                client.setExtensionsUrl(protocolConfig.getClient().getExtensionsUrl());
            }
            setClientDelegates(client, protocolConfig);
            return client;
        } catch (Exception e) {
            LOG.error(String.format(CLIENT_CREATION_ERROR, clientType), e);
            throw new UnhandledClientException(String.format(CLIENT_CREATION_ERROR, clientType), e);
        }
	}

	/**
	 * Instantiates the client specified in parameters
	 * @param clientType Type of client required
	 * @param primaryConfig the path to the configuration file.
	 * @param secondaryConfig the path to the secondary configuration file (for failover).
	 * @return An instance of Client
	 * @throws EtiqetException if client type is not found.
	 */
	public static Client create(String clientType, String primaryConfig, String secondaryConfig) throws EtiqetException {
        try {
			ProtocolConfig protocolConfig = GlobalConfig.getInstance().getProtocol(clientType);
            if(protocolConfig == null) {
				throw new UnhandledProtocolException(String.format(PROTOCOL_ERROR, clientType));
            }
			String classUri = protocolConfig.getClient().getImpl();
			Client client = (Client) Class.forName(classUri)
                                          .getConstructor(String.class, String.class)
                                          .newInstance(primaryConfig, secondaryConfig);

			client.setProtocolConfig(protocolConfig);
            client.setProtocolName(clientType);
            if(!StringUtils.isNullOrEmpty(protocolConfig.getClient().getExtensionsUrl())) {
                client.setExtensionsUrl(protocolConfig.getClient().getExtensionsUrl());
            }
            setClientDelegates(client, protocolConfig);
			return client;
		} catch (Exception e) {
			LOG.error(String.format(CLIENT_CREATION_ERROR, clientType), e);
			throw new UnhandledClientException(String.format(CLIENT_CREATION_ERROR, clientType), e);
		}
	}

	public static Client create(
		String clientClass,
		String primaryClientConfig,
		String secondaryClientConfig,
		ProtocolConfig protocolConfig
	) throws EtiqetException {
		try {
            Client client = (Client) Class.forName(clientClass)
                                          .getConstructor(String.class, String.class, ProtocolConfig.class)
                                          .newInstance(primaryClientConfig, secondaryClientConfig, protocolConfig);
            setClientDelegates(client, protocolConfig);
            return client;
		} catch (Exception e) {
		    LOG.error(String.format(CLIENT_CREATION_ERROR, clientClass), e);
		    throw new UnhandledClientException(String.format(CLIENT_CREATION_ERROR, clientClass), e);
        }
	}

    /**
     * Sets the client delegates for the client from the protocol config. Suppresses unchecked warnings because we're
     * ignoring the generics from the ClientDelegate class.
     *
     * @param client client that needs the delegates
     * @param protocolConfig protocol configuration containing delegate definitions
     * @throws EtiqetException when ClientDelegates aren't found
     */
    @SuppressWarnings("unchecked")
    private static void setClientDelegates(Client client, ProtocolConfig protocolConfig) throws EtiqetException {
        ClientDelegateFactory cdf = new ClientDelegateFactory(protocolConfig);
        Delegates clientDelegates = protocolConfig.getClientDelegates();
        if(clientDelegates != null) {
            Delegate[] delegates = clientDelegates.getDelegate();
            ClientDelegate del = new SinkClientDelegate<>();
            for(int i = delegates.length - 1; i >= 0; i--) {
                ClientDelegate tmp = cdf.create(delegates[i].getKey());
                tmp.setNextDelegate(del);
                del = tmp;
            }

            client.setDelegate(del);
        }
    }

}
