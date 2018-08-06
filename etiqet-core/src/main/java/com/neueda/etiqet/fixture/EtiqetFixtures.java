package com.neueda.etiqet.fixture;

import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import cucumber.api.java.After;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import java.io.IOException;

/**
 * Class to define Steps to that implement feature definitions.
 *  
 * @author Neueda
 *
 */
public class EtiqetFixtures {

    private final EtiqetHandlers handlers;

    /**
     * Default constructor.
     */
    public EtiqetFixtures(EtiqetHandlers handlers) {
        this.handlers = handlers;
    }

	@Given("^a server type \"([^\"]*)\"")
	public void createServer(String serverType) {
		createServer(EtiqetHandlers.DEFAULT_SERVER_NAME, serverType, null);
	}

	@Given("^a server type \"([^\"]*)\" with configuration \"([^\"]*)\"")
	public void createServer(String serverType, String serverConfig) {
		createServer(EtiqetHandlers.DEFAULT_SERVER_NAME, serverType, serverConfig);
	}

	@Given("^a server type \"([^\"]*)\" as \"([^\"]*)\" with configuration \"([^\"]*)\"")
	public void createServer(String serverType, String serverName, String serverConfig) {
		handlers.createServer(serverName, serverType, serverConfig);
	}

	@Given("^a started server type \"([^\"]*)\"")
	public void createAndStartServer(String serverType) {
		createServer(EtiqetHandlers.DEFAULT_SERVER_NAME, serverType, null);
	}

	@Given("^a started server type \"([^\"]*)\" with configuration \"([^\"]*)\"")
	public void createAndStartServer(String serverType, String serverConfig) {
		createServer(EtiqetHandlers.DEFAULT_SERVER_NAME, serverType, serverConfig);
	}

	@Given("^a started server type \"([^\"]*)\" as \"([^\"]*)\" with configuration \"([^\"]*)\"")
	public void createAndStartServer(String serverType, String serverName, String serverConfig) {
		handlers.createServer(serverName, serverType, serverConfig);
		handlers.startServer(serverName);
	}

	@Given("^server \"([^\"]*)\" is started")
	public void startServer(String serverName) {
		handlers.startServer(serverName);
	}

	/**
	 * Create a client with the given client type and with default client name.
	 * @param implementation client type.
	 */
	@Given("^an initialised \"([^\"]*)\" client$")
	public void createDefaultClient(String implementation) throws EtiqetException {
		createClientAs(implementation, EtiqetHandlers.DEFAULT_CLIENT_NAME);
	}

	/**
	 * Create a client with the given client type and name.
	 * @param implementation client type.
	 * @param clientName the name of the client.
	 */
	@Given("^an initialised \"([^\"]*)\" client as \"([^\"]*)\"$")
	public void createClientAs(String implementation, String clientName) throws EtiqetException {
		handlers.createClient(implementation, clientName);
	}

	/**
	 * Run the default client.
	 */
	@When("^client is started$")
	public void runDefaultClient() {
		runClientAs(EtiqetHandlers.DEFAULT_CLIENT_NAME);
	}

	/**
	 * Run a client the was previously initialised.
	 * @param clientName the name of the client to be started.
	 */
	@When("^client \"([^\"]*)\" is started$")
	public void runClientAs(String clientName) {
		handlers.startClient(clientName);
	}

	/**
	 * Create and start a client named default.
	 * @param implementation client type.
	 */
	@Given("^a \"([^\"]*)\" client$")
	public void startDefaultClient(String implementation) throws EtiqetException {
		handlers.startClient(implementation, EtiqetHandlers.DEFAULT_CLIENT_NAME);
	}
	
	/**
	 * Method to create and start a client named default.
	 * @param implementation client type.
	 */
	@Given("^a \"([^\"]*)\" client as \"([^\"]*)\"$")
	public void startNamedClient(String implementation, String clientName) throws EtiqetException {
        handlers.startClient(implementation, clientName);
	}

	/**
	 * Method to create and start a client named default.
	 * @param implementation client type.
	 */
	@Given("^a \"([^\"]*)\" client with configuration file \"([^\"]*)\"$")
	public void startDefaultClient(String implementation, String configFile) {
		startAClientWithConfig(implementation, EtiqetHandlers.DEFAULT_CLIENT_NAME, configFile);
	}

	/**
	 * Method to create and start a client named default.
	 * @param implementation client type.
	 */
	@Given("^a \"([^\"]*)\" client \"([^\"]*)\" with config \"([^\"]*)\"$")
	public void startAClientWithConfig(String implementation, String clientName, String configFile) {
		handlers.startClient(implementation, clientName, configFile);
	}

	/**
	 * Starting a client with a primary and secondary config
	 * @param implementation
	 * @param primaryConfig
	 * @param secondaryConfig
	 */
	@Given("^a \"([^\"]*)\" client with primary config \"([^\"]*)\" and secondary config \"([^\"]*)\"$")
	public void startAClientWithSecondaryConfig(String implementation, String primaryConfig, String secondaryConfig) {
		handlers.startClientWithFailover(implementation, EtiqetHandlers.DEFAULT_CLIENT_NAME, primaryConfig, secondaryConfig);
	}

