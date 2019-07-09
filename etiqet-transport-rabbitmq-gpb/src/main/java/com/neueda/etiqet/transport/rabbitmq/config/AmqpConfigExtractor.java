package com.neueda.etiqet.transport.rabbitmq.config;

import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import com.neueda.etiqet.core.common.exceptions.EtiqetRuntimeException;
import com.neueda.etiqet.core.transport.delegate.BinaryMessageConverterDelegate;
import com.neueda.etiqet.transport.amqp_091.*;
import com.neueda.etiqet.transport.rabbitmq.config.model.AmqpConfig;
import com.neueda.etiqet.transport.rabbitmq.config.model.ExchangeConfig;
import com.neueda.etiqet.transport.rabbitmq.config.model.QueueConfig;
import com.rabbitmq.client.BuiltinExchangeType;

import java.util.List;
import java.util.Map;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

public class AmqpConfigExtractor {
    private AmqpConfigXmlParser configXmlParser;
    private Map<String, Queue> queues;

    public AmqpConfigExtractor() {
        configXmlParser = new AmqpConfigXmlParser();
    }

    public AmqpConfig retrieveConfiguration(final String configFile) throws EtiqetException {
        AmqpConfiguration xmlConfiguration = configXmlParser.parse(configFile);
        queues = queues(xmlConfiguration.getQueues().getQueue());
        Class binaryMessageConverterDelegateClass = binaryMessageConverterDelegateClass(xmlConfiguration);
        return new AmqpConfig(
            xmlConfiguration.getHost(),
            exchangeConfigs(xmlConfiguration),
            binaryMessageConverterDelegateClass
        );
    }

    private Map<String, Queue> queues(List<Queue> xmlQueues) {
        return xmlQueues.stream()
            .collect(toMap(Queue::getName, identity()));
    }

    private List<ExchangeConfig> exchangeConfigs(final AmqpConfiguration xmlConfiguration) {
        return xmlConfiguration.getExchanges().getExchange().stream()
            .map(this::exchangeConfig)
            .collect(toList());
    }

    private ExchangeConfig exchangeConfig(final Exchange xmlExchangeConfig) {
        return new ExchangeConfig(
            xmlExchangeConfig.getName(),
            exchangeType(xmlExchangeConfig.getExchangeType()),
            queueConfigs(xmlExchangeConfig.getQueueRef())
        );
    }

    private List<QueueConfig> queueConfigs(final List<QueueRef> queueRefs) {
        return queueRefs.stream()
            .map(this::queueConfig)
            .collect(toList());
    }

    private QueueConfig queueConfig(QueueRef xmlQueueConfig) {
        String queueName = xmlQueueConfig.getRef();
        Queue xmlQueue = queues.get(queueName);
        if (xmlQueue == null) {
            throw new EtiqetRuntimeException("Could not find queue definition for " + queueName);
        }
        return new QueueConfig(
            xmlQueueConfig.getRef(),
            xmlQueueConfig.getBindingKey(),
            xmlQueue.isDurable(),
            xmlQueue.isExclusive(),
            xmlQueue.isAutodelete()
        );
    }

    private BuiltinExchangeType exchangeType(final ExchangeType xmlExchangeType) {
        switch (xmlExchangeType) {
            case TOPIC: return BuiltinExchangeType.TOPIC;
            case DIRECT: return BuiltinExchangeType.DIRECT;
            case FANOUT: return BuiltinExchangeType.FANOUT;
            case HEADERS: return BuiltinExchangeType.HEADERS;
            default: throw new EtiqetRuntimeException("No built-in exchange type found for " + xmlExchangeType.name());
        }
    }

    private Class binaryMessageConverterDelegateClass(AmqpConfiguration xmlConfiguration) throws EtiqetException{
        try {
            return Class.forName(xmlConfiguration.getBinaryMessageConverterDelegate());
        } catch (ReflectiveOperationException e) {
            throw new EtiqetException("Unable to find BinaryMessageConverterDelegate class " + xmlConfiguration.getBinaryMessageConverterDelegate());
        }
    }

}
