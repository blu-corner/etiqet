package com.neueda.etiqet.selenium.fixture;

import com.neueda.etiqet.selenium.SeleniumException;
import com.neueda.etiqet.selenium.browser.BrowserBase;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * Implements functionality of the step definitions.
 * Actions are performed on the selectedElement by default.
 *
 * When Selecting elements, there are optional lines at the end of each selection method. One optional feature is to
 * include 'relative to the selected element' that will cause selenium to search for the element relative to the
 * currently selected element rather than from the root of the DOM. You can also include an alias that will save
 * the element in a map with the alias name as the key. To do this end the selection method with 'as [alias name]'.
 * Aliases only apply when selecting a single element rather than multiple elements.
 *
 * Explicit and implicit wait when set will be used on applicable handlers until they are disabled. i.e. implicit wait
 * is set to 0 or explicit wait is disabled. If a search method returns multiple elements explicit wait will wait for
 * all elements to be visible, present etc if applicable.
 */

public class SeleniumHandlers {

    private final static Logger logger = LoggerFactory.getLogger(SeleniumHandlers.class);

    private static WebDriver driver;

    private static HashMap<String, WebElement> namedElements;
    private static HashMap<String, String> namedValues;
    private static List<WebElement> selectedElements;
    private static WebElement selectedElement;

    private static ExplicitWait explicitWait;
    private static long explicitWaitTimeout;

    static {
        SeleniumHandlers.namedElements = new HashMap<>();
        SeleniumHandlers.selectedElements = new ArrayList<>();
        SeleniumHandlers.namedValues = new HashMap<>();
    }

    /**
     * Browsers will be loaded from the config before any tests run and can be selected using this method
     * @param browserName The name of the browser which should be defined as a name attribute in config
     */

    public static void openBrowser(String browserName){
        BrowserBase.selectBrowser(browserName);
        openBrowser();
    }

    public static void openBrowser() {
        BrowserBase.getCurrentBrowser().setupDriver();
        driver = BrowserBase.getCurrentBrowser().getDriver();
    }

    public static void setImplicitWaitNano(long nanos) {
        driver.manage().timeouts().implicitlyWait(nanos, TimeUnit.NANOSECONDS);
        logger.info("Implicit wait set to " + nanos + " nanoseconds");
    }

    public static void setImplicitWaitMs(long ms) {
        driver.manage().timeouts().implicitlyWait(ms, TimeUnit.MILLISECONDS);
        logger.info("Implicit wait set to " + ms + " milliseconds");
    }

    public static void setImplicitWaitSeconds(long seconds) {
        driver.manage().timeouts().implicitlyWait(seconds, TimeUnit.SECONDS);
        logger.info("Implicit wait set to " + seconds + " seconds");
    }

    /**
     * Will only be applied to wait for individual elements, unlike other wait conditions that
     * will wait for multiple elements if multiple elements are being selected
     * After being called it must be explicitly disabled by calling the relevant method if you
     * no longer want to use it.
     * @param timout in seconds
     */
    public static void setExplicitWaitToClickable(int timout) {
        explicitWait = ExplicitWait.CLICKABLE;
        explicitWaitTimeout = timout;
    }

    /**
     * Will be automatically applied to multiple elements if multiple elements are being selected
     * After being called it must be explicitly disabled by calling the relevant method if you
     * no longer want to use it.
     * @param timout in seconds
     */
    public static void setExplicitWaitToPresent(int timout) {
        explicitWait = ExplicitWait.PRESENT;
        explicitWaitTimeout = timout;
    }

    /**
     * Will be automatically applied to multiple elements if multiple elements are being selected
     * After being called it must be explicitly disabled by calling the relevant method if you
     * no longer want to use it.
     * @param timout in seconds
     */
    public static void setExplicitWaitToVisibility(int timout) {
        explicitWait = ExplicitWait.VISIBLE;
        explicitWaitTimeout = timout;
    }

    /**
     * Disable explicit wait
     */
    public static void clearExplicitWait() {
        explicitWait = null;
        explicitWaitTimeout = 0;
    }

    /**
     * Opens a url
     *
     * @param url should be a fully qualified url such as https://www.google.com rather than google.com
     */
    public static void goToUrl(String url) {
        driver.get(url);
    }

    public static void maximizeWindow() {
        driver.manage().window().maximize();
    }

