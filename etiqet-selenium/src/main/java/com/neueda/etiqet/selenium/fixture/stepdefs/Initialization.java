package com.neueda.etiqet.selenium.fixture.stepdefs;

import cucumber.api.java.en.Given;
import com.neueda.etiqet.selenium.fixture.SeleniumHandlers;

public class Initialization {

    @Given("^I? ?open (?:the)? ?browser$")
    public void openBrowser() {
        SeleniumHandlers.openBrowser();
    }

    @Given("(?:the)? ?\"([^\"]*)\" browser is open")
    public void openGivenWebBrowser(String browserName) throws Throwable {
        SeleniumHandlers.openBrowser(browserName);
    }

    @Given("^I? ?(?:(?:go to)|(?:visit)) (?:the)? ?(?:website|url) \"([^\"]*)\"$")
    public void goToUrl(String url) {
        SeleniumHandlers.goToUrl(url);
    }

    @Given("^(?:the)? ?window is maximized$")
    public void maximizeWindow() {
        SeleniumHandlers.maximizeWindow();
    }

    // ALIASES

    @Given("^I (?:choose|select|am using) ?(?:the)? browser \"([^\"]*)\"$")
    public void selectDriver(String browserName) throws Throwable {
        openGivenWebBrowser(browserName);
    }

    @Given("^I? ?maximize (?:the)? ?window$")
    public void maximizeWindowAlias() {
        maximizeWindow();
    }

    @Given("^I? ?launch (?:the )? ?browser$")
    public void openBrowserAlias() {
        openBrowser();
    }

    @Given("^(?:the)? ?browser (?:is|has been) (?:launched|(?:opened|open))$")
    public void openBrowserAlias2() {
        openBrowser();
    }
}
