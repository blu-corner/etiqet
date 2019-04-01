package com.neueda.etiqet.selenium.runner;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        plugin = {"pretty", "html:target/cucumber"},
        strict = true,
        monochrome = true,
        features="src/test/resources/scenarios/lakeshore/admin_ui_update_edit_admin_users_74.feature:101",
        glue={"com.neueda.etiqet"})
public class Runner {
}
