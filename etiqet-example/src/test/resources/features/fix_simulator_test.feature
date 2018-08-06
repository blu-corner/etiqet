Feature: FIX Simulator test

  Scenario: Check that a fix message value is greater than another fix message value
    Given a "fix" client
    When client is logged on
    And "BME" order book is purged
    And filter out "Logon" message
    Then send a "NewOrderSingle" message with "OrderID=1,ClOrdID=99,AccountType=3,ReceivedDeptID=EQ,Symbol=NICK,Side=2,OrderQty=100" as "order"
    Then wait for an "ExecutionReport" message as "ackSell"
    Then wait for an "ExecutionReport" message as "ackAcceptSell"
    Then send a "NewOrderSingle" message with "OrderID=2,ClOrdID=98,AccountType=3,ReceivedDeptID=EQ,Symbol=NICK,Side=1,OrderQty=50" as "order"
    Then wait for an "ExecutionReport" message as "ackBuy"
    Then wait for an "ExecutionReport" message as "ackAcceptBuy"
    Then wait for an "ExecutionReport" message as "FillBuy"
    Then wait for an "ExecutionReport" message as "PartialFillSell"
    Then check that "OrderQty" in "ackSell" is equal to "OrderQty" in "PartialFillSell"
    Then check that "OrderQty" in "ackSell" is greater than "OrderQty" in "ackBuy"
    Then check that "OrderQty" in "ackBuy" is less than "OrderQty" in "ackSell"
    Then check that "OrderQty" in "ackBuy" is greater than "5"
    Then check that "OrderQty" in "ackBuy" is less than "500"
    Then check that "OrderQty" in "FillBuy" is equal to "50"
    Then check that "SendingTime" in "PartialFillSell" is greater than "SendingTime" in "ackSell"
    Then check that "OrderQty,Symbol,Side,AccountType" match in "ackBuy,ackAcceptBuy,FillBuy"
    Then check that "29" in "ackBuy" is not set
    Then check that "LastCapacity" in "ackSell" is equal to ""
    Then stop client

  Scenario: Check the time for messages are in the correct order
    Given a "fix" client
    And filter out "Logon" message
    When client is logged on
    Then send a "NewOrderSingle" message with "AccountType=3,ReceivedDeptID=EQ" as "order"
    And wait for an "ExecutionReport" message as "newExecReport"
    And check "newExecReport" contains "SendingTime"
    Then send a "OrderCancelReplaceRequest" message with "AccountType=1,ReceivedDeptID=FX,OrigClOrdID=order->ClOrdID"
    And wait for an "ExecutionReport" message as "replaceExecReport"
    And check "replaceExecReport" contains "SendingTime"
    And check "replaceExecReport" for "ExecType=0,OrdType=2"
    And check that "SendingTime" in "replaceExecReport" is greater than "SendingTime" in "newExecReport"
    Then stop client

  Scenario: Logon Example
    Given a "fix" client
    And filter out "Logon" message
    When client is logged on
    Then send a "Heartbeat" message
    And stop client

  Scenario: System and Admin Messages - Resend request - single
    Given a "fix" client
    When client is logged on
      And filter out "Logon" message
    And "BME" order book is purged
    Then send a "NewOrderSingle" message with "AccountType=3,ReceivedDeptID=EQ" as "order"
      And wait for an "ExecutionReport" message as "newExecReport"
      And check "newExecReport" contains "SendingTime"
    Then send a "OrderCancelReplaceRequest" message with "AccountType=1,ReceivedDeptID=FX,OrigClOrdID=order->ClOrdID"
      And wait for an "ExecutionReport" message as "replaceExecReport"
      And check "replaceExecReport" contains "SendingTime"
      And check "replaceExecReport" for "ExecType=0,OrdType=2"
    Then stop client

