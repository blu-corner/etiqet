package com.neueda.etiqet.core.transport;

import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import com.neueda.etiqet.core.message.cdr.Cdr;

import java.time.Duration;
import java.util.function.Consumer;

public interface ExchangeTransport extends Transport {

    void sendToExchange(Cdr cdr, String exchangeName) throws EtiqetException;

    void sendToExchange(Cdr cdr, String exchangeName, String routingKey) throws EtiqetException;

    void subscribeToQueue(String queueName, Consumer<Cdr> cdrListener) throws EtiqetException;

    Cdr subscribeAndConsumeFromQueue(String queueName, Duration timeout) throws EtiqetException;

}
