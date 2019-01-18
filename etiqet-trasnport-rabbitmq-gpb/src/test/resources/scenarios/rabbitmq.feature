Feature: Etiqet - Google Protocol Buffer & Rabbit MQ test

  Scenario: RabbitMQ logon
    Given a "GenericClient" client
    When client is started
    Then send a "NewOrderSingle" message with params "a=1;b=2;c=3"