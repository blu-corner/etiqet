package com.neueda.etiqet.selenium.browser;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;
import java.util.concurrent.TimeUnit;

@XmlRootElement(name = "Edge")
public class Edge extends Browser {

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

    private List<BrowserSetting> capabilities;

    private WebDriver driver;

    public Edge() {
        pageLoadTimeout = -1;
    }

    @Override
    public void setupDriver() {

        WebDriverManager.edgedriver().setup();

        logger.info("Launching browser: " + name);
        EdgeOptions edgeOptions = new EdgeOptions();
        if (capabilities != null) {
            for (BrowserSetting browserSetting : capabilities) {
                edgeOptions.setCapability(browserSetting.getName(), browserSetting.getValue());
            }
        }

        driver = new EdgeDriver();

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

    @XmlElementWrapper(name = "capabilities")
    @XmlElement(name="capability")
    public List<BrowserSetting> getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(List<BrowserSetting> capabilities) {
        this.capabilities = capabilities;
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

