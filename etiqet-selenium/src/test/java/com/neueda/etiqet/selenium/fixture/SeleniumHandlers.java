package com.neueda.etiqet.selenium.fixture;

import com.neueda.etiqet.selenium.browser.BrowserBase;
import org.apache.log4j.Logger;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

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
 * When Selecting elements, if an element is already selected, the search will look for elements relative to
 * the currently selected element. If no element is currently selected then the whole DOM will be searched.
 *
 * Explicit and implicit wait when set will be used on applicable handlers until they are disabled. i.e. implicit wait
 * is set to 0 or explicit wait is disabled. If a search method returns multiple elements explicit wait will wait for
 * all elements to be visible, present etc if applicable.
 */

public class SeleniumHandlers {

    private final static org.apache.log4j.Logger logger = Logger.getLogger(SeleniumHandlers.class);

    private static WebDriver driver;

    private static HashMap<String, WebElement> namedElements;
    private static List<WebElement> selectedElements;
    private static WebElement selectedElement;

    private static ExplicitWait explicitWait;
    private static long explicitWaitTimeout;

    static {
        SeleniumHandlers.namedElements = new HashMap<>();
        SeleniumHandlers.selectedElements = new ArrayList<>();
    }

    /**
     * Browsers will be loaded from the config before any tests run and can be selected using this method
     * @param browserName The name of the browser which should be defined as a name attribute in config
     */
    public static void openBrowser(String browserName) {
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
     *
     * If explicit wait is not null it will be used where applicable
     *
     * @param locator a string that targets an element in the dom.
     * @param alias (optional) add an alias to the element and a reference will be saved in namedElements
     * @throws NoSuchElementException when no element is not found
     */
    public static void selectElementByCss(String locator, String alias) throws NoSuchElementException {
        if (explicitWait != null) {
            selectedElement = selectedElement == null ?
                explicitWait.applyWait(driver, SelectorMethod.CSS, locator, explicitWaitTimeout).get(0) :
                explicitWait.applyWait(driver, SelectorMethod.CSS, locator, explicitWaitTimeout, selectedElement).get(0);
            return;
        }
        selectedElement = selectedElement == null ? driver.findElement(By.cssSelector(locator)) :
            selectedElement.findElement(By.cssSelector(locator));
        if (alias != null) {
            namedElements.put(alias, selectedElement);
        }
    }

    /**
     * Selected elements will be stored in selectedElements and any of these can be assigned to selectedElement
     * by calling selectFromElements(int index) step definition.
     * Selenium returns an empty list if no elements found, however NoSuchElementException is thrown to
     * remain consistent with the selectElementBy... methods.
     *
     * If explicit wait is not null it will be used where applicable
     *
     * @param locator a string that targets an element in the dom.
     * @throws NoSuchElementException when no element is not found
     */
    public static void selectElementsByCss(String locator) throws NoSuchElementException {
        if (explicitWait != null) {
            selectedElements = selectedElement == null ?
                explicitWait.applyWaitAll(driver, SelectorMethod.CSS, locator, explicitWaitTimeout) :
                explicitWait.applyWaitAll(driver, SelectorMethod.CSS, locator, explicitWaitTimeout, selectedElement);
            return;
        }
        selectedElements = selectedElement == null ? driver.findElements(By.cssSelector(locator)) :
            selectedElement.findElements(By.cssSelector(locator));
    }

    /**
     * Selected element will be stored in selectedElement which is the webElement used by default for most step defs
     * An exception is thrown if no element is found and the value of selectedElement will remain unchanged
     *
     * If explicit wait is not null it will be used where applicable
     *
     * @param locator a string that targets an element in the dom.
     * @param alias (optional) add an alias to the element and a reference will be saved in namedElements
     * @throws NoSuchElementException when no element is not found
     */
    public static void selectElementByXpath(String locator, String alias) throws NoSuchElementException {
        if (explicitWait != null) {
            selectedElement = selectedElement == null ?
                explicitWait.applyWait(driver, SelectorMethod.XPATH, locator, explicitWaitTimeout).get(0) :
                explicitWait.applyWait(driver, SelectorMethod.XPATH, locator, explicitWaitTimeout, selectedElement).get(0);
            return;
        }
        selectedElement = selectedElement == null ? driver.findElement(By.xpath(locator)) :
            selectedElement.findElement(By.xpath(locator));
        if (alias != null) {
            namedElements.put(alias, selectedElement);
        }
    }

    /**
     * Selected elements will be stored in selectedElements and any of these can be assigned to selectedElement
     * by calling selectFromElements(int index) step definition.
     * Selenium returns an empty list if no elements found, however NoSuchElementException is thrown to
     * remain consistent with the selectElementBy... methods.
     *
     * If explicit wait is not null it will be used where applicable
     *
     * @param locator a string that targets an element in the dom.
     * @throws NoSuchElementException when no element is not found
     */
    public static void selectElementsByXpath(String locator) throws NoSuchElementException {
        if (explicitWait != null) {
            selectedElements = selectedElement == null ?
                explicitWait.applyWaitAll(driver, SelectorMethod.XPATH, locator, explicitWaitTimeout) :
                explicitWait.applyWaitAll(driver, SelectorMethod.XPATH, locator, explicitWaitTimeout, selectedElement);
            return;
        }
        selectedElements = selectedElement == null ? driver.findElements(By.xpath(locator)) :
            selectedElement.findElements(By.xpath(locator));
    }

    /**
     * Selected element will be stored in selectedElement which is the webElement used by default for most step defs
     * An exception is thrown if no element is found and the value of selectedElement will remain unchanged
     *
     * If explicit wait is not null it will be used where applicable
     *
     * @param locator a string that targets an element in the dom.
     * @param alias (optional) add an alias to the element and a reference will be saved in namedElements
     * @throws NoSuchElementException when no element is not found
     */
    public static void selectElementById(String locator, String alias) throws NoSuchElementException {
        if (explicitWait != null) {
            selectedElement = selectedElement == null ?
                explicitWait.applyWait(driver, SelectorMethod.ID, locator, explicitWaitTimeout).get(0) :
                explicitWait.applyWait(driver, SelectorMethod.ID, locator, explicitWaitTimeout, selectedElement).get(0);
            return;
        }
        selectedElement = selectedElement == null ? driver.findElement(By.id(locator)) :
            selectedElement.findElement(By.id(locator));
        if (alias != null) {
            namedElements.put(alias, selectedElement);
        }
    }

    /**
     * Selected element will be stored in selectedElement which is the webElement used by default for most step defs
     * An exception is thrown if no element is found and the value of selectedElement will remain unchanged
     *
     * If explicit wait is not null it will be used where applicable
     *
     * @param locator a string that targets an element in the dom.
     * @param alias (optional) add an alias to the element and a reference will be saved in namedElements
     * @throws NoSuchElementException when no element is not found
     */
    public static void selectElementByTag(String locator, String alias) throws NoSuchElementException {
        if (explicitWait != null) {
            selectedElement = selectedElement == null ?
                explicitWait.applyWait(driver, SelectorMethod.TAG, locator, explicitWaitTimeout).get(0) :
                explicitWait.applyWait(driver, SelectorMethod.TAG, locator, explicitWaitTimeout, selectedElement).get(0);
            return;
        }
        selectedElement = selectedElement == null ? driver.findElement(By.tagName(locator)) :
            selectedElement.findElement(By.tagName(locator));
        if (alias != null) {
            namedElements.put(alias, selectedElement);
        }
    }

    /**
     * Selected elements will be stored in selectedElements and any of these can be assigned to selectedElement
     * by calling selectFromElements(int index) step definition.
     * Selenium returns an empty list if no elements found, however NoSuchElementException is thrown to
     * remain consistent with the selectElementBy... methods.
     *
     * If explicit wait is not null it will be used where applicable
     *
     * @param locator a string that targets an element in the dom.
     * @throws NoSuchElementException when no element is not found
     */
    public static void selectElementsByTag(String locator) throws NoSuchElementException {
        if (explicitWait != null) {
            selectedElements = selectedElement == null ?
                explicitWait.applyWaitAll(driver, SelectorMethod.TAG, locator, explicitWaitTimeout) :
                explicitWait.applyWaitAll(driver, SelectorMethod.TAG, locator, explicitWaitTimeout, selectedElement);
            return;
        }
        selectedElements = selectedElement == null ? driver.findElements(By.tagName(locator)) :
            selectedElement.findElements(By.tagName(locator));
    }

    /**
     * Selected element will be stored in selectedElement which is the webElement used by default for most step defs
     * An exception is thrown if no element is found and the value of selectedElement will remain unchanged
     * If multiple elements are found the first element will be assigned to selectedElement
     *
     * If explicit wait is not null it will be used where applicable
     *
     * @param locator a string that targets an element in the dom.
     * @param alias (optional) add an alias to the element and a reference will be saved in namedElements
     * @throws NoSuchElementException when no element is not found
     */
    public static void selectElementByClassName(String locator, String alias) throws NoSuchElementException {
        if (explicitWait != null) {
            selectedElement = selectedElement == null ?
                explicitWait.applyWait(driver, SelectorMethod.CLASS, locator, explicitWaitTimeout).get(0) :
                explicitWait.applyWait(driver, SelectorMethod.CLASS, locator, explicitWaitTimeout, selectedElement).get(0);
            return;
        }
        selectedElement = selectedElement == null ? driver.findElement(By.className(locator)) :
            selectedElement.findElement(By.className(locator));
        if (alias != null) {
            namedElements.put(alias, selectedElement);
        }
    }

    /**
     * Selected elements will be stored in selectedElements and any of these can be assigned to selectedElement
     * by calling selectFromElements(int index) step definition.
     * Selenium returns an empty list if no elements found, however NoSuchElementException is thrown to
     * remain consistent with the selectElementBy... methods.
     *
     * If explicit wait is not null it will be used where applicable
     *
     * @param locator a string that targets an element in the dom.
     * @throws NoSuchElementException when no element is not found
     */
    public static void selectElementsByClassName(String locator) throws NoSuchElementException {
        if (explicitWait != null) {
            selectedElements = selectedElement == null ?
                explicitWait.applyWaitAll(driver, SelectorMethod.CLASS, locator, explicitWaitTimeout) :
                explicitWait.applyWaitAll(driver, SelectorMethod.CLASS, locator, explicitWaitTimeout, selectedElement);
            return;
        }
        selectedElements = selectedElement == null ? driver.findElements(By.className(locator)) :
            selectedElement.findElements(By.className(locator));
    }

    /**
     * Selected element will be stored in selectedElement which is the webElement used by default for most step defs
     * An exception is thrown if no element is found and the value of selectedElement will remain unchanged
     *
     * If explicit wait is not null it will be used where applicable
     *
     * @param locator a string that targets an element in the dom.
     * @param alias (optional) add an alias to the element and a reference will be saved in namedElements
     * @throws NoSuchElementException when no element is not found
     */
    public static void selectElementByLinkText(String locator, String alias) throws NoSuchElementException {
        if (explicitWait != null) {
            selectedElement = selectedElement == null ?
                explicitWait.applyWait(driver, SelectorMethod.LINK, locator, explicitWaitTimeout).get(0) :
                explicitWait.applyWait(driver, SelectorMethod.LINK, locator, explicitWaitTimeout, selectedElement).get(0);
            return;
        }
        selectedElement = selectedElement == null ? driver.findElement(By.linkText(locator)) :
            selectedElement.findElement(By.linkText(locator));
        if (alias != null) {
            namedElements.put(alias, selectedElement);
        }
    }

    /**
     * Selected element will be stored in selectedElement which is the webElement used by default for most step defs
     * An exception is thrown if no element is found and the value of selectedElement will remain unchanged
     *
     * If explicit wait is not null it will be used where applicable
     *
     * @param locator a string that targets an element in the dom.
     * @param alias (optional) add an alias to the element and a reference will be saved in namedElements
     * @throws NoSuchElementException when no element is not found
     */
    public static void selectElementByPartialLinkText(String locator, String alias) throws NoSuchElementException {
        if (explicitWait != null) {
            selectedElement = selectedElement == null ?
                explicitWait.applyWait(driver, SelectorMethod.PARTIAL_LINK, locator, explicitWaitTimeout).get(0) :
                explicitWait.applyWait(driver, SelectorMethod.PARTIAL_LINK, locator, explicitWaitTimeout, selectedElement).get(0);
            return;
        }
        selectedElement = selectedElement == null ? driver.findElement(By.partialLinkText(locator)) :
            selectedElement.findElement(By.partialLinkText(locator));
        if (alias != null) {
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
     * Selects WebElement from selectedElements and assigned to selectedElement
     *
     * @param index to select from selectedElements
     */
    public static void selectFromElements(Integer index) {
        selectedElement = selectedElements.get(index);
    }

    /**
     * Filters the selectedElements by removing elements that are not found using an xpath expression
     * @param filter the xpath expression
     */
    public static void filterSelectedElementsByXPath(String filter) {

        if (explicitWait != null) {

            int i = selectedElements.size(); // todo - better solution
            while (i != 0) {
                i--;
                try {
                    List<WebElement> ignore = selectedElement == null ?
                        explicitWait.applyWait(driver, SelectorMethod.XPATH, ".//*[contains(text(),'" + filter + "')]", explicitWaitTimeout) :
                        explicitWait.applyWait(driver, SelectorMethod.XPATH, ".//*[contains(text(),'" + filter + "')]", explicitWaitTimeout, selectedElement);

                } catch (TimeoutException e) {
                    selectedElements.remove(i);
                }
            }
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
                    List<WebElement> ignore = selectedElement == null ?
                        explicitWait.applyWait(driver, SelectorMethod.XPATH, ".//*[contains(text(),'" + filterText + "')]", explicitWaitTimeout) :
                        explicitWait.applyWait(driver, SelectorMethod.XPATH, ".//*[contains(text(),'" + filterText + "')]", explicitWaitTimeout, selectedElement);
                } catch (TimeoutException e) {
                    selectedElements.remove(i);
                }
            }
        }
        selectedElements.removeIf(element -> element.findElements(By.xpath(".//*[contains(text(),'" + filterText + "')]")).size() == 0);
    }

    /**
     * If the named element exists it will become the selectedElement so that actions can be performed on it
     *
     * @param elementName alias to be selected
     */
    public static void selectNamedElement(String elementName) {
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

    public static void pressF1Key(){
        selectedElement.sendKeys(Keys.F1);
    }

    public static void pressF2Key(){
        selectedElement.sendKeys(Keys.F2);
    }

    public static void pressF3Key(){
        selectedElement.sendKeys(Keys.F3);
    }

    public static void pressF4Key(){
        selectedElement.sendKeys(Keys.F4);
    }

    public static void pressF5Key(){
        selectedElement.sendKeys(Keys.F5);
    }
    public static void pressF6Key(){
        selectedElement.sendKeys(Keys.F6);
    }

    public static void pressF7Key(){
        selectedElement.sendKeys(Keys.F7);
    }

    public static void pressF8Key(){
        selectedElement.sendKeys(Keys.F8);
    }

    public static void pressF9Key(){
        selectedElement.sendKeys(Keys.F9);
    }

    public static void pressF10Key(){
        selectedElement.sendKeys(Keys.F10);
    }

    public static void pressF11Key(){
        selectedElement.sendKeys(Keys.F11);
    }

    public static void pressF12Key(){
        selectedElement.sendKeys(Keys.F12);
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
    }

    public static void pauseSecs(long seconds) {
        Actions actions = new Actions(driver);
        actions.pause(Duration.ofSeconds(seconds));
    }

    public static void nameSelectedElement(String elementName) {
        namedElements.put(elementName, selectedElement);
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
            ExplicitWait.PRESENT.applyWait(driver, SelectorMethod.XPATH, "//*[contains(text(),'" + expectedText + "')]", timeout);
            return;
        }
        List<WebElement> elementsFound = driver.findElements(By.xpath("//*[contains(text(),'" + expectedText + "')]"));
        assertTrue("Text " + expectedText + " not found", elementsFound.size() > 0);
    }

    public static void checkPageDoesNotContainText(String unexpectedText, Long timeout) {
        if (timeout != null) {
            try {
                ExplicitWait.PRESENT.applyWait(driver, SelectorMethod.XPATH, "//*[contains(text(),'" + unexpectedText + "')]", timeout);
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
            ExplicitWait.PRESENT.applyWait(driver, SelectorMethod.XPATH, "//*[text()='" + expectedText + "']", timeout);
            return;
        }
        List<WebElement> elementsFound = driver.findElements(By.xpath("//*[text()='" + expectedText + "']"));
        assertTrue("Text " + expectedText + " not found", elementsFound.size() > 0);
    }

    public static void checkNoPageElementHasInnerText(String unexpectedText, Long timeout) {
        if (timeout != null) {
            try {
                ExplicitWait.PRESENT.applyWait(driver, SelectorMethod.XPATH, "//*[text()='" + unexpectedText + "']", timeout);
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
    }
}
