Feature: Testing scenarios on the Oanda API

  Scenario: GET request with 404 response
    Given a "rest" client
    When client is started
    Then send a "GET" message to "/api/test"
    And check the response has status code "404" with fields "errorMessage=No such endpoint."

  Scenario: Valid GET request with nested response
    Given a "rest" client
    When client is started
    Then send a "GET" message with headers "Authorization=Bearer 11df0fdd9c1d739d5f1151bd2200345e-09de019613becf13954f93a5b3d95693" to "/accounts/101-004-7816475-001/pricing/?instruments=EUR_USD"
    And check the response has status code "200" with fields "prices->0->instrument=EUR_USD"

  Scenario: Valid POST request with body
    Given a "rest" client
    When client is started
    Then send a "POST" message with payload "order->units=100,order->instrument=EUR_USD,order->timeInForce=FOK,order->type=MARKET,order->positionFill=DEFAULT" and headers "Authorization=Bearer 11df0fdd9c1d739d5f1151bd2200345e-09de019613becf13954f93a5b3d95693" to "/accounts/101-004-7816475-001/orders"
    And check the response has a status code "201"

  Scenario: Valid PUT request with body
    Given a "rest" client
    When client is started
    Then send a "PUT" message with payload "longUnits=ALL,shortUnits=ALL" and headers "Authorization=Bearer 11df0fdd9c1d739d5f1151bd2200345e-09de019613becf13954f93a5b3d95693" to "/accounts/101-004-7816475-001/positions/EUR_USD/close"
    And check the response has status code "400" with fields "longOrderRejectTransaction->type=shortOrderRejectTransaction->type,errorCode=CLOSEOUT_POSITION_DOESNT_EXIST"
