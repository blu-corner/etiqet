package com.neueda.etiqet.db.fixture.stepdefs;

import com.neueda.etiqet.db.fixture.DbHandlers;
import cucumber.api.java.en.When;

import java.util.ArrayList;

public class Queries {

    @When("^I? ?select all from (?:the)? ?table \"([^\"]*)\"(?: distinct on columns \"([^\"]*)\")?$")
    public void selectAll(String tableName, String distinctColumns) {
        DbHandlers.selectAll(tableName, distinctColumns);
    }

    @When("^I? ?select all from table \"([^\"]*)\" where \"([^\"]*)\"(?: distinct on columns \"([^\"]*)\")?$")
    public void selectAll(String tableName, String condition, String distinctColumns) {
        DbHandlers.selectAllWithCondition(tableName, condition, distinctColumns);
    }

    @When("^I? ?select (?:the)? ?columns \"([^\"]*)\" from table \"([^\"]*)\"(?: distinct on columns \"([^\"]*)\")?$")
    public void selectColumns(ArrayList<String> columns, String table, String distinctColumns) {
        DbHandlers.selectColumns(columns, table, distinctColumns);
    }

    @When("^I? ?select (?:the)? ?columns \"([^\"]*)\" from table \"([^\"]*)\" where \"([^\"]*)\"(?: distinct on columns \"([^\"]*)\")?$")
    public void selectColumns(ArrayList<String> columns, String table, String condition, String distinctColumns) {
        DbHandlers.selectColumnsWithCondition(columns, table, condition, distinctColumns);
    }

    @When("^I? ?send a raw SQL query \"([^\"]*)\"$")
    public void sendSQLQuery(String query) {
        DbHandlers.sendRawSQLQuery(query);
    }
}
