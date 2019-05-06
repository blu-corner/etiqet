Feature: NeuedaFixRest wrong Endpoint Test

     # rest wrong not enabled
    Scenario: Can't proceed when neueda extensions are not enabled
        Given a "fix-wrongvalue" client
        And filter out "Logon" message
        Given a failure is expected
        And fail to assert Neueda extensions enabled
        And check if failure had occurred
        And stop client

    Scenario: Can't alter phase when matchingengine not provided
        Given a "fix" client
        And filter out "Logon" message
        Given a failure is expected
        Then attempt to change trading phase to "opening-auction"
        And check if failure had occurred
        And stop client

    Scenario: Can't alter phase when phase not provided
        Given a "fix" client
        And filter out "Logon" message
        Given a failure is expected
        Then attempt to change "BME" trading phase
        And check if failure had occurred
        And stop client
