package com.neueda.etiqet.selenium.fixture;

import com.neueda.etiqet.selenium.SeleniumUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.pagefactory.ByChained;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Enumeration of explicit wait selector methods.
 * Apply wait will call the corresponding explicit wait.
 * Some methods are intended to return a single element, however for compatibility across waits an immutable list is
 * returned and it is the caller's responsibility to extract the single element.
 * Waits that are actually intended to return multiple elements are prefixed by 'ALL'
 */
public enum ExplicitWait {

    CLICKABLE {
        @Override
        public List<WebElement> applyWait(WebDriver driver, LocatorType locatorType, String locator, long timeout) {
            WebDriverWait wait = new WebDriverWait(driver, timeout);

            return new ArrayList<>(Arrays.asList(
                    wait.until(ExpectedConditions.elementToBeClickable(resolveSearchMethod(locatorType, locator)))));
        }

        @Override
        public List<WebElement> applyWait(WebDriver driver, LocatorType locatorType, String locator, long timeout,
                                          WebElement element) {
            WebDriverWait wait = new WebDriverWait(driver, timeout);
            return new ArrayList<>(Arrays.asList(
                    wait.until(ExpectedConditions.elementToBeClickable(element.findElement(resolveSearchMethod(locatorType, locator))))));
        }

        @Override
        public List<WebElement> applyWaitAll(WebDriver driver, LocatorType locatorType, String locator, long timeout) {
            return applyWait(driver, locatorType, locator, timeout);
        }

        @Override
        public List<WebElement> applyWaitAll(WebDriver driver, LocatorType locatorType, String locator, long timeout,
                                             WebElement element) {
            return applyWait(driver, locatorType, locator, timeout, element);
        }
    },

    PRESENT {
        @Override
        public List<WebElement> applyWait(WebDriver driver, LocatorType locatorType, String locator, long timeout) {
            WebDriverWait wait = new WebDriverWait(driver, timeout);
            return new ArrayList<>(Arrays.asList(
                    wait.until(ExpectedConditions.presenceOfElementLocated(resolveSearchMethod(locatorType, locator)))));
        }

        @Override
        public List<WebElement> applyWait(WebDriver driver, LocatorType locatorType, String locator, long timeout,
                                          WebElement element) {
            WebDriverWait wait = new WebDriverWait(driver, timeout);
            return new ArrayList<>(Arrays.asList(
                    wait.until(ExpectedConditions.presenceOfNestedElementLocatedBy(element, resolveSearchMethod(locatorType, locator)))));
        }

        @Override
        public List<WebElement> applyWaitAll(WebDriver driver, LocatorType locatorType, String locator, long timeout) {
            WebDriverWait wait = new WebDriverWait(driver, timeout);
            return wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(resolveSearchMethod(locatorType, locator)));
        }

        @Override
        public List<WebElement> applyWaitAll(WebDriver driver, LocatorType locatorType, String locator, long timeout,
                                             WebElement element) {

            WebDriverWait wait = new WebDriverWait(driver, timeout);
            LocatorType relativeLocatorType = SeleniumUtils.getLocatorType(element);
            String relativeLocator = SeleniumUtils.getLocator(element);
            ByChained byChained = new ByChained(resolveSearchMethod(relativeLocatorType, relativeLocator),
                resolveSearchMethod(locatorType, locator));
            return wait.until(
                ExpectedConditions.presenceOfAllElementsLocatedBy(byChained));
        }
    },

    VISIBLE {
        @Override
        public List<WebElement> applyWait(WebDriver driver, LocatorType locatorType, String locator, long timeout) {
            WebDriverWait wait = new WebDriverWait(driver, timeout);
            return new ArrayList<>(Arrays.asList(
                    wait.until(ExpectedConditions.visibilityOfElementLocated(ExplicitWait.resolveSearchMethod(locatorType, locator)))));
        }

        @Override
        public List<WebElement> applyWait(WebDriver driver, LocatorType locatorType, String locator, long timeout,
                                          WebElement element) {
            WebDriverWait wait = new WebDriverWait(driver, timeout);
            return new ArrayList<>(Arrays.asList(
                    wait.until(ExpectedConditions.visibilityOf(element.findElement(resolveSearchMethod(locatorType, locator))))));
        }

        @Override
        public List<WebElement> applyWaitAll(WebDriver driver, LocatorType locatorType, String locator, long timeout) {
            WebDriverWait wait = new WebDriverWait(driver, timeout);
            return wait.until(
                    ExpectedConditions.visibilityOfAllElementsLocatedBy(resolveSearchMethod(locatorType, locator)));
        }

        @Override
        public List<WebElement> applyWaitAll(WebDriver driver, LocatorType locatorType, String locator, long timeout,
                                             WebElement element) {
            WebDriverWait wait = new WebDriverWait(driver, timeout);

            return wait.until(
                    ExpectedConditions.visibilityOfNestedElementsLocatedBy(element, resolveSearchMethod(locatorType, locator)));
        }
    };

    public abstract List<WebElement> applyWait(WebDriver driver, LocatorType searchMethod, String locator, long timeout);
    public abstract List<WebElement> applyWait(WebDriver driver, LocatorType searchMethod, String locator, long timeout,
                                               WebElement element);

    public abstract List<WebElement> applyWaitAll(WebDriver driver, LocatorType searchMethod, String locator, long timeout);
    public abstract List<WebElement> applyWaitAll(WebDriver driver, LocatorType searchMethod, String locator, long timeout,
                                                  WebElement element);

    private static By resolveSearchMethod(LocatorType locatorType, String locator) {

        switch (locatorType) {
            case CSS:
                return By.cssSelector(locator);

            case XPATH:
                return By.xpath(locator);

            case ID:
                return By.id(locator);

            case CLASS:
                return By.className(locator);

            case TAG:
                return By.tagName(locator);

            case LINK:
                return By.linkText(locator);

            case PARTIAL_LINK:
                return By.partialLinkText(locator);

            default:
                throw new RuntimeException("Unknown locatorType");
        }
    }
}