    /**
     * Selected element will be stored in selectedElement which is the webElement used by default for most step defs
     * An exception is thrown if no element is found and the value of selectedElement will remain unchanged
     * @param locator a string that targets an element in the dom.
     * @param relative [optional] if expression matched then traverse DOM relative to selected element
     * @param alias [optional] if included a named reference will be saved to element for later use
     * @throws NoSuchElementException when no element is not found
     */
    public static void selectElementByCss(String locator, String relative, String alias) throws NoSuchElementException {
        if (explicitWait != null) {
            selectedElement = relative == null || selectedElement == null  ?
                explicitWait.applyWait(driver, LocatorType.CSS, locator, explicitWaitTimeout).get(0) :
                explicitWait.applyWait(driver, LocatorType.CSS, locator, explicitWaitTimeout, selectedElement).get(0);
        }
        else {
            selectedElement = relative == null || selectedElement == null  ? driver.findElement(By.cssSelector(locator)) :
                selectedElement.findElement(By.cssSelector(locator));
        }
        if (alias != null) {
            namedElements.put(alias, selectedElement);
        }
    }

    /**
     * Selected elements will be stored in selectedElements and any of these can be assigned to selectedElement
     * by calling selectElementAtIndex(int index) step definition.
     * Selenium returns an empty list if no elements found, however NoSuchElementException is thrown to
     * remain consistent with the selectElementBy... methods.
     * @param locator a string that targets an element in the dom.
     * @param relative [optional] if expression matched then traverse DOM relative to selected element
     * @throws NoSuchElementException when no element is not found
     */
    public static void selectElementsByCss(String locator, String relative) throws NoSuchElementException {
        if (explicitWait != null) {
            selectedElements = relative == null || selectedElement == null  ?
                explicitWait.applyWaitAll(driver, LocatorType.CSS, locator, explicitWaitTimeout) :
                explicitWait.applyWaitAll(driver, LocatorType.CSS, locator, explicitWaitTimeout, selectedElement);
            return;
        }
        selectedElements = relative == null || selectedElement == null ? driver.findElements(By.cssSelector(locator)) :
            selectedElement.findElements(By.cssSelector(locator));
    }

    /**
     * Selected element will be stored in selectedElement which is the webElement used by default for most step defs
     * An exception is thrown if no element is found and the value of selectedElement will remain unchanged
     * @param locator a string that targets an element in the dom.
     * @param relative [optional] if expression matched then traverse DOM relative to selected element
     * @param alias [optional] if included a named reference will be saved to element for later use
     * @throws NoSuchElementException when no element is not found
     */
    public static void selectElementByXpath(String locator, String relative, String alias) throws NoSuchElementException {
        if (explicitWait != null) {
            selectedElement = relative == null || selectedElement == null ?
                explicitWait.applyWait(driver, LocatorType.XPATH, locator, explicitWaitTimeout).get(0) :
                explicitWait.applyWait(driver, LocatorType.XPATH, locator, explicitWaitTimeout, selectedElement).get(0);
        }
        else {
            selectedElement = relative == null || selectedElement == null ? driver.findElement(By.xpath(locator)) :
                selectedElement.findElement(By.xpath(locator));
        }
        if (alias != null) {
            namedElements.put(alias, selectedElement);
        }
    }

    /**
     * Selected elements will be stored in selectedElements and any of these can be assigned to selectedElement
     * by calling selectElementAtIndex(int index) step definition.
     * Selenium returns an empty list if no elements found, however NoSuchElementException is thrown to
     * remain consistent with the selectElementBy... methods.
     * @param locator a string that targets an element in the dom.
     * @param relative [optional] if expression matched then traverse DOM relative to selected element
     * @throws NoSuchElementException when no element is not found
     */
    public static void selectElementsByXpath(String locator, String relative) throws NoSuchElementException {
        if (explicitWait != null) {
            selectedElements = relative == null || selectedElement == null ?
                explicitWait.applyWaitAll(driver, LocatorType.XPATH, locator, explicitWaitTimeout) :
                explicitWait.applyWaitAll(driver, LocatorType.XPATH, locator, explicitWaitTimeout, selectedElement);
            return;
        }
        selectedElements = relative == null || selectedElement == null ? driver.findElements(By.xpath(locator)) :
            selectedElement.findElements(By.xpath(locator));
    }

    /**
     * Selected element will be stored in selectedElement which is the webElement used by default for most step defs
     * An exception is thrown if no element is found and the value of selectedElement will remain unchanged
     * @param locator a string that targets an element in the dom.
     * @param relative [optional] if expression matched then traverse DOM relative to selected element
     * @param alias [optional] if included a named reference will be saved to element for later use
     * @throws NoSuchElementException when no element is not found
     */
    public static void selectElementById(String locator, String relative, String alias) throws NoSuchElementException {
        if (explicitWait != null) {
            selectedElement = relative == null || selectedElement == null ?
                explicitWait.applyWait(driver, LocatorType.ID, locator, explicitWaitTimeout).get(0) :
                explicitWait.applyWait(driver, LocatorType.ID, locator, explicitWaitTimeout, selectedElement).get(0);
        }
        else {
            selectedElement = relative == null || selectedElement == null ? driver.findElement(By.id(locator)) :
                selectedElement.findElement(By.id(locator));
        }
        if (alias != null) {
            namedElements.put(alias, selectedElement);
        }
    }

