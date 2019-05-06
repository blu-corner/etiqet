package com.neueda.etiqet.core.client;

import com.neueda.etiqet.core.client.delegate.ClientDelegate;
import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import com.neueda.etiqet.core.config.dtos.Delegate;
import com.neueda.etiqet.core.message.config.ProtocolConfig;
import java.util.HashMap;
import java.util.Map;


/**
 * Factory to create a Client implementation
 *
 * @author Neueda
 */
public class ClientDelegateFactory {

    private Map<String, String> delegatesUriFromAction;

    public ClientDelegateFactory(ProtocolConfig protocolConfig) {
        delegatesUriFromAction = new HashMap<>();
        if (protocolConfig.getClientDelegates() != null) {
            for (Delegate del : protocolConfig.getClientDelegates()) {
                delegatesUriFromAction.put(del.getKey(), del.getImpl());
            }
        }
    }

    /**
     * Creates a delegate to take actions of the given type.
     *
     * @param actionType The type of action to be taken by the client.
     * @return An instance of Client
     * @throws EtiqetException if client type is not found.
     */
    public ClientDelegate create(String actionType) throws EtiqetException {
        try {
            return (ClientDelegate) Class.forName(delegatesUriFromAction.get(actionType)).newInstance();
        } catch (Exception e) {
            throw new EtiqetException(e);
        }
    }

}
