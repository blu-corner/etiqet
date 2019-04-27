package com.neueda.etiqet.selenium.browser;

import java.util.concurrent.TimeUnit;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

@XmlRootElement
public class Firefox extends Browser {

    @XmlAttribute
    private String name;

    @XmlAttribute(name = "driver_path")
    private String driverPath;

    @XmlElement
    private boolean headless;

    @XmlElement(name = "close_on_exit")
    private boolean closeOnExit;

    @XmlElement(name = "screenshot_on_exit")
    private boolean screenShotOnExitEnabled;

    @XmlElement(name = "implicit_wait")
    private long implicitWait;

    @XmlElement(name = "page_load_timeout", defaultValue = "-1")
    private long pageLoadTimeout;

    @XmlElement(name = "script_timeout")
    private long scriptTimeout;

    private WebDriver driver;

    @XmlElements({
        @XmlElement(name = "FirefoxOptions", type = FirefoxSettings.class),
        @XmlElement(name = "ChromeOptions", type = ChromeSettings.class)
    })
    private Options options;

    public Firefox() {
        options = new FirefoxSettings();
        pageLoadTimeout = -1;
    }

    @Override
    public void setupDriver() {
        System.setProperty("webdriver.gecko.driver", driverPath);

        org.openqa.selenium.firefox.FirefoxOptions firefoxOptions = new org.openqa.selenium.firefox.FirefoxOptions();
        firefoxOptions.addArguments(options.getStartupArgs());
        firefoxOptions.setHeadless(isHeadless());

        logger.info("Launching browser: " + name);
        driver = new FirefoxDriver(firefoxOptions);

        driver.manage().timeouts().implicitlyWait(implicitWait, TimeUnit.SECONDS);
        if (pageLoadTimeout >= 0) {
            driver.manage().timeouts().pageLoadTimeout(pageLoadTimeout, TimeUnit.SECONDS);
        }
        driver.manage().timeouts().setScriptTimeout(scriptTimeout, TimeUnit.SECONDS);
    }

    @Override
    public boolean isHeadless() {
        return headless;
    }

    @Override
    public boolean shouldCloseOnExit() {
        return closeOnExit;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDriverPath() {
        return driverPath;
    }

    @Override
    public WebDriver getDriver() {
        return driver;
    }

    @Override
    public void close() {
        if (driver == null) {
            return;
        }
        if (screenShotOnExitEnabled) {
            takeScreenshot();
        }
        logger.info("Closing browser: " + name);
        driver.close();
        driver = null;
        logger.info("Browser closed: " + name);
    }

    @Override
    public boolean getScreenShotOnExitEnabled() {
        return screenShotOnExitEnabled;
    }

    @Override
    public long getImplicitWait() {
        return implicitWait;
    }

    @Override
    public long getPageTimeout() {
        return pageLoadTimeout;
    }

    @Override
    public long getScriptTimeout() {
        return scriptTimeout;
    }
}
