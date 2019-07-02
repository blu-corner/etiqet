Feature: Jms Testing

    Scenario: Connection Test
        Given a "jms" client as "jms_client"
        And client "jms_client" is started
        Then wait for 2 seconds
        And send an "TestMessage" "jms" message with "Test=Value" using "jms_client" with session id "testTopic"