	/**
	 * Starting a named client with a primary and secondary config
	 * @param implementation
	 * @param clientName
	 * @param primaryConfig
	 * @param secondaryConfig
	 */
	@Given("^a \"([^\"]*)\" client \"([^\"]*)\" with primary config \"([^\"]*)\" and secondary config \"([^\"]*)\"$")
	public void startANamedClientWithSecondaryConfig(String implementation, String clientName, String primaryConfig, String secondaryConfig) {
		handlers.startClientWithFailover(implementation, clientName, primaryConfig, secondaryConfig);
	}

	@Then("^failover$")
	public void failover() throws EtiqetException{
		handlers.failover(EtiqetHandlers.DEFAULT_CLIENT_NAME);
	}

	@Then("^failover client \"([^\"]*)\"$")
	public void namedFailover(String clientName) throws EtiqetException {
		handlers.failover(clientName);
	}

	/**
	 * Method to wait for default client log on. 
	 */
	@When("^client is logged on$")
	public void waitForClientLogon() {
        handlers.waitForClientLogon(EtiqetHandlers.DEFAULT_CLIENT_NAME);
	}

	/**
	 * Method to wait for a named client log on.
	 * @param name client name 
	 */
	@When("^client \"([^\"]*)\" is logged on$")
	public void waitForClientLogon(String name) {
        handlers.waitForClientLogon(name);
	}

	/**
	 * Method to check if a client, given by name, is logged on. 
	 * @param clientName name to find the client.
	 */
	@And("^check if client \"([^\"]*)\" is logged on$")
	public void checkIfNamedClientIsLoggedOn(String clientName) {
        handlers.isClientLoggedOn(clientName);
	}

	/**
	 * Method to check if default client is logged on. 
	 */
	@And("^check if client is logged on$")
	public void checkIfDefaultClientIsLoggedOn() {
		handlers.isClientLoggedOn(EtiqetHandlers.DEFAULT_CLIENT_NAME);
	}

	/**
	 * Method to check if default client is logged on.
	 */
	@And("^check if client is logged out$")
	public void checkIfDefaultClientIsLoggedOut() {
		handlers.isClientLoggedOff(EtiqetHandlers.DEFAULT_CLIENT_NAME);
	}

	// Create messages
	@And("^create a \"([^\"]*)\" \"([^\"]*)\" message with \"([^\"]*)\"$")
	public void createMessage(String msgType, String protocol, String params) throws EtiqetException {
        handlers.createMessage(msgType, protocol, EtiqetHandlers.DEFAULT_MESSAGE_NAME, params);
	}

	@And("^create a \"([^\"]*)\" message with \"([^\"]*)\" as \"([^\"]*)\"$")
	public void createMessageWithAs(String msgType, String params, String varName) throws EtiqetException {
		handlers.createMessageForClient(msgType, EtiqetHandlers.DEFAULT_CLIENT_NAME, varName, params);
	}

	@And("^create a \"([^\"]*)\" \"([^\"]*)\" message as \"([^\"]*)\" with \"([^\"]*)\"$")
	public void createMessage(String msgType, String protocol, String messageName, String params)
			throws EtiqetException {
        handlers.createMessage(msgType, protocol, messageName, params);
	}

	@Then("^send message$")
	public void sendDefaultMessageUsingDefaultClient() throws EtiqetException {
        handlers.sendMessage(EtiqetHandlers.DEFAULT_MESSAGE_NAME, EtiqetHandlers.DEFAULT_CLIENT_NAME);
	}

	@Then("^send message using (.*)$")
	public void sendDefaultMessageUsingNamedClient(String clientName) throws EtiqetException {
        handlers.sendMessage(EtiqetHandlers.DEFAULT_MESSAGE_NAME, clientName);
	}

	@Then("^send message \"([^\"]*)\" using client \"([^\"]*)\"$")
	public void sendNamedMessageUsingNamedClient(String msgName, String clientName) throws EtiqetException {
        handlers.sendMessage(msgName, clientName);
	}
	
	@Then("^send message \"([^\"]*)\"$")
	public void sendNamedMessageUsingDefaultClient(String msgName) throws EtiqetException {
        handlers.sendMessage(msgName, EtiqetHandlers.DEFAULT_CLIENT_NAME);
	}

	// Message with default protocol
	@Then("^send an? \"([^\"]*)\" message$")
	public void sendAMsg(String msgType) throws EtiqetException {
		sendAMsgParamsClient(msgType, EtiqetHandlers.DEFAULT_PARAMS, EtiqetHandlers.DEFAULT_CLIENT_NAME);
	}

	@Then("^send an? \"([^\"]*)\" message using \"([^\"]*)\"$")
	public void sendMsgClient(String messageType, String clientName) throws EtiqetException {
		sendAMsgParamsClient(messageType, EtiqetHandlers.DEFAULT_PARAMS, clientName);
	}

	@Then("^send an? \"([^\"]*)\" message with \"([^\"]*)\"$")
	public void sendAMsgParams(String msgType, String params) throws EtiqetException {
		sendAMsgParamsClient(msgType, params, EtiqetHandlers.DEFAULT_CLIENT_NAME);
	}

