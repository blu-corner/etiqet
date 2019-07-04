package com.neueda.etiqet.messageBroker.mock;

import com.neueda.etiqet.core.message.cdr.Cdr;
import com.neueda.etiqet.core.transport.BrokerTransport;
import com.neueda.etiqet.core.transport.EchoTransport;

import java.time.Duration;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

public class BrokerTransportStub extends EchoTransport implements BrokerTransport {

    private static final Map<String, Cdr> topicMessages = new HashMap<>();
    private static final Map<String, List<Consumer<Cdr>>> listenersByTopic = new HashMap<>();
    private static final Map<String, List<Consumer<Cdr>>> listenersByQueue = new HashMap<>();
    private final String DEFAULT_TOPIC_NAME = "defaultTopicName";

    @Override
    public void subscribeToTopic(Optional<String> topicName, Consumer<Cdr> cdrListener) {
        listenersByTopic.merge(
            topicName.orElse(DEFAULT_TOPIC_NAME),
            singletonList(cdrListener),
            (list1, list2) ->
                Stream.of(list1, list2)
                    .flatMap(Collection::stream)
                    .collect(toList()));
    }

    @Override
    public void subscribeToQueue(String queueName, Consumer<Cdr> cdrListener) {
        listenersByQueue.merge(
            queueName,
            singletonList(cdrListener),
            (list1, list2) ->
                Stream.of(list1, list2)
                    .flatMap(Collection::stream)
                    .collect(toList()));
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
}