    /**
     * Selected element will be stored in selectedElement which is the webElement used by default for most step defs
     * An exception is thrown if no element is found and the value of selectedElement will remain unchanged
     * @param locator a string that targets an element in the dom.
     * @param relative [optional] if expression matched then traverse DOM relative to selected element
     * @param alias [optional] if included a named reference will be saved to element for later use
     * @throws NoSuchElementException when no element is not found
     */
    public static void selectElementByTag(String locator, String relative, String alias) throws NoSuchElementException {
        if (explicitWait != null) {
            selectedElement = relative == null || selectedElement == null ?
                explicitWait.applyWait(driver, LocatorType.TAG, locator, explicitWaitTimeout).get(0) :
                explicitWait.applyWait(driver, LocatorType.TAG, locator, explicitWaitTimeout, selectedElement).get(0);
        }
        else {
            selectedElement = relative == null || selectedElement == null ? driver.findElement(By.tagName(locator)) :
                selectedElement.findElement(By.tagName(locator));
            System.out.println(selectedElement.getText());
        }
        if (alias != null) {
            namedElements.put(alias, selectedElement);
        }
    }

    /**
     * Selected elements will be stored in selectedElements and any of these can be assigned to selectedElement
     * by calling selectElementAtIndex(int index) step definition.
     * Selenium returns an empty list if no elements found, however NoSuchElementException is thrown to
     * remain consistent with the selectElementBy... methods.
     * @param locator a string that targets an element in the dom.
     * @param relative [optional] if expression matched then traverse DOM relative to selected element
     * @throws NoSuchElementException when no element is not found
     */
    public static void selectElementsByTag(String locator, String relative) throws NoSuchElementException {
        if (explicitWait != null) {
            selectedElements = relative == null || selectedElement == null ?
                explicitWait.applyWaitAll(driver, LocatorType.TAG, locator, explicitWaitTimeout) :
                explicitWait.applyWaitAll(driver, LocatorType.TAG, locator, explicitWaitTimeout, selectedElement);
            return;
        }
        selectedElements = relative == null || selectedElement == null ? driver.findElements(By.tagName(locator)) :
            selectedElement.findElements(By.tagName(locator));
    }

    /**
     * Selected element will be stored in selectedElement which is the webElement used by default for most step defs
     * An exception is thrown if no element is found and the value of selectedElement will remain unchanged
     * If multiple elements are found the first element will be assigned to selectedElement
     * @param locator a string that targets an element in the dom.
     * @param relative [optional] if expression matched then traverse DOM relative to selected element
     * @param alias [optional] if included a named reference will be saved to element for later use
     * @throws NoSuchElementException when no element is not found
     */
    public static void selectElementByClassName(String locator, String relative, String alias) throws NoSuchElementException {
        if (explicitWait != null) {
            selectedElement = relative == null || selectedElement == null ?
                explicitWait.applyWait(driver, LocatorType.CLASS, locator, explicitWaitTimeout).get(0) :
                explicitWait.applyWait(driver, LocatorType.CLASS, locator, explicitWaitTimeout, selectedElement).get(0);
        }
        else {
            selectedElement = relative == null || selectedElement == null ? driver.findElement(By.className(locator)) :
                selectedElement.findElement(By.className(locator));
        }
        if (alias != null) {
            namedElements.put(alias, selectedElement);
        }
    }

    /**
     * Selected elements will be stored in selectedElements and any of these can be assigned to selectedElement
     * by calling selectElementAtIndex(int index) step definition.
     * Selenium returns an empty list if no elements found, however NoSuchElementException is thrown to
     * remain consistent with the selectElementBy... methods.
     * @param locator a string that targets an element in the dom.
     * @param relative [optional] if expression matched then traverse DOM relative to selected element
     * @throws NoSuchElementException when no element is not found
     */
    public static void selectElementsByClassName(String locator, String relative) throws NoSuchElementException {
        if (explicitWait != null) {
            selectedElements = relative == null || selectedElement == null ?
                explicitWait.applyWaitAll(driver, LocatorType.CLASS, locator, explicitWaitTimeout) :
                explicitWait.applyWaitAll(driver, LocatorType.CLASS, locator, explicitWaitTimeout, selectedElement);
            return;
        }
        selectedElements = relative == null || selectedElement == null ? driver.findElements(By.className(locator)) :
            selectedElement.findElements(By.className(locator));
    }

