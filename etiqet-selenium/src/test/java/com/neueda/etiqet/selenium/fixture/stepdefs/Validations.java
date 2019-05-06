package com.neueda.etiqet.selenium.fixture.stepdefs;

import com.neueda.etiqet.selenium.fixture.SeleniumHandlers;
import cucumber.api.java.en.Then;

import java.util.ArrayList;

public class Validations {

    @Then("^I? ?check (?:the)? ?current url is \"([^\"]*)\"$")
    public void checkCurrentUrl(String expectedUrl) {
        SeleniumHandlers.checkCurrentUrl(expectedUrl);
    }

    @Then("^I? ?check (?:the)? ?current url contains \"([^\"]*)\"$")
    public void checkCurrentUrlContains(String urlPart) {
        SeleniumHandlers.checkCurrentUrlContains(urlPart);
    }

    @Then("^I? ?check (?:the)? ?page title is \"([^\"]*)\"$")
    public void checkPageTitle(String pageTitle) {
        SeleniumHandlers.checkPageTitle(pageTitle);
    }

    @Then("^I? ?check (?:the)? ?page title contains \"([^\"]*)\"$")
    public void checkPageTitleContains(String expectedTitlePart) {
        SeleniumHandlers.checkPageTitleContains(expectedTitlePart);
    }

    @Then("^I? ?check (?:the)? ?page contains the text \"([^\"]*)\"(?: with a timeout of (\\d+) seconds)?$")
    public void checkPageContainsText(String expectedText, Long timeout) {
        SeleniumHandlers.checkPageContainsText(expectedText, timeout);
    }

    @Then("^I? ?check (?:the)? ?page does not contain the text \"([^\"]*)\"(?: with a timeout of (\\d+) seconds)?$")
    public void checkPageDoesNotContainsText(String unexpectedText, Long timeout) {
        SeleniumHandlers.checkPageDoesNotContainText(unexpectedText, timeout);
    }

    @Then("^I? ?check (?:that)? ?any element's inner text is equal to \"([^\"]*)\"(?: with a timeout of (\\d+) seconds)?$")
    public void checkPageElementHasInnerText(String expectedText, Long timeout) {
        SeleniumHandlers.checkPageElementHasInnerText(expectedText, timeout);
    }

    @Then("^I? ?check (?:that)? ?no element's inner text is equal to \"([^\"]*)\"(?: with a timeout of (\\d+) seconds)?$")
    public void checkNoPageElementHasInnerText(String unexpectedText, Long timeout) {
        SeleniumHandlers.checkNoPageElementHasInnerText(unexpectedText, timeout);
    }

    @Then("^I? ?check (?:the)? ?selected element's inner text is equal to \"([^\"]*)\"$")
    public void checkInnerText(String expectedText) {
        SeleniumHandlers.checkInnerText(expectedText);
    }

    @Then("^I? ?check (?:the)? ?selected element's inner text contains \"([^\"]*)\"$")
    public void checkInnerTextContains(String expectedSubText) {
        SeleniumHandlers.checkInnerTextContains(expectedSubText);
    }

    @Then("^I? ?check multiple (?:the)? ?selected elements' inner text is equal to \"([^\"]*)\"$")
    public void checkMultipleElementsInnerTextEquals(ArrayList<String> expectedText) {
        SeleniumHandlers.checkMultipleElementsInnerTextEquals(expectedText);
    }

    @Then("^I? ?check multiple (?:the)? ?selected elements' inner text contains \"([^\"]*)\"$")
    public void checkMultipleElementsInnerTextContains(ArrayList<String> expectedSubText) {
        SeleniumHandlers.checkMultipleElementsInnerTextContains(expectedSubText);
    }

    @Then("^I? ?check if (?:the)? ?selected element's descendents contain the text \"([^\"]*)\"$")
    public void checkDescendents(String expectedText) {
        SeleniumHandlers.checkDescendents(expectedText);
    }

    @Then("^I? ?check (?:the)? ?attribute \"([^\"]*)\" exists$")
    public void checkAttributeExists(String attribute) {
        SeleniumHandlers.checkAttributeExists(attribute);
    }

    @Then("^I? ?check (?:the)? ?element's attribute \"([^\"]*)\" is equal to \"([^\"]*)\"$")
    public void checkAttributeValue(String attribute, String value) {
        SeleniumHandlers.checkAttributeValue(attribute, value);
    }

    @Then("^I? ?check (?:the)? ?selected element's attribute \"([^\"]*)\" contains \"([^\"]*)\"$")
    public void checkAttributeValueContains(String attribute, String value) {
        SeleniumHandlers.checkAttributeValueContains(attribute, value);
    }

    /**
     * @param expectedCount expected number of elements in selectedElements
     */
    @Then("^I? ?check (?:the)? ?number of elements found is (\\d+)$")
    public void checkNumberOfElementsFound(int expectedCount) {
        SeleniumHandlers.checkNumberOfElementsFound(expectedCount);
    }

