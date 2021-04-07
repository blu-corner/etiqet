package com.neueda.etiqet.sql;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
    plugin = {"pretty", "html:target/cucumber"},
    strict = true,
    monochrome = true,
    features="src/test/resources/scenarios/connecting.feature",
    glue={"com.neueda.etiqet"})
public class ConnectRunner {
}
