package com.neueda.etiqet.core.client;

import com.neueda.etiqet.core.client.delegate.ClientDelegate;
import com.neueda.etiqet.core.client.delegate.SinkClientDelegate;
import com.neueda.etiqet.core.client.event.StopObserver;
import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import com.neueda.etiqet.core.common.exceptions.UnhandledClientException;
import com.neueda.etiqet.core.common.exceptions.UnhandledProtocolException;
import com.neueda.etiqet.core.config.GlobalConfig;
import com.neueda.etiqet.core.config.dtos.Delegate;
import com.neueda.etiqet.core.config.dtos.Observer;
import com.neueda.etiqet.core.config.dtos.StopEvent;
import com.neueda.etiqet.core.message.config.ProtocolConfig;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class implementing the factory method pattern to create Clients.
 */
public class ClientFactory {

    static final String CLIENT_CREATION_ERROR = "Error creating client type %s";
    static final String PROTOCOL_ERROR = "Could not find protocol for %s";
    private static final Logger LOG = LoggerFactory.getLogger(ClientFactory.class);

    private ClientFactory() {
    }

    /**
     * Instantiates the client specified in parameters
     *
     * @param clientType Type of client required
     * @return An instance of Client
     * @throws EtiqetException if client type is not found.
     */
    public static Client create(String clientType) throws EtiqetException {
        ProtocolConfig protocol = GlobalConfig.getInstance().getProtocol(clientType);
        if (protocol == null) {
            throw new UnhandledProtocolException(String.format(PROTOCOL_ERROR, clientType));
        }
        return create(clientType, protocol.getClient().getDefaultConfig());
    }

    /**
     * Instantiates the client specified in parameters
     *
     * @param clientType Type of client required
     * @param config the path to the configuration file.
     * @return An instance of Client
     * @throws EtiqetException if client type is not found.
     */
    public static Client create(String clientType, String config) throws EtiqetException {
        return create(clientType, config, null);
    }

    /**
     * Instantiates the client specified in parameters
     *
     * @param clientType Type of client required
     * @param primaryConfig the path to the configuration file.
     * @param secondaryConfig the path to the secondary configuration file (for failover).
     * @return An instance of Client
     * @throws EtiqetException if client type is not found.
     */
    public static Client create(String clientType, String primaryConfig, String secondaryConfig)
        throws EtiqetException {
        return create(clientType, primaryConfig, secondaryConfig,
            GlobalConfig.getInstance().getProtocol(clientType));
    }

    public static Client create(
        String clientType,
        String primaryConfig,
        String secondaryConfig,
        ProtocolConfig protocolConfig
    ) throws EtiqetException {
        try {
            if (protocolConfig == null) {
                throw new UnhandledProtocolException(String.format(PROTOCOL_ERROR, clientType));
            }
            String classUri = protocolConfig.getClient().getImpl();
            Client client = (Client) Class.forName(classUri)
                .getConstructor(String.class, String.class)
                .newInstance(primaryConfig, secondaryConfig);

            client.setProtocolConfig(protocolConfig);
            client.setProtocolName(clientType);
            client.setUrlExtensions(protocolConfig.getClientUrlExtensions());
            setClientDelegates(client, protocolConfig);
            setClientStopEventObservers(client, protocolConfig);
            return client;
        } catch (Exception e) {
            LOG.error(String.format(CLIENT_CREATION_ERROR, clientType), e);
            throw new UnhandledClientException(String.format(CLIENT_CREATION_ERROR, clientType), e);
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
    private static void setClientDelegates(Client client, ProtocolConfig protocolConfig)
        throws EtiqetException {
        ClientDelegateFactory cdf = new ClientDelegateFactory(protocolConfig);
        List<Delegate> clientDelegates = protocolConfig.getClientDelegates();
        if (clientDelegates != null) {
            Delegate[] delegates = clientDelegates.toArray(new Delegate[0]);
            ClientDelegate del = new SinkClientDelegate();
            for (int i = delegates.length - 1; i >= 0; i--) {
                ClientDelegate tmp = cdf.create(delegates[i].getKey());
                tmp.setNextDelegate(del);
                del = tmp;
            }

            client.setDelegate(del);
        }
    }

    private static void setClientStopEventObservers(Client client, ProtocolConfig protocolConfig)
        throws EtiqetException {
        StopEvent event = protocolConfig.getStopEvent();
        if (event != null) {
            for (Observer observer : event.getObservers()) {
                StopObserver observerImpl;
                try {
                    observerImpl = (StopObserver) Class.forName(observer.getImpl()).getConstructor()
                        .newInstance();
                } catch (ClassNotFoundException e) {
                    throw new EtiqetException(
                        String.format("Can't find StopObserver impl '%s'\n\t%s", observer.getImpl(),
                            e.getMessage())
                    );
                } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    throw new EtiqetException(
                        String.format("Failed to instantiate '%s'\n\t%s", observer.getImpl(), e.getMessage())
                    );
                }
                client.addStopEventObserver(observerImpl);
            }
        }
    }

}