Scenario: Test Execution Report is accepted with Side set to 2
 Given a "fix" client
  When client is logged on
    And filter out "Logon" message
    Then send a "NewOrderSingle" message with "ClOrdID=2222,ReceivedDeptID=9,AccountType=1,Account=35944156,HandlInst=1,Symbol=EURUSD,Side=2,TransactTime=20180115-13:30:00.000,OrderQty=1,OrdType=2,TimeInForce=4,Currency=EUR"
      And wait for an "ExecutionReport" message with "ExecType=A,OrdStatus=A" within 10 seconds
      And wait for an "ExecutionReport" message with "ExecType=0,OrdStatus=0" within 10 seconds
      And wait for an "ExecutionReport" message with "ExecType=C,OrdStatus=8" within 10 seconds
  And stop client

  Scenario: Test Execution Report is accepted with Correct Side, ExecType and OrdStatus
    Given a "fix" client
    When client is logged on
    And filter out "Logon" message
      Then send a "NewOrderSingle" message with "ClOrdID=2222,ReceivedDeptID=9,AccountType=1,Account=35944156,HandlInst=1,Symbol=EURUSD,Side=1,TransactTime=20180115-13:30:00.000,OrderQty=1,OrdType=2,TimeInForce=4,Currency=EUR"
        And wait for an "ExecutionReport" message with "ExecType=A,OrdStatus=A" within 10 seconds
        And wait for an "ExecutionReport" message with "ExecType=0,OrdStatus=0" within 10 seconds
        And wait for an "ExecutionReport" message with "ExecType=C,OrdStatus=8" within 10 seconds
    And stop client

  Scenario: Test Reject message is accepted when correctly formed
    Given a "fix" client
      And filter out "Logon" message
    When client is logged on
      Then send a "NewOrderSingle" message with "ReceivedDeptID=9,AccountType=1,Account=35944156,HandlInst=1,Symbol=EURUSD,Side=Z,TransactTime=20180115-13:30:00.000,OrderQty=1,OrdType=2,TimeInForce=4,Currency=EUR"
        And wait for an "Reject" message with "MsgType=3" within 10 seconds
    And stop client

  Scenario: Test Execution Report is has the correct time precision
    Given a "fix" client
    And filter out "Logon" message
    When client is logged on
    Then send a "NewOrderSingle" message with "ClOrdID=2222,ReceivedDeptID=9,AccountType=1,Account=35944156,HandlInst=1,Symbol=EURUSD,Side=1,TransactTime=20180115-13:30:00.000,OrderQty=1,OrdType=2,TimeInForce=4,Currency=EUR"
    And wait for an "ExecutionReport" message as "mymessage"
    And check that "TransactTime" in "mymessage" has "3" precision
    And stop client


    #wrong fields in returned message
  Scenario: Logon Msg failed: Required field missing: MaxMessageSize
    Given a failure is expected
    Given a "fix-wrongfield" client
    When you attempt to wait for a "Logon" message with "MsgType=A,SenderCompID=BME" within 10 seconds
    Then check if failure had occurred
    And stop client

  Scenario: Logout Msg failed: Required field missing: Text
    Given a failure is expected
    Given a "fix-wrongfield" client
    When you attempt to wait for a "Logon" message with "MsgType=A,SenderCompID=BME" within 10 seconds
    Then check if failure had occurred
    Given a failure is expected
    And stop client
    When you attempt to wait for a "Logout" message with "MsgType=5" within 10 seconds
    Then check if failure had occurred

  Scenario: ExecutionReport Msg failed: Required field missing: SecondaryOrderID
    Given a failure is expected
    Given a "fix-wrongfield" client
    And filter out "Logon" message
    When client is logged on
    Then send a "NewOrderSingle" message with "ClOrdID=2222,ReceivedDeptID=9,AccountType=1,Account=35944156,HandlInst=1,Symbol=EURUSD,Side=2,TransactTime=20180115-13:30:00.000,OrderQty=1,OrdType=2,TimeInForce=4,Currency=EUR"
    When you attempt to wait for an "ExecutionReport" message with "ExecType=A,OrdStatus=A" within 10 seconds
    Then check if failure had occurred
    And stop client

  Scenario: Reject Msg failed: Required field missing: RefTagID
    Given a failure is expected
    Given a "fix-wrongfield" client
    And filter out "Logon" message
    When client is logged on
    Then send a "NewOrderSingle" message with "ReceivedDeptID=9,AccountType=1,Account=35944156,HandlInst=1,Symbol=EURUSD,Side=2,TransactTime=20180115-13:30:00.000,OrderQty=1,OrdType=2,TimeInForce=4,Currency=EUR"
    When you attempt to wait for a "Reject" message with "MsgType=3,ClOrdId=2222" within 10 seconds
    Then check if failure had occurred
    And stop client

    #wrong value in messages returned from gw
  Scenario: Logon fails with HeartBtInt contains illegal value: 30
    Given a failure is expected
    Given a "fix-wrongvalue" client
    When you attempt to wait for a "Logon" message with "MsgType=A,SenderCompID=BME" within 10 seconds
    Then check if failure had occurred
    And stop client

  Scenario: ExecutionReport fails Field: Side contains illegal value: 2
    Given a failure is expected
    Given a "fix-wrongvalue" client
    And filter out "Logon" message
    When client is logged on
    Then send a "NewOrderSingle" message with "ClOrdID=2222,ReceivedDeptID=9,AccountType=1,Account=35944156,HandlInst=1,Symbol=EURUSD,Side=2,TransactTime=20180115-13:30:00.000,OrderQty=1,OrdType=2,TimeInForce=4,Currency=EUR"
    When you attempt to wait for an "ExecutionReport" message with "ExecType=A,OrdStatus=A" within 10 seconds
    Then check if failure had occurred
    And stop client

  Scenario: Reject fails with Field: SessionRejectReason contains illegal value: 1
    Given a failure is expected
    Given a "fix-wrongvalue" client
    And filter out "Logon" message
    When client is logged on
    Then send a "NewOrderSingle" message with "ReceivedDeptID=9,AccountType=1,Account=35944156,HandlInst=1,Symbol=EURUSD,Side=2,TransactTime=20180115-13:30:00.000,OrderQty=1,OrdType=2,TimeInForce=4,Currency=EUR"
    When you attempt to wait for a "Reject" message with "MsgType=3,ClOrdId=2222" within 10 seconds
    Then check if failure had occurred
    And stop client

    #rest ok
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
    And "BME" order book is purged
    And stop client

  Scenario: Can input order on preopening phase AND change to opening auction
    Given a "fix" client
    And filter out "Logon" message
    And Neueda extensions enabled
    And "BME" order book is purged
    And "BME" phase is "preopening"
    When client is logged on
    Then send a "NewOrderSingle" message with "ReceivedDeptID=9,ClOrdID=2222,AccountType=1,Account=35944156,HandlInst=1,Symbol=EURUSD,Side=2,TransactTime=20180115-13:30:00.000,OrderQty=1,OrdType=2,TimeInForce=4,Price=100,Currency=EUR"
    Then change "BME" trading phase to "opening auction"
    Then wait for a "Heartbeat" message within 32 seconds
    And stop client

    # rest wrong not enabled
  Scenario: Can't proceed when neueda extensions are not enabled
    Given a "fix-wrongvalue" client
    And filter out "Logon" message
    Given a failure is expected
    And fail to assert Neueda extensions enabled
    And check if failure had occurred
    And stop client

  Scenario: Can't alter phase when matchingengine not provided
    Given a "fix" client
    And filter out "Logon" message
    Given a failure is expected
    Then attempt to change trading phase to "opening-auction"
    And check if failure had occurred
    And stop client

  Scenario: Can't alter phase when phase not provided
    Given a "fix" client
    And filter out "Logon" message
    Given a failure is expected
    Then attempt to change "BME" trading phase
    And check if failure had occurred
    And stop client

   # wrong endpoint
  Scenario: Can't purge with wrong endpoint
    Given a "fix-wrongfield" client
    And filter out "Logon" message
    And Neueda extensions enabled
    Given a failure is expected
    And fail to purge a "BME" order book
    And check if failure had occurred
    And stop client

