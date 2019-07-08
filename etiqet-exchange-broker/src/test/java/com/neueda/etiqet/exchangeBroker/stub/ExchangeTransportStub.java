package com.neueda.etiqet.exchangeBroker.stub;

import com.neueda.etiqet.core.message.cdr.Cdr;
import com.neueda.etiqet.core.transport.EchoTransport;
import com.neueda.etiqet.core.transport.ExchangeTransport;
import com.neueda.etiqet.core.util.MapUtils;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static java.util.Collections.emptyList;

public class ExchangeTransportStub extends EchoTransport implements ExchangeTransport {

    private static final Map<String, List<Consumer<Cdr>>> listenersByQueue = new HashMap<>();
    private static final Map<String, List<Cdr>> messagesByQueue = new HashMap<>();

    @Override
    public void subscribeToQueue(String queueName, Consumer<Cdr> cdrListener) {
        MapUtils.addToMappedList(listenersByQueue, queueName, cdrListener);
    }

    @Override
    public Cdr subscribeAndConsumeFromQueue(String queueName, Duration timeout) {
        List<Cdr> messages = messagesByQueue.get(queueName);
        return messages.get(messages.size() - 1);
    }

    @Override
    public void sendToExchange(Cdr cdr, String exchangeName) {
        final String queueName = mapQueueToExchange(exchangeName);
        listenersByQueue.getOrDefault(queueName, emptyList())
            .forEach(listener -> listener.accept(cdr));
        MapUtils.addToMappedList(messagesByQueue, queueName, cdr);
    }

    @Override
    public void sendToExchange(Cdr cdr, String exchangeName, String routingKey) {
        sendToExchange(cdr, exchangeName);
    }

    private String mapQueueToExchange(String exchangeName) {
        return exchangeName + "_queue";
    }

}
