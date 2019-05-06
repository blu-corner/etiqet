package com.neueda.etiqet.selenium.fixture.stepdefs;

import com.neueda.etiqet.selenium.fixture.SeleniumHandlers;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.When;

public class FlowControl {

    @Given("^implicit wait is set to (\\d+) nanoseconds$")
    public void setImplicitWaitNano(long nanos) {
        SeleniumHandlers.setImplicitWaitNano(nanos);
    }

    @Given("^implicit wait is set to (\\d+) milliseconds$")
    public void setImplicitWaitMs(long ms) {
        SeleniumHandlers.setImplicitWaitMs(ms);
    }

    @Given("^implicit wait is set to (\\d+) seconds$")
    public void setImplicitWaitSeconds(long seconds) {
        SeleniumHandlers.setImplicitWaitSeconds(seconds);
    }

    @Given("^I? ?am waiting for elements to be clickable with a timeout of (\\d+) seconds(?: when selecting)?$")
    public void setExplicitWaitToClickable(int timout) {
        SeleniumHandlers.setExplicitWaitToClickable(timout);
    }

    @Given("^I? ?am waiting for elements to be present with a timeout of (\\d+) seconds(?: when selecting)?$")
    public void setExplicitWaitToPresent(int timout) {
        SeleniumHandlers.setExplicitWaitToPresent(timout);
    }

    @Given("^I? ?am waiting for elements to be visible with a timeout of (\\d+) seconds(?: when selecting)?$")
    public void setExplicitWaitToVisibility(int timout) {
        SeleniumHandlers.setExplicitWaitToVisibility(timout);
    }

    @Given("^no explicit wait$")
    public void clearExplicitWait() {
        SeleniumHandlers.clearExplicitWait();
    }

    @When("^I? ?pause for (\\d+) milliseconds$")
    public void pauseMs(long milliseconds) {
        SeleniumHandlers.pauseMs(milliseconds);
    }

    @When("^I? ?pause for (\\d+) seconds$")
    public void pauseSecs(long seconds) {
        SeleniumHandlers.pauseSecs(seconds);
    }

    // ALIASES

    @Given("^I? ?set implicit wait to (\\d+) nanoseconds$")
    public void setImplicitWaitNanoAlias(long nanos) {
        setImplicitWaitNano(nanos);
    }

    @Given("^I? ?set implicit wait to (\\d+) milliseconds$")
    public void setImplicitWaitMsAlias(long ms) {
        setImplicitWaitMs(ms);
    }

    @Given("^I? ?set implicit wait to (\\d+) seconds$")
    public void setImplicitWaitSecondsAlias(long seconds) {
        setImplicitWaitSeconds(seconds);
    }

    @Given("^I? ?disable explicit wait$")
    public void clearExplicitWaitAlias2() {
        clearExplicitWait();
    }

    @Given("^explicit wait is disabled$")
    public void clearExplicitWaitAlias3() {
        SeleniumHandlers.clearExplicitWait();
    }
}