	@Then("^send an? \"([^\"]*)\" message with \"([^\"]*)\" as \"([^\"]*)\"$")
	public void sendAMsgParamsAs(String msgType, String params, String alias) throws EtiqetException {
		sendAMsgParamClientSessionAs(msgType, params, EtiqetHandlers.DEFAULT_CLIENT_NAME, EtiqetHandlers.DEFAULT_SESSION, alias);
	}

	@Then("^send an? \"([^\"]*)\" message with session id \"([^\"]*)\"$")
	public void sendAMsgSession(String msgType, String sessionId) throws EtiqetException {
		sendAMsgClientSession(msgType, EtiqetHandlers.DEFAULT_CLIENT_NAME, sessionId);
	}

	@Then("^send an? \"([^\"]*)\" message with \"([^\"]*)\" using \"([^\"]*)\"$")
	public void sendAMsgParamsClient(String msgType, String params, String clientName) throws EtiqetException {
		sendAMsgParamClientSession(msgType, params, clientName, EtiqetHandlers.DEFAULT_SESSION);
	}

	@Then("^send an? \"([^\"]*)\" message using \"([^\"]*)\" with session id \"([^\"]*)\"$")
	public void sendAMsgClientSession(String msgType, String clientName, String sessionId) throws EtiqetException {
		sendAMsgParamClientSession(msgType, EtiqetHandlers.DEFAULT_PARAMS, clientName, sessionId);
	}

	@Then("^send an? \"([^\"]*)\" message with \"([^\"]*)\" with session id \"([^\"]*)\"$")
	public void sendAMsgParamSession(String msgType, String params, String sessionId) throws EtiqetException {
		sendAMsgParamClientSession(msgType, params, EtiqetHandlers.DEFAULT_CLIENT_NAME, sessionId);
	}

	@Then("^send an? \"([^\"]*)\" message with \"([^\"]*)\" with session id \"([^\"]*)\" as \"([^\"]*)\"$")
	public void sendAMsgParamSession(String msgType, String params, String sessionId, String alias) throws EtiqetException {
		sendAMsgParamClientSessionAs(msgType, params, EtiqetHandlers.DEFAULT_CLIENT_NAME, sessionId, alias);
	}

	@Then("^send an? \"([^\"]*)\" message with \"([^\"]*)\" using \"([^\"]*)\" with session id \"([^\"]*)\"$")
	public void sendAMsgParamClientSession(String msgType, String params, String clientName, String sessionId) throws EtiqetException {
		sendAMsgParamClientSessionAs(msgType, params, clientName, sessionId, EtiqetHandlers.DEFAULT_MESSAGE_NAME);
	}

	@Then("^send an? \"([^\"]*)\" message with \"([^\"]*)\" using \"([^\"]*)\" with session id \"([^\"]*)\" as \"([^\"]*)\"$")
	public void sendAMsgParamClientSessionAs(String msgType, String params, String clientName, String sessionId, String alias) throws EtiqetException {
		handlers.createMessageForClient(msgType, clientName, alias, params);
		handlers.sendMessage(alias, clientName, sessionId);
	}

	// Messages defining protocol
	@Then("^send an? \"([^\"]*)\" \"([^\"]*)\" message$")
	public void sendAMsgProtocol(String msgType, String protocol) throws EtiqetException {
		sendAMsgProtocolParam(msgType, protocol, EtiqetHandlers.DEFAULT_PARAMS);
	}

	@Then("^send an? \"([^\"]*)\" \"([^\"]*)\" message with \"([^\"]*)\"$")
	public void sendAMsgProtocolParam(String msgType, String protocol, String params) throws EtiqetException {
		sendAMsgProtocolParamClient(msgType, protocol, params, EtiqetHandlers.DEFAULT_CLIENT_NAME);
	}

	@Then("^send an? \"([^\"]*)\" \"([^\"]*)\" message with \"([^\"]*)\" using \"([^\"]*)\"$")
	public void sendAMsgProtocolParamClient(String msgType, String protocolName, String params, String clientName) throws EtiqetException {
		sendAMsgProtocolParamClientSession(msgType, protocolName, params, clientName, EtiqetHandlers.DEFAULT_SESSION);
	}

	@Then("^send an? \"([^\"]*)\" \"([^\"]*)\" message with \"([^\"]*)\" using \"([^\"]*)\" with session id \"([^\"]*)\"$")
	public void sendAMsgProtocolParamClientSession(String msgType, String protocolName, String params, String clientName, String sessionId) throws EtiqetException {
		sendAMsgProtocolParamClientSessionAs(msgType, protocolName, params, clientName, sessionId, EtiqetHandlers.DEFAULT_MESSAGE_NAME);
	}

	@Then("^send an? \"([^\"]*)\" \"([^\"]*)\" message with \"([^\"]*)\" using \"([^\"]*)\" with session id \"([^\"]*)\" as \"([^\"]*)\"$")
	public void sendAMsgProtocolParamClientSessionAs(String msgType, String protocolName, String params, String clientName, String sessionId, String alias) throws EtiqetException {
		handlers.createMessage(msgType, protocolName, alias, params);
		handlers.sendMessage(alias, clientName, sessionId);
	}

	@Then("^send (\\d+) \"([^\"]*)\" messages as \"([^\"]*)\" client$")
	public void sendMultipleMessagesAsClient(int repeat, String msgType, String clientName) throws EtiqetException {
		for(int i=0;i<repeat;i++) {
			sendAMsgParamsClient(msgType, EtiqetHandlers.DEFAULT_PARAMS, clientName);
		}
	}

