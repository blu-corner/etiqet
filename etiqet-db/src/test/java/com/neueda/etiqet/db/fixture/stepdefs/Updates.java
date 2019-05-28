package com.neueda.etiqet.db.fixture.stepdefs;

import com.neueda.etiqet.db.fixture.DbHandlers;
import cucumber.api.java.en.When;

public class Updates {

    @When("^I? ?update table \"([^\"]*)\" for columns \"([^\"]*)\" where \"([^\"]*)\"$")
    public void updateWithCondition(String tableName, String newFieldValues, String condition) {
        DbHandlers.updateWithCondition(tableName, newFieldValues, condition);
    }

}
