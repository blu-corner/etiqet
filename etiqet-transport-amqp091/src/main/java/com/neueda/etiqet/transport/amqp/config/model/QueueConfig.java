package com.neueda.etiqet.transport.amqp.config.model;

import java.util.Optional;

public class QueueConfig {
    private String name;
    private String bindingKey;
    private boolean durable;
    private boolean exclusive;
    private boolean autodelete;

    public QueueConfig(String name, String bindingKey, boolean durable, boolean exclusive, boolean autodelete) {
        this.name = name;
        this.bindingKey = bindingKey;
        this.durable = durable;
        this.exclusive = exclusive;
        this.autodelete = autodelete;
    }

    public String getName() {
        return name;
    }

    public Optional<String> getBindingKey() {
        return Optional.ofNullable(bindingKey);
    }

    public boolean isDurable() {
        return durable;
    }

    public boolean isExclusive() {
        return exclusive;
    }

    public boolean isAutodelete() {
        return autodelete;
    }
}
