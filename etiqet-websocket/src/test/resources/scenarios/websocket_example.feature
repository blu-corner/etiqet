#Feature: WebSocket Etiqet test
#
#    Scenario: Websocket test with a heartbeat response
#        Given a "websocket" client as "exchClient"
#        When client is started
#        Then wait for "exchClient" to receive a websocket message "ClientHeartbeatMessage" as "heartbeatMsg"
#        And check "heartbeatMsg" for "MessageType=R"