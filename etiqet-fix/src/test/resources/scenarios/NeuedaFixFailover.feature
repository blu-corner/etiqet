Feature: Test client failover

  Scenario: Test failover to secondary client
    Given a "fix" client with primary config "${etiqet.directory}\etiqet-fix\src\test\resources\config\client.cfg" and secondary config "${etiqet.directory}\etiqet-fix\src\test\resources\config\secondary_client.cfg"
    When client is logged on
    And "BME" order book is purged
    Then send a "NewOrderSingle" message with "AccountType=3,ReceivedDeptID=EQ" as "order"
    And wait for an "ExecutionReport" message with "TargetSubID=DBL"
    And wait for an "ExecutionReport" message with "TargetSubID=DBL"
    Then failover
    When client is logged on
    And "BME" order book is purged
    Then send a "NewOrderSingle" message with "AccountType=3,ReceivedDeptID=EQ"
    And wait for an "ExecutionReport" message with "TargetSubID=DBL"
    And wait for an "ExecutionReport" message with "TargetSubID=DBL"
    And "BME" order book is purged
    Then stop client

  Scenario: Test named client failover to secondary client
    Given a "fix" client "myFixClient" with primary config "${etiqet.directory}\etiqet-fix\src\test\resources\config\client.cfg" and secondary config "${etiqet.directory}\etiqet-fix\src\test\resources\config\secondary_client.cfg"
    And filter out "Logon" message
    When client "myFixClient" is logged on
    And "BME" order book is purged for "myFixClient"
    Then send a "NewOrderSingle" message with "AccountType=3,ReceivedDeptID=EQ" using "myFixClient"
    Then wait for "myFixClient" to receive an "ExecutionReport"
    Then wait for "myFixClient" to receive an "ExecutionReport"
    Then failover client "myFixClient"
    Then stop client "myFixClient"
