package com.neueda.etiqet.core.transport;

import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import com.neueda.etiqet.core.message.cdr.Cdr;

import java.time.Duration;
import java.util.Optional;
import java.util.function.Consumer;

public interface BrokerTransport extends Transport {

    void subscribeToTopic(Optional<String> topicName, Consumer<Cdr> cdrListener) throws EtiqetException;

    void subscribeToQueue(String queueName, Consumer<Cdr> cdrListener) throws EtiqetException;

    Cdr subscribeAndConsumeFromTopic(Optional<String> topicName, Duration timeout) throws EtiqetException;

    Cdr subscribeAndConsumeFromQueue(String queueName, Duration timeout) throws EtiqetException;

    void sendToTopic(Cdr cdr, Optional<String> topicName) throws EtiqetException;

    void sendToQueue(Cdr cdr, String queueName) throws EtiqetException;

    void clearQueue(String queueName) throws EtiqetException;

}
