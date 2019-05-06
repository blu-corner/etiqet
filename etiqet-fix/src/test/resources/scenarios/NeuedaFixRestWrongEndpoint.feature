Feature: NeuedaFixRest wrong Endpoint Test

   # wrong endpoint
    Scenario: Can't purge with wrong endpoint
        Given a "fix-wrongfield" client
        And filter out "Logon" message
        And Neueda extensions enabled
        Given a failure is expected
        And fail to purge a "BME" order book
        And check if failure had occurred
        And stop client
