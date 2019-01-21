package com.neueda.etiqet.rabbitmq;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(plugin = {"pretty", "html:target/cucumber"},
        features = "src/test/resources/scenarios/rabbitmq.feature",
        glue = { "com.neueda.etiqet.fixture" })
public class RabbitMqTest {}
