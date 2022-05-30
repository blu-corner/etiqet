# Library

- [Server Steps](#server-steps)
    - [Given a server](#given-a-server)
    - [Given a started server](#given-a-started-server)
    - [Given server is started](#given-server-is-started)
    - [Then stop server](#then-stop-server)
- [Client Steps](#client-steps)
    - [Given a client](#given-a-client)
    - [Given an initialised client](#given-an-initialised-client)
    - [When client is started](#when-client-is-started)
    - [When client is logged on](#when-client-is-logged-on)
    - [Then failover](#then-failover)
    - [Then stop client](#then-stop-client)
- [Message Steps](#message-steps)
    - [Then send message](#then-send-message)
    - [Then send message with parameters](#then-send-message-with-parameters)
    - [Then send a protocol message](#then-send-a-protocol-message)
    - [Then send multiple messages](#then-send-multiple-messages)
- [Receiving Messages](#receiving-messages)
    - [Then wait for a message](#then-wait-for-a-message)
    - [Then check message](#then-check-message)
    - [Then filter messages](#then-filter-messages)
    - [Check message values](#check-message-values)
    - [Trading Phases](#trading-phases)
    - [Time Precision](#time-precision)
- [Miscellaneous Steps](#miscellaneous-steps)

Etiqet allows for the creation and use of named clients as well as the use of
default clients. To create a named client, follow this example:

```gherkin
Given a "fix" client as "clientOne"
When client "clientOne" is logged on
```

By naming clients it allows for more complex test cases to be developed. For
example, a default "fix" client could be initialised along with another named
"fix" client, called "fixTwo" for instance. Doing this allows for multiple 
messages to be sent at once. Using FIX, a Buy Order could be sent from one
client and a Sell Order from the other. These orders could then cross and 
separate Execution Reports sent back to the respective clients. The following
displays sending a message with a named client "fixTwo"

```gherkin
Then send message using "fixTwo"
```

It is also possible to name a sent message and also a received message. This
allows for access to the fields within the message using the Etiqet ->
notation. Here is an example of naming a sent message.

```gherkin
Then send a "QuoteRequest" message with "QuoteReqID=ABC-123#L0" as "qr"
Then send a "QuoteResponse" message with "QuoteRespID=1,QuoteRespType=1,QuoteReqID=qr->QuoteReqID,Symbol=EURUSD"
```

# Server Steps

## Given a server

```gherkin
Given a server type “<serverType>”
Given a server type “<serverType>” with configuration “<serverConfig>”
Given a “<serverType>” as “<serverName>” with configuration “<serverConfig>”
```

Creates a server of specified type to receive messages. Server can be given an
optional name, and also an optional configuration file. User can add message
filters prior to starting the server.

## Given a started server

```gherkin
Given a started server type “<serverType>”
Given a started server type “<serverType>” with configuration “<serverConfig>”
Given a started “<serverType>” as “<serverName>” with configuration “<serverConfig>”
```

Creates and starts a server. In this instance there is no option to add message
filters for example.

## Given server is started

```gherkin
Given server "<serverName>" is started
```

Starts a named server.

## Then stop server

```gherkin
Then stop server "<serverName>"
```

Stops a named server.

# Client Steps

## Given a client

```gherkin
Given a "<implementation>" client
Given a "<implementation>" client as "<clientName>"
Given a “<implementation>” client with configuration file “<configFile>”
Given a “<implementation>” client “<clientName>” with config “<configFile>”
Given a “<implementation>” client with primary config “<primaryConfig>” and secondary config “<secondaryConfig>”
Given a “<implementation>” client “<clientName>” with primary config “<primaryConfig>” and secondary config “<secondaryConfig>”
```

Starts a new client of specified type <*implementation*> with/without a name.
Supports an optional config file. Additionally a client can be created with
primary and secondary config file, should the user wish to fail the client
over with a different configuration.

## Given an initialised client

```gherkin
Given an initialised “<implementation>” client
Given an initialised “<implementation>” client as “<clientName>”
```

Retrieves an already initialised client.

## When client is started

```gherkin
When client is started
When client “<clientName>” is started
```

Starts a default or named client. 

## When client is logged on

```gherkin
When client is logged on
When client "<clientName>" is logged on
```

Waits for the client or named client to be logged on.

## Then failover

```gherkin
Then failover
Then failover client "<clientName>"
```

Fails over to the secondary configuration defined for the client in useimport cucumber.api.java.en.Given;

## Then stop client

```gherkin
Then stop client
Then stop client "<clientName>"
```

Stops either the default client or a named client.

# Message Steps

## Then send message

```gherkin
Then send message
Then send message using "<clientName>"
Then send message "<msgName>" using client "<clientName>"
Then send message "<msgName>"
```

Sends a default message using the default client, unless a message name or client name is provided.

```gherkin
Then send an "<msgType>" message
Then send an "<msgType>" message using "<clientName>"
Then send an "<msgType>" message with session id "<sessionId>"
Then send an “<msgType>” message using “<clientName>” with session id “<sessionId>”
```
Sends a message of specified type <*msgType*>. 
Messages can be sent from named clients, session Id can be specified.


## Then send message with parameters

```gherkin
Then send an "<msgType>" message with "<params>"
Then send an "<msgType>" message with "<params>" as "<alias>"
Then send an "<msgType>" message with "<params>" using "<clientName>"
Then send an “<msgType>” message with “<params>” with session id “<sessionId>”
Then send an "<msgType>" message with "<params>" with session id "<sessionId>" as "<alias>"
Then send an "<msgTpye>" message with "<params>" using "<clientName>" with session id "<sessionId>"
Then send an "<msgTpye>" message with "<params>" using "<clientName>" with session id "<sessionId>" as "<alias>"
```
Sends a message of specified type <*msgType*> with parameters. An alias for the message being sent can be added.
Messages can be sent from named clients and a session ID can be specified.

## Then send a protocol message

```gherkin
Then send an “<msgType>” “<protocol>” message
Then send an “<msgType>” “<protocol>” message with “<params>”
Then send an “<msgType>” “<protocolName>” message with “<params>” using “<clientName>” 
Then send an “<msgType>” “<protocolName>” message with “<params>” using “<clientName>” with session id “<sessionId>”
Then send an “<msgType>” “<protocolName>” message with “<params>” using “<clientName>” with session id “<sessionId>” as “<alias>”
```
Sends a message of specified type <*msgType*> using a protocol defined in the etiqet.config.xml file.
Messages can be sent from named clients, parameters can be added and a session Id can be specified.

## Then send multiple messages

```gherkin
Then send "<int>" "<msgType>" messages
Then send "<int>" "<msgType>" messages as "<clientName>" client
Then send "<int>" "<msgType>" messages with "<params>"
Then send "<int>" "<msgType>" messages with "<params>" as "<clientName>" client
```
Sends multiple messages of the same type, specified be <*msgType*>.
Messages can be sent from named clients and parameters can be added.

# Receiving Messages

## Then wait for a message

```gherkin
Then wait for an "<msgType>"
Then wait for an "<msgType>" message as "<responseName>"
Then wait for "<clientName>" to receive an "<msgType>"
Then wait for an "<msgType>" message
Then wait for an "<msgType>" message with "<int>" seconds
Then wait for an "<msgType>" message with "<int>" milliseconds
Then wait for "<clientName>" to receive an "<msgType>" as "<responseName>"
```
Waits for a message of a specified type to be received, 
if the specified message type is not received the test step will fail.
Client names can be used to wait for a named client to received a message, 
these messages can also be given an alias.

```gherkin
Then wait for an "<messageType>" message with "<params>"
Then wait for an "<messageType>" message within "<int>" milliseconds
Then wait for an "<messageType>" message within "<int>" seconds
```
Waits for a specified message type to be received with a specific set of parameters.
Allows users to check if messages are received within a certain amount of seconds/milliseconds.


## Then check message

```gherkin
Then check for "<params>"
Then check "<msgName>" for "<params>"
Then check contains "<params>"
Then check "<msgName>" contains "<params>"
Then check "<msgName>" does not contain "<params>"
```

Checks received messages contain or do not contain a list of parameters.
Without specifying a message name the last message received is checked.

```gherkin
Then get response "<responseName>" to "<messageName>" from "<responseList>" by "<fieldName>"
Then check that "<paramList>" match in "<messageList>"
Then check if match in "<paramList>"
```
Finds a response to a message from a list of responses, by field name. 
Allows a user to check a list of messages for a list of parameters.

```gherkin
Then check that "<responseName>" has "<responseParams>"
```
Checks that a message has specific values.

```gherkin
Then consume the response message
Then consume the response message "<responseName>"
```
Consumes either the default response message or a named response message.

## Then filter messages

```gherkin
Then filter out "<msgType>" message
Then remove filter for "<msgType>" message
```
Sets or removes filters for specified message types.

## Check message values

```gherkin
Then check that "<field>" in "<messageAlias>" is greater than variable "<variable>"
Then check that "<field>" in "<messageAlias>" is less than variable "<variable>"
Then check that "<field>" in "<messageAlias>" is equal to variable "<variable>"
Then check that "<field>" in "<messageAlias>" is greater than "<value>"
Then check that "<field>" in "<messageAlias>" is less than "<value>"
Then check that "<field>" in "<messageAlias>" is equal to "<value>"
Then check that "<field>" in "<messageAlias>" is not set
```
Compares fields in messages with a variable's value, 
any specific value or to check that the field has not been set at all.

```gherkin
Then check that the response field "<field>" in "<messageAlias>" is greater than variable "<variable>"
Then check that the response field "<field>" in "<messageAlias>" is less than variable "<variable>"
Then check that the response field "<field>" in "<messageAlias>" is equal to variable "<variable>"
Then check that the response field "<field>" in "<messageAlias>" is greater than "<value>"
Then check that the response field "<field>" in "<messageAlias>" is less than "<value>"
Then check that the response field "<field>" in "<messageAlias>" is equal to "<value>"
```
Compares fields in response messages with a variable's value or any specific value.

```gherkin
Then check that "<firstField>" in "<firstMessageAlias>" is equal to "<secondField>" in "<secondMessageAlias>"
Then check that "<firstField>" in "<firstMessageAlias>" is not equal to "<secondField>" in "<secondMessageAlias>"
Then check that "<firstField>" in "<firstMessageAlias>" is greater than "<secondField>" in "<secondMessageAlias>"
Then check that "<firstField>" in "<firstMessageAlias>" is greater than "<secondField>" in "<secondMessageAlias>" by no more than "<millis>" milliseconds
Then check that "<firstField>" in "<firstMessageAlias>" is greater than "<secondField>" in "<secondMessageAlias>" by no more than "<seconds>" seconds
Then check that "<firstField>" in "<firstMessageAlias>" is less than "<secondField>" in "<secondMessageAlias>"
Then check that "<firstField>" in "<firstMessageAlias>" is less than "<secondField>" in "<secondMessageAlias>" by no more than "<millis>" milliseconds
Then check that "<firstField>" in "<firstMessageAlias>" is less than "<secondField>" in "<secondMessageAlias>" by no more than "<seconds>" seconds
```
Comparisons between two values can be performed, these values can be in the same message or two different messages.

```gherkin
Then check that "<field>"  in "<messageAlias>" matches time format "<timestampFormat>"
```
Checks the format of a field matches a particular time stamp format.

## Trading Phases

```gherkin
Then "<exchange>" change trading phase to "<auctionPhase>" for "<clientName>"
Then change "<exchange>" trading phase to "<auctionPhase>"
Then attempt to change "<exchange>" trading phase
Then attempt to change trading phase to "<auctionPhase>"
```
Changes the trading phase for an exchange to a specified trading phase. 
Client name can be used to specify if this change is for all clients or a particular client.

## Time Precision

```gherkin
Then check that "<field>" in "<messageAlias>" has precision of "<precision>"
Then check that "<field>" in "<messageAlias> has "<precisionName>" precision
```
Checks that the values of a specified field are of a certain precision, for example - seconds or milliseconds.

# Miscellaneous Steps

```gherkin
Then check bit flags of "<message>" on field "<field>" are (true|false) at indexes "<indexes>"
```
Checks that the bit flags of a field are true or false at specified indexes.

--
```gherkin
Then set "<varName>"="<value>"
Then set "<varName>"="<messageID->fieldName>"
```
Sets a variable with specified value, to be later used in message compositions. When read, it must be surrounded by "${varName}" For now it only supports being read at message definition steps, e.g.
```gherkin
Then set "referenceOrdID"="12345"
Then send a "<msgType>" messages with "OrigClOrdID=${referenceOrdID}" as "testMsg"
```

It can also read a variable to set another variable, or include chunks of messages
```gherkin
Then set "referenceOrdID"="testMsg->OrigClOrdID"
Then set "redundantID"="${referenceOrdID}"
Then set "commonMsgChunk"="OrigClOrdID=${redundantID},Symbol=EURUSD"
...
Then send a "<msgType>" messages with "ClOrdID=12,${commonMsgChunk},Side=1"
```
--
<!--

THESE STEPS NEED EXPLANATIONS!


```gherkin
Then try
Then check errors "<exceptionList>"
```

```gherkin
Then set actions "<actions>"
Then set actions "<actions>" for client "<clientName>"
Then reset actions
Then reset actions for client "<clientName>"
```

```gherkin
Given a failure is expected
When you attempt something that causes a failure
```

```gherkin
When you attempt to wait for an "<methodName>" message with "<params>" within "<int>" seconds
When you attempt something that causes a "<cukeExpectedException>"
Then check if failure had occurred
Given variable "<alias>" set to current timestamp
Given variable "<alias>" set to "<value>"

Then check that "<responseName>" has "<responseParams>" split by "<split>" index "<index>"
```
-->