    /**
     * Selected element will be stored in selectedElement which is the webElement used by default for most step defs
     * An exception is thrown if no element is found and the value of selectedElement will remain unchanged
     * @param locator a string that targets an element in the dom.
     * @param relative [optional] if expression matched then traverse DOM relative to selected element
     * @param alias [optional] if included a named reference will be saved to element for later use
     * @throws NoSuchElementException when no element is not found
     */
    public static void selectElementByLinkText(String locator, String relative, String alias) throws NoSuchElementException {
        if (explicitWait != null) {
            selectedElement = relative == null || selectedElement == null ?
                explicitWait.applyWait(driver, LocatorType.LINK, locator, explicitWaitTimeout).get(0) :
                explicitWait.applyWait(driver, LocatorType.LINK, locator, explicitWaitTimeout, selectedElement).get(0);
        }
        else {
            selectedElement = relative == null || selectedElement == null ? driver.findElement(By.linkText(locator)) :
                selectedElement.findElement(By.linkText(locator));
        }
        if (alias != null) {
            namedElements.put(alias, selectedElement);
        }
    }

    /**
     * Selected element will be stored in selectedElement which is the webElement used by default for most step defs
     * An exception is thrown if no element is found and the value of selectedElement will remain unchanged
     * @param locator a string that targets an element in the dom.
     * @param relative [optional] if expression matched then traverse DOM relative to selected element
     * @param alias [optional] if included a named reference will be saved to element for later use
     * @throws NoSuchElementException when no element is not found
     */
    public static void selectElementByPartialLinkText(String locator, String relative, String alias) throws NoSuchElementException {
        if (explicitWait != null) {
            selectedElement = relative == null || selectedElement == null ?
                explicitWait.applyWait(driver, LocatorType.PARTIAL_LINK, locator, explicitWaitTimeout).get(0) :
                explicitWait.applyWait(driver, LocatorType.PARTIAL_LINK, locator, explicitWaitTimeout, selectedElement).get(0);
        }
        else {
            selectedElement = relative == null || selectedElement == null ? driver.findElement(By.partialLinkText(locator)) :
                selectedElement.findElement(By.partialLinkText(locator));
        }
        if (alias != null) {
            namedElements.put(alias, selectedElement);
        }
    }

    /**
     * Selects WebElement from selectedElements and assigned to selectedElement
     * A negative index will index backwards from the end of the list -> index -1 will be the last element
     * @param index to select from selectedElements
     */
    public static void selectElementAtIndex(Integer index) {
        selectedElement = index < 0 ? selectedElements.get(selectedElements.size() + index) : selectedElements.get(index);
    }

    /**
     * Selects the last WebElement from selectedElements and assigned to selectedElement
     */
    public static void selectLastElement() {
        selectedElement = selectedElements.get(selectedElements.size() - 1);
    }

    public static void selectFirstElementByContainedText(String text, String alias) {
        for(WebElement element : selectedElements) {
            if(element.getText().contains(text)) {
                selectedElement = element;
                break;
            }
        }
        if(alias != null) {
            namedElements.put(alias, selectedElement);
        }
    }

    public static void selectFromDropdownByLabel(String label) {
        Select select = new Select(selectedElement);
        select.deselectAll();
        select.selectByVisibleText(label);
    }

    public static void selectFromDropdownByValue(String value) {
        Select select = new Select(selectedElement);
        select.deselectAll();
        select.selectByValue(value);
    }

    public static void selectFromDropdownByIndex(int index) {
        Select select = new Select(selectedElement);
        select.deselectAll();
        select.selectByIndex(index);
    }

    /**
     * Saves selected elements inner text that other methods can then use
     * @param alias name that references the text
     */
    public static void saveSelectedElementsInnerTextAs(String alias) {
        namedValues.put(alias, selectedElement.getText());
    }

    /**
     * Saves selected element's attribute value that other methods can then use
     * @param attributeName name of attribute
     * @param alias name that references the attribute value
     */
    public static void saveSelectedElementsAttributeValueAs(String attributeName, String alias) {
        namedValues.put(alias, selectedElement.getAttribute(attributeName));
    }

    /**
     * Saves selected elements count that other methods can thenuse
     * @param alias name that references the count
     */
    public static void saveSelectedElementsCountAs(String alias) {
        namedValues.put(alias, Integer.toString(selectedElements.size()));
    }

