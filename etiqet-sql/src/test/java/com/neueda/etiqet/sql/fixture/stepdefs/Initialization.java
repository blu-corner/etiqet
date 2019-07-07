package com.neueda.etiqet.sql.fixture.stepdefs;

import com.neueda.etiqet.sql.UnsupportedDialectException;
import com.neueda.etiqet.sql.fixture.SqlHandlers;
import cucumber.api.java.en.Given;

public class Initialization {

    @Given("^I? ?connect to (?:the)? database$")
    public void connect() throws UnsupportedDialectException {
        SqlHandlers.connect(SqlHandlers.DEFAULT_SERVER_ALIAS);
    }

    @Given("^I? ?connect to (?:the)? \"([^\"]*)\" database$")
    public void connect(String sqlServer) throws UnsupportedDialectException {
        SqlHandlers.connect(sqlServer);
    }
}
