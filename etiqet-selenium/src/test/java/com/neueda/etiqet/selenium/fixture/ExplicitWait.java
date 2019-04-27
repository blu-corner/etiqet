package com.neueda.etiqet.selenium.fixture;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Enumeration of explicit wait selector methods. Apply wait will call the corresponding explicit wait. Some methods are
 * intended to return a single element, however for compatibility across waits an immutable list is returned and it is
 * the caller's responsibility to extract the single element. Waits that are actually intended to return multiple
 * elements are prefixed by 'ALL'
 */
public enum ExplicitWait {

    CLICKABLE {
        @Override
        public List<WebElement> applyWait(WebDriver driver, SelectorMethod selectorMethod, String locator,
            long timeout) {
            WebDriverWait wait = new WebDriverWait(driver, timeout);

            return new ArrayList<>(Arrays.asList(
                wait.until(ExpectedConditions.elementToBeClickable(resolveSearchMethod(selectorMethod, locator)))));
        }

        @Override
        public List<WebElement> applyWait(WebDriver driver, SelectorMethod selectorMethod, String locator, long timeout,
            WebElement element) {
            WebDriverWait wait = new WebDriverWait(driver, timeout);
            return new ArrayList<>(Arrays.asList(
                wait.until(ExpectedConditions
                    .elementToBeClickable(element.findElement(resolveSearchMethod(selectorMethod, locator))))));
        }

        @Override
        public List<WebElement> applyWaitAll(WebDriver driver, SelectorMethod selectorMethod, String locator,
            long timeout) {
            return applyWait(driver, selectorMethod, locator, timeout);
        }

        @Override
        public List<WebElement> applyWaitAll(WebDriver driver, SelectorMethod selectorMethod, String locator,
            long timeout,
            WebElement element) {
            return applyWait(driver, selectorMethod, locator, timeout, element);
        }
    },

    PRESENT {
        @Override
        public List<WebElement> applyWait(WebDriver driver, SelectorMethod selectorMethod, String locator,
            long timeout) {
            WebDriverWait wait = new WebDriverWait(driver, timeout);
            return new ArrayList<>(Arrays.asList(
                wait.until(ExpectedConditions.presenceOfElementLocated(resolveSearchMethod(selectorMethod, locator)))));
        }

        @Override
        public List<WebElement> applyWait(WebDriver driver, SelectorMethod selectorMethod, String locator, long timeout,
            WebElement element) {
            WebDriverWait wait = new WebDriverWait(driver, timeout);
            return new ArrayList<>(Arrays.asList(
                wait.until(ExpectedConditions
                    .presenceOfNestedElementLocatedBy(element, resolveSearchMethod(selectorMethod, locator)))));
        }

        @Override
        public List<WebElement> applyWaitAll(WebDriver driver, SelectorMethod selectorMethod, String locator,
            long timeout) {
            WebDriverWait wait = new WebDriverWait(driver, timeout);
            return wait.until(
                ExpectedConditions.presenceOfAllElementsLocatedBy(resolveSearchMethod(selectorMethod, locator)));
        }

        @Override
        public List<WebElement> applyWaitAll(WebDriver driver, SelectorMethod selectorMethod, String locator,
            long timeout,
            WebElement element) {
            WebDriverWait wait = new WebDriverWait(driver, timeout);

            // todo find a way to get a 'By' object from a webelement to get nested elements
            return wait.until(
                ExpectedConditions.presenceOfAllElementsLocatedBy(resolveSearchMethod(selectorMethod, locator)));
        }
    },

    VISIBLE {
        @Override
        public List<WebElement> applyWait(WebDriver driver, SelectorMethod selectorMethod, String locator,
            long timeout) {
            WebDriverWait wait = new WebDriverWait(driver, timeout);
            return new ArrayList<>(Arrays.asList(
                wait.until(ExpectedConditions
                    .visibilityOfElementLocated(ExplicitWait.resolveSearchMethod(selectorMethod, locator)))));
        }

        @Override
        public List<WebElement> applyWait(WebDriver driver, SelectorMethod selectorMethod, String locator, long timeout,
            WebElement element) {
            WebDriverWait wait = new WebDriverWait(driver, timeout);
            return new ArrayList<>(Arrays.asList(
                wait.until(ExpectedConditions
                    .visibilityOf(element.findElement(resolveSearchMethod(selectorMethod, locator))))));
        }

        @Override
        public List<WebElement> applyWaitAll(WebDriver driver, SelectorMethod selectorMethod, String locator,
            long timeout) {
            WebDriverWait wait = new WebDriverWait(driver, timeout);
            return wait.until(
                ExpectedConditions.visibilityOfAllElementsLocatedBy(resolveSearchMethod(selectorMethod, locator)));
        }

        @Override
        public List<WebElement> applyWaitAll(WebDriver driver, SelectorMethod selectorMethod, String locator,
            long timeout,
            WebElement element) {
            WebDriverWait wait = new WebDriverWait(driver, timeout);

            return wait.until(
                ExpectedConditions
                    .visibilityOfNestedElementsLocatedBy(element, resolveSearchMethod(selectorMethod, locator)));
        }
    };

    private static By resolveSearchMethod(SelectorMethod selectorMethod, String locator) {

        switch (selectorMethod) {
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
                throw new RuntimeException("Unknown selectorMethod");
        }
    }

    public abstract List<WebElement> applyWait(WebDriver driver, SelectorMethod searchMethod, String locator,
        long timeout);

    public abstract List<WebElement> applyWait(WebDriver driver, SelectorMethod searchMethod, String locator,
        long timeout,
        WebElement element);

    public abstract List<WebElement> applyWaitAll(WebDriver driver, SelectorMethod searchMethod, String locator,
        long timeout);

    public abstract List<WebElement> applyWaitAll(WebDriver driver, SelectorMethod searchMethod, String locator,
        long timeout,
        WebElement element);
}
