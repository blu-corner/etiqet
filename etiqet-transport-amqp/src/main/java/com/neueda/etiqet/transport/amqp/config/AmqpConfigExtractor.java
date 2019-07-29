package com.neueda.etiqet.transport.amqp.config;

import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import com.neueda.etiqet.core.common.exceptions.EtiqetRuntimeException;
import com.neueda.etiqet.core.transport.delegate.ByteArrayConverterDelegate;
import com.neueda.etiqet.transport.amqp.*;
import com.neueda.etiqet.transport.amqp.config.model.AmqpConfig;
import com.neueda.etiqet.transport.amqp.config.model.ExchangeConfig;
import com.neueda.etiqet.transport.amqp.config.model.QueueConfig;
import com.rabbitmq.client.BuiltinExchangeType;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.OptionalInt;

import static java.util.OptionalInt.empty;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

public class AmqpConfigExtractor {
    private AmqpConfigXmlParser configXmlParser;
    private Map<String, Queue> queues;

    public AmqpConfigExtractor() {
        this(new AmqpConfigXmlParser());
    }

    AmqpConfigExtractor(final AmqpConfigXmlParser configXmlParser) {
        this.configXmlParser = configXmlParser;
    }

    public AmqpConfig retrieveConfiguration(final String configFile) throws EtiqetException {
        AmqpConfiguration xmlConfiguration = configXmlParser.parse(configFile);
        queues = queues(xmlConfiguration.getQueues().getQueue());
        return new AmqpConfig(
            xmlConfiguration.getHost(),
            portNumber(xmlConfiguration),
            exchangeConfigs(xmlConfiguration),
            binaryMessageConverterDelegateClass(xmlConfiguration)
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
            default: throw new EtiqetRuntimeException("No built-in exchange type found for " + xmlExchangeType.name());
        }
    }

    private Class binaryMessageConverterDelegateClass(AmqpConfiguration xmlConfiguration) throws EtiqetException{
        final String binaryMessageConverterClassName = xmlConfiguration.getBinaryMessageConverterDelegate();
        if (StringUtils.isEmpty(binaryMessageConverterClassName)) {
            return ByteArrayConverterDelegate.class;
        }
        try {
            return Class.forName(xmlConfiguration.getBinaryMessageConverterDelegate());
        } catch (ReflectiveOperationException e) {
            throw new EtiqetException("Unable to find BinaryMessageConverterDelegate class " + xmlConfiguration.getBinaryMessageConverterDelegate());
        }
    }

    private OptionalInt portNumber(AmqpConfiguration xmlConfiguration) {
        String port = xmlConfiguration.getPort();
        if (StringUtils.isEmpty(port)) {
            return empty();
        }
        return OptionalInt.of(Integer.valueOf(port));
    }

}
