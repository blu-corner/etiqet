package com.neueda.etiqet.exchangeBroker.client;

import com.neueda.etiqet.core.client.Client;
import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import com.neueda.etiqet.core.common.exceptions.EtiqetRuntimeException;
import com.neueda.etiqet.core.message.cdr.Cdr;
import com.neueda.etiqet.core.message.config.ProtocolConfig;
import com.neueda.etiqet.core.transport.ExchangeTransport;
import com.neueda.etiqet.core.util.MapUtils;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ExchangeBrokerClient extends Client {

    private Map<String, Deque<Cdr>> messagesByQueue;

    public ExchangeBrokerClient(String clientConfig) throws EtiqetException {
        this(clientConfig, null);
    }

    public ExchangeBrokerClient(String primaryClientConfig, String secondaryClientConfig) throws EtiqetException {
        this(primaryClientConfig, secondaryClientConfig, null);
    }

    public ExchangeBrokerClient(String primaryClientConfig, String secondaryClientConfig, ProtocolConfig protocol) throws EtiqetException {
        super(primaryClientConfig, secondaryClientConfig, protocol);
        messagesByQueue = new ConcurrentHashMap<>();
    }

    @Override
    public boolean isLoggedOn() {
        return transport != null && transport.isLoggedOn();
    }

    @Override
    public ExchangeTransport getTransport() {
        if (transport instanceof ExchangeTransport) {
            return (ExchangeTransport) transport;
        }
        throw new EtiqetRuntimeException("Transport for exchange broker client should be ExchangeTransport");
    }

    public void sendMessageToExchange(final Cdr cdr, final String exchangeName) throws EtiqetException {
        getTransport().sendToExchange(cdr, exchangeName);
    }

    public void sendMessageToExchange(final Cdr cdr, final String exchangeName, final String routingKey) throws EtiqetException {
        getTransport().sendToExchange(cdr, exchangeName, routingKey);
    }

    public void subscribeToQueue(final String queueName) throws EtiqetException {
        getTransport().subscribeToQueue(
            queueName,
            message -> addMessageToGroup(messagesByQueue, queueName, message)
        );
    }

    public Cdr retrieveMessageFromQueue(final String queueName, Duration timeout) throws EtiqetException {
        return getTransport().subscribeAndConsumeFromQueue(queueName, timeout);
    }

    public List<Cdr> getReceivedMessagesFromQueue(final String queueName) {
        return new LinkedList(messagesByQueue.getOrDefault(queueName, new LinkedList<>()));
    }

    public Optional<Cdr> getLastMessageFromQueue(final String queueName) {
        return Optional.ofNullable(
            messagesByQueue.getOrDefault(queueName, new LinkedList<>())
                .peekLast()
        );
    }

    private void addMessageToGroup(final Map<String, Deque<Cdr>> map, final String groupKey, final Cdr message) {
        MapUtils.addMessageToGroup(map, groupKey, message);
        msgQueue.add(message);
    }

}
