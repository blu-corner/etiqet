package com.neueda.etiqet.transport.rabbitmq.config.model;

import java.util.List;

public class AmqpConfig {

    private String host;
    private List<ExchangeConfig> exchangeConfigs;

    public AmqpConfig(String host, List<ExchangeConfig> exchangeConfigs) {
        this.host = host;
        this.exchangeConfigs = exchangeConfigs;
    }

    public String getHost() {
        return host;
    }

    public List<ExchangeConfig> getExchangeConfigs() {
        return exchangeConfigs;
    }

}
