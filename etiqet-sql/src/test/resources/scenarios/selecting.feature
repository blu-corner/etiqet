Feature: Select queries

    Scenario: Basic select
        Given I connect to the database
        When I select all from the table "trader"
        Then I print the results

    Scenario: Selecting all columns with conditions
        Given I connect to the database
        When I select all from table "trader" where "retail_trader=false and id=2"
        Then I print the results

    Scenario: Selecting specific columns
        Given I connect to the database
        When I select the columns "id, username, password" from table "trader"
        Then I print the results

    Scenario: Selecting specific columns with conditions
        Given I connect to the database
        When I select the columns "id, username, password, active, retail_trader" from table "trader" where "active=true and retail_trader=false"
        Then I print the results
