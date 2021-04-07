package com.neueda.etiqet.selenium.browser;

public class BrowserSetting {

    private String name;

    private String value;

    public BrowserSetting() {
    }

    public BrowserSetting(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
