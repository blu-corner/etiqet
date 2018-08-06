Feature: Test failure occurs when incoming messages contains the wrong values

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
