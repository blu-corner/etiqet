package com.neueda.etiqet.selenium.fixture.stepdefs;

import com.neueda.etiqet.selenium.config.Config;
import cucumber.api.java.Before;

public class GlobalHooks {

    private static boolean dunit = false;

    @Before
    public void beforeAll() {
        if (!dunit) {
            Config.init();
            dunit = true;
        }
    }
}
