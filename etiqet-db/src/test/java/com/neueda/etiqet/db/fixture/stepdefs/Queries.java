package com.neueda.etiqet.db.fixture.stepdefs;

import com.neueda.etiqet.db.fixture.DbHandlers;
import cucumber.api.java.en.When;

import java.util.ArrayList;

public class Queries {

    @When("^I? ?select all from (?:the)? ?table \"([^\"]*)\"$")
    public void selectAll(String tableName) {
        DbHandlers.selectAll(tableName);
    }

    @When("^I select all from table \"([^\"]*)\" where \"([^\"]*)\"$")
    public void selectAll(String tableName, String condition) {
        DbHandlers.selectAllWithCondition(tableName, condition);
    }

    @When("^I select columns \"([^\"]*)\" from table \"([^\"]*)\"$")
    public void selectColumns(ArrayList<String> columns, String table) {
        DbHandlers.selectColumns(columns, table);
    }

    @When("^I select columns \"([^\"]*)\" from table \"([^\"]*)\" where \"([^\"]*)\"$")
    public void selectColumns(ArrayList<String> columns, String table, String condition) {
        DbHandlers.selectColumnsWithCondition(columns, table, condition);
    }
}