	@Then("^send (\\d+) \"([^\"]*)\" messages with \"([^\"]*)\" as \"([^\"]*)\" client$")
	public void sendMultipleMessagesWithParamsAsClient(int repeat, String msgType, String params, String clientName) throws EtiqetException {
		for(int i=0;i<repeat;i++) {
			sendAMsgParamsClient(msgType, params, clientName);
		}
	}
	@Then("^send (\\d+) \"([^\"]*)\" messages$")
	public void sendMultipleMessages(int repeat, String msgType) throws EtiqetException {
		for(int i=0;i<repeat;i++) {
			sendAMsgParamsClient(msgType, EtiqetHandlers.DEFAULT_PARAMS, EtiqetHandlers.DEFAULT_CLIENT_NAME);
		}
	}

	@Then("^send (\\d+) \"([^\"]*)\" messages with \"([^\"]*)\"$")
	public void sendMultipleMessagesWithParams(int repeat, String msgType, String params) throws EtiqetException {
		for(int i=0;i<repeat;i++) {
			sendAMsgParamsClient(msgType, params, EtiqetHandlers.DEFAULT_CLIENT_NAME);
		}
	}

	// receive default response, from default client
    @Then("^wait for an? \"([^\"]*)\"$")
    public void defaultClientDefaultResponseOfType(String t) throws EtiqetException {
		defaultClientNamedResponse(t, EtiqetHandlers.DEFAULT_MESSAGE_NAME);
    }

    // receive named response, from default client
    @Then("^wait for an? \"([^\"]*)\" message as \"([^\"]*)\"$")
    public void defaultClientNamedResponse(String t, String responseName) throws EtiqetException {
        if (EtiqetHandlers.RESPONSE.equals(t))
            handlers.waitForResponse(responseName, EtiqetHandlers.DEFAULT_CLIENT_NAME);
        else
            handlers.waitForResponseOfType(responseName, EtiqetHandlers.DEFAULT_CLIENT_NAME, t);
    }

    // receive default response from named client
    @Then("^wait for \"([^\"]*)\" to receive an? \"([^\"]*)\"$")
    public void namedClientDefaultResponse(String clientName, String t) throws EtiqetException {
        if (EtiqetHandlers.RESPONSE.equals(t))
            handlers.waitForResponse(EtiqetHandlers.DEFAULT_MESSAGE_NAME, clientName);
        else
            handlers.waitForResponseOfType(EtiqetHandlers.DEFAULT_MESSAGE_NAME, clientName, t);
    }
  
    // receive named response from named client
    @Then("^wait for an? \"([^\"]*)\" message$")
    public void waitForAMessageDefaultTimeout(String t) throws EtiqetException {
        waitForAMessageDuringMs(t, 5000);
    }

    @Then("^wait for an? \"([^\"]*)\" message within (\\d+) seconds$")
    public void waitForAMessageDuringSeconds(String t, int time) throws EtiqetException {
        waitForAMessageDuringMs(t, time * 1000);
    }

    @Then("^wait for an? \"([^\"]*)\" message within (\\d+) milliseconds$")
    public void waitForAMessageDuringMs(String t, int time) throws EtiqetException {
        if (EtiqetHandlers.RESPONSE.equals(t))
            handlers.waitForResponse(EtiqetHandlers.DEFAULT_MESSAGE_NAME, EtiqetHandlers.DEFAULT_CLIENT_NAME, time);
        else
            handlers.waitForResponseOfType(EtiqetHandlers.DEFAULT_MESSAGE_NAME, EtiqetHandlers.DEFAULT_CLIENT_NAME, t,
                    time);
    }

    @Then("^wait for \"([^\"]*)\" to receive an? \"([^\"]*)\\\" as \"([^\"]*)\"$")
    public void namedClientNamedResponse(String clientName, String t, String responseName) throws EtiqetException {
        if (EtiqetHandlers.RESPONSE.equals(t))
            handlers.waitForResponse(responseName, clientName);
        else
            handlers.waitForResponseOfType(responseName, clientName, t);
    }

    // Checks
    @Then("^check for \"([^\"]*)\"$")
	public void checkLastResponseContainsKeyValueList(String params) {
        handlers.checkResponseKeyPresenceAndValue(EtiqetHandlers.DEFAULT_MESSAGE_NAME, params);
	}

	@Then("^check \"([^\"]*)\" for \"([^\"]*)\"$")
	public void checkLastResponseContainsKeyValueList(String msgName, String params) {
        handlers.checkResponseKeyPresenceAndValue(msgName, params);
	}
	
	/**
	 * Method to check if last received message has a list of params. 
	 * @param params param list.
	 */
	@Then("^check contains \"([^\"]*)\"$")
	public void checkResponseContains(String params) {
        handlers.checkFieldPresence(EtiqetHandlers.DEFAULT_MESSAGE_NAME, params);
	}

	@Then("^check \"([^\"]*)\" contains \"([^\"]*)\"$")
	public void checkResponseContains(String msgName, String params) {
        handlers.checkFieldPresence(msgName, params);
	}
	
