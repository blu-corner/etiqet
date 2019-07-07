package com.neueda.etiqet.sql.fixture.stepdefs;

import com.neueda.etiqet.sql.config.DbConfig;
import cucumber.api.java.Before;

public class GlobalHooks {
    private static boolean dunit = false;

    @Before
    public void beforeAll() {
        if(!dunit) {
            DbConfig.init();
            dunit = true;
        }
    }
}
