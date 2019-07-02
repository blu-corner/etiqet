package com.neueda.etiqet.sql.fixture.stepdefs;

import com.neueda.etiqet.sql.fixture.SqlHandlers;
import cucumber.api.java.en.When;

public class Filters {

    @When("^I? ?get first row for column \"([^\"]*)\" as \"([^\"]*)\"$")
    public void getFirstRowForColumnAs(String column, String alias) {
        SqlHandlers.getColumnValAtRow(0, column, alias);
    }

    @When("^I? ?get column \"([^\"]*)\" at row (\\d+) as \"([^\"]*)\"$")
    public static void getColumnValAtRow(String column, Integer rowIndex, String alias) {
        SqlHandlers.getColumnValAtRow(rowIndex, column, alias);
    }
}
