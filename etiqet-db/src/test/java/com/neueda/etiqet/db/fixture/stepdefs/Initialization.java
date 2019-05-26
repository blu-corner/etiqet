package com.neueda.etiqet.db.fixture.stepdefs;

import com.neueda.etiqet.db.fixture.DbHandlers;
import cucumber.api.java.en.Given;

public class Initialization {

    @Given("^I? ?connect to (?:the)? database$")
    public void connect() {
        DbHandlers.connect(DbHandlers.DEFAULT_SERVER_ALIAS);
    }
}
