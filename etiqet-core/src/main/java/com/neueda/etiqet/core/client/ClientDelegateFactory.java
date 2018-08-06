package com.neueda.etiqet.core.client;

import com.neueda.etiqet.core.client.delegate.ClientDelegate;
import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import com.neueda.etiqet.core.config.dtos.Delegate;
import com.neueda.etiqet.core.message.config.ProtocolConfig;

import java.util.HashMap;
import java.util.Map;


/**
 * Factory to create a Client implementation
 * @param <U> unmarshalled native message format
 * @param <M> marshalled format of message for Etiqet to handle
 * @author Neueda
 */
public class ClientDelegateFactory<U, M> {

    private Map<String, String> delegatesUriFromAction;

    public ClientDelegateFactory(ProtocolConfig protocolConfig) {
        delegatesUriFromAction = new HashMap<>();
        if(protocolConfig.getClientDelegates() != null) {
            for(Delegate del: protocolConfig.getClientDelegates().getDelegate()) {
                delegatesUriFromAction.put(del.getKey(), del.getImpl());
            }
        }
    }

    /**
     * Creates a delegate to take actions of the given type.
     * Suppressing warnings of unchecked casting from reflection.
     * @param actionType The type of action to be taken by the client.
     * @return An instance of Client
     * @throws EtiqetException if client type is not found.
     */
    @SuppressWarnings("unchecked")
    public ClientDelegate<U, M> create(String actionType) throws EtiqetException {
        try {
            return (ClientDelegate<U, M>) Class.forName(delegatesUriFromAction.get(actionType)).newInstance();
        } catch (Exception e) {
            throw new EtiqetException(e);
        }
    }

}
