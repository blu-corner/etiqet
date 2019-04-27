package com.neueda.etiqet.selenium.browser;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "ChromeOptions")
public class ChromeSettings extends Options {

    @XmlElementWrapper(name = "startup_arguments")
    @XmlElement(name = "argument")
    private List<String> startupArgs;

    @XmlElementWrapper(name = "encoded_extensions")
    @XmlElement(name = "extension")
    private List<String> encodedExtensions;

    public ChromeSettings() {
        startupArgs = new ArrayList<>();
        encodedExtensions = new ArrayList<>();
    }

    @Override
    public List<String> getStartupArgs() {
        return startupArgs;
    }

    public List<String> getEncodedExtensions() {
        return encodedExtensions;
    }
}
