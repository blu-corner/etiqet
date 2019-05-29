package com.neueda.etiqet.selenium.fixture.stepdefs;

import com.neueda.etiqet.selenium.fixture.SeleniumHandlers;
import cucumber.api.java.en.When;
import javax.annotation.Nullable;

public class Properties {

    @When("^I get index for element amongst selected elements containing \"([^\"]*)\"$")
    public void getIndexOfElementContaining(String text) {
        SeleniumHandlers.getIndexOfElementContaining(text);
    }

    @When("^I change the value of attribute \"([^\"]*)\" in selected element (?: named (\"[^\"]*\")?)? to \"([^\"]*)\"$")
    public void setAttribute(String attr, @Nullable String alias, String value) {
        SeleniumHandlers.setAttributeValue(attr, alias, value);
    }

    @When("^I? ?set the value of (?:the)? ?attribute \"([^\"]*)\" to \"([^\"]*)\"(?: for named element \"([^\"]*)\"?)?$")
    public void setSelectedAttributeValue(String attr, String value, String alias) {
        SeleniumHandlers.setAttributeValue(attr, value, alias);
    }
}
