package com.neueda.etiqet.transport.rabbitmq.config.model;

import java.util.ArrayList;
import java.util.List;

public class AmqpConfigBuilder {
    private String host;
    private List<ExchangeConfig> exchangeConfigs;

    private AmqpConfigBuilder() {
        host = "localhost";
        exchangeConfigs = new ArrayList<>();
    }

    public static AmqpConfigBuilder aAmqpConfig() {
        return new AmqpConfigBuilder();
    }

    public AmqpConfigBuilder addExchangeConfig(ExchangeConfig exchangeConfig) {
        exchangeConfigs.add(exchangeConfig);
        return this;
    }

    public AmqpConfig build() {
        return new AmqpConfig(
            host,
            exchangeConfigs
        );
    }
}
