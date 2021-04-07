package com.neueda.etiqet.sql.fixture.stepdefs;

import com.neueda.etiqet.sql.fixture.SqlHandlers;
import cucumber.api.java.en.When;

import java.util.ArrayList;

public class Queries {

    @When("^I? ?select all from (?:the)? ?table \"([^\"]*)\"(?: distinct on columns \"([^\"]*)\")?$")
    public void selectAll(String tableName, String distinctColumns) {
        SqlHandlers.selectAll(tableName, distinctColumns);
    }

    @When("^I? ?select all from table \"([^\"]*)\" where \"([^\"]*)\"(?: distinct on columns \"([^\"]*)\")?$")
    public void selectAll(String tableName, String condition, String distinctColumns) {
        SqlHandlers.selectAllWithCondition(tableName, condition, distinctColumns);
    }

    @When("^I? ?select (?:the)? ?columns \"([^\"]*)\" from table \"([^\"]*)\"(?: distinct on columns \"([^\"]*)\")?$")
    public void selectColumns(ArrayList<String> columns, String table, String distinctColumns) {
        SqlHandlers.selectColumns(columns, table, distinctColumns);
    }

    @When("^I? ?select (?:the)? ?columns \"([^\"]*)\" from table \"([^\"]*)\" where \"([^\"]*)\"(?: distinct on columns \"([^\"]*)\")?$")
    public void selectColumns(ArrayList<String> columns, String table, String condition, String distinctColumns) {
        SqlHandlers.selectColumnsWithCondition(columns, table, condition, distinctColumns);
    }

    @When("^I? ?send a raw SQL query \"([^\"]*)\"$")
    public void sendSQLQuery(String query) {
        SqlHandlers.sendRawSQLQuery(query);
    }
}
