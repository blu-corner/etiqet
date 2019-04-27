Feature: Test failure occurs when incoming messages contains the wrong fields

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
