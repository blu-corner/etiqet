package com.neueda.etiqet.messageBroker.stub;

import com.neueda.etiqet.core.message.cdr.Cdr;
import com.neueda.etiqet.core.transport.BrokerTransport;
import com.neueda.etiqet.core.transport.EchoTransport;
import com.neueda.etiqet.core.util.MapUtils;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import static java.util.Collections.emptyList;

public class BrokerTransportStub extends EchoTransport implements BrokerTransport {

    private static final Map<String, Cdr> topicMessages = new HashMap<>();
    private static final Map<String, List<Consumer<Cdr>>> listenersByTopic = new HashMap<>();
    private static final Map<String, List<Consumer<Cdr>>> listenersByQueue = new HashMap<>();
    private final String DEFAULT_TOPIC_NAME = "defaultTopicName";

    @Override
    public void subscribeToTopic(Optional<String> topicName, Consumer<Cdr> cdrListener) {
        MapUtils.addToMappedList(listenersByTopic, topicName.orElse(DEFAULT_TOPIC_NAME), cdrListener);
    }

    @Override
    public void subscribeToQueue(String queueName, Consumer<Cdr> cdrListener) {
        MapUtils.addToMappedList(listenersByQueue, queueName, cdrListener);
    }

    @Override
    public Cdr subscribeAndConsumeFromTopic(Optional<String> topicName, Duration timeout) {
        return topicMessages.get(topicName.orElse(DEFAULT_TOPIC_NAME));
    }

    @Override
    public Cdr subscribeAndConsumeFromQueue(String queueName, Duration timeout) {
        return null;
    }

    @Override
    public void sendToTopic(Cdr cdr, Optional<String> topicName) {
        topicMessages.put(topicName.orElse(DEFAULT_TOPIC_NAME), cdr);
        listenersByTopic.getOrDefault(topicName.orElse(DEFAULT_TOPIC_NAME), emptyList())
            .forEach(listener -> listener.accept(cdr));
    }

    @Override
    public void sendToQueue(Cdr cdr, String queueName) {
        listenersByQueue.getOrDefault(queueName, emptyList())
                        .forEach(listener -> listener.accept(cdr));

    }

    @Override
    public void clearQueue(String queueName) {
        // nothing to do here
    }
}
