Feature: REST Etiqet test

Scenario: GET request with 404 response
    Given a "rest" client
    When client is started
    Then "GET" a "test_01" from "test/api" as "test01response"
    And check that "test01Response" has status code "404"

Scenario: Valid GET request with nested response
   Given a "rest" client
   When client is started
   Then "GET" a "test_01" with headers "Authorization=Bearer 11df0fdd9c1d739d5f1151bd2200345e-09de019613becf13954f93a5b3d95693" from "accounts/101-004-7816475-001/pricing?instruments=EUR_USD"
     And check that status code "200"
     And check that matches "response_01"
     And check that contains "prices"
     And check that has "prices->0->instrument=EUR_USD"

Scenario: Valid POST request with body and overridden header
    Given a "rest" client
    When client is started
    Then "POST" a "test_01" with headers "Authorization=Bearer 11df0fdd9c1d739d5f1151bd2200345e-09de019613becf13954f93a5b3d95693" and payload "order->units=100,order->instrument=EUR_USD,order->timeInForce=FOK,order->type=MARKET,order->positionFill=DEFAULT" to "accounts/101-004-7816475-001/orders"
      And check that status code "201"

Scenario: Valid PUT request with body and default header and fields
    Given a "rest" client
    When client is started
    Then "PUT" a "test_03" with payload "shortUnits=ALL" to "accounts/101-004-7816475-001/positions/EUR_USD/close"
      And check that status code "400"
      And check that contains "errorCode"
      And check that the response field "errorCode" is equal to "CLOSEOUT_POSITION_DOESNT_EXIST"
      And check that has "longOrderRejectTransaction->type=shortOrderRejectTransaction->type"

 Scenario: Multiple requests and referencing previous request in payload of subsequent request
    Given a "rest" client
    When client is started
    Then "GET" a "test_04" from "accounts/101-004-7816475-001/pricing?instruments=EUR_USD" as "testSecenario1"
    Then "GET" a "test_04" from "accounts/101-004-7816475-001/pricing?instruments=GBP_USD" as "testSecenario2"
      And check that "testSecenario1" has status code "200"
      And check that "testSecenario2" has status code "200"
      And check that "testSecenario1" contains "prices"
      And check that "testSecenario2" contains "prices"
      And check that "testSecenario1" has "prices->0->instrument=EUR_USD"
      And check that "testSecenario2" has "prices->0->instrument=GBP_USD"
    Then "POST" a "test_04" with payload "order->units=100,order->instrument=testSecenario2->instrument,order->timeInForce=FOK,order->type=MARKET,order->positionFill=DEFAULT" to "accounts/101-004-7816475-001/orders"
      And check that status code "201"
