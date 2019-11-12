package com.neueda.etiqet.selenium.browser;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;
import java.util.concurrent.TimeUnit;

@XmlRootElement(name = "Chrome")
public class Chrome extends Browser {

    @XmlAttribute
    private String name;

    @XmlAttribute(name="driver_path")
    private String driverPath;

    @XmlElement
    private boolean headless;

    @XmlElement
    private boolean closeOnExit;

    @XmlElement(name = "screenshot_on_exit")
    private boolean screenShotOnExitEnabled;

    @XmlElement(name = "implicit_wait")
    private long implicitWait;

    @XmlElement(name = "page_load_timeout", defaultValue = "-1")
    private long pageLoadTimeout;

    @XmlElement(name = "script_timeout")
    private long scriptTimeout;

    private List<BrowserSetting> capabilities;

    private List<String> startupArguments;

    private List<String> extensions;

    private WebDriver driver;

    public Chrome(){
        pageLoadTimeout = -1;
    }

    @Override
    public void setupDriver() {
        System.setProperty("webdriver.chrome.driver", driverPath);
        org.openqa.selenium.chrome.ChromeOptions chromeOptions = new ChromeOptions();
        if (startupArguments != null) {
            chromeOptions.addArguments(startupArguments);
        }
        if (extensions != null) {
            chromeOptions.addEncodedExtensions(extensions);
        }
        chromeOptions.setHeadless(isHeadless());

        logger.info("Launching browser: " + name);
        driver = new ChromeDriver(chromeOptions);

        driver.manage().timeouts().implicitlyWait(implicitWait, TimeUnit.SECONDS);
        if (pageLoadTimeout >= 0) {
            driver.manage().timeouts().pageLoadTimeout(pageLoadTimeout, TimeUnit.SECONDS);
        }
        driver.manage().timeouts().setScriptTimeout(scriptTimeout, TimeUnit.SECONDS);
    }

    @Override
    public void close() {
        if (screenShotOnExitEnabled) {
            takeScreenshot();
        }
        logger.info("Closing browser: " + name);
        driver.close();
        logger.info("Browser closed: " + name);
    }

    @XmlElementWrapper
    @XmlElement(name="startup_arguments")
    public List<String> getStartupArguments() {
        return startupArguments;
    }

    public void setStartupArguments(List<String> startupArguments) {
        this.startupArguments = startupArguments;
    }

    @XmlElementWrapper
    @XmlElement(name="extensions")
    public List<String> getExtensions() {
        return extensions;
    }

    public void setExtensions(List<String> extensions) {
        this.startupArguments = extensions;
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
