package com.neueda.etiqet.db.fixture.stepdefs;

import com.neueda.etiqet.db.fixture.DbHandlers;
import cucumber.api.java.en.Then;

public class Util {

    @Then("^I? ?print (?:the)? ?results$")
    public void printResults() {
        DbHandlers.printResults();
    }
}
