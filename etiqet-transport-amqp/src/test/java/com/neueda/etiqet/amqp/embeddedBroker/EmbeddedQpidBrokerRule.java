package com.neueda.etiqet.amqp.embeddedBroker;

import org.apache.qpid.server.SystemLauncher;
import org.junit.rules.ExternalResource;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class EmbeddedQpidBrokerRule extends ExternalResource {

    private SystemLauncher systemLauncher;

    @Override
    protected void before() throws Throwable {
        systemLauncher = new SystemLauncher();
        systemLauncher.startup(createSystemConfig());
    }

    @Override
    protected void after() {
        systemLauncher.shutdown();
    }

    private Map<String, Object> createSystemConfig() {
        Map<String, Object> attributes = new HashMap<>();
        URL initialConfig = getClass().getClassLoader().getResource("config/qpidConfig.json");
        attributes.put("type", "Memory");
        attributes.put("initialConfigurationLocation", initialConfig.toExternalForm());
        attributes.put("startupLoggedToSystemOut", true);
        return attributes;
    }
}
