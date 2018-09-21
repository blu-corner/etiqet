Feature: WebSocket Etiqet test

    Scenario: GET request with 404 response
        Given a "websocket" client as "exchClient"
        When client is started
        Then wait for "exchClient" to receive a websocket message "AcceptedMessage" as "acceptMsg"
        And check "acceptMsg" for "OrderVerb=S"