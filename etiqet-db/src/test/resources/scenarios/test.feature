Feature: testing

    Scenario: test
        Given I connect to the database
        When I select all from the table "trader"
        When I select column "username" at row 1 as "blah"
        
        When I select all from table "trader" where "retail_trader=false"

        When I select
        
        Then I print the results