    /**
     * Filters the selectedElements by removing elements that are not found using an xpath expression
     * @param filter the xpath selection
     */

    public static void filterSelectedElementsByXPath(String filter) {

        if (explicitWait != null) {

            int i = selectedElements.size(); // todo - better solution
            while (i != 0) {
                i--;
                try {
                    List<WebElement> ignore = explicitWait.applyWait(driver, LocatorType.XPATH, ".//*[contains(text(),'" + filter + "')]", explicitWaitTimeout, selectedElement);

                } catch (TimeoutException | NoSuchElementException | StaleElementReferenceException e) {
                    selectedElements.remove(i);
                }
            }
            return;
        }
        selectedElements.removeIf(element -> element.findElements(By.xpath(filter)).size() == 0);
    }

    /**
     * Filters elements from selectedElements if their descendents do not contain filterText
     * @param filterText text to be searched
     */
    public static void filterSelectedElementsDescendentsText(String filterText) {
        if (explicitWait != null) {

            int i = selectedElements.size();
            while (i != 0) {
                i--;
                try {
                    List<WebElement> ignore = explicitWait.applyWait(driver, LocatorType.XPATH, ".//*[contains(text(),'" + filterText + "')]", explicitWaitTimeout, selectedElements.get(i));
                } catch (TimeoutException | NoSuchElementException | StaleElementReferenceException e) {
                    selectedElements.remove(i);
                }
            }
            return;
        }
        selectedElements.removeIf(element -> element.findElements(By.xpath(".//*[contains(text(),'" + filterText + "')]")).size() == 0);
    }

    /**
     * Select convenience methods for navigating tree structure of nodes
     */

    public static void selectAllChildrenForSelectedElement(){
        selectedElements = selectedElement.findElements(By.xpath("./child::*"));
    }

    public static void selectAllDescendantsForSelectedElement(){
        selectedElements = selectedElement.findElements(By.xpath("./descendant::*"));
    }

    public static void selectEverythingFollowingTheSelectedElement(){
        selectedElements = selectedElement.findElements(By.xpath("./following::*"));
    }

    public static void selectTheSiblingsPrecedingTheSelectedElement(){
        selectedElements = selectedElement.findElements(By.xpath("./preceding-sibling::*"));
    }

    public static void selectTheSiblingsFollowingTheSelectedElement(){
        selectedElements = selectedElement.findElements(By.xpath("./following-sibling::*"));
    }

    public static void selectTheSiblingsOfTheSelectedElement(){
        selectedElements = selectedElement.findElements(By.xpath("./preceding-sibling::*|./following-sibling::*"));
    }

    public static void selectTheParentOfSelectedElement(){
        selectedElements = selectedElement.findElements(By.xpath("./parent::*"));
    }

    public static void selectAllAncestorsForSelectedElement(){
        selectedElements = selectedElement.findElements(By.xpath("./ancestor::*"));
    }

    public static void setAttributeValue(String attribute, String value, String alias){
        JavascriptExecutor js = (JavascriptExecutor) driver;
        if(alias != null){
            js.executeScript("arguments[0].setAttribute(arguments[1],arguments[2])", namedElements.get(alias), attribute, value);
        }
        else{
            js.executeScript("arguments[0].setAttribute(arguments[1],arguments[2])", selectedElement, attribute, value);
        }
    }

    /**
     * If the named element exists it will become the selectedElement so that actions can be performed on it
     * @param elementName alias to be selected
     */
    public static void selectNamedElement(String elementName) throws SeleniumException {
        if (!namedElements.containsKey(elementName)) {
            throw new SeleniumException("Named element '" + elementName + "' does not exist");
        }
        selectedElement = namedElements.get(elementName);
    }

    public static void clickElement() {
        selectedElement.click();
    }

    public static void rightClickElement() {
        Actions actions = new Actions(driver);
        actions.contextClick(selectedElement).perform();
    }

    public static void doubleClickElement() {
        Actions actions = new Actions(driver);
        actions.doubleClick(selectedElement).perform();
    }

    public static void clickAndHold() {
        Actions actions = new Actions(driver);
        actions.clickAndHold(selectedElement).perform();
    }

    /**
     * Convenience method - Submit a form if element is contained within a form
     */
    public static void submit() {
        selectedElement.submit();
    }

    public static void hoverElement() {
        Actions actions = new Actions(driver);
        actions.moveToElement(selectedElement).perform();
    }

    public static void enterText(String text) {
        selectedElement.sendKeys(text);
    }

    public static void pressEnter() {
        Actions actions = new Actions(driver);
        actions.sendKeys(Keys.ENTER);
        actions.perform();    }

