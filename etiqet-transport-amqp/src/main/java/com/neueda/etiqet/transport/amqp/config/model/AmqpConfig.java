package com.neueda.etiqet.transport.amqp.config.model;

import java.util.List;
import java.util.OptionalInt;

public class AmqpConfig {

    private String host;
    private OptionalInt port;
    private List<ExchangeConfig> exchangeConfigs;
    private Class binaryMessageConverterDelegateClass;

    public AmqpConfig(String host,  OptionalInt port, List<ExchangeConfig> exchangeConfigs, Class binaryMessageConverterDelegateClass) {
        this.host = host;
        this.port = port;
        this.exchangeConfigs = exchangeConfigs;
        this.binaryMessageConverterDelegateClass = binaryMessageConverterDelegateClass;
    }

    public String getHost() {
        return host;
    }

    public OptionalInt getPort() {
        return port;
    }

    public List<ExchangeConfig> getExchangeConfigs() {
        return exchangeConfigs;
    }

    public Class getBinaryMessageConverterDelegateClass() {
        return binaryMessageConverterDelegateClass;
    }
}
