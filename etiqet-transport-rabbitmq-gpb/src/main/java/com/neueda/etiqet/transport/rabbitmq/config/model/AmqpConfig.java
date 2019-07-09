package com.neueda.etiqet.transport.rabbitmq.config.model;

import java.util.List;
import java.util.Optional;

public class AmqpConfig {

    private String host;
    private List<ExchangeConfig> exchangeConfigs;
    private Class binaryMessageConverterDelegateClass;

    public AmqpConfig(String host, List<ExchangeConfig> exchangeConfigs, Class binaryMessageConverterDelegateClass) {
        this.host = host;
        this.exchangeConfigs = exchangeConfigs;
        this.binaryMessageConverterDelegateClass = binaryMessageConverterDelegateClass;
    }

    public String getHost() {
        return host;
    }

    public List<ExchangeConfig> getExchangeConfigs() {
        return exchangeConfigs;
    }

    public Optional<Class> getBinaryMessageConverterDelegateClass() {
        return Optional.ofNullable(binaryMessageConverterDelegateClass);
    }
}
