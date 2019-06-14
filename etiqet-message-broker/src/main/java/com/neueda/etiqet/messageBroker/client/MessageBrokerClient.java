package com.neueda.etiqet.messageBroker.client;

import com.neueda.etiqet.core.client.Client;
import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import com.neueda.etiqet.core.common.exceptions.EtiqetRuntimeException;
import com.neueda.etiqet.core.message.cdr.Cdr;
import com.neueda.etiqet.core.message.config.ProtocolConfig;
import com.neueda.etiqet.core.transport.SubscriptionTransport;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class MessageBrokerClient extends Client {

    public MessageBrokerClient(String clientConfig) throws EtiqetException {
        this(clientConfig, null);
    }

    public MessageBrokerClient(String primaryClientConfig, String secondaryClientConfig) throws EtiqetException {
        this(primaryClientConfig, secondaryClientConfig, null);
    }

    public MessageBrokerClient(String primaryClientConfig, String secondaryClientConfig, ProtocolConfig protocol) throws EtiqetException {
        super(primaryClientConfig, secondaryClientConfig, protocol);
    }

    @Override
    public boolean isLoggedOn() {
        return transport != null && transport.isLoggedOn();
    }

    public void sendMessageToTopic(Cdr message, Optional<String> topicName) throws EtiqetException {
        getTransport().sendToTopic(message, topicName);
    }

    public void sendMessageToQueue(Cdr message, String queueName) throws EtiqetException {
        getTransport().sendToQueue(message, queueName);
    }

    public Cdr waitForMsgOnTopic(Optional<String> topic, int milliseconds) throws EtiqetException {
        CompletableFuture<Cdr> eventualCdr = getTransport().consumeFromTopic(topic);
        try {
            return eventualCdr.get(milliseconds, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            throw new EtiqetException(e);
        }
    }

    public Cdr waitForMsgOnQueue(String queueName, int milliseconds) throws EtiqetException {
        CompletableFuture<Cdr> eventualCdr = getTransport().consumeFromQueue(queueName);
        try {
            return eventualCdr.get(milliseconds, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            throw new EtiqetException(e);
        }
    }

    public SubscriptionTransport getTransport() {
        if (!(transport instanceof SubscriptionTransport)) {
            throw new EtiqetRuntimeException("Transport for messagebrokerClient should be a SubscriptionTransport");
        }
        return (SubscriptionTransport) transport;
    }
}
