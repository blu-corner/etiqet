package com.neueda.etiqet.selenium.fixture.stepdefs;

import cucumber.api.java.en.When;
import com.neueda.etiqet.selenium.fixture.SeleniumHandlers;

public class Input {

    @When("^I? ?(?:type|enter|input) (?:the text|text)? ?\"([^\"]*)\"$")
    public void enterText(String text) {
        SeleniumHandlers.enterText(text);
    }

    /**Keys*/

    @When("^I? press enter ?(?:key)?$")
    public void pressEnter() { SeleniumHandlers.pressEnter(); }

    /**ArrowKeys*/

    @When("^I? press arrow down key(?: (\\d+) times)?$")
    public void pressArrowDownKeyTimes(Integer times) { SeleniumHandlers.pressArrowDownKeyTimes(times);}

    @When("^I? press arrow up key(?: (\\d+) times)?$")
    public void pressArrowUpKeyTimes(Integer times) { SeleniumHandlers.pressArrowKeyUpTimes(times);}

    @When("^I? press arrow left key(?: (\\d+) times)?$")
    public void pressArrowLeftKeyTimes(Integer times) { SeleniumHandlers.pressArrowLeftKeyTimes(times);}

    @When("^I? press arrow right key(?: (\\d+) times)?")
    public void pressArrowRightKeyTimes(Integer times) {SeleniumHandlers.pressArrowRightKeyTimes(times);}

    /**OtherKeys*/

    @When("^I? press backspace(?: (\\d+) times)?$")
    public void pressBackspaceTimes(Integer times) {SeleniumHandlers.pressBackspaceTimes(times);}

    @When("^I? press delete$")
    public void pressDelete(){SeleniumHandlers.pressDelete();}

    @When("^I? press ctrl key$")
    public void pressCtrlKey(){SeleniumHandlers.pressCtrlKey();}

    @When("^I? press alt key$")
    public void pressAltKey(){SeleniumHandlers.pressAltKey();}

    @When("^I? press shift key$")
    public void pressShiftKey(){SeleniumHandlers.pressShiftKey();}

    @When("^I? press spacebar(?: (\\d+) times)?$")
    public void pressSpacebarTimes(Integer times) {SeleniumHandlers.pressSpacebarTimes(times);}

    @When("^I? press tab(?: (\\d+) times)?$")
    public void pressTabKeyTimes(Integer times) {SeleniumHandlers.pressTabKeyTimes(times);}

    @When("^I? press equals key$")
    public void pressEqualsKey(){SeleniumHandlers.pressEqualsKey();}

    @When("^I? press home key$")
    public void pressHomeKey(){SeleniumHandlers.pressHomeKey();}

    @When("^I? press insert key$")
    public void pressInsertKey(){SeleniumHandlers.pressInsertKey();}

    @When("^I? press arrow page up(?: (\\d+) times)?$")
    public void pressPageUpTimes(Integer times) {SeleniumHandlers.pressPageUpTimes(times);}

    @When("^I? press arrow page down(?: (\\d+) times)?$")
    public void pressPageDownTimes(Integer times) {SeleniumHandlers.pressPageDownTimes(times);}

    /**End of sendKeys*/
}
