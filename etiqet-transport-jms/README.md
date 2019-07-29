# Etiqet Transport JMS

This module provides the transport needed to send messages using JMS API. The client can specify any JMS-compliant implementation. The particular connection factory implementation must be available in the classpath.

## Transport Configuration ##
The following information must be provided in XML format:
- **Implementation class** Connection factory implementation. Must be available in the classpath.
- **Constructor arguments** Optional. A series of arguments that will be passed to the constructor. The type must also be provided (string)
- **Properties** Optional. A series of properties that will be set using their corresponding set method.
- **Default topic**

As an example, the following configuration would use ActiveMQ connection factory:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<e:jmsConfiguration implementation="org.apache.activemq.ActiveMQConnectionFactory"
                  defaultTopic="testTopic"
                  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                  xmlns:e="http://www.neueda.com/etiqet/transport/jms">
    <constructor-args>
        <arg argType="string" argValue="tcp://localhost:61616"/>
    </constructor-args>
    <properties>
        <property argType="string" argName="userName" argValue="USERNAME"/>
        <property argType="string" argName="password" argValue="PASSWORD"/>
    </properties>
</e:jmsConfiguration>
```

The connection factory will be instantiated using reflection; the equivalent sequence of actions would be:
```java
ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
connectionFactory.setUserName("USERNAME");
connectionFactory.setPassword("PASSWORD");
```