	@Then("^get response \"([^\"]*)\" to \"([^\"]*)\" from \"(.*)\" by \"([^\"]*)\"$")
	public void getResponseToMessageFromListByField(String responseName, String messageName, String responseList, String fieldName) {
		handlers.getResponseToMessageFromListByField(responseName, messageName, responseList, fieldName);
	}
	
	@Then("^check that \"([^\"]*)\" match in \"([^\"]*)\"$")
	public void checkThatListOfParamsMatchInListOfMessages(String paramList, String messageList) {
		handlers.checkThatListOfParamsMatchInListOfMessages(paramList, messageList);
	}
	
	@Then("^check if match in \"([^\"]*)\"$")
	public void checkThatMessageParamsMatch(String paramList) {
		handlers.checkThatMessageParamsMatch(paramList);
	}
	
	@Then("^consume the response message$")
	public void consumeDefaultResponse() {
		handlers.consumeNamedResponse(EtiqetHandlers.DEFAULT_MESSAGE_NAME);
	}
	
	@Then("^consume the response message \"([^\"]*)\"$")
	public void consumeNamedResponse(String responseName) {
		handlers.consumeNamedResponse(responseName);
	}
	
	@Then("^try")
	public void startHandleExceptions() {
		handlers.startHandleExceptions();
	}
	
	@Then("^check errors \"([^\"]*)\"")
	public void checkHandleExceptions(String exceptionList) {
		handlers.checkHandledExceptions(exceptionList);
	}
	
	@Then("^stop client$")
	public void stopDefaultClient() {
        handlers.stopClient(EtiqetHandlers.DEFAULT_CLIENT_NAME);
	}

	@Then("^stop client \"([^\"]*)\"$")
	public void stopNamedClient(String clientName) {
        handlers.stopClient(clientName);
	}
	
	@Then("^stop server \"([^\"]*)\"$")
	public void stopServer(String serverName) {
        handlers.closeServer(serverName);
	}
	
	/**
	 * The last step must be close all the clients.
	 * Because if a execution fails, stop step will not be called.
	 */
	@After
	public void closeAllClients() {
        handlers.closeAllClients();
        handlers.closeAllServers();
	}

	@Then("^wait for an? \"([^\"]*)\" message with \"([^\"]*)\"$")
	public void waitForAWith(String messageType, String params) throws EtiqetException {
		waitForAMessageDefaultTimeout(messageType);
		checkLastResponseContainsKeyValueList(params);
	}

	@Then("^wait for an? \"([^\"]*)\" message with \"([^\"]*)\" within (\\d+) milliseconds?$")
	public void waitForADuringMsWith(String messageType, String params, int millis) throws EtiqetException {
		waitForAMessageDuringMs(messageType, millis);
		checkLastResponseContainsKeyValueList(params);
	}

	@Then("^wait for an? \"([^\"]*)\" message with \"([^\"]*)\" within (\\d+) seconds?$")
	public void waitForADuringWith(String messageType, String params, int seconds) throws EtiqetException {
		waitForAMessageDuringMs(messageType, seconds * 1000);
		checkLastResponseContainsKeyValueList(params);
	}

	@Then("^filter out \"([^\"]*)\" message$")
	public void filterOutMsg(String msgType) {
		handlers.filterMessage(msgType);
	}

	@Then("^remove filter for \"([^\"]*)\" message$")
	public void removeFilterfor(String msgType) {
		handlers.removeFromFiltered(msgType);
	}

    @Then("^set actions \"([^\"]*)\"$")
    public void setActions(String actions) throws EtiqetException {
        setActionsForClient(actions, EtiqetHandlers.DEFAULT_CLIENT_NAME);
    }

    @Then("^set actions \"([^\"]*)\" for client \"([^\"]*)\"$")
    public void setActionsForClient(String actions, String clientName) throws EtiqetException {
        handlers.setActions(clientName, actions);
    }

    @Then("^reset actions$")
    public void resetActions() throws EtiqetException {
		resetActionsForClient(EtiqetHandlers.DEFAULT_CLIENT_NAME);
    }

    @Then("^reset actions for client \"([^\"]*)\"$")
    public void resetActionsForClient(String clientName) throws EtiqetException {
        handlers.resetActions(clientName);
    }

	@And("^wait for a \"([^\"]*)\" message times out$")
	public void waitForAMessageTimesOut(String messageType) throws EtiqetException {
		handlers.waitForNoResponse(EtiqetHandlers.DEFAULT_MESSAGE_NAME, EtiqetHandlers.DEFAULT_CLIENT_NAME, messageType);
		handlers.validateMessageTypeDoesNotExistInResponseMap(EtiqetHandlers.DEFAULT_MESSAGE_NAME);
	}

	@And("^wait for a \"([^\"]*)\" message as \"([^\"]*)\" times out$")
	public void waitForAMessageAsTimesOut(String messageType, String messageName) throws EtiqetException {
		handlers.waitForNoResponse(messageName, EtiqetHandlers.DEFAULT_CLIENT_NAME, messageType);
		handlers.validateMessageTypeDoesNotExistInResponseMap(messageName);
	}

