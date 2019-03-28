package com.neueda.etiqet.selenium.fixture.stepdefs;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import com.neueda.etiqet.selenium.fixture.SeleniumHandlers;
import org.openqa.selenium.NoSuchElementException;

public class Selection {

    /**
     * Selected element will be stored in selectedElement which is the webElement used by default for most step defs
     * An exception is thrown if no element is found and the value of selectedElement will remain unchanged
     * @param locator a string that targets an element in the dom.
     * @throws NoSuchElementException when no element is not found
     */
    @When("^I? ?select element by css selector using value \"([^\"]*)\"(?: as \"([^\"]*)\"?)?$")
    public void selectElementByCss(String locator, String alias) throws NoSuchElementException {
        SeleniumHandlers.selectElementByCss(locator, alias);
    }

    /**
     * Selected elements will be stored in selectedElements and any of these can be assigned to selectedElement
     * by calling selectFromElements(int index) step definition.
     * Selenium returns an empty list if no elements found, however NoSuchElementException is thrown to
     * remain consistent with the selectElementBy... methods.
     * @param locator a string that targets an element in the dom.
     * @throws NoSuchElementException when no element is not found
     */
    @When("^I? ?select elements by css selector using value \"([^\"]*)\"$")
    public void selectElementsByCss(String locator) throws NoSuchElementException {
        SeleniumHandlers.selectElementsByCss(locator);
    }

    /**
     * Selected element will be stored in selectedElement which is the webElement used by default for most step defs
     * An exception is thrown if no element is found and the value of selectedElement will remain unchanged
     * @param locator a string that targets an element in the dom.
     * @throws NoSuchElementException when no element is not found
     */
    @When("^I? ?select element by xpath using value \"([^\"]*)\"(?: as \"([^\"]*)\"?)?$")
    public void selectElementByXpath(String locator, String alias) throws NoSuchElementException {
        SeleniumHandlers.selectElementByXpath(locator, alias);
    }

    /**
     * Selected elements will be stored in selectedElements and any of these can be assigned to selectedElement
     * by calling selectFromElements(int index) step definition.
     * Selenium returns an empty list if no elements found, however NoSuchElementException is thrown to
     * remain consistent with the selectElementBy... methods.
     * @param locator a string that targets an element in the dom.
     * @throws NoSuchElementException when no element is not found
     */
    @When("^I? ?select elements by xpath using value \"([^\"]*)\"$")
    public void selectElementsByXpath(String locator) throws NoSuchElementException {
        SeleniumHandlers.selectElementsByXpath(locator);
    }

    /**
     * Selected element will be stored in selectedElement which is the webElement used by default for most step defs
     * An exception is thrown if no element is found and the value of selectedElement will remain unchanged
     * @param locator a string that targets an element in the dom.
     * @throws NoSuchElementException when no element is not found
     */
    @When("^I? ?select element by id using value \"([^\"]*)\"(?: as \"([^\"]*)\"?)?$")
    public void selectElementById(String locator, String alias) throws NoSuchElementException {
        SeleniumHandlers.selectElementById(locator, alias);
    }

    /**
     * Selected element will be stored in selectedElement which is the webElement used by default for most step defs
     * An exception is thrown if no element is found and the value of selectedElement will remain unchanged
     * @param locator a string that targets an element in the dom.
     * @throws NoSuchElementException when no element is not found
     */
    @When("^I? ?select element by tag using value \"([^\"]*)\"(?: as \"([^\"]*)\"?)?$")
    public void selectElementByTag(String locator, String alias) throws NoSuchElementException {
        SeleniumHandlers.selectElementByTag(locator, alias);
    }

    /**
     * Selected elements will be stored in selectedElements and any of these can be assigned to selectedElement
     * by calling selectFromElements(int index) step definition.
     * Selenium returns an empty list if no elements found, however NoSuchElementException is thrown to
     * remain consistent with the selectElementBy... methods.
     * @param locator a string that targets an element in the dom.
     * @throws NoSuchElementException when no element is not found
     */
    @When("^I? ?select elements by tag using value \"([^\"]*)\"$")
    public void selectElementsByTag(String locator) throws NoSuchElementException {
        SeleniumHandlers.selectElementsByTag(locator);
    }

