package com.neueda.etiqet.selenium.browser;

import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import javax.xml.bind.annotation.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.TimeUnit;

@XmlRootElement(name = "Android")
public class Android extends Browser {

    @XmlAttribute
    private String name;

    @XmlAttribute(name = "driver_path")
    private String driverPath;

    @XmlElement(name = "remote_url")
    private String remoteUrl;

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

    public Android() {
        pageLoadTimeout = -1;
    }

    @Override
    public void setupDriver() {
        logger.info("Launching browser: " + name);
        DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
        if (capabilities != null) {
            for (BrowserSetting browserSetting : capabilities) {
                desiredCapabilities.setCapability(browserSetting.getName(), browserSetting.getValue());
            }
        }

        try {
            URL url = new URL(remoteUrl);
            driver = new AndroidDriver(url, desiredCapabilities);
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }

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