    public static void pressArrowDownKeyTimes(Integer times) {
        if (times == null) {
            times = 1;
        }
        Actions actions = new Actions(driver);
        for (int i = 0; i < times; i++) {
            actions.sendKeys(Keys.ARROW_DOWN);
        }
        actions.perform();
    }

    public static void pressArrowKeyUpTimes(Integer times) {
        if (times == null) {
            times = 1;
        }
        Actions actions = new Actions(driver);
        for (int i = 0; i < times; i++) {
            actions.sendKeys(Keys.ARROW_UP);
        }
        actions.perform();
    }

    public static void pressArrowLeftKeyTimes(Integer times) {
        if (times == null) {
            times = 1;
        }
        Actions actions = new Actions(driver);
        for (int i = 0; i < times; i++) {
            actions.sendKeys(Keys.ARROW_LEFT);
        }
        actions.perform();
    }

    public static void pressArrowRightKeyTimes(Integer times) {
        if (times == null) {
            times = 1;
        }
        Actions actions = new Actions(driver);
        for (int i = 0; i < times; i++) {
            actions.sendKeys(Keys.ARROW_RIGHT);
        }
        actions.perform();
    }

    /**
     * OtherKeys
     */

    public static void pressBackspaceTimes(Integer times) {
        if (times == null) {
            times = 1;
        }
        Actions actions = new Actions(driver);
        for (int i = 0; i < times; i++) {
            actions.sendKeys(Keys.BACK_SPACE);
        }
        actions.perform();
    }

    public static void pressDelete() {
        selectedElement.sendKeys(Keys.DELETE);
    }

    public static void pressCtrlKey(){
        selectedElement.sendKeys(Keys.CONTROL);
    }

    public static void pressAltKey(){
        selectedElement.sendKeys(Keys.ALT);
    }

    public static void pressShiftKey(){
        selectedElement.sendKeys(Keys.SHIFT);
    }

    public static void pressSpacebarTimes(Integer times) {
        if (times == null) {
            times = 1;
        }
        Actions actions = new Actions(driver);
        for (int i = 0; i < times; i++) {
            actions.sendKeys(Keys.SPACE);
        }
        actions.perform();
    }

    public static void pressTabKeyTimes(Integer times) {
        if (times == null) {
            times = 1;
        }
        Actions actions = new Actions(driver);
        for (int i = 0; i < times; i++) {
            actions.sendKeys(Keys.TAB);
        }
        actions.perform();
    }

    public static void pressEqualsKey(){
        selectedElement.sendKeys(Keys.EQUALS);
    }

    public static void pressHomeKey(){
        selectedElement.sendKeys(Keys.HOME);
    }

    public static void pressInsertKey(){
        selectedElement.sendKeys(Keys.INSERT);
    }

    public static void pressPageUpTimes(Integer times) {
        if (times == null) {
            times = 1;
        }
        Actions actions = new Actions(driver);
        for (int i = 0; i < times; i++) {
            actions.sendKeys(Keys.PAGE_UP);
        }
        actions.perform();
    }

    public static void pressPageDownTimes(Integer times) {
        if (times == null) {
            times = 1;
        }
        Actions actions = new Actions(driver);
        for (int i = 0; i < times; i++) {
            actions.sendKeys(Keys.PAGE_DOWN);
        }
        actions.perform();
    }

    /**End of sendKeys*/

    public static void refresh() {
        driver.navigate().refresh();
    }

    public static void back() {
        driver.navigate().back();
    }

    public static void forward() {
        driver.navigate().forward();
    }

    public static void pauseMs(long milliseconds) {
        Actions actions = new Actions(driver);
        actions.pause(Duration.ofMillis(milliseconds));
        actions.build().perform();
    }

    public static void pauseSecs(long seconds) {
        Actions actions = new Actions(driver);
        actions.pause(Duration.ofSeconds(seconds));
        actions.build().perform();
    }

    /**
     * Creates an alias to the element after the element has been selected
     * @param alias
     */
    public static void nameSelectedElement(String alias) {
        namedElements.put(alias, selectedElement);
    }

    public static void checkCurrentUrl(String expectedUrl) {
        assertEquals(expectedUrl, driver.getCurrentUrl());
    }

    public static void checkCurrentUrlContains(String urlPart) {
        assertThat(driver.getCurrentUrl(), CoreMatchers.containsString(urlPart));
    }

    public static void checkPageTitle(String pageTitle) {
        assertEquals(pageTitle, driver.getTitle());
    }

    public static void checkPageTitleContains(String expectedTitlePart) {
        assertThat(driver.getTitle(), CoreMatchers.containsString(expectedTitlePart));
    }

