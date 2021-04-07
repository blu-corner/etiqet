Feature: Connecting to a database

    # To run this example you need to have a sql server to access and configure the default profile
    # in db-conn.xml
    Scenario: Connect with default config
        Given I connect to the database
