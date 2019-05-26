package com.neueda.etiqet.db.fixture.stepdefs;

import com.neueda.etiqet.db.fixture.DbHandlers;
import cucumber.api.java.en.Then;

import java.sql.SQLException;

public class Validations {

    @Then("^I? ?check (?:the)? ?number of rows found is (\\d+)$")
    public void rowCountEqualTo(int expectedRows) throws SQLException {
        DbHandlers.checkRowCountEqualTo(expectedRows);
    }

    @Then("^I? ?check (?:the)? ?number of rows found is greater than (\\d+)$")
    public void rowCountGreaterThan(int expectedRows) throws SQLException {
        DbHandlers.checkRowCountGreaterThan(expectedRows);
    }

    @Then("^I? ?check (?:the)? ?number of rows found is less than (\\d+)$")
    public void rowCountLessThan(int expectedRows) throws SQLException {
        DbHandlers.checkRowCountLessThan(expectedRows);
    }
}
