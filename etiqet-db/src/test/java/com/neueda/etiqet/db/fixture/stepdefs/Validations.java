package com.neueda.etiqet.db.fixture.stepdefs;

import com.neueda.etiqet.db.fixture.DbHandlers;
import cucumber.api.java.en.Then;

import java.sql.SQLException;
import java.util.ArrayList;

public class Validations {

    @Then("^I? ?check (?:the)? ?number of rows found is (\\d+)$")
    public void rowCountEqualTo(int expectedRows) {
        DbHandlers.checkRowCountEqualTo(expectedRows);
    }

    @Then("^I? ?check (?:the)? ?number of rows found is greater than (\\d+)$")
    public void rowCountGreaterThan(int expectedRows) {
        DbHandlers.checkRowCountGreaterThan(expectedRows);
    }

    @Then("^I? ?check (?:the)? ?number of rows found is less than (\\d+)$")
    public void rowCountLessThan(int expectedRows) {
        DbHandlers.checkRowCountLessThan(expectedRows);
    }

    @Then("^I? ?check (?:the)? ?value for column \"([^\"]*)\" at row (\\d+) is equal to \"([^\"]*)\"$")
    public static void checkValueForColumnAtRow(String value, int rowIndex, String columnName) {
        DbHandlers.checkValueForColumnAtRow(value, rowIndex, columnName);
    }

    @Then("^I? ?check (?:the)? ?value for column \"([^\"]*)\" at row (\\d+) contains \"([^\"]*)\"$")
    public static void checkValueForColumnAtRowContains(String value, int rowIndex, String columnName) {
        DbHandlers.checkValueForColumnAtRowContains(value, rowIndex, columnName);
    }

    @Then("^I? ?check (?:the)? ?row values are equal to \"([^\"]*)\" for column \"([^\"]*)\"$")
    public static void checkValuesAcrossRows(ArrayList<String> values, String columnName) {
        DbHandlers.checkValuesAcrossRows(values, columnName);
    }

    @Then("^I? ?check (?:the)? ?column values are equal to \"([^\"]*)\" for row (\\d+)$")
    public static void checkValuesAcrossColumns(ArrayList<String> values, int rowIndex) {
        DbHandlers.checkValuesAcrossColumns(values, rowIndex);
    }
}
