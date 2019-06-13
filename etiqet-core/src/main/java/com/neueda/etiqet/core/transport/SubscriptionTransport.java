package com.neueda.etiqet.core.transport;

import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import com.neueda.etiqet.core.message.cdr.Cdr;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface SubscriptionTransport extends Transport {

    void sendToTopic(Cdr cdr, Optional<String> topicName) throws EtiqetException;

    void sendToQueue(Cdr cdr, String queueName) throws EtiqetException;

    CompletableFuture<Cdr> consumeFromTopic(Optional<String> topicName) throws EtiqetException;

    CompletableFuture<Cdr> consumeFromQueue(String queueName) throws EtiqetException;
}
