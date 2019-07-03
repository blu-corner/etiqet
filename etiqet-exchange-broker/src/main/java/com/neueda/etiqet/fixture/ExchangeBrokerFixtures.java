package com.neueda.etiqet.fixture;

import com.neueda.etiqet.core.client.Client;
import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import com.neueda.etiqet.core.message.cdr.Cdr;
import com.neueda.etiqet.exchangeBroker.client.ExchangeBrokerClient;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

import static com.neueda.etiqet.fixture.EtiqetHandlers.DEFAULT_MESSAGE_NAME;
import static junit.framework.Assert.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertNotNull;

public class ExchangeBrokerFixtures {

    private final EtiqetHandlers handlers;

    public ExchangeBrokerFixtures(EtiqetHandlers handlers) {
        this.handlers = handlers;
    }

    @When("^client \"([^\"]*)\" is subscribed to queue \"([^\"]*)\"$")
    public void subscribeToQueueFromClient(String clientName, String queueName) throws EtiqetException {
        ExchangeBrokerClient client = getExchangeBrokerClient(clientName);
        client.subscribeToQueue(queueName);
    }

    @Then("^wait for \"([^\"]*)\" to receive a message on queue \"([^\"]*)\" within (\\d+) seconds as \"([^\"]*)\"$")
    public void listenToQueueFromClientWithAlias(String clientName,
                                        String queue,
                                        int seconds,
                                        String alias) throws EtiqetException {
        ExchangeBrokerClient client = getExchangeBrokerClient(clientName);
        Cdr cdr = client.retrieveMessageFromQueue(queue, Duration.ofSeconds(seconds));
        assertNotNull(cdr);
        handlers.addResponse(alias, cdr);
    }

    @Then("^wait for \"([^\"]*)\" to receive a message on queue \"([^\"]*)\" within (\\d+) seconds$")
    public void listenToQueueFromClient(String clientName,
                                        String queue,
                                        int seconds) throws EtiqetException {
        ExchangeBrokerClient client = getExchangeBrokerClient(clientName);
        Cdr cdr = client.retrieveMessageFromQueue(queue, Duration.ofSeconds(seconds));
        assertNotNull(cdr);
    }

    @Then("^send an? \"([^\"]*)\" message with \"([^\"]*)\" using \"([^\"]*)\" to exchange \"([^\"]*)\"")
    public void sendMessageToExchangeWithParams(String msgType,
                                                String params,
                                                String clientName,
                                                String exchangeName) throws EtiqetException {
        final Cdr cdr = createMessageForClient(msgType, Optional.of(params), clientName, Optional.empty());
        ExchangeBrokerClient client = getExchangeBrokerClient(clientName);
        client.sendMessageToExchange(cdr, exchangeName);
    }

    @Then("^send an? \"([^\"]*)\" message with \"([^\"]*)\" using \"([^\"]*)\" to exchange \"([^\"]*)\" with routing key \"([^\"]*)\"")
    public void sendMessageToExchangeWithParamsAndRoutingKey(String msgType,
                                                             String params,
                                                             String clientName,
                                                             String exchangeName,
                                                             String routingKey) throws EtiqetException {
        final Cdr cdr = createMessageForClient(msgType, Optional.of(params), clientName, Optional.empty());
        ExchangeBrokerClient client = getExchangeBrokerClient(clientName);
        client.sendMessageToExchange(cdr, exchangeName, routingKey);
    }

    @Then("^send an? \"([^\"]*)\" message using \"([^\"]*)\" to exchange \"([^\"]*)\"")
    public void sendMessageToExchange(String msgType,
                                      String clientName,
                                      String exchangeName) throws EtiqetException {
        final Cdr cdr = createMessageForClient(msgType, Optional.empty(), clientName, Optional.empty());
        ExchangeBrokerClient client = getExchangeBrokerClient(clientName);
        client.sendMessageToExchange(cdr, exchangeName);
    }

    @Then("check that \"([^\"]*)\" has received (\\d+) messages from queue \"([^\"]*)\"$")
    public void checkNumberOfMessagesReceivedOnQueue(String clientName, int numMessages, String queueName) {
        ExchangeBrokerClient client = getExchangeBrokerClient(clientName);
        List<Cdr> receivedMessages = client.getReceivedMessagesFromQueue(queueName);
        assertEquals(numMessages, receivedMessages.size());
    }

    @Then("check that last message received by \"([^\"]*)\" from queue \"([^\"]*)\" contains \"([^\"]*)\"$")
    public void checkLastQueueMessageContent(String clientName, String queueName, String params) {
        ExchangeBrokerClient client = getExchangeBrokerClient(clientName);

        Optional<Cdr> lastMessage = client.getLastMessageFromQueue(queueName);
        assertTrue("No message found for queue", lastMessage.isPresent());
        handlers.checkMessageContent(lastMessage.get(), params);
    }

    private ExchangeBrokerClient getExchangeBrokerClient(final String clientName) {
        Client client = handlers.getClient(clientName);
        assertTrue(client instanceof ExchangeBrokerClient);
        return (ExchangeBrokerClient) client;
    }

    private Cdr createMessageForClient(final String msgType,
                                       final Optional<String> params,
                                       final String clientName,
                                       final Optional<String> maybeAlias) throws EtiqetException {
        final String alias = maybeAlias.orElse(DEFAULT_MESSAGE_NAME);
        handlers.createMessageForClient(msgType, clientName, alias, params);
        return handlers.getSentMessage(alias);
    }
}
