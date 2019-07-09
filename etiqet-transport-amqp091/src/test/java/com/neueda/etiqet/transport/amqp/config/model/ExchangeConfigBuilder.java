package com.neueda.etiqet.transport.amqp.config.model;

import com.rabbitmq.client.BuiltinExchangeType;

import java.util.ArrayList;
import java.util.List;

public class ExchangeConfigBuilder {
    private String name;
    private BuiltinExchangeType exchangeType;
    private List<QueueConfig> queueConfigs;

    private ExchangeConfigBuilder(String name, BuiltinExchangeType exchangeType) {
        this.name = name;
        this.exchangeType = exchangeType;
        this.queueConfigs = new ArrayList<>();
    }

    public static ExchangeConfigBuilder aExchangeConfig(String name, BuiltinExchangeType exchangeType) {
        return new ExchangeConfigBuilder(name, exchangeType);
    }

    public ExchangeConfigBuilder addQueueConfig(QueueConfig queueConfig) {
        this.queueConfigs.add(queueConfig);
        return this;
    }

    public ExchangeConfig build() {
        return new ExchangeConfig(
            name,
            exchangeType,
            queueConfigs
        );
    }
}
