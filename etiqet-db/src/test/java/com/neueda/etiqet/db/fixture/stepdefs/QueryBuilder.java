package com.neueda.etiqet.db.fixture.stepdefs;

import com.neueda.etiqet.db.fixture.DbHandlers;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.When;
import org.jooq.JoinType;

import java.util.ArrayList;

public class QueryBuilder {

    @Given("^I? ?am creating a query builder as \"([^\"]*)\"$")
    public  void initQueryBuilder(String alias) {
        DbHandlers.initQueryBuilder(alias);
    }

    @When("^I? ?execute (?:the)? ?query \"([^\"]*)\"$")
    public void executeQuery(String alias) {
        DbHandlers.executeQuery(alias);
    }

    @When("^I? ?add a? ?SELECT statement selecting all columns to query \"([^\"]*)\"$")
    public void addSelect(String alias) {
        DbHandlers.addSelect(alias);
    }

    @When("^I? ?add a? ?SELECT statement selecting columns \"([^\"]*)\" to query \"([^\"]*)\"$")
    public void addSelect(ArrayList<String> columnNames, String alias) {
        DbHandlers.addSelect(columnNames, alias);
    }

    @When("^I? ?add a? ?DISTINCT ON rule for columns \"([^\"]*)\" to query \"([^\"]*)\"$")
    public void addCondition(ArrayList<String> distinctColumns, String alias) {
        DbHandlers.addDistinctOn(distinctColumns, alias);
    }

    @When("^I? ?add a? ?FROM statement using table \"([^\"]*)\" to query \"([^\"]*)\"$")
    public void addFrom(String tableName, String alias) {
        DbHandlers.addFrom(tableName, alias);
    }

    @When("^I? ?add a? ?condition \"([^\"]*)\" to query \"([^\"]*)\"$")
    public void addCondition(String conditionExp, String alias) {
        DbHandlers.addCondition(conditionExp, alias);
    }

    @When("^I? ?add a? ?GROUP BY statement \"([^\"]*)\" to query \"([^\"]*)\"$")
    public void addHaving(ArrayList<String> columnNames, String alias) {
        DbHandlers.addGroupBy(columnNames, alias);
    }

    @When("^I? ?add a? ?HAVING clause \"([^\"]*)\" to query \"([^\"]*)\"$")
    public void addHaving(String conditionExp, String alias) {
        DbHandlers.addHaving(conditionExp, alias);
    }

    @When("^I? ?add an? ?ORDER BY for columns \"([^\"]*)\" to query \"([^\"]*)\"$")
    public void addOrderBy(ArrayList<String> columnNames, String alias) {
        DbHandlers.addOrderBy(columnNames, alias);
    }

    /**JOINS*/

    @When("^I? ?add (?:an)? ?inner join with table \"([^\"]*)\" to query \"([^\"]*)\"$")
    public void addInnerJoin(String tableName, String alias) {
        DbHandlers.addJoin(tableName, JoinType.JOIN, alias);
    }

    @When("^I? ?add a? ?left outer join with table \"([^\"]*)\" to query \"([^\"]*)\"$")
    public void addLeftOuterJoin(String tableName, String alias) {
        DbHandlers.addJoin(tableName, JoinType.LEFT_OUTER_JOIN, alias);
    }

    @When("^I? ?add a? ?right outer join with table \"([^\"]*)\" to query \"([^\"]*)\"$")
    public void addRightOuterJoin(String tableName, String alias) {
        DbHandlers.addJoin(tableName, JoinType.RIGHT_OUTER_JOIN, alias);
    }

    @When("^I? ?add a? ?full (?:outer)? ?join with table \"([^\"]*)\" to query \"([^\"]*)\"$")
    public void addFullJoin(String tableName, String alias) {
        DbHandlers.addJoin(tableName, JoinType.FULL_OUTER_JOIN, alias);
    }

    @When("^I? ?add (?:an)? ?inner join with table \"([^\"]*)\" on condition \"([^\"]*)\" to query \"([^\"]*)\"$")
    public static void addInnerJoinWithCondition(String tableName, String conditionExp, String alias) {
        DbHandlers.addJoinWithCondition(tableName, JoinType.JOIN, conditionExp,  alias);
    }

    @When("^I? ?add a? ?left outer join with table \"([^\"]*)\" on condition \"([^\"]*)\" to query \"([^\"]*)\"$")
    public static void addLeftJoinWithCondition(String tableName, String conditionExp, String alias) {
        DbHandlers.addJoinWithCondition(tableName, JoinType.LEFT_OUTER_JOIN, conditionExp,  alias);
    }

    @When("^I? ?add a? ?right outer join with table \"([^\"]*)\" on condition \"([^\"]*)\" to query \"([^\"]*)\"$")
    public static void addRightJoinWithCondition(String tableName, String conditionExp, String alias) {
        DbHandlers.addJoinWithCondition(tableName, JoinType.RIGHT_OUTER_JOIN, conditionExp,  alias);
    }

    @When("^I? ?add a? ?full (?:outer)? ?join with table \"([^\"]*)\" on condition \"([^\"]*)\" to query \"([^\"]*)\"$")
    public static void addFullJoinWithCondition(String tableName, String conditionExp, String alias) {
        DbHandlers.addJoinWithCondition(tableName, JoinType.FULL_OUTER_JOIN, conditionExp,  alias);
    }
}
