Feature: Jms Testing

    Scenario: Connection Test
        Given a "broker" client as "broker_client"
        And client "broker_client" is started
        Then wait for 500 milliseconds
        Then send an "TestMessage" message with "Test=Value" using "broker_client" to exchange "exchange"

    Scenario: Queue consumer
        Given a "broker" client as "broker_client"
        And client "broker_client" is started
        Then wait for 500 milliseconds
        Then send an "TestMessage" message with "Test=Value" using "broker_client" to exchange "exchange"
        Then wait for "broker_client" to receive a message on queue "exchange_queue" within 2 seconds

    Scenario: Client A sends message to the exchange, client B retrieves it from associated queue
        Given a "broker" client as "client_a"
        Given a "broker" client as "client_b"
        And client "client_a" is started
        And client "client_b" is started
        Then wait for 500 milliseconds
        Then send a "TestMessage" message with "Test=Value" using "client_a" to exchange "exchange"
        Then wait for "client_b" to receive a message on queue "exchange_queue" within 2 seconds as "response"
        And check that "Test" in "response" is equal to "Value"
        Then send a "TestMessage" message with "Test=Value2" using "client_b" to exchange "exchange"
        Then wait for "client_a" to receive a message on queue "exchange_queue" within 2 seconds as "response2"
        And check that "Test" in "response2" is equal to "Value2"

    Scenario: Client A sends two messages to the topic; client B consumes them
        Given a "broker" client as "client_a"
        Given a "broker" client as "client_b"
        And client "client_a" is started
        And client "client_b" is started
        Then wait for 500 milliseconds
        And client "client_b" is subscribed to queue "exchange_queue"
        Then send a "TestMessage" message with "field1=a1,field2=a2" using "client_a" to exchange "exchange"
        Then send a "TestMessage" message with "field1=b1,field2=b2" using "client_a" to exchange "exchange"
        Then check that "client_b" has received 2 messages from queue "exchange_queue"
        Then check that last message received by "client_b" from queue "exchange_queue" contains "field1=b1"

    Scenario: Client B consumes messages from queue
        Given a "broker" client as "client_a"
        Given a "broker" client as "client_b"
        And client "client_a" is started
        And client "client_b" is started
        Then wait for 500 milliseconds
        And client "client_b" is subscribed to queue "exchange_queue"
        Then send a "TestMessage" message with "field1=a1,field2=a2" using "client_a" to exchange "exchange"
        Then check that "client_b" has received 1 messages from queue "exchange_queue"
        Then check that last message received by "client_b" from queue "exchange_queue" contains "field1=a1"
        Then send a "TestMessage" message with "field1=b1,field2=b2" using "client_a" to exchange "exchange"
        Then check that "client_b" has received 2 messages from queue "exchange_queue"
        Then check that last message received by "client_b" from queue "exchange_queue" contains "field1=b1,field2=b2"



