Feature: Jms Testing

    Scenario: Connection Test
        Given a "jms_protobuf" client as "jms_producer"
        Given a "jms_protobuf" client as "jms_consumer"
        And client "jms_producer" is started
        And client "jms_consumer" is started
        Then wait for 2 seconds
        And send an "Person" "jms_protobuf" message with "name=personName,id=20,email=aaa@aaa.aaa" using "jms_producer" with session id "topic"
