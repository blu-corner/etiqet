Feature: Etiqet - Google Protocol Buffer & AMQP test

  Scenario: Amqp logon
    Given a "amqp" client
    And client "amqp" is started
    Then wait for 2 seconds
    Then send a "TestMessage" message with session id "exchange"