    /**
     * Selected element will be stored in selectedElement which is the webElement used by default for most step defs
     * An exception is thrown if no element is found and the value of selectedElement will remain unchanged
     * If multiple elements are found the first element will be assigned to selectedElement
     * @param locator a string that targets an element in the dom.
     * @throws NoSuchElementException when no element is not found
     */
    @When("^I? ?select element by class name using value \"([^\"]*)\"(?: as \"([^\"]*)\"?)?$")
    public void selectElementByClassName(String locator, String alias) throws NoSuchElementException {
        SeleniumHandlers.selectElementByClassName(locator, alias);
    }

    /**
     * Selected elements will be stored in selectedElements and any of these can be assigned to selectedElement
     * by calling selectFromElements(int index) step definition.
     * Selenium returns an empty list if no elements found, however NoSuchElementException is thrown to
     * remain consistent with the selectElementBy... methods.
     * @param locator a string that targets an element in the dom.
     * @throws NoSuchElementException when no element is not found
     */
    @When("^I? ?select elements by class name using value \"([^\"]*)\"$")
    public void selectElementsByClassName(String locator) throws NoSuchElementException {
        SeleniumHandlers.selectElementsByClassName(locator);
    }

    /**
     * Selected element will be stored in selectedElement which is the webElement used by default for most step defs
     * An exception is thrown if no element is found and the value of selectedElement will remain unchanged
     * @param locator a string that targets an element in the dom.
     * @throws NoSuchElementException when no element is not found
     */
    @When("^I? ?select element by link text using value \"([^\"]*)\"(?: as \"([^\"]*)\"?)?$")
    public void selectElementByLinkText(String locator, String alias) throws NoSuchElementException {
        SeleniumHandlers.selectElementByLinkText(locator, alias);
    }

