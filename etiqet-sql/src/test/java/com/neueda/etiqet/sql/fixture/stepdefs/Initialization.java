package com.neueda.etiqet.sql.fixture.stepdefs;

import com.neueda.etiqet.sql.fixture.SqlHandlers;
import cucumber.api.java.en.Given;

public class Initialization {

    @Given("^I? ?connect to (?:the)? database$")
    public void connect() {
        SqlHandlers.connect(SqlHandlers.DEFAULT_SERVER_ALIAS);
    }
}
