# Library

- [Server Steps](#server-steps)
    - [Given a server](#given-a-server)
    - [Given a started server](#given-a-started-server)
- [Client Steps](#client-steps)
    - [Given a client](#given-a-client)
    - [Given an initialised client](#given-an-initialised-client)
    - [When client is started](#when-client-is-started)
    - [Then failover](#then-failover)

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

## Then failover

```gherkin
Then failover
Then failover client "<clientName>"
```

Fails over to the secondary configuration defined for the client in useimport cucumber.api.java.en.Given;
<!--
    Undocumented

	@Given("^a server type \"([^\"]*)\"")
	@Given("^server \"([^\"]*)\" is started")
	@When("^client is logged on$")
	@When("^client \"([^\"]*)\" is logged on$")
	@Then("^send message$")
	@Then("^send message using (.*)$")
	@Then("^send message \"([^\"]*)\" using client \"([^\"]*)\"$")
	@Then("^send message \"([^\"]*)\"$")
	@Then("^send an? \"([^\"]*)\" message$")
	@Then("^send an? \"([^\"]*)\" message using \"([^\"]*)\"$")
	@Then("^send an? \"([^\"]*)\" message with \"([^\"]*)\"$")
	@Then("^send an? \"([^\"]*)\" message with \"([^\"]*)\" as \"([^\"]*)\"$")
	@Then("^send an? \"([^\"]*)\" message with session id \"([^\"]*)\"$")
	@Then("^send an? \"([^\"]*)\" message with \"([^\"]*)\" using \"([^\"]*)\"$")
	@Then("^send an? \"([^\"]*)\" message using \"([^\"]*)\" with session id \"([^\"]*)\"$")
	@Then("^send an? \"([^\"]*)\" message with \"([^\"]*)\" with session id \"([^\"]*)\"$")
	@Then("^send an? \"([^\"]*)\" message with \"([^\"]*)\" with session id \"([^\"]*)\" as \"([^\"]*)\"$")
	@Then("^send an? \"([^\"]*)\" message with \"([^\"]*)\" using \"([^\"]*)\" with session id \"([^\"]*)\"$")
	@Then("^send an? \"([^\"]*)\" message with \"([^\"]*)\" using \"([^\"]*)\" with session id \"([^\"]*)\" as \"([^\"]*)\"$")
	@Then("^send an? \"([^\"]*)\" \"([^\"]*)\" message$")
	@Then("^send an? \"([^\"]*)\" \"([^\"]*)\" message with \"([^\"]*)\"$")
	@Then("^send an? \"([^\"]*)\" \"([^\"]*)\" message with \"([^\"]*)\" using \"([^\"]*)\"$")
	@Then("^send an? \"([^\"]*)\" \"([^\"]*)\" message with \"([^\"]*)\" using \"([^\"]*)\" with session id \"([^\"]*)\"$")
	@Then("^send an? \"([^\"]*)\" \"([^\"]*)\" message with \"([^\"]*)\" using \"([^\"]*)\" with session id \"([^\"]*)\" as \"([^\"]*)\"$")
	@Then("^send (\\d+) \"([^\"]*)\" messages as \"([^\"]*)\" client$")
	@Then("^send (\\d+) \"([^\"]*)\" messages with \"([^\"]*)\" as \"([^\"]*)\" client$")
	@Then("^send (\\d+) \"([^\"]*)\" messages$")
	@Then("^send (\\d+) \"([^\"]*)\" messages with \"([^\"]*)\"$")
    @Then("^wait for an? \"([^\"]*)\"$")
    @Then("^wait for an? \"([^\"]*)\" message as \"([^\"]*)\"$")
    @Then("^wait for \"([^\"]*)\" to receive an? \"([^\"]*)\"$")
    @Then("^wait for an? \"([^\"]*)\" message$")
    @Then("^wait for an? \"([^\"]*)\" message within (\\d+) seconds$")
    @Then("^wait for an? \"([^\"]*)\" message within (\\d+) milliseconds$")
    @Then("^wait for \"([^\"]*)\" to receive an? \"([^\"]*)\\\" as \"([^\"]*)\"$")
    @Then("^check for \"([^\"]*)\"$")
	@Then("^check \"([^\"]*)\" for \"([^\"]*)\"$")
	@Then("^check contains \"([^\"]*)\"$")
	@Then("^check \"([^\"]*)\" contains \"([^\"]*)\"$")
	@Then("^check \"([^\"]*)\" does not contain \"([^\"]*)\"$")
	@Then("^get response \"([^\"]*)\" to \"([^\"]*)\" from \"(.*)\" by \"([^\"]*)\"$")
	@Then("^check that \"([^\"]*)\" match in \"([^\"]*)\"$")
	@Then("^check if match in \"([^\"]*)\"$")
	@Then("^consume the response message$")
	@Then("^consume the response message \"([^\"]*)\"$")
	@Then("^try")
	@Then("^check errors \"([^\"]*)\"")
	@Then("^stop client$")
	@Then("^stop client \"([^\"]*)\"$")
	@Then("^stop server \"([^\"]*)\"$")
	@Then("^wait for an? \"([^\"]*)\" message with \"([^\"]*)\"$")
	@Then("^wait for an? \"([^\"]*)\" message with \"([^\"]*)\" within (\\d+) milliseconds?$")
	@Then("^wait for an? \"([^\"]*)\" message with \"([^\"]*)\" within (\\d+) seconds?$")
	@Then("^filter out \"([^\"]*)\" message$")
	@Then("^remove filter for \"([^\"]*)\" message$")
    @Then("^set actions \"([^\"]*)\"$")
    @Then("^set actions \"([^\"]*)\" for client \"([^\"]*)\"$")
    @Then("^reset actions$")
    @Then("^reset actions for client \"([^\"]*)\"$")
    @Given("^a failure is expected$")
    @When("^you attempt something that causes a failure$")
	@When("^you attempt to wait for an? \"([^\"]*)\" message with \"([^\"]*)\" within (\\d+) seconds$")
    @When("^you attempt something that causes a \"([^\"]*)\"$")
    @Then("^check if failure had occurred$")
    @Given("^variable \"([^\"]*)\" set to current timestamp$")
    @Given("^variable \"([^\"]*)\" set to \"([^\"]*)\"$")
    @Then("^check that \"([^\"]*)\" in \"([^\"]*)\" is greater than variable \"([^\"]*)\"$")
    @Then("^check that \"([^\"]*)\" in \"([^\"]*)\" is less than variable \"([^\"]*)\"$")
    @Then("^check that \"([^\"]*)\" in \"([^\"]*)\" is equal to variable \"([^\"]*)\"$")
    @Then("^check that \"([^\"]*)\" in \"([^\"]*)\" is greater than \"([^\"]*)\"$")
    @Then("^check that \"([^\"]*)\" in \"([^\"]*)\" is less than \"([^\"]*)\"$")
    @Then("^check that \"([^\"]*)\" in \"([^\"]*)\" is equal to \"([^\"]*)\"$")
	@Then("^check that \"([^\"]*)\" in \"([^\"]*)\" is not set$")
    @Then("^check that the response field \"([^\"]*)\" is greater than variable \"([^\"]*)\"$")
    @Then("^check that the response field \"([^\"]*)\" is less than variable \"([^\"]*)\"$")
    @Then("^check that the response field \"([^\"]*)\" is equal to variable \"([^\"]*)\"$")
    @Then("^check that the response field \"([^\"]*)\" is greater than \"([^\"]*)\"$")
    @Then("^check that the response field \"([^\"]*)\" is less than \"([^\"]*)\"$")
    @Then("^check that the response field \"([^\"]*)\" is equal to \"([^\"]*)\"$")
    @Then("^check that \"([^\"]*)\" in \"([^\"]*)\" matches time format \"([^\"]*)\"$")
	@Then("^check that \"([^\"]*)\" in \"([^\"]*)\" is equal to \"([^\"]*)\" in \"([^\"]*)\"$")
	@Then("^check that \"([^\"]*)\" in \"([^\"]*)\" is not equal to \"([^\"]*)\" in \"([^\"]*)\"$")
	@Then("^check that \"([^\"]*)\" in \"([^\"]*)\" is greater than \"([^\"]*)\" in \"([^\"]*)\"$")
    @Then("^check that \"([^\"]*)\" in \"([^\"]*)\" is greater than \"([^\"]*)\" in \"([^\"]*)\" by no more than \"([^\"]*)\" milliseconds$")
    @Then("^check that \"([^\"]*)\" in \"([^\"]*)\" is greater than \"([^\"]*)\" in \"([^\"]*)\" by no more than \"([^\"]*)\" seconds$")
    @Then("^check that \"([^\"]*)\" in \"([^\"]*)\" is less than \"([^\"]*)\" in \"([^\"]*)\"$")
    @Then("^check that \"([^\"]*)\" in \"([^\"]*)\" is less than \"([^\"]*)\" in \"([^\"]*)\" by no more than \"([^\"]*)\" milliseconds$")
    @Then("^check that \"([^\"]*)\" in \"([^\"]*)\" is less than \"([^\"]*)\" in \"([^\"]*)\" by no more than \"([^\"]*)\" seconds$")
	@Then("^\"([^\"]*)\" change trading phase to \"([^\"]*)\" for  \"([^\"]*)\"$")
	@Then("^change \"([^\"]*)\" trading phase to \"([^\"]*)\"$")
	@Then("^attempt to change \"([^\"]*)\" trading phase$")
	@Then("^attempt to change trading phase to \"([^\"]*)\"$")
	@Then("^check that \"([^\"]*)\" in \"([^\"]*)\" has precision of \"([^\"]*)\"$")
	@Then("^check that \"([^\"]*)\" in \"([^\"]*)\" has \"([^\"]*)\" precision$")
	@Then("^check bit flags of \"([^\"]*)\" on field \"([^\"]*)\" are (true|false) at indexes \"([^\"]*)\"$")
	@Then("check that ?\"?([^\"]*)?\"? has \"(\\S+)\"$")
	@Then("check that ?\"?([^\"]*)?\"? has \"(\\S+)\" split by \"(\\S+)\" index \"(\\d+)\"$")
-->