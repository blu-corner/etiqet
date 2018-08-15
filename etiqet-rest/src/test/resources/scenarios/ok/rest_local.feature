Feature: REST Etiqet test

Scenario: Test local post request
    Given a "rest" client
    When client is started
    Then "POST" a "test_type_03" with payload "val=1111" to "/echo" as "message1"
    And check that "message1" has status code "200"
    Then "POST" a "test_type_04" with payload "order->1->instrument=EUR_USD,AccountType=1,OrigClOrdID=message1->val" to "/echo" as "message2"
    And check that "message2" has status code "200"