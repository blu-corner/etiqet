package com.neueda.etiqet.selenium;

import com.neueda.etiqet.selenium.fixture.LocatorType;
import org.openqa.selenium.WebElement;

public class SeleniumUtils {

    public static LocatorType getLocatorType(WebElement element) {
        String sElement = element.toString().split("-> ")[1];
        String locatorType = sElement.split(": ")[0];

        if (locatorType.contains("css")) {
            return LocatorType.CSS;
        }

        else if (locatorType.contains("xpath")) {
            return LocatorType.XPATH;
        }

        else if (locatorType.contains("id")) {
            return LocatorType.ID;
        }

        else if (locatorType.contains("class")) {
            return LocatorType.CLASS;
        }

        else if (locatorType.contains("tag")) {
            return LocatorType.TAG;
        }

        else if (locatorType.contains("link")) {
            return LocatorType.LINK;
        }

        else if (locatorType.contains("partial")) {
            return LocatorType.PARTIAL_LINK;
        }

        else {
            throw new LocatorException("Unable to determine locator from " + locatorType);
        }
    }

    public static String getLocatorTypeRaw(WebElement element) {
        String sElement = element.toString().split("-> ")[1];
        return sElement.split(": ")[0];
    }

    public static String getLocator(WebElement element) {
        String sElement = element.toString().split("-> ")[1];
        String locPre = sElement.split(": ")[1];
        return locPre.substring(0, locPre.length() - 1);
    }
}
