package com.neueda.etiqet.selenium.fixture.stepdefs;

import com.neueda.etiqet.selenium.browser.BrowserBase;
import com.neueda.etiqet.selenium.fixture.SeleniumHandlers;
import cucumber.api.java.After;
import cucumber.api.java.en.Then;

public class Exiting {

    @Then("^I? ?take a? ?screenshot$")
    public void takeScreenshot() {
        BrowserBase.getCurrentBrowser().takeScreenshot();
    }

    @Then("^I? ?(?:close|quit)(?:(?: the)? browser)?$")
    public void closeBrowser() {
        SeleniumHandlers.closeBrowser();
    }

    @After
    @Then("^--AUTO_CLOSE--DO NOT USE$")
    public void autocloseBrowser() {
        SeleniumHandlers.autoCloseBrowser();
    }
}
