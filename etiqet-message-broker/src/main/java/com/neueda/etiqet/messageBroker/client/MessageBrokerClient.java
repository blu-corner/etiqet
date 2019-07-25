package com.neueda.etiqet.messageBroker.client;

import com.neueda.etiqet.core.client.Client;
import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import com.neueda.etiqet.core.common.exceptions.EtiqetRuntimeException;
import com.neueda.etiqet.core.message.cdr.Cdr;
import com.neueda.etiqet.core.message.config.ProtocolConfig;
import com.neueda.etiqet.core.transport.BrokerTransport;
import com.neueda.etiqet.core.util.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MessageBrokerClient extends Client {

    private ConcurrentHashMap<String, Deque<Cdr>> messagesByTopic;
    private ConcurrentHashMap<String, Deque<Cdr>> messagesByQueue;
    private final static Logger logger = LoggerFactory.getLogger(MessageBrokerClient.class);

    public MessageBrokerClient(String clientConfig) throws EtiqetException {
        this(clientConfig, null);
    }

    public MessageBrokerClient(String primaryClientConfig, String secondaryClientConfig) throws EtiqetException {
        this(primaryClientConfig, secondaryClientConfig, null);
    }

    public MessageBrokerClient(String primaryClientConfig, String secondaryClientConfig, ProtocolConfig protocol) throws EtiqetException {
        super(primaryClientConfig, secondaryClientConfig, protocol);
        messagesByTopic = new ConcurrentHashMap<>();
        messagesByQueue = new ConcurrentHashMap<>();
    }

    @Override
    public boolean isLoggedOn() {
        return transport != null && transport.isLoggedOn();
    }

    public void subscribeToTopic(String topicName) throws EtiqetException {
        getTransport().subscribeToTopic(
            Optional.of(topicName),
            message -> addMessageToGroup(messagesByTopic, topicName, message)
        );
    }

    public void subscribeToQueue(String queueName) throws EtiqetException {
        getTransport().subscribeToQueue(
            queueName,
            message -> addMessageToGroup(messagesByQueue, queueName, message)
        );
    }

    private void addMessageToGroup(final Map<String, Deque<Cdr>> map, final String groupKey, final Cdr message) {
        MapUtils.addMessageToGroup(map, groupKey, message);
        if (message == null) {
            throw new EtiqetRuntimeException("Empty message received from " + groupKey);
        }
        msgQueue.add(message);
    }

    public void sendMessageToTopic(Cdr message, Optional<String> topicName) throws EtiqetException {
        getTransport().sendToTopic(message, topicName);
    }

    public void sendMessageToQueue(Cdr message, String queueName) throws EtiqetException {
        getTransport().sendToQueue(message, queueName);
    }

    public Cdr waitForMsgOnTopic(Optional<String> topic, int milliseconds) throws EtiqetException {
        return getTransport().subscribeAndConsumeFromTopic(topic, Duration.ofMillis(milliseconds));
    }

    public Cdr waitForMsgOnQueue(String queueName, int milliseconds) throws EtiqetException {
        return getTransport().subscribeAndConsumeFromQueue(queueName, Duration.ofMillis(milliseconds));
    }

    public List<Cdr> getReceivedMessagesFromTopic(String topicName) {
        return getReceivedMessagesInGroup(messagesByTopic, topicName);
    }

    public List<Cdr> getReceivedMessagesFromQueue(String queueName) {
        return getReceivedMessagesInGroup(messagesByQueue, queueName);
    }

    private List<Cdr> getReceivedMessagesInGroup(final Map<String, Deque<Cdr>> map, final String groupName) {
        return new ArrayList<>(map.getOrDefault(groupName, new LinkedList<>()));
    }

    public Optional<Cdr> getLastMessageFromTopic(String topicName) {
        return getLastMessageFromGroup(messagesByTopic, topicName);
    }

    public Optional<Cdr> getLastMessageFromQueue(String queueName) {
        return getLastMessageFromGroup(messagesByQueue, queueName);
    }

    private Optional<Cdr> getLastMessageFromGroup(final Map<String, Deque<Cdr>> map, final String groupName) {
        return Optional.ofNullable(
            map
                .getOrDefault(groupName, new ArrayDeque<>())
                .getLast()
        );
    }

    public void clearQueue(String queueName) throws EtiqetException {
        getTransport().clearQueue(queueName);
    }

    @Override
    public BrokerTransport getTransport() {
        if (transport == null) {
            throw new EtiqetRuntimeException("Transport not available");
        }
        if (transport instanceof BrokerTransport) {
            return (BrokerTransport) transport;
        }
        throw new EtiqetRuntimeException("Transport for messagebrokerClient should be a BrokerTransport");
    }


}
