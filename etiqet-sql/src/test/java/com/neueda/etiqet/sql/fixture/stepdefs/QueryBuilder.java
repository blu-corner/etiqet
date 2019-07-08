package com.neueda.etiqet.sql.fixture.stepdefs;

import com.neueda.etiqet.sql.fixture.SqlHandlers;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.When;
import org.jooq.JoinType;

import java.util.ArrayList;

public class QueryBuilder {

    @Given("^I? ?am creating a query builder as \"([^\"]*)\"$")
    public  void initQueryBuilder(String alias) {
        SqlHandlers.initQueryBuilder(alias);
    }

    @When("^I? ?execute (?:the)? ?query \"([^\"]*)\"$")
    public void executeQuery(String alias) {
        SqlHandlers.executeQuery(alias);
    }

    @When("^I? ?add a? ?SELECT statement selecting all columns to query \"([^\"]*)\"$")
    public void addSelect(String alias) {
        SqlHandlers.addSelect(alias);
    }

    @When("^I? ?add a? ?SELECT statement selecting columns \"([^\"]*)\" to query \"([^\"]*)\"$")
    public void addSelect(ArrayList<String> columnNames, String alias) {
        SqlHandlers.addSelect(columnNames, alias);
    }

    @When("^I? ?add a? ?DISTINCT ON rule for columns \"([^\"]*)\" to query \"([^\"]*)\"$")
    public void addCondition(ArrayList<String> distinctColumns, String alias) {
        SqlHandlers.addDistinctOn(distinctColumns, alias);
    }

    @When("^I? ?add a? ?FROM statement using table \"([^\"]*)\" to query \"([^\"]*)\"$")
    public void addFrom(String tableName, String alias) {
        SqlHandlers.addFrom(tableName, alias);
    }

    @When("^I? ?add a? ?condition \"([^\"]*)\" to query \"([^\"]*)\"$")
    public void addCondition(String conditionExp, String alias) {
        SqlHandlers.addCondition(conditionExp, alias);
    }

    @When("^I? ?add a? ?GROUP BY statement \"([^\"]*)\" to query \"([^\"]*)\"$")
    public void addHaving(ArrayList<String> columnNames, String alias) {
        SqlHandlers.addGroupBy(columnNames, alias);
    }

    @When("^I? ?add a? ?HAVING clause \"([^\"]*)\" to query \"([^\"]*)\"$")
    public void addHaving(String conditionExp, String alias) {
        SqlHandlers.addHaving(conditionExp, alias);
    }

    @When("^I? ?add an? ?ORDER BY for columns \"([^\"]*)\" to query \"([^\"]*)\"$")
    public void addOrderBy(ArrayList<String> columnNames, String alias) {
        SqlHandlers.addOrderBy(columnNames, alias);
    }

    /**JOINS*/

    @When("^I? ?add (?:an)? ?inner join with table \"([^\"]*)\" to query \"([^\"]*)\"$")
    public void addInnerJoin(String tableName, String alias) {
        SqlHandlers.addJoin(tableName, JoinType.JOIN, alias);
    }

    @When("^I? ?add a? ?left outer join with table \"([^\"]*)\" to query \"([^\"]*)\"$")
    public void addLeftOuterJoin(String tableName, String alias) {
        SqlHandlers.addJoin(tableName, JoinType.LEFT_OUTER_JOIN, alias);
    }

    @When("^I? ?add a? ?right outer join with table \"([^\"]*)\" to query \"([^\"]*)\"$")
    public void addRightOuterJoin(String tableName, String alias) {
        SqlHandlers.addJoin(tableName, JoinType.RIGHT_OUTER_JOIN, alias);
    }

    @When("^I? ?add a? ?full (?:outer)? ?join with table \"([^\"]*)\" to query \"([^\"]*)\"$")
    public void addFullJoin(String tableName, String alias) {
        SqlHandlers.addJoin(tableName, JoinType.FULL_OUTER_JOIN, alias);
    }

    @When("^I? ?add (?:an)? ?inner join with table \"([^\"]*)\" on condition \"([^\"]*)\" to query \"([^\"]*)\"$")
    public static void addInnerJoinWithCondition(String tableName, String conditionExp, String alias) {
        SqlHandlers.addJoinWithCondition(tableName, JoinType.JOIN, conditionExp,  alias);
    }

    @When("^I? ?add a? ?left outer join with table \"([^\"]*)\" on condition \"([^\"]*)\" to query \"([^\"]*)\"$")
    public static void addLeftJoinWithCondition(String tableName, String conditionExp, String alias) {
        SqlHandlers.addJoinWithCondition(tableName, JoinType.LEFT_OUTER_JOIN, conditionExp,  alias);
    }

    @When("^I? ?add a? ?right outer join with table \"([^\"]*)\" on condition \"([^\"]*)\" to query \"([^\"]*)\"$")
    public static void addRightJoinWithCondition(String tableName, String conditionExp, String alias) {
        SqlHandlers.addJoinWithCondition(tableName, JoinType.RIGHT_OUTER_JOIN, conditionExp,  alias);
    }

    @When("^I? ?add a? ?full (?:outer)? ?join with table \"([^\"]*)\" on condition \"([^\"]*)\" to query \"([^\"]*)\"$")
    public static void addFullJoinWithCondition(String tableName, String conditionExp, String alias) {
        SqlHandlers.addJoinWithCondition(tableName, JoinType.FULL_OUTER_JOIN, conditionExp,  alias);
    }
}
