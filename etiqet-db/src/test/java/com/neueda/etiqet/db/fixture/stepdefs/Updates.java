package com.neueda.etiqet.db.fixture.stepdefs;

import com.neueda.etiqet.db.fixture.DbHandlers;
import cucumber.api.java.en.When;

import java.util.ArrayList;

public class Updates {

    @When("^I? ?update table \"([^\"]*)\" for columns \"([^\"]*)\" where \"([^\"]*)\"$")
    public void updateWithCondition(String tableName, String newFieldValues, String condition) {
        DbHandlers.updateWithCondition(tableName, newFieldValues, condition);
    }

    @When("^I? ?insert values \"([^\"]*)\" into table \"([^\"]*)\"$")
    public void insertInto(ArrayList<String> values, String table) {
        DbHandlers.insertInto(values, table);
    }

    @When("^I? ?insert values \"([^\"]*)\" for columns \"([^\"]*)\" into table \"([^\"]*)\"$")
    public void insertInto(ArrayList<String> values, ArrayList<String> columns, String table) {
        DbHandlers.insertInto(values, columns, table);
    }

    @When("^I? ?delete all from table \"([^\"]*)\"$")
    public void deleteAll(String table) {
        DbHandlers.deleteAll(table);
    }

    @When("^I? ?delete all from table \"([^\"]*)\" where \"([^\"]*)\"$")
    public void deleteWithCondition(String table, String condition) {
        DbHandlers.deleteWithCondition(table, condition);
    }
}