	@And("^wait for a \"([^\"]*)\" message as \"([^\"]*)\" times out within (\\d+) seconds$")
	public void waitForAMessageAsTimesOutWithinSeconds(String messageType, String messageName, int time) throws EtiqetException {
		handlers.waitForNoResponse(messageName, EtiqetHandlers.DEFAULT_CLIENT_NAME, messageType, time);
		handlers.validateMessageTypeDoesNotExistInResponseMap(messageName);
	}

    @Given("^a failure is expected$")
    public void aFailureIsExpected() {
        handlers.expectException();
    }

    // Placeholder method to demonstrate exception expecting steps
    @When("^you attempt something that causes a failure$")
    public void youAttemptSomething() {
        try {
            throw new NullPointerException();
        } catch (RuntimeException e) {
            handlers.addException(e, EtiqetHandlers.DEFAULT_EXCEPTION);
        }
    }

	@When("^you attempt to wait for an? \"([^\"]*)\" message with \"([^\"]*)\" within (\\d+) seconds$")
	public void youAttemptCallMethod(String methodName, String params, int time) throws EtiqetException {
		try {
			waitForADuringWith(methodName, params, time);
		} catch (EtiqetException e){
			RuntimeException exc = new RuntimeException(e);
			handlers.addException(exc, EtiqetHandlers.DEFAULT_EXCEPTION);
		}
	}

    @When("^you attempt something that causes a \"([^\"]*)\"$")
    public void youAttemptSomethingThatCausesAFailure(String cukeExpectedException) {
        try {
            throw new NullPointerException();
        } catch (RuntimeException e) {
            handlers.addException(e, cukeExpectedException);
        }
    }

    @Then("^check if failure had occurred$")
    public void checkForFailures() {
        handlers.checkForExceptions();
    }

    @Given("^variable \"([^\"]*)\" set to current timestamp$")
    public void variableSetToCurrentTimestamp(String alias) {
		variableSetTo(alias, "currentTimestamp");
    }

    @Given("^variable \"([^\"]*)\" set to \"([^\"]*)\"$")
    public void variableSetTo(String alias, String value) {
        handlers.addCukeVariable(alias,value);
    }

    @Then("^check that \"([^\"]*)\" in \"([^\"]*)\" is greater than variable \"([^\"]*)\"$")
    public void checkThatInIsGreaterThanVariable(String field, String messageAlias, String cukeVariable) {
	    handlers.compareTimestampGreaterCukeVar(field, messageAlias, handlers.cukeVariables.get(cukeVariable));
    }

    @Then("^check that \"([^\"]*)\" in \"([^\"]*)\" is less than variable \"([^\"]*)\"$")
    public void checkThatInIsLessThanVariable(String field, String messageAlias, String cukeVariable) {
        handlers.compareTimestampLesserCukeVar(field, messageAlias, handlers.cukeVariables.get(cukeVariable));
    }

    @Then("^check that \"([^\"]*)\" in \"([^\"]*)\" is equal to variable \"([^\"]*)\"$")
    public void checkThatInIsEqualToVariable(String field, String messageAlias, String cukeVariable) {
        handlers.compareTimestampEqualsCukeVar(field, messageAlias, handlers.cukeVariables.get(cukeVariable));
    }

    @Then("^check that \"([^\"]*)\" in \"([^\"]*)\" is greater than \"([^\"]*)\"$")
    public void checkThatInIsGreaterThan(String field, String messageAlias, String value) {
        handlers.compareTimestampGreaterCukeVar(field, messageAlias, value);
    }

    @Then("^check that \"([^\"]*)\" in \"([^\"]*)\" is less than \"([^\"]*)\"$")
    public void checkThatInIsLessThan(String field, String messageAlias, String value) {
        handlers.compareTimestampLesserCukeVar(field, messageAlias, value);
    }

    @Then("^check that \"([^\"]*)\" in \"([^\"]*)\" is equal to \"([^\"]*)\"$")
    public void checkThatInIsEqualTo(String field, String messageAlias, String value) {
        handlers.compareTimestampEqualsCukeVar(field, messageAlias, value);
    }

	@Then("^check that \"([^\"]*)\" in \"([^\"]*)\" is not set$")
	public void checkThatInIsNotSet(String field, String messageAlias) {
		handlers.compareTimestampEqualsCukeVar(field, messageAlias, "");
	}

    @Then("^check that the response field \"([^\"]*)\" is greater than variable \"([^\"]*)\"$")
    public void checkThatTheResponseFieldIsGreaterThanVariable(String field, String cukeVariable) {
        handlers.compareTimestampGreaterCukeVar(field, null, handlers.cukeVariables.get(cukeVariable));
    }

    @Then("^check that the response field \"([^\"]*)\" is less than variable \"([^\"]*)\"$")
    public void checkThatTheResponseFieldIsLessThanVariable(String field, String cukeVariable) {
        handlers.compareTimestampLesserCukeVar(field, null, handlers.cukeVariables.get(cukeVariable));
    }

    @Then("^check that the response field \"([^\"]*)\" is equal to variable \"([^\"]*)\"$")
    public void checkThatTheResponseFieldIsEqualToVariable(String field, String cukeVariable) {
        handlers.compareTimestampEqualsCukeVar(field, null, handlers.cukeVariables.get(cukeVariable));
    }

