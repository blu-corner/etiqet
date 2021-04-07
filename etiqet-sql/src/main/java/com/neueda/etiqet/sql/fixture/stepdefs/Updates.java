package com.neueda.etiqet.sql.fixture.stepdefs;

import com.neueda.etiqet.sql.fixture.SqlHandlers;
import cucumber.api.java.en.When;

import java.util.ArrayList;

public class Updates {

    @When("^I? ?update table \"([^\"]*)\" for columns \"([^\"]*)\" where \"([^\"]*)\"$")
    public void updateWithCondition(String tableName, String newFieldValues, String condition) {
        SqlHandlers.updateWithCondition(tableName, newFieldValues, condition);
    }

    @When("^I? ?insert values \"([^\"]*)\" into table \"([^\"]*)\"$")
    public void insertInto(ArrayList<String> values, String table) {
        SqlHandlers.insertInto(values, table);
    }

    @When("^I? ?insert values \"([^\"]*)\" for columns \"([^\"]*)\" into table \"([^\"]*)\"$")
    public void insertInto(ArrayList<String> values, ArrayList<String> columns, String table) {
        SqlHandlers.insertInto(values, columns, table);
    }

    @When("^I? ?delete all from table \"([^\"]*)\"$")
    public void deleteAll(String table) {
        SqlHandlers.deleteAll(table);
    }

    @When("^I? ?delete all from table \"([^\"]*)\" where \"([^\"]*)\"$")
    public void deleteWithCondition(String table, String condition) {
        SqlHandlers.deleteWithCondition(table, condition);
    }
}
