# Etiqet Transport AMQP

This module provides the transport functionality to publish and subscribe using AMQP 0.91, using RabbitMQ as the underlying broker client. The following methods are implemented here and accessible to the **etiqet-exchange-broker** client:

```java
public interface ExchangeTransport extends Transport {

    void sendToExchange(Cdr cdr, String exchangeName) throws EtiqetException;

    void sendToExchange(Cdr cdr, String exchangeName, String routingKey) throws EtiqetException;

    void subscribeToQueue(String queueName, Consumer<Cdr> cdrListener) throws EtiqetException;

    Cdr subscribeAndConsumeFromQueue(String queueName, Duration timeout) throws EtiqetException;
}

```
More information about the model used in AMQP 0.91 can be found at https://www.rabbitmq.com/tutorials/amqp-concepts.html

## Configuration
The configuration for this transport must be provided in a XML file. This configuration must include exchanges, queues and their relationships, so they can be bound together. As an example:
```xml
<e:amqpConfiguration
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:e="http://www.neueda.com/etiqet/transport/amqp"
    host="localhost"
    binaryMessageConverterDelegate="com.neueda.etiqet.core.transport.delegate.StringBinaryMessageConverterDelegate">
    <queues>
        <queue name="queue1"/>
        <queue name="queue2"/>
    </queues>
    <exchanges>
        <exchange name="exchange" exchangeType="fanout">
            <queue-ref ref="queue1"/>
            <queue-ref ref="queue2"/>
        </exchange>
    </exchanges>
</e:amqpConfiguration>
```

The above configuration binds a fanout exchange with two queues. Any message published to the exchange will be also available in both queues.

The following elements can be defined in the configuration file:
* **host** (required). 
* **port**. 5672 by default.
* **binaryMessageConverterDelegate**. If the codec used doesn't convert to/from a byte array, an additional binary converter can be specified for the binary conversion. If not specified, _ByteArrayConverterDelegate_ will be used, assuming that the codec converts to byte array.
* **queues**. The list of queues that will be bound to any exchanges. Properties:
  * name (required)
  * autodelete
  * durable
  * exclusive
* **exchanges**. A list of exchanges where messages will be published. They contain a list of _queue_ref_ elements, each of which will refer to the specified queues. Each _queue_ref_ element might also include a binding key.
  * **exchangeType** (required). Determines the routing mechanism. There are three types:
    * **direct** The routing key will be used to determine which queue to deliver the message.
	* **fanout** The message will be delivered to all queues bound to the exchange, ignoring the routing key.
	* **topic** The message will be delivered to any queues which binding key matches the message's routing key.
