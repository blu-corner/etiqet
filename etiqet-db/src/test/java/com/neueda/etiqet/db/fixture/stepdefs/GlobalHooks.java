package com.neueda.etiqet.db.fixture.stepdefs;

import com.neueda.etiqet.db.config.Config;
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
