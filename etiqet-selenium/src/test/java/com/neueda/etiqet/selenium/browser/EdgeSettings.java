package com.neueda.etiqet.selenium.browser;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.util.ArrayList;
import java.util.List;

public class EdgeSettings extends Options {
    @XmlElementWrapper(name = "startup_arguments")
    @XmlElement(name="argument")
    private List<String> startupArgs;

    public EdgeSettings(){
        startupArgs = new ArrayList<>();
    }

    @Override
    public List<String> getStartupArgs() {
        return startupArgs;
    }
}
