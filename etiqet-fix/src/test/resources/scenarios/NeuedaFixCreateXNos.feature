Feature: Test correctly formed messages are accepted

  Scenario: Check can add x num of NOS messages
    Given a "fix" client
    And filter out "Logon" message
    When client is logged on
    And "BME" order book is purged
      # single client use
    Then send 20 "NewOrderSingle" messages
    Then send 0 "NewOrderSingle" messages
    Then send 10 "NewOrderSingle" messages with "Side=1"
    Then stop client

   Scenario: Check can add x num of NOS messages as named client
    Given a "fix" client as "Buy"
    And filter out "Logon" message
    And client "Buy" is logged on
    # This allows for mulitple client use
    Then send 20 "NewOrderSingle" messages as "Buy" client
    Then send 0 "NewOrderSingle" messages as "Buy" client
    Then send 10 "NewOrderSingle" messages with "Side=1" as "Buy" client
    Then send 10 "NewOrderSingle" messages with "Side=2" as "Buy" client







