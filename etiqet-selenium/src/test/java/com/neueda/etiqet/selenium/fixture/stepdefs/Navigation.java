package com.neueda.etiqet.selenium.fixture.stepdefs;

import cucumber.api.java.en.When;
import com.neueda.etiqet.selenium.fixture.SeleniumHandlers;

public class Navigation {

    @When("^I? ?go back a page$")
    public void back() {
        SeleniumHandlers.back();
    }

    @When("^I? ?go forward a page$")
    public void forward() {
        SeleniumHandlers.forward();
    }

    // ALIASES

    @When("^I? ?(?:press|select|click|click on|go) back$")
    public void backAlias() {
        back();
    }

    @When("^I? ?(?:press|select|click|click on|go) forward$")
    public void forwardAlias() {
        forward();
    }
}
