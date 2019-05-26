package com.neueda.etiqet.db.fixture.stepdefs;

import com.neueda.etiqet.db.fixture.DbHandlers;
import cucumber.api.java.en.When;

public class Filters {

    @When("^I? ?get first row for column \"([^\"]*)\" as \"([^\"]*)\"$")
    public void getFirstRowForColumnAs(String column, String alias) {
        DbHandlers.getColumnValAtRow(0, column, alias);
    }

    @When("^I? ?get column \"([^\"]*)\" at row (\\d+) as \"([^\"]*)\"$")
    public static void getColumnValAtRow(String column, Integer rowIndex, String alias) {
        DbHandlers.getColumnValAtRow(rowIndex, column, alias);
    }
}
