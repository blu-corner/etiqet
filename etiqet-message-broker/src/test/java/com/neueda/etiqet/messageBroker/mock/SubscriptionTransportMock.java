package com.neueda.etiqet.messageBroker.mock;

import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import com.neueda.etiqet.core.message.cdr.Cdr;
import com.neueda.etiqet.core.transport.EchoTransport;
import com.neueda.etiqet.core.transport.SubscriptionTransport;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class SubscriptionTransportMock extends EchoTransport implements SubscriptionTransport {

    private final Map<String, Cdr> topicMessages = new HashMap<>();
    private final String DEFAULT_TOPIC_NAME = "defaultTopicName";

    @Override
    public void sendToTopic(Cdr cdr, Optional<String> topicName) throws EtiqetException {
        topicMessages.put(topicName.orElse(DEFAULT_TOPIC_NAME), cdr);
    }

    @Override
    public void sendToQueue(Cdr cdr, String queueName) throws EtiqetException {

    }

    @Override
    public CompletableFuture<Cdr> consumeFromTopic(Optional<String> topicName) throws EtiqetException {
        CompletableFuture<Cdr> eventualCdr = new CompletableFuture<>();
        Cdr cdr = topicMessages.get(topicName.orElse(DEFAULT_TOPIC_NAME));
        eventualCdr.complete(cdr);
        return eventualCdr;
    }

    @Override
    public CompletableFuture<Cdr> consumeFromQueue(String queueName) throws EtiqetException {
        return null;
    }
}
