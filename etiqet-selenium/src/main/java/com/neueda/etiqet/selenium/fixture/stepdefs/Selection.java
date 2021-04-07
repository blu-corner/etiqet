package com.neueda.etiqet.selenium.fixture.stepdefs;

import com.neueda.etiqet.selenium.SeleniumException;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import com.neueda.etiqet.selenium.fixture.SeleniumHandlers;
import org.openqa.selenium.NoSuchElementException;

public class Selection {

    @When("^I? ?select element by css selector using value \"([^\"]*)\"( relative to the selected element)?(?: as \"([^\"]*)\"?)?$")
    public void selectElementByCss(String locator, String relative, String alias) throws NoSuchElementException {
        SeleniumHandlers.selectElementByCss(locator, relative, alias);
    }

    @When("^I? ?select elements by css selector using value \"([^\"]*)\"( relative to the selected element)?$")
    public void selectElementsByCss(String locator, String relative) throws NoSuchElementException {
        SeleniumHandlers.selectElementsByCss(locator, relative);
    }

    @When("^I? ?select element by xpath using value \"([^\"]*)\"( relative to the selected element)?(?: as \"([^\"]*)\"?)?$")
    public void selectElementByXpath(String locator, String relative, String alias) throws NoSuchElementException {
        SeleniumHandlers.selectElementByXpath(locator, relative, alias);
    }

    @When("^I? ?select elements by xpath using value \"([^\"]*)\"( relative to the selected element)?$")
    public void selectElementsByXpath(String locator, String relative) throws NoSuchElementException {
        SeleniumHandlers.selectElementsByXpath(locator, relative);
    }

    @When("^I? ?select element by id using value \"([^\"]*)\"( relative to the selected element)?(?: as \"([^\"]*)\"?)?$")
    public void selectElementById(String locator, String relative, String alias) throws NoSuchElementException {
        SeleniumHandlers.selectElementById(locator, relative, alias);
    }

    @When("^I? ?select element by tag using value \"([^\"]*)\"( relative to the selected element)?(?: as \"([^\"]*)\"?)?$")
    public void selectElementByTag(String locator, String relative, String alias) throws NoSuchElementException {
        SeleniumHandlers.selectElementByTag(locator, relative, alias);
    }

    @When("^I? ?select elements by tag using value \"([^\"]*)\"( relative to the selected element)?$")
    public void selectElementsByTag(String locator, String relative) throws NoSuchElementException {
        SeleniumHandlers.selectElementsByTag(locator, relative);
    }

    @When("^I? ?select element by class name using value \"([^\"]*)\"( relative to the selected element)?(?: as \"([^\"]*)\"?)?$")
    public void selectElementByClassName(String locator, String relative, String alias) throws NoSuchElementException {
        SeleniumHandlers.selectElementByClassName(locator, relative, alias);
    }

    @When("^I? ?select elements by class name using value \"([^\"]*)\"( relative to the selected element)?$")
    public void selectElementsByClassName(String locator, String relative) throws NoSuchElementException {
        SeleniumHandlers.selectElementsByClassName(locator, relative);
    }

    @When("^I? ?select element by link text using value \"([^\"]*)\"( relative to the selected element)?(?: as \"([^\"]*)\"?)?$")
    public void selectElementByLinkText(String locator, String relative, String alias) throws NoSuchElementException {
        SeleniumHandlers.selectElementByLinkText(locator, relative, alias);
    }

    @When("^I? ?select element by partial link text using value \"([^\"]*)\"( relative to the selected element)?(?: as \"([^\"]*)\"?)?$")
    public void selectElementByPartialLinkText(String locator, String relative, String alias) throws NoSuchElementException {
        SeleniumHandlers.selectElementByPartialLinkText(locator, relative, alias);
    }

    @When("^I? ?select element at index (-?\\d+) from selected elements$")
    public void selectFromElements(Integer index) {
        SeleniumHandlers.selectElementAtIndex(index);
    }

    @When("^I? ?select (?:the)? ?last element from selected elements$")
    public void selectFromElements() {
        SeleniumHandlers.selectLastElement();
    }

    @When("^I? ?select first element from elements by contained text \"([^\"]*)\"(?: as \"([^\"]*)\"?)?$")
    public void selectFirstElementByContainedText(String text, String alias){
        SeleniumHandlers.selectFirstElementByContainedText(text, alias);
    }

    @When("^I? ?select element from dropdown by (?:the)? ?label \"([^\"]*)\"$")
    public void selectFromDropdownByLabel(String label) {
        SeleniumHandlers.selectFromDropdownByLabel(label);
    }

