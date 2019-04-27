package com.neueda.etiqet.selenium.fixture.stepdefs;

import com.neueda.etiqet.selenium.fixture.SeleniumHandlers;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class Interaction {

    @When("^I? ?(?:left)? ?click (?:on)? ?(?:the)? ?selected element$")
    public void clickElement() {
        SeleniumHandlers.clickElement();
    }

    @When("^I? ?(?:left)? ?click (?:on)? ?and clear (?:the)? ?selected element$")
    public void clickElementAndClear() {
        SeleniumHandlers.clickElement();
        SeleniumHandlers.clearSelectedElements();
    }

    @Then("^I right click the selected element$")
    public void rightClick() {
        SeleniumHandlers.rightClickElement();
    }

    @When("^I? ?double click selected element$")
    public void doubleClickElement() {
        SeleniumHandlers.doubleClickElement();
    }

    @When("^I? ?click and hold element$")
    public void clickAndHold() {
        SeleniumHandlers.clickAndHold();
    }

    /**
     * Convenience method - Submit a form if element is contained within a form
     */
    @When("^I? ?submit(?: form)?$")
    public void submit() {
        SeleniumHandlers.submit();
    }

    @When("^I? ?(?:(?:hover over)|(?:move to)) (?:the)? ?selected element$")
    public void hoverElement() {
        SeleniumHandlers.hoverElement();
    }

    @When("^I? ?(?:press|select|click|click on)? ?(?:refresh|reload) ?(?:the)? ?(?:page)?$")
    public void refresh() {
        SeleniumHandlers.refresh();
    }


}
