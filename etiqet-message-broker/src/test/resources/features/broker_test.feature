Feature: Jms Testing

    Scenario: Connection Test
        Given a "broker" client as "broker_client"
        And client "jms_client" is started
        Then wait for 2 seconds
        Then send an "TestMessage" message with "Test=Value" using "broker_client" to topic "topic_broker"

    Scenario: Topic consumer
        Given a "broker" client as "broker_client"
        And client "jms_client" is started
        Then wait for 2 seconds
        Then send an "TestMessage" message with "Test=Value" using "broker_client" to topic "topic_broker"
        Then wait for "broker_client" to receive a message on topic "topic_broker"

