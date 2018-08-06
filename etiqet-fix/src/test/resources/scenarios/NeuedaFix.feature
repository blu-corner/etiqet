Feature: Test correctly formed messages are accepted

  Scenario: Test Execution Report is accepted with Side set to 2
    Given a "fix" client
    And "BME" order book is purged
    And filter out "Logon" message
    When client is logged on
    Then send a "NewOrderSingle" message with "ClOrdID=2222,ReceivedDeptID=9,AccountType=1,Account=15944156,HandlInst=1,Symbol=EURUSD,Side=2,TransactTime=20180115-13:40:00.000,OrderQty=1,OrdType=2,TimeInForce=4,Currency=EUR"
    And wait for an "ExecutionReport" message with "ExecType=A,OrdStatus=A" within 10 seconds
    And wait for an "ExecutionReport" message with "ExecType=0,OrdStatus=0" within 10 seconds
    And wait for an "ExecutionReport" message with "ExecType=C,OrdStatus=8" within 10 seconds
    And "BME" order book is purged
    And stop client

  Scenario: Check the time for messages are in the correct order
    Given a "fix" client
    And "BME" order book is purged
    And filter out "Logon" message
    When client is logged on
    Then send a "NewOrderSingle" message with "AccountType=3,ReceivedDeptID=EQ" as "order"
    And wait for an "ExecutionReport" message as "newExecReport"
    And check "newExecReport" contains "SendingTime"
    Then send a "OrderCancelReplaceRequest" message with "AccountType=1,ReceivedDeptID=FX,OrigClOrdID=order->ClOrdID" as "test"
    And wait for an "ExecutionReport" message as "replaceExecReport"
    And check "replaceExecReport" contains "SendingTime"
    And check "replaceExecReport" for "ExecType=0,OrdType=2"
    And check that "SendingTime" in "replaceExecReport" is greater than "SendingTime" in "newExecReport"
    And "BME" order book is purged
    Then stop client

Scenario: Test correctly formed Logon is accepted - HeartBtInt is required
  Given a "fix" client
    And wait for a "Logon" message with "MsgType=A,HeartBtInt=30" within 10 seconds
  And stop client

  Scenario: Test Execution Report is accepted with Correct Side, ExecType and OrdStatus
    Given a "fix" client
    And "BME" order book is purged
      And filter out "Logon" message
    When client is logged on
      Then send a "NewOrderSingle" message with "ClOrdID=2222,ReceivedDeptID=9,AccountType=1,Account=35944156,HandlInst=1,Symbol=EURUSD,Side=1,TransactTime=20180115-13:30:00.000,OrderQty=1,OrdType=2,TimeInForce=4,Currency=EUR"
        And wait for an "ExecutionReport" message with "ExecType=A,OrdStatus=A" within 10 seconds
        And wait for an "ExecutionReport" message with "ExecType=0,OrdStatus=0" within 10 seconds
        And wait for an "ExecutionReport" message with "ExecType=C,OrdStatus=8" within 10 seconds
    And stop client

  Scenario: Test Execution Report is accepted with Correct Side, ExecType and OrdStatus
    Given a "fix" client
    And "BME" order book is purged
    And filter out "Logon" message
    When client is logged on
    Then send a "NewOrderSingle" message with "ClOrdID=2222,ReceivedDeptID=9,AccountType=1,Account=35944156,HandlInst=1,Symbol=EURUSD,Side=1,TransactTime=20180115-13:30:00.000,OrderQty=1,OrdType=2,TimeInForce=4,Currency=EUR"
    And wait for an "ExecutionReport" message with "ExecType=A,OrdStatus=A" within 10 seconds
    And wait for an "ExecutionReport" message with "ExecType=0,OrdStatus=0" within 10 seconds
    And wait for an "ExecutionReport" message with "ExecType=C,OrdStatus=8" within 10 seconds
    And stop client

  Scenario: Test Execution Report is has the correct time precision milli
    Given a "fix" client
    And filter out "Logon" message
    When client is logged on
    And "BME" order book is purged
    Then send a "NewOrderSingle" message with "ClOrdID=2222,ReceivedDeptID=9,AccountType=1,Account=35944156,HandlInst=1,Symbol=EURUSD,Side=1,TransactTime=20180115-13:30:00.000,OrderQty=1,OrdType=2,TimeInForce=4,Currency=EUR"
    And wait for an "ExecutionReport" message as "mymessage"
    And check that "TransactTime" in "mymessage" has "3" precision
    And stop client

  Scenario: Test Execution Report is has the correct time precision milli
    Given a "fix" client
    And filter out "Logon" message
    When client is logged on
    And "BME" order book is purged
    Then send a "NewOrderSingle" message with "ClOrdID=2222,ReceivedDeptID=9,AccountType=1,Account=35944156,HandlInst=1,Symbol=EURUSD,Side=1,TransactTime=20180115-13:30:00.000,OrderQty=1,OrdType=2,TimeInForce=4,Currency=EUR"
   And wait for an "ExecutionReport" message as "mymessage"
   And check that "TransactTime" in "mymessage" has "milli" precision
   And stop client

   # Throwing an error for FIXUtils not found
  Scenario: Test Reject message is accepted when correctly formed
    Given a "fix" client
      And filter out "Logon" message
    When client is logged on
    And "BME" order book is purged
    Then send a "NewOrderSingle" message with "ClOrdID=2222,ReceivedDeptID=9,AccountType=1,Account=35944156,HandlInst=1,Symbol=EURUSD,Side=u,TransactTime=20180115-13:30:00.000,OrderQty=1,OrdType=2,TimeInForce=4,Currency=EUR"
        And wait for an "Reject" message with "Text=Invalid Side" within 10 seconds
    And stop client
