Feature: NeuedaFixRest Test

  Scenario: Can change the trading phase for matchingengine
    Given a "fix" client
    And filter out "Logon" message
    And Neueda extensions enabled
    When client is logged on
    And "BME" order book is purged
    And "BME" phase is "preopening"
    Then change "BME" trading phase to "opening-auction"
    Then change "BME" trading phase to "regular-trading"
    Then change "BME" trading phase to "closing-auction"
    Then change "BME" trading phase to "extended-hours"
    Then change "BME" trading phase to "trading-closed"
    Then change "BME" trading phase to "preopening"
    And stop client

  Scenario: Can input order on preopening phase
    Given a "fix" client
    And filter out "Logon" message
    And Neueda extensions enabled
    And "BME" order book is purged
    And "BME" phase is "preopening"
    When client is logged on
    Then send a "NewOrderSingle" message with "ReceivedDeptID=9,ClOrdID=2222,AccountType=1,Account=35944156,HandlInst=1,Symbol=EURUSD,Side=2,TransactTime=20180115-13:30:00.000,OrderQty=1,OrdType=2,TimeInForce=4,Price=100,Currency=EUR"
    Then wait for a "Heartbeat" message within 35 seconds
    And "BME" order book is purged
    And stop client

  Scenario: Can input order on preopening phase AND change to opening auction
    Given a "fix" client
    And filter out "Logon" message
    And Neueda extensions enabled
    And "BME" order book is purged
    And "BME" phase is "preopening"
    When client is logged on
    Then wait for a "Heartbeat" message within 35 seconds
    Then send a "NewOrderSingle" message with "ReceivedDeptID=9,ClOrdID=2222,AccountType=1,Account=35944156,HandlInst=1,Symbol=EURUSD,Side=2,TransactTime=20180115-13:30:00.000,OrderQty=1,OrdType=2,TimeInForce=4,Price=100,Currency=EUR"
    Then change "BME" trading phase to "opening auction"
    And stop client