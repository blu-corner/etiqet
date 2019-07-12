package com.neueda.etiqet.transport.amqp.config.model;

import com.rabbitmq.client.BuiltinExchangeType;

import java.util.List;

public class ExchangeConfig {
    private String name;
    private BuiltinExchangeType exchangeType;
    private List<QueueConfig> queueConfigs;

    public ExchangeConfig(String name, BuiltinExchangeType exchangeType, List<QueueConfig> queueConfigs) {
        this.name = name;
        this.exchangeType = exchangeType;
        this.queueConfigs = queueConfigs;
    }

    public String getName() {
        return name;
    }

    public BuiltinExchangeType getExchangeType() {
        return exchangeType;
    }

    public List<QueueConfig> getQueueConfigs() {
        return queueConfigs;
    }
}
