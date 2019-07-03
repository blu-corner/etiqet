Feature: Etiqet - Google Protocol Buffer & Rabbit MQ test

  Scenario: RabbitMQ logon
    Given a "rabbit-gpb" client
    And client "rabbit-gbp" is started
    Then wait for 2 seconds
#    Then send a "NewOrderSingle" message with session id "Session.Channel"
    Then send a "TestMessage" message with session id "exchange"
