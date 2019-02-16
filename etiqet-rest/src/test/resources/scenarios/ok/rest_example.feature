Feature: REST Etiqet test

    Scenario: GET request with 404 response
        Given a "rest" client
        When client is started
        Then "GET" a "test_01" from "test/api" as "test01response"
        And check that "test01Response" has status code "404"

    Scenario: Valid GET request with response
        Given a "rest" client
        When client is started
        Then "GET" a "test_01" from "test" as "test02response"
        And check that "test02response" has status code "200"
        And check that "test02response" has "response=ok,code=200"

    Scenario: POST request with data
        Given a "rest" client
        When client is started
        Then "POST" a "test_02" from "dataNeeded" as "test03response"
        And check that "test03response" has status code "200"
        And check that "test03response" has "response->message=received_ok,code=200"

    Scenario: Valid POST requested wih auth header
        Given a "rest" client
        When client is started
        Then "POST" a "test_03" from "authRequired" as "test04response"
        And check that "test04response" has status code "200"
        And check that "test04response" has "response->message=protected_information,response->login=true,code=200"
