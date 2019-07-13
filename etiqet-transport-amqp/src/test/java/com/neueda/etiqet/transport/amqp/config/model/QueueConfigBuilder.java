package com.neueda.etiqet.transport.amqp.config.model;

public class QueueConfigBuilder {
    private String name;
    private String bindingKey;
    private boolean durable;
    private boolean exclusive;
    private boolean autodelete;

    private QueueConfigBuilder(String name) {
        this.name = name;
        durable = exclusive = autodelete = false;
    }

    public static QueueConfigBuilder aQueueConfig(String name) {
        return new QueueConfigBuilder(name);
    }

    public QueueConfigBuilder withBindingKey(String bindingKey) {
        this.bindingKey = bindingKey;
        return this;
    }

    public QueueConfig build() {
        return new QueueConfig(name, bindingKey, durable, exclusive, autodelete);
    }

}