    public static void checkPageContainsText(String expectedText, Long timeout) {
        if (timeout != null) {
            ExplicitWait.PRESENT.applyWait(driver, LocatorType.XPATH, "//*[contains(text(),'" + expectedText + "')]", timeout);
            return;
        }
        List<WebElement> elementsFound = driver.findElements(By.xpath("//*[contains(text(),'" + expectedText + "')]"));
        assertTrue("Text " + expectedText + " not found", elementsFound.size() > 0);
    }

    public static void checkPageDoesNotContainText(String unexpectedText, Long timeout) {
        if (timeout != null) {
            try {
                ExplicitWait.PRESENT.applyWait(driver, LocatorType.XPATH, "//*[contains(text(),'" + unexpectedText + "')]", timeout);
                throw new UnexpectedElementFoundException("Unexpected element with text " + unexpectedText + " was found on page");
            } catch (TimeoutException e) {
                assert (true);
            }
            return;
        }
        List<WebElement> elementsFound = driver.findElements(By.xpath("//*[contains(text(),'" + unexpectedText + "')]"));
        assertEquals(0, elementsFound.size());
    }

    public static void checkPageElementHasInnerText(String expectedText, Long timeout) {
        if (timeout != null) {
            ExplicitWait.PRESENT.applyWait(driver, LocatorType.XPATH, "//*[text()='" + expectedText + "']", timeout);
            return;
        }
        List<WebElement> elementsFound = driver.findElements(By.xpath("//*[text()='" + expectedText + "']"));
        assertTrue("Text " + expectedText + " not found", elementsFound.size() > 0);
    }

    public static void checkNoPageElementHasInnerText(String unexpectedText, Long timeout) {
        if (timeout != null) {
            try {
                ExplicitWait.PRESENT.applyWait(driver, LocatorType.XPATH, "//*[text()='" + unexpectedText + "']", timeout);
                throw new UnexpectedElementFoundException("Unexpected element with text " + unexpectedText + " was found on page");
            } catch (TimeoutException e) {
                assert (true);
            }
            return;
        }
        List<WebElement> elementsFound = driver.findElements(By.xpath("//*[text()='" + unexpectedText + "']"));
        assertEquals(0, elementsFound.size());
    }

    public static void checkInnerText(String expectedText) {
        assertEquals(expectedText, selectedElement.getText());
    }

    public static void checkInnerTextContains(String expectedSubText) {
        assertThat(selectedElement.getText(), CoreMatchers.containsString(expectedSubText));
    }

    public static void checkMultipleElementsInnerTextEquals(ArrayList<String> expectedValues) {
        for (int i = 0; i < selectedElements.size(); i++) {
            assertEquals(expectedValues.get(i), selectedElements.get(i).getText());
        }
    }

    public static void checkMultipleElementsInnerTextContains(ArrayList<String> expectedValues) {
        for (int i = 0; i < selectedElements.size(); i++) {
            assertTrue(selectedElements.get(i).getText().contains(expectedValues.get(i)));
        }
    }

    public static void checkDescendents(String expectedText) {
        assertTrue(selectedElement == null ?
            driver.findElements(By.xpath("//*[contains(text(),'" + expectedText + "')]")).size() > 0 :
            selectedElement.findElements(By.xpath(".//*[contains(text(),'" + expectedText + "')]")).size() > 0);
    }

    public static void checkAttributeExists(String attribute) {
        String expectedAttribute = selectedElement.getAttribute(attribute);
        assertNotNull(expectedAttribute);
    }

    public static void checkAttributeValue(String attribute, String value) {
        assertEquals(value, selectedElement.getAttribute(attribute));
    }

    public static void checkAttributeValueContains(String attribute, String value) {
        assertThat(selectedElement.getAttribute(attribute), CoreMatchers.containsString(value));
    }

    /**
     * @param expectedCount expected number of elements in selectedElements
     */
    public static void checkNumberOfElementsFound(int expectedCount) {
        assertEquals(expectedCount, selectedElements.size());
    }

    /**
     * @param expectedCount expected number of elements in selectedElements
     */
    public static void checkNumberOfElementsFoundAtLeast(int expectedCount) {
        assertTrue(selectedElements.size() >= expectedCount);
    }

    public static void checkElementIsDisplayed() {
        assertTrue(selectedElement.isDisplayed());
    }

    public static void checkElementIsSelected() {
        assertTrue(selectedElement.isSelected());
    }

    public static void checkElementIsEnabled() {
        assertTrue(selectedElement.isEnabled());
    }

