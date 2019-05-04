package com.neueda.etiqet.selenium.fixture.stepdefs;

import com.neueda.etiqet.selenium.fixture.SeleniumHandlers;
import cucumber.api.java.en.When;
import javax.annotation.Nullable;

public class Properties {

    @When("^I get index for element amongst selected elements containing \"([^\"]*)\"$")
    public void getIndexOfElementContaining(String text) {
        SeleniumHandlers.getIndexOfElementContaining(text);
    }

    @When("^I get the value of attribute \"([^\"]*)\" from the selected element (?: named (\"[^\"]*\")?)?$")
    public void getAttribute(String attr, @Nullable String alias) {
        SeleniumHandlers.getAttribute(attr, alias);
    }

    @When("^I change the value of attribute \"([^\"]*)\" in selected element (?: named (\"[^\"]*\")?)? to \"([^\"]*)\"$")
    public void setAttribute(String attr, @Nullable String alias, String value) {
        SeleniumHandlers.changeAttribute(attr, alias, value);
    }

    @When("^I change the value of the selected attribute$")
    public void setSelectedAtrributeValue(String attr, String value) {
        SeleniumHandlers.changeAttributeOf(attr, value);
    }

}
