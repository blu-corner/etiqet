package com.neueda.etiqet.sql.fixture.stepdefs;

import com.neueda.etiqet.sql.config.Config;
import cucumber.api.java.Before;

public class GlobalHooks {
    private static boolean dunit = false;

    @Before
    public void beforeAll() {
        if(!dunit) {
            Config.init();
            dunit = true;
        }
    }
}
