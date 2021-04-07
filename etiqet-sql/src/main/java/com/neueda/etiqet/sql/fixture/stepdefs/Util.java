package com.neueda.etiqet.sql.fixture.stepdefs;

import com.neueda.etiqet.sql.fixture.SqlHandlers;
import cucumber.api.java.en.Then;

public class Util {

    @Then("^I? ?print (?:the)? ?results$")
    public void printResults() {
        SqlHandlers.printResults();
    }
}