    /**
     * Selected element will be stored in selectedElement which is the webElement used by default for most step defs
     * An exception is thrown if no element is found and the value of selectedElement will remain unchanged
     * @param locator a string that targets an element in the dom.
     * @throws NoSuchElementException when no element is not found
     */
    @When("^I? ?select element by partial link text using value \"([^\"]*)\"(?: as \"([^\"]*)\"?)?$")
    public void selectElementByPartialLinkText(String locator, String alias) throws NoSuchElementException {
        SeleniumHandlers.selectElementByPartialLinkText(locator, alias);
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

    /**
     * Selects WebElement from selectedElements and assigned to selectedElement
     * @param index to select from selectedElements
     */
    @When("^I? ?select index (\\d+) from selected elements$")
    public void selectFromElements(Integer index) {
        SeleniumHandlers.selectFromElements(index);
    }

    @When("^I select element at current index from selected elements$")
    public void checkEntryInSpecificCell() {
        SeleniumHandlers.getElementAtIndex();
    }

    @When("^I? ?filter selected elements by xpath using value \"([^\"]*)\"$")
    public void filterSelectedElementsByXPath(String filter) {
        SeleniumHandlers.filterSelectedElementsByXPath(filter);
    }

    @When("^I? ?filter selected elements based on if their descendents contain the text \"([^\"]*)\"$")
    public void filterSelectedElementsDescendentsText(String filterText) {
        SeleniumHandlers.filterSelectedElementsDescendentsText(filterText);
    }

    @When("^I? ?get (?:the)? ?children of selected element$")
    public void getChildrenForSelectedElement(){
        SeleniumHandlers.getAllChildrenForSelectedElement();
    }

    @When("^I? ?get (?:the)? ?children of (?:the)? ?parent of the selected element$")
    public void getAllChildrenForParentOfSelectedElement(){
        SeleniumHandlers.getAllChildrenForParentOfSelectedElement();
    }

    @When("^I? ?get (?:the)? ?descendants of (?:the)? ?selected element$")
    public void getAllDescendantsForSelectedElement(){
        SeleniumHandlers.getAllDescendantsForSelectedElement();
    }

    @When("^I? ?get both (?:the)? ?selected elements and all it's selected elements descendants$")
    public void getAllDescendantsIncludingSelectedElement(){
        SeleniumHandlers.getAllDescendantsIncludingSelectedElement();
    }

    @When("^I? ?get all (?:the)? ?descendants for (?:the)? ?parent of the selected element$")
    public void getAllDescendantsForParentofSelectedElement(){
        SeleniumHandlers.getAllDescendantsForParentofSelectedElement();
    }

    @When("^I? ?get everything following (?:the)? ?closing tag of the selected element$")
    public void getEverythingFollowingTheSelectedElement(){
        SeleniumHandlers.getEverythingFollowingTheSelectedElement();
    }

    @When("^I? ?get everything following (?:the)? ?closing tag of (?:the)? ?parent of the selected element$")
    public void getEverythingFollowingTheParentOfSelectedElement(){
        SeleniumHandlers.getEverythingFollowingTheParentOfSelectedElement();
    }

    @When("^I? ?get all siblings following (?:the)? ?selected element$")
    public void getTheSiblingsFollowingTheSelectedElement(){
        SeleniumHandlers.getTheSiblingsFollowingTheSelectedElement();
    }

    @When("^I? ?get all siblings following (?:the)? ?parent of (?:the)? ?selected element$")
    public void getTheSiblingsFollowingTheParentOfSelectedElement(){
        SeleniumHandlers.getTheSiblingsFollowingTheParentOfSelectedElement();
    }

    @When("^I? ?get all siblings preceding (?:the)? ?selected element$")
    public void getTheSiblingsPrecedingTheSelectedElement(){
        SeleniumHandlers.getTheSiblingsPrecedingTheSelectedElement();
    }

    @When("^I? ?get all siblings preceding (?:the)? ?parent of (?:the)? ?selected element$")
    public void getTheSiblingsPrecedingTheParentOfSelectedElement(){
        SeleniumHandlers.getTheSiblingsPrecedingTheParentOfSelectedElement();
    }

    @When("^I? ?get (?:the)? ?parent of (?:the)? ?selected element$")
    public void getTheParentOfSelectedElement(){
        SeleniumHandlers.getTheParentOfSelectedElement();
    }

    @When("^I? ?get (?:the)? ?grandparent of (?:the)? ?selected element$")
    public void getTheParentOfTheParentOfSelectedElement(){
        SeleniumHandlers.getTheParentOfTheParentOfSelectedElement();
    }

    @When("^I? ?get all ancestors of (?:the)? ?selected element$")
    public void getAllAncestorsForSelectedElement(){
        SeleniumHandlers.getAllAncestorsForSelectedElement();
    }

    @When("^I? ?get all ancestors of (?:the)? ?parent of (?:the)? ?selected element$")
    public void getAllAncestorsForParentOfSelectedElement(){
        SeleniumHandlers.getAllAncestorsForParentOfSelectedElement();
    }

    @When("^I? ?get (?:the)? ?selected element and all its ancestors$")
    public void getAllAncestorsIncludingTheSelectedElement(){
        SeleniumHandlers.getAllAncestorsIncludingTheSelectedElement();
    }

    /**
     * If the named element exists it will become the selectedElement so that actions can be performed on it
     * @param elementName alias to be selected
     */
    @When("^I? ?select (?:the)? ?named element \"([^\"]*)\"$")
    public void selectNamedElement(String elementName) {
        SeleniumHandlers.selectNamedElement(elementName);
    }

    @When("^I? ?name (?:the)? ?selected element as \"([^\"]*)\"$")
    public void nameSelectedElement(String elementName) {
        SeleniumHandlers.nameSelectedElement(elementName);
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

    /**
     * Clears only the single selectedElement
     */
    @Then("^I? ?clear (?:the)? ?selected element$")
    public void clearSelectedElement() {
        SeleniumHandlers.clearSelectedElement();
    }

    /**
     * Clears both selectedElements list and the single selectedElement
     */
    @Then("^I? ?clear (?:the)? ?selected elements$")
    public void clearSelectedElements() {
        SeleniumHandlers.clearSelectedElements();
    }

    /**
     * Clears only named elements
     */
    @Then("^I? ?clear (?:the)? ?named elements$")
    public void clearNamedElements() {
        SeleniumHandlers.clearNamedElements();
    }

    @Then("^I? ?clear (?:the)? ?named values$")
    public void clearNamedValues() {
        SeleniumHandlers.clearNamedValues();
    }

    // ALIASES

    @When("^element at index (\\d+) (?:is selected|from results is selected)$")
    public void selectFromElementsAlias(Integer index) {
        selectFromElements(index);
    }

    @When("^I (?:select|choose|get|grab|fetch|focus in on) element number (\\d+) from (?:results|selected elements|list|elements|group)$")
    public void selectFromElementsAlias2(Integer index) {
        selectFromElements(index);
    }
}
