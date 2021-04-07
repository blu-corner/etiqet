package com.neueda.etiqet.sql.fixture.stepdefs;

import com.neueda.etiqet.sql.fixture.SqlHandlers;
import cucumber.api.java.en.Then;

import java.util.ArrayList;

public class Validations {

    @Then("^I? ?check (?:the)? ?number of rows found is (\\d+)$")
    public void rowCountEqualTo(int expectedRows) {
        SqlHandlers.checkRowCountEqualTo(expectedRows);
    }

    @Then("^I? ?check (?:the)? ?number of rows found is greater than (\\d+)$")
    public void rowCountGreaterThan(int expectedRows) {
        SqlHandlers.checkRowCountGreaterThan(expectedRows);
    }

    @Then("^I? ?check (?:the)? ?number of rows found is less than (\\d+)$")
    public void rowCountLessThan(int expectedRows) {
        SqlHandlers.checkRowCountLessThan(expectedRows);
    }

    @Then("^I? ?check (?:the)? ?value for column \"([^\"]*)\" at row (\\d+) is equal to \"([^\"]*)\"$")
    public static void checkValueForColumnAtRow(String columnName, int rowIndex, String value) {
        SqlHandlers.checkValueForColumnAtRow(value, rowIndex, columnName);
    }

    @Then("^I? ?check (?:the)? ?value for column \"([^\"]*)\" at row (\\d+) contains \"([^\"]*)\"$")
    public static void checkValueForColumnAtRowContains(String columnName, int rowIndex, String value) {
        SqlHandlers.checkValueForColumnAtRowContains(value, rowIndex, columnName);
    }

    @Then("^I? ?check (?:the)? ?row values are equal to \"([^\"]*)\" for column \"([^\"]*)\"$")
    public static void checkValuesAcrossRows(ArrayList<String> values, String columnName) {
        SqlHandlers.checkValuesAcrossRows(values, columnName);
    }

    @Then("^I? ?check (?:the)? ?column values are equal to \"([^\"]*)\" for row (\\d+)$")
    public static void checkValuesAcrossColumns(ArrayList<String> values, int rowIndex) {
        SqlHandlers.checkValuesAcrossColumns(values, rowIndex);
    }
}