    /**
     * @param expectedCount expected number of elements in selectedElements
     */
    @Then("^I? ?check (?:the)? ?number of elements found is at least (\\d+)$")
    public void checkNumberOfElementsFoundAtLeast(int expectedCount) {
        SeleniumHandlers.checkNumberOfElementsFoundAtLeast(expectedCount);
    }

    @Then("^I? ?check (?:the)? ?selected element is displayed$")
    public void checkElementIsDisplayed() {
        SeleniumHandlers.checkElementIsDisplayed();
    }

    @Then("^I? ?check (?:the)? ?selected element is selected$")
    public void checkElementIsSelected() {
        SeleniumHandlers.checkElementIsSelected();
    }

    @Then("^I? ?check (?:the)? ?selected element is enabled$")
    public void checkElementIsEnabled() {
        SeleniumHandlers.checkElementIsEnabled();
    }

    @Then("^I? ?check (?:the)? ?invisibility of (?:the)? ?selected element with a timeout of (\\d+) seconds$")
    public void invisibilityOfElement(long timeout) {
        SeleniumHandlers.invisibilityOfElement(timeout);
    }

    @Then("^I? ?check (?:the)? ?invisibility of (?:the)? ?selected elements with a timeout of (\\d+) seconds$")
    public void invisibilityOfAllElements(long timeout) {
        SeleniumHandlers.invisibilityOfAllElements(timeout);
    }

    @Then("^I? ?check (?:the)? ?selected element is in focus with a timeout of (\\d+) seconds$")
    public void elementIsSelected(long timeout) {
        SeleniumHandlers.elementIsSelected(timeout);
    }

    @Then("^I? ?check (?:the)? ?selected element is not in focus with a timeout of (\\d+) seconds$")
    public void elementIsNotSelected(long timeout) {
        SeleniumHandlers.elementIsNotSelected(timeout);
    }

    @Then("^I? ?check (?:the)? ?selected element is no longer attached to (?:the)? ?dom with a timeout of (\\d+) seconds$")
    public void elementIsStale(long timeout) {
        SeleniumHandlers.elementIsStale(timeout);
    }

    @Then("^I? ?check (?:the)? ?text \"([^\"]*)\" is present in the selected element with a timeout of (\\d+) seconds$")
    public void textIsPresent(String expectedText, long timeout) {
        SeleniumHandlers.textIsPresent(expectedText, timeout);
    }

    @Then("^I? ?check (?:the)? ?text \"([^\"]*)\" is present in the selected element's value with a timeout of (\\d+) seconds$")
    public void textIsPresentInValue(String expectedText, long timeout) {
        SeleniumHandlers.textIsPresentInValue(expectedText, timeout);
    }

    @Then("^I? ?check (?:the)? ?page title is \"([^\"]*)\" with a timeout of (\\d+) seconds$")
    public void checkTitleWithWait(String expectedTitle, long timeout) {
        SeleniumHandlers.checkTitleWithWait(expectedTitle, timeout);
    }

    @Then("^I? ?check (?:the)? ?page title contains \"([^\"]*)\" with a timeout of (\\d+) seconds$")
    public void checkTitleContainsWithWait(String expectedTitlePart, long timeout) {
        SeleniumHandlers.checkTitleContainsWithWait(expectedTitlePart, timeout);
    }

    @Then("^I? ?check the element \"([^\"]*)\"(?:'s)? inner text is equal to the element \"([^\"]*)\"(?:'s)? inner text$")
    public void checkTwoElementsTextIsEqual(String firstElementText, String secondElementText) {
        SeleniumHandlers.checkTwoElementsTextIsEqual(firstElementText, secondElementText);
    }

    @Then("^I? ?check the element \"([^\"]*)\"'s attribute \"([^\"]*)\"'s value is equal to " +
        "the element \"([^\"]*)\"'s attribute \"([^\"]*)\"'s value$")
    public void checkTwoElementsAttrValIsEqual(String firstElement, String firstAttr, String secondElement,
        String secondAttr) {
        SeleniumHandlers.checkTwoElementsAttrValIsEqual(firstElement, firstAttr, secondElement, secondAttr);
    }

    @Then("^I? ?check the named value \"([^\"]*)\" is equal to the named value \"([^\"]*)\"$")
    public void checkNamedValuesAreEqual(String firstAlias, String secondAlias) {
        SeleniumHandlers.checkNamedValuesAreEqual(firstAlias, secondAlias);
    }

    @Then("^I? ?check the named value \"([^\"]*)\" is less than the named value \"([^\"]*)\"$")
    public void checkNamedValueIsLessThan(String firstAlias, String secondAlias) {
        SeleniumHandlers.checkNamedValueIsLessThan(firstAlias, secondAlias);
    }

    @Then("^I? ?check the named value \"([^\"]*)\" is greater than the named value \"([^\"]*)\"$")
    public void checkNamedValueIsGreaterThan(String firstAlias, String secondAlias) {
        SeleniumHandlers.checkNamedValueIsGreaterThan(firstAlias, secondAlias);
    }
}
