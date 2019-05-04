package com.neueda.etiqet.selenium.browser;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.xml.bind.annotation.XmlRootElement;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for all browsers. This class should be extended when adding another browser
 */
@XmlRootElement(name = "Browser")
public abstract class Browser {

    protected static Logger logger = LoggerFactory.getLogger(Browser.class);

    public abstract void setupDriver();

    public abstract String getName();

    public abstract String getDriverPath();

    public abstract WebDriver getDriver();

    public abstract boolean isHeadless();

    public abstract boolean shouldCloseOnExit();

    public abstract long getImplicitWait();

    public abstract long getPageTimeout();

    public abstract long getScriptTimeout();

    public abstract boolean getScreenShotOnExitEnabled();

    public abstract void close();


    public void takeScreenshot() {
        File screenshot = ((TakesScreenshot) getDriver())
            .getScreenshotAs(OutputType.FILE);
        String datetime = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(Calendar.getInstance().getTime());
        String destination = "src/test/resources/screenshots/screenshot_at_" + datetime + ".png";
        try {
            FileUtils.copyFile(screenshot, new File(destination));
            logger.info("Saved screenshot to destination " + destination);
        } catch (IOException e) {
            logger.error("Failed to save screenshot at " + datetime + " to destination " + destination);
        }
    }
}