    @Then("^check that the response field \"([^\"]*)\" is greater than \"([^\"]*)\"$")
    public void checkThatTheResponseFieldIsGreaterThan(String field, String value) {
        handlers.compareTimestampGreaterCukeVar(field, null, value);
    }

    @Then("^check that the response field \"([^\"]*)\" is less than \"([^\"]*)\"$")
    public void checkThatTheResponseFieldIsLessThan(String field, String value) {
        handlers.compareTimestampLesserCukeVar(field, null, value);
    }

    @Then("^check that the response field \"([^\"]*)\" is equal to \"([^\"]*)\"$")
    public void checkThatTheResponseFieldIsEqualTo(String field, String value) {
        handlers.compareTimestampEqualsCukeVar(field, null, value);
    }

    @Then("^check that \"([^\"]*)\" in \"([^\"]*)\" matches time format \"([^\"]*)\"$")
    public void checkThatInMatchesTimeFormat(String field, String messageAlias, String timestampFormat) {
        handlers.validateTimestampAgainstFormatParam(timestampFormat, messageAlias, field);
    }

	@Then("^check that \"([^\"]*)\" in \"([^\"]*)\" is equal to \"([^\"]*)\" in \"([^\"]*)\"$")
	public void checkThatInIsEqualToIn(String firstField, String firstMessageAlias, String secondField, String secondMessageAlias) {
		handlers.compareValuesEqual(firstField, firstMessageAlias, secondField, secondMessageAlias);
	}

	@Then("^check that \"([^\"]*)\" in \"([^\"]*)\" is greater than \"([^\"]*)\" in \"([^\"]*)\"$")
    public void checkThatInIsGreaterThanIn(String firstField, String firstMessageAlias, String secondField, String secondMessageAlias) {
        handlers.compareValues(firstField, firstMessageAlias, secondField, secondMessageAlias, null);
    }

    @Then("^check that \"([^\"]*)\" in \"([^\"]*)\" is greater than \"([^\"]*)\" in \"([^\"]*)\" by no more than \"([^\"]*)\" milliseconds$")
    public void checkThatInIsGreaterThanInByNoMoreThanMilliseconds(String firstField, String firstMessageAlias, String secondField, String secondMessageAlias, String millis) {
        handlers.compareValues(firstField, firstMessageAlias, secondField, secondMessageAlias, Long.parseLong(millis));
    }

    @Then("^check that \"([^\"]*)\" in \"([^\"]*)\" is greater than \"([^\"]*)\" in \"([^\"]*)\" by no more than \"([^\"]*)\" seconds$")
    public void checkThatInIsGreaterThanInByNoMoreThanSeconds(String firstField, String firstMessageAlias, String secondField, String secondMessageAlias, String seconds) {
        handlers.compareValues(firstField, firstMessageAlias, secondField, secondMessageAlias, (Long.parseLong(seconds)*1000));
    }

    @Then("^check that \"([^\"]*)\" in \"([^\"]*)\" is less than \"([^\"]*)\" in \"([^\"]*)\"$")
    public void checkThatInIsLessThanIn(String firstField, String firstMessageAlias, String secondField, String secondMessageAlias) {
        handlers.compareValues(secondField, secondMessageAlias, firstField, firstMessageAlias, null);
    }

    @Then("^check that \"([^\"]*)\" in \"([^\"]*)\" is less than \"([^\"]*)\" in \"([^\"]*)\" by no more than \"([^\"]*)\" milliseconds$")
    public void checkThatInIsLessThanInByNoMoreThanMilliseconds(String firstField, String firstMessageAlias, String secondField, String secondMessageAlias, String millis) {
        handlers.compareValues(secondField, secondMessageAlias, firstField, firstMessageAlias, Long.parseLong(millis));
    }

    @Then("^check that \"([^\"]*)\" in \"([^\"]*)\" is less than \"([^\"]*)\" in \"([^\"]*)\" by no more than \"([^\"]*)\" seconds$")
    public void checkThatInIsLessThanInByNoMoreThanSeconds(String firstField, String firstMessageAlias, String secondField, String secondMessageAlias, String seconds) {
        handlers.compareValues(secondField, secondMessageAlias, firstField, firstMessageAlias, (Long.parseLong(seconds)*1000));
    }

	@And("^Neueda extensions enabled for \"([^\"]*)\"$")
	public void checkThatNeuedaExtensionsAreEnabled(String clientName) throws EtiqetException {
		handlers.checkExtensionsEnabled(clientName);
	}

	@And("^\"([^\"]*)\" order book is purged for \"([^\"]*)\"$")
	public void checkThatOrderBookIsPurged(String exchange, String clientName) throws EtiqetException, IOException {
		handlers.sendNamedRestMessageWithPayloadHeaders(EtiqetHandlers.HTTP_POST, handlers.getDefaultHeader(), handlers.getJson(exchange, null), EtiqetHandlers.PURGE_ORDERS, clientName);
	}

	@And("^\"([^\"]*)\" phase is \"([^\"]*)\" for  \"([^\"]*)\"$")
	public void checkThatPhaseIsOfType(String exchange, String auctionPhase, String clientName) throws EtiqetException, IOException {
		handlers.sendNamedRestMessageWithPayloadHeaders(EtiqetHandlers.HTTP_POST, handlers.getDefaultHeader(), handlers.getJson(exchange, auctionPhase), EtiqetHandlers.SET_TRADE_PHASE,clientName);
	}

