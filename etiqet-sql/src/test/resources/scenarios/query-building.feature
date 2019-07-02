Feature: Building a query

    Scenario: Basic query building
        Given I connect to the database
        Given I am creating a query builder as "builder"
        When I add a FROM statement using table "trader" to query "builder"
        When I add a SELECT statement selecting columns "id, username, active, retail_trader" to query "builder"
        When I add a condition "id=2" to query "builder"
            And I add a condition "username != 'user'" to query "builder"
        Then I execute the query "builder"
        Then I print the results

    Scenario: Another basic query building example
        Given I connect to the database
        Given I am creating a query builder as "query"
            And I add a FROM statement using table "app_type" to query "query"
            And I add a SELECT statement selecting all columns to query "query"
            And I add a DISTINCT ON rule for columns "fix_sessions_conf" to query "query"
        Then I execute the query "query"
