Feature: Connecting to a database

    # To run this example you need to pull a docker image with the database from
    # Pull with the command > docker pull genschsa/mysql-employees
    # You can then run the image with > docker run -d \
    #  --name mysql-employees \
    #  -p 3306:3306 \
    #  -e MYSQL_ROOT_PASSWORD=college \
    #  -v $PWD/data:/var/lib/mysql \
    #  genschsa/mysql-employees

    Scenario: Connect with mysql-example config
        Given I connect to the "mysql-example" database

    Scenario: Select all with condition
        Given I connect to the "mysql-example" database
        And I select all from table "employees" where "last_name='Bridgland'"
        Then I print the results

    Scenario: Select columns with condition
        Given I connect to the "mysql-example" database
        And I select the columns "first_name, last_name" from table "employees" where "last_name='Bridgland'"
        Then I print the results

    Scenario: Select columns with multiple conditions
        Given I connect to the "mysql-example" database
        And I select the columns "first_name, last_name" from table "employees" where "last_name='Bridgland' and first_name='Deborah'"
        Then I print the results

        Scenario: Select distinct rows will be ignored for dialects that do not use 'DISTINCT ON'
        Given I connect to the "mysql-example" database
        And I select all from table "employees" where "last_name='Bridgland'" distinct on columns "last_name"
        Then I print the results
    
    Scenario: Delete rows
        Given I connect to the "mysql-example" database
        And I delete all from table "employees" where "last_name='Bridgland'"
        And I select the columns "first_name, last_name" from table "employees" where "last_name='Bridgland'"
        Then I check the number of rows found is 0
        Then I print the results

    Scenario: Insert rows
        Given I connect to the "mysql-example" database
        And I insert values "1132233221, 2015-12-17, New, Bridgland, M, 2015-12-17" for columns "emp_no, birth_date, first_name, last_name, gender, hire_date" into table "employees"
        And I select the columns "first_name, last_name" from table "employees" where "last_name='Bridgland'"
        Then I check the number of rows found is 1
        Then I print the results

    Scenario: Query builder example
        Given I connect to the "mysql-example" database
        Given I am creating a query builder as "builder"
        When I add a FROM statement using table "employees" to query "builder"
        When I add a SELECT statement selecting columns "first_name, last_name" to query "builder"
        When I add a condition "last_name = 'Warwick'" to query "builder"
        And I add a condition "first_name != 'Mayuko'" to query "builder"
        Then I execute the query "builder"
        Then I print the results

    Scenario: Check value equals
        Given I connect to the "mysql-example" database
        Given I am creating a query builder as "builder"
        When I add a FROM statement using table "employees" to query "builder"
        When I add a SELECT statement selecting columns "first_name, last_name" to query "builder"
        When I add a condition "last_name = 'Warwick'" to query "builder"
        Then I execute the query "builder"
        Then I check the value for column "first_name" at row 2 is equal to "Eric"
        Then I print the results

    Scenario: Check value contains
        Given I connect to the "mysql-example" database
        Given I am creating a query builder as "builder"
        When I add a FROM statement using table "employees" to query "builder"
        When I add a SELECT statement selecting columns "first_name, last_name" to query "builder"
        When I add a condition "last_name = 'Warwick'" to query "builder"
        Then I execute the query "builder"
        Then I check the value for column "first_name" at row 2 contains "Too"
        Then I print the results


