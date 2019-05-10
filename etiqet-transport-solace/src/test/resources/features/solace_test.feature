Feature: Solace Testing

    Scenario: Connection Test
        Given a "solace" client as "solace_client"
        And client "solace_client" is started
        And check if client "solace_client" is logged on