	@Then("^\"([^\"]*)\" change trading phase to \"([^\"]*)\" for  \"([^\"]*)\"$")
	public void changeTradingPhaseToOpeningAuction(String exchange, String auctionPhase, String clientName) throws EtiqetException, IOException {
		handlers.sendNamedRestMessageWithPayloadHeaders(EtiqetHandlers.HTTP_POST, handlers.getDefaultHeader(), handlers.getJson(exchange, auctionPhase), EtiqetHandlers.SET_TRADE_PHASE,clientName);
	}

	@And("^Neueda extensions enabled$")
	public void checkThatNeuedaExtensionsAreEnabled() throws EtiqetException {
		handlers.checkExtensionsEnabled(EtiqetHandlers.DEFAULT_CLIENT_NAME);
	}

	@And("^fail to assert Neueda extensions enabled$")
	public void checkThatNeuedaExtensionsAreDisabled() {
		try {
			handlers.checkExtensionsEnabled(EtiqetHandlers.DEFAULT_CLIENT_NAME);
		} catch (EtiqetException e) {
			handlers.addException( new RuntimeException(e), EtiqetHandlers.DEFAULT_EXCEPTION);
		}
	}

	@And("^\"([^\"]*)\" order book is purged$")
	public void checkThatOrderBookIsPurged(String exchange) throws EtiqetException, IOException {
		handlers.sendNamedRestMessageWithPayloadHeaders(EtiqetHandlers.HTTP_POST, handlers.getDefaultHeader(),handlers.getJson(exchange, null),EtiqetHandlers.PURGE_ORDERS,EtiqetHandlers.DEFAULT_CLIENT_NAME);
	}

	@And("^fail to purge a \"([^\"]*)\" order book$")
	public void youFailToPurge(String exchange) {
		try {
			handlers.sendNamedRestMessageWithPayloadHeaders(EtiqetHandlers.HTTP_POST, handlers.getDefaultHeader(),handlers.getJson(exchange, null),EtiqetHandlers.PURGE_ORDERS,EtiqetHandlers.DEFAULT_CLIENT_NAME);
		} catch (EtiqetException | IOException e) {
			handlers.addException( new RuntimeException(e), EtiqetHandlers.DEFAULT_EXCEPTION);
		}
	}

	@And("^\"([^\"]*)\" phase is \"([^\"]*)\"$")
	public void checkThatPhaseIsOfType(String exchange, String auctionPhase) throws IOException, EtiqetException {
		handlers.sendNamedRestMessageWithPayloadHeaders(EtiqetHandlers.HTTP_POST, handlers.getDefaultHeader(), handlers.getJson(exchange, auctionPhase), EtiqetHandlers.SET_TRADE_PHASE,EtiqetHandlers.DEFAULT_CLIENT_NAME);
	}

	@Then("^change \"([^\"]*)\" trading phase to \"([^\"]*)\"$")
	public void changeTradingPhaseToOpeningAuction(String exchange, String auctionPhase) throws IOException, EtiqetException {
		handlers.sendNamedRestMessageWithPayloadHeaders(EtiqetHandlers.HTTP_POST,  handlers.getDefaultHeader(),handlers.getJson(exchange, auctionPhase), EtiqetHandlers.SET_TRADE_PHASE,EtiqetHandlers.DEFAULT_CLIENT_NAME);
	}

	@Then("^attempt to change \"([^\"]*)\" trading phase$")
	public void attemptToChangeTradingPhaseToOpeningAuction(String exchange) {
		try {
			handlers.sendNamedRestMessageWithPayloadHeaders(EtiqetHandlers.HTTP_POST, handlers.getDefaultHeader(), handlers.getJson(exchange, null), EtiqetHandlers.SET_TRADE_PHASE, EtiqetHandlers.DEFAULT_CLIENT_NAME);
		} catch (EtiqetException | IOException e) {
			handlers.addException(new RuntimeException(e), EtiqetHandlers.DEFAULT_EXCEPTION);
		}
	}

	@Then("^attempt to change trading phase to \"([^\"]*)\"$")
	public void attemptToChangeTradingPhase(String auctionPhase) {
		try {
			handlers.sendNamedRestMessageWithPayloadHeaders(EtiqetHandlers.HTTP_POST, handlers.getDefaultHeader(), handlers.getJson(null, auctionPhase), EtiqetHandlers.SET_TRADE_PHASE,EtiqetHandlers.DEFAULT_CLIENT_NAME);
		} catch (EtiqetException | IOException e) {
			handlers.addException( new RuntimeException(e), EtiqetHandlers.DEFAULT_EXCEPTION);
		}
	}

	@Then("^check that \"([^\"]*)\" in \"([^\"]*)\" has precision of \"([^\"]*)\"$")
	public void checkTimeStampPrecision(String field, String messageAlias, String precision) {
		handlers.checkTimeStampPrecision(field, messageAlias, precision);
	}

	@Then("^check that \"([^\"]*)\" in \"([^\"]*)\" has \"([^\"]*)\" precision$")
	public void checkTimeStampForNamedPrecision(String field, String messageAlias, String precisionName) {
		handlers.checkTimeStampPrecision(field, messageAlias, precisionName);
	}
}