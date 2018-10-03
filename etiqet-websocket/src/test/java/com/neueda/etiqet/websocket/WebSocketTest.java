package com.neueda.etiqet.websocket;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(plugin = {"pretty", "html:target/cucumber"},
        features = "src/test/resources/scenarios/websocket_example.feature",
        glue = { "com.neueda.etiqet.fixture", "com.neueda.etiqet.websocket.fixture" })
public class WebSocketTest { }
