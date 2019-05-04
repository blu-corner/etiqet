package com.neueda.etiqet.selenium.runner;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
    plugin = {"pretty", "html:target/cucumber"},
    strict = true,
    monochrome = true,
    features = "src/test/resources/scenarios/examples_selenium_with_rest.feature",
    glue = {"com.neueda.etiqet"})
public class SeleniumFixRunner {

}