    public static void invisibilityOfElement(long timeout) {
        WebDriverWait wait = new WebDriverWait(driver, timeout);
        assertTrue(wait.until(ExpectedConditions.invisibilityOf(selectedElement)));
    }

    public static void invisibilityOfAllElements(long timeout) {
        WebDriverWait wait = new WebDriverWait(driver, timeout);
        assertTrue(wait.until(ExpectedConditions.invisibilityOfAllElements(selectedElements)));
    }

    public static void elementIsSelected(long timeout) {
        WebDriverWait wait = new WebDriverWait(driver, timeout);
        assertTrue(wait.until(ExpectedConditions.elementSelectionStateToBe(selectedElement, true)));
    }

    public static void elementIsNotSelected(long timeout) {
        WebDriverWait wait = new WebDriverWait(driver, timeout);
        assertTrue(wait.until(ExpectedConditions.elementSelectionStateToBe(selectedElement, false)));
    }

    public static void elementIsStale(long timeout) {
        WebDriverWait wait = new WebDriverWait(driver, timeout);
        assertTrue(wait.until(ExpectedConditions.stalenessOf(selectedElement)));
    }

    public static void textIsPresent(String expectedText, long timeout) {
        WebDriverWait wait = new WebDriverWait(driver, timeout);
        assertTrue(wait.until(ExpectedConditions.textToBePresentInElement(selectedElement, expectedText)));
    }

    public static void textIsPresentInValue(String expectedText, long timeout) {
        WebDriverWait wait = new WebDriverWait(driver, timeout);
        assertTrue(wait.until(ExpectedConditions.textToBePresentInElementValue(selectedElement, expectedText)));
    }

    public static void checkTitleWithWait(String expectedTitle, long timeout) {
        WebDriverWait wait = new WebDriverWait(driver, timeout);
        assertTrue(wait.until(ExpectedConditions.textToBePresentInElement(selectedElement, expectedTitle)));
    }

    public static void checkTitleContainsWithWait(String expectedTitlePart, long timeout) {
        WebDriverWait wait = new WebDriverWait(driver, timeout);
        assertTrue(wait.until(ExpectedConditions.textToBePresentInElement(selectedElement, expectedTitlePart)));
    }

    public static void checkTwoElementsTextIsEqual(String firstElementText, String secondElementText) {
        Assert.assertEquals(namedElements.get(firstElementText).getText(), namedElements.get(secondElementText).getText());
    }

    public static void checkTwoElementsAttrValIsEqual(String firstElement, String firstAttr, String secondElement,
                                                      String secondAttr) {
        Assert.assertEquals(namedElements.get(firstElement).getAttribute(firstAttr),
            namedElements.get(secondElement).getAttribute(secondAttr));
    }

    public static void checkNamedValuesAreEqual(String firstAlias, String secondAlias) {
        Assert.assertEquals(namedValues.get(firstAlias), namedValues.get(secondAlias));
    }

    public static void checkNamedValueIsLessThan(String firstAlias, String secondAlias) {
        Assert.assertTrue(Integer.parseInt(namedValues.get(firstAlias)) < Integer.parseInt(namedValues.get(secondAlias)));
    }

    public static void checkNamedValueIsGreaterThan(String firstAlias, String secondAlias) {
        Assert.assertTrue(Integer.parseInt(namedValues.get(firstAlias)) > Integer.parseInt(namedValues.get(secondAlias)));
    }

    /**
     * Clears only the single selectedElement
     */
    public static void clearSelectedElement() {
        selectedElement = null;
    }

    /**
     * Clears both selectedElements list and the single selectedElement
     */
    public static void clearSelectedElements() {
        selectedElement = null;
        selectedElements = new ArrayList<>();
    }

    /**
     * Clears only named elements
     */
    public static void clearNamedElements() {
        namedElements = new HashMap<>();
    }

    public static void clearNamedValues() {
        namedValues = new HashMap<>();
    }

    public static void closeBrowser() {
        if (BrowserBase.getCurrentBrowser().getDriver() != null) {
            logger.info("Performing closing down operations...");
            BrowserBase.getCurrentBrowser().close();
        }
    }

    public static void autoCloseBrowser() {
        if (BrowserBase.getCurrentBrowser().shouldCloseOnExit() && BrowserBase.getCurrentBrowser().getDriver() != null) {
            logger.info("Performing closing down operations...");
            BrowserBase.getCurrentBrowser().close();
        }
        else {
            if (BrowserBase.getCurrentBrowser().getScreenShotOnExitEnabled()) {
                BrowserBase.getCurrentBrowser().takeScreenshot();
            }
        }
    }
}