    @When("^I? ?select element from dropdown by (?:the)? ?value \"([^\"]*)\"$")
    public void selectFromDropdownByValue(String value) {
        SeleniumHandlers.selectFromDropdownByValue(value);
    }

    @When("^I? ?select element from dropdown by (?:the)? ?value (\\d+)$")
    public void selectFromDropdownByIndex(int index) {
        SeleniumHandlers.selectFromDropdownByIndex(index);
    }

    @When("^I? ?filter selected elements by xpath using value \"([^\"]*)\"$")
    public void filterSelectedElementsByXPath(String filter) {
        SeleniumHandlers.filterSelectedElementsByXPath(filter);
    }

    @When("^I? ?filter selected elements based on if their descendents contain the text \"([^\"]*)\"$")
    public void filterSelectedElementsDescendentsText(String filterText) {
        SeleniumHandlers.filterSelectedElementsDescendentsText(filterText);
    }

    @When("^I? ?select (?:the)? ?children of selected element$")
    public void selectChildrenForSelectedElement(){
        SeleniumHandlers.selectAllChildrenForSelectedElement();
    }

    @When("^I? ?select (?:the)? ?descendants of (?:the)? ?selected element$")
    public void selectAllDescendantsForSelectedElement(){
        SeleniumHandlers.selectAllDescendantsForSelectedElement();
    }

    @When("^I? ?select everything following (?:the)? ?closing tag of the selected element$")
    public void selectEverythingFollowingTheSelectedElement(){
        SeleniumHandlers.selectEverythingFollowingTheSelectedElement();
    }

    @When("^I? ?select all siblings preceding (?:the)? ?selected element$")
    public void selectTheSiblingsPrecedingTheSelectedElement(){
        SeleniumHandlers.selectTheSiblingsPrecedingTheSelectedElement();
    }

    @When("^I? ?select all siblings following (?:the)? ?selected element$")
    public void selectTheSiblingsFollowingTheSelectedElement(){
        SeleniumHandlers.selectTheSiblingsFollowingTheSelectedElement();
    }

    @When("^I? ?select all siblings of (?:the)? ?selected element$")
    public void selectTheSiblingsOfTheSelectedElement(){
        SeleniumHandlers.selectTheSiblingsOfTheSelectedElement();
    }

    @When("^I? ?select (?:the)? ?parent of (?:the)? ?selected element$")
    public void selectTheParentOfSelectedElement(){
        SeleniumHandlers.selectTheParentOfSelectedElement();
    }

    @When("^I? ?select all ancestors of (?:the)? ?selected element$")
    public void selectAllAncestorsForSelectedElement(){
        SeleniumHandlers.selectAllAncestorsForSelectedElement();
    }

    @When("^I? ?select (?:the)? ?named element \"([^\"]*)\"$")
    public void selectNamedElement(String elementName) throws SeleniumException {
        SeleniumHandlers.selectNamedElement(elementName);
    }

    @When("^I? ?name (?:the)? ?selected element as \"([^\"]*)\"$")
    public void nameSelectedElement(String alias) {
        SeleniumHandlers.nameSelectedElement(alias);
    }

    @When("^I? ?save selected elements text as \"([^\"]*)\"$")
    public void saveSelectedElementsInnerTextAs(String alias) {
        SeleniumHandlers.saveSelectedElementsInnerTextAs(alias);
    }

    @When("^I? ?save selected elements count as \"([^\"]*)\"$")
    public void saveSelectedElementsCountAs(String alias) {
        SeleniumHandlers.saveSelectedElementsCountAs(alias);
    }

    @When("^I? ?save selected element's attribute \"([^\"]*)\" value as \"([^\"]*)\"$")
    public void saveSelectedElementsInnerTextAs(String attributeName, String alias) {
        SeleniumHandlers.saveSelectedElementsAttributeValueAs(attributeName, alias);
    }

    @Then("^I? ?clear (?:the)? ?selected element$")
    public void clearSelectedElement() {
        SeleniumHandlers.clearSelectedElement();
    }

    @Then("^I? ?clear (?:the)? ?selected elements$")
    public void clearSelectedElements() {
        SeleniumHandlers.clearSelectedElements();
    }

    @Then("^I? ?clear (?:the)? ?named elements$")
    public void clearNamedElements() {
        SeleniumHandlers.clearNamedElements();
    }

    @Then("^I? ?clear (?:the)? ?named values$")
    public void clearNamedValues() {
        SeleniumHandlers.clearNamedValues();
    }
}
