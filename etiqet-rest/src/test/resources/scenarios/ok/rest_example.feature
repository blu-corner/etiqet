Feature: REST Etiqet test

  Scenario: GET request with 404 response
    Given a "rest" client
    When client is started
    Then "GET" a "test_01" from "test/api" as "test01response"
    And check that "test01Response" has status code "404"

  Scenario: Valid GET request with nested response
    Given a "rest" client
    When client is started
    Then "GET" a "test_01" from "test" as "test02response"
    And check that "test02response" has status code "200"
    And check that "test02response" has "response=ok,code=200"

