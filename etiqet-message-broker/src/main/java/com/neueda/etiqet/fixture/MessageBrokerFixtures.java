package com.neueda.etiqet.fixture;

import com.neueda.etiqet.core.client.Client;
import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import com.neueda.etiqet.core.message.cdr.Cdr;
import com.neueda.etiqet.messageBroker.client.MessageBrokerClient;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import java.util.List;
import java.util.Optional;

import static com.neueda.etiqet.fixture.EtiqetHandlers.DEFAULT_MESSAGE_NAME;
import static junit.framework.Assert.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertNotNull;

public class MessageBrokerFixtures {

    private final EtiqetHandlers handlers;

    public MessageBrokerFixtures(EtiqetHandlers handlers) {
        this.handlers = handlers;
    }

    @When("^client \"([^\"]*)\" is subscribed to topic \"([^\"]*)\"$")
    public void subscribeToTopicFromClient(String clientName, String topic) throws EtiqetException {
        MessageBrokerClient client = getMessageBrokerClient(clientName);
        client.subscribeToTopic(topic);
    }

    @When("^client \"([^\"]*)\" is subscribed to queue \"([^\"]*)\"$")
    public void subscribeToQueueFromClient(String clientName, String queueName) throws EtiqetException {
        MessageBrokerClient client = getMessageBrokerClient(clientName);
        client.subscribeToQueue(queueName);
    }

    @And("^wait for a message on topic \"([^\"]*)\"")
    public void listenToTopic(String topic) throws EtiqetException {
        listenToTopicFromClient(EtiqetHandlers.DEFAULT_CLIENT_NAME, topic, 5, DEFAULT_MESSAGE_NAME);
    }

    @And("^wait for \"([^\"]*)\" to receive a message on topic \"([^\"]*)\"$")
    public void listenToTopicFromClient(String clientName, String topic) throws EtiqetException {
        listenToTopicFromClient(clientName, topic, 5, DEFAULT_MESSAGE_NAME);
    }

    @And("^wait for \"([^\"]*)\" to receive a message on topic \"([^\"]*)\" within (\\d+) seconds as \"([^\"]*)\"")
    public void listenToTopicFromClient(String clientName,
                                        String topic,
                                        int seconds,
                                        String alias) throws EtiqetException {
        MessageBrokerClient client = getMessageBrokerClient(clientName);
        Cdr cdr = client.waitForMsgOnTopic(Optional.of(topic), seconds * 1000);
        assertNotNull(cdr);
        handlers.addResponse(alias, cdr);
    }

    @And("^wait for a message on queue \"([^\"]*)\"")
    public void listenToQueue(String queue) throws EtiqetException {
        listenToQueueFromClient(EtiqetHandlers.DEFAULT_CLIENT_NAME, queue, 5, DEFAULT_MESSAGE_NAME);
    }

    @And("^wait for \"([^\"]*)\" to receive a message on queue \"([^\"]*)\"$")
    public void listenToQueueFromClient(String clientName, String topic) throws EtiqetException {
        listenToQueueFromClient(clientName, topic, 5, DEFAULT_MESSAGE_NAME);
    }

    @And("^wait for \"([^\"]*)\" to receive a message on queue \"([^\"]*)\" within (\\d+) seconds as \"([^\"]*)\"")
    public void listenToQueueFromClient(String clientName,
                                        String queue,
                                        int seconds,
                                        String alias) throws EtiqetException {
        MessageBrokerClient client = getMessageBrokerClient(clientName);
        Cdr cdr = client.waitForMsgOnQueue(queue, seconds * 1000);
        assertNotNull(cdr);
        handlers.addResponse(alias, cdr);
    }

    @Then("^send an? \"([^\"]*)\" message with \"([^\"]*)\" using \"([^\"]*)\" to queue \"([^\"]*)\" as \"([^\"]*)\"$")
    public void sendAMsgToQueueWithParamsAndAlias(String msgType,
                                                  String params,
                                                  String clientName,
                                                  String queueName,
                                                  String alias) throws EtiqetException {
        final Cdr cdr = createMessageForClient(msgType, Optional.of(params), clientName, Optional.of(alias));
        MessageBrokerClient client = getMessageBrokerClient(clientName);
        client.sendMessageToQueue(cdr, queueName);
    }

    @Then("^send an? \"([^\"]*)\" message with \"([^\"]*)\" using \"([^\"]*)\" to queue \"([^\"]*)\"")
    public void sendAMsgToQueueWithParams(String msgType,
                                          String params,
                                          String clientName,
                                          String queueName) throws EtiqetException {
        final Cdr cdr = createMessageForClient(msgType, Optional.of(params), clientName, Optional.empty());
        MessageBrokerClient client = getMessageBrokerClient(clientName);
        client.sendMessageToQueue(cdr, queueName);
    }

    @Then("^send an? \"([^\"]*)\" message using \"([^\"]*)\" to queue \"([^\"]*)\" as \"([^\"]*)\"$")
    public void sendAMsgToQueueWithAlias(String msgType,
                                         String clientName,
                                         String queueName,
                                         String alias) throws EtiqetException {
        final Cdr cdr = createMessageForClient(msgType, Optional.empty(), clientName, Optional.of(alias));
        MessageBrokerClient client = getMessageBrokerClient(clientName);
        client.sendMessageToQueue(cdr, queueName);
    }

    @Then("^send an? \"([^\"]*)\" message using \"([^\"]*)\" to queue \"([^\"]*)\"")
    public void sendAMsgToQueue(String msgType,
                                String clientName,
                                String queueName) throws EtiqetException {
        final Cdr cdr = createMessageForClient(msgType, Optional.empty(), clientName, Optional.empty());
        MessageBrokerClient client = getMessageBrokerClient(clientName);
        client.sendMessageToQueue(cdr, queueName);
    }


    // Sending to a Topic


    @Then("^send an? \"([^\"]*)\" message with \"([^\"]*)\" using \"([^\"]*)\" to topic \"([^\"]*)\" as \"([^\"]*)\"$")
    public void sendAMsgToTopicWithParamsAndAlias(String msgType,
                                                  String params,
                                                  String clientName,
                                                  String topicName,
                                                  String alias) throws EtiqetException {
        final Cdr cdr = createMessageForClient(msgType, Optional.of(params), clientName, Optional.of(alias));
        MessageBrokerClient client = getMessageBrokerClient(clientName);
        client.sendMessageToTopic(cdr, Optional.of(topicName));
    }

    @Then("^send an? \"([^\"]*)\" message with \"([^\"]*)\" using \"([^\"]*)\" to topic \"([^\"]*)\"")
    public void sendAMsgToTopicWithParams(String msgType,
                                          String params,
                                          String clientName,
                                          String topicName) throws EtiqetException {
        final Cdr cdr = createMessageForClient(msgType, Optional.of(params), clientName, Optional.empty());
        MessageBrokerClient client = getMessageBrokerClient(clientName);
        client.sendMessageToTopic(cdr, Optional.of(topicName));
    }

    @Then("^send an? \"([^\"]*)\" message using \"([^\"]*)\" to topic \"([^\"]*)\" as \"([^\"]*)\"$")
    public void sendAMsgToTopicWithAlias(String msgType,
                                         String clientName,
                                         String topicName,
                                         String alias) throws EtiqetException {
        final Cdr cdr = createMessageForClient(msgType, Optional.empty(), clientName, Optional.of(alias));
        MessageBrokerClient client = getMessageBrokerClient(clientName);
        client.sendMessageToTopic(cdr, Optional.of(topicName));
    }

    @Then("^send an? \"([^\"]*)\" message using \"([^\"]*)\" to topic \"([^\"]*)\"")
    public void sendAMsgToTopic(String msgType,
                                String clientName,
                                String topicName) throws EtiqetException {
        final Cdr cdr = createMessageForClient(msgType, Optional.empty(), clientName, Optional.empty());
        MessageBrokerClient client = getMessageBrokerClient(clientName);
        client.sendMessageToTopic(cdr, Optional.of(topicName));
    }

    @Then("^send message \"([^\"]*)\" using \"([^\"]*)\" to topic \"([^\"]*)\"")
    public void sendMessageToTopicAsAlias(String alias, String clientName, String topicName) throws EtiqetException {
        final Cdr cdr = handlers.getSentMessage(alias);
        MessageBrokerClient client = getMessageBrokerClient(clientName);
        client.sendMessageToTopic(cdr, Optional.of(topicName));
    }

    @Then("^send message \"([^\"]*)\" using \"([^\"]*)\" to queue \"([^\"]*)\"")
    public void sendMessageToQueueAsAlias(String alias, String clientName, String queueName) throws EtiqetException {
        final Cdr cdr = handlers.getSentMessage(alias);
        MessageBrokerClient client = getMessageBrokerClient(clientName);
        client.sendMessageToQueue(cdr, queueName);
    }

    @Then("^create message from file \"([^\"]*)\" as \"([^\"]*)\" and send using \"([^\"]*)\" to topic \"([^\"]*)\"")
    public void createMessageFromFileSendToTopic(String fileName, String alias, String clientName, String topicName) throws EtiqetException {
        this.handlers.createMessageFromFile(fileName, alias);
        final Cdr cdr = handlers.getSentMessage(alias);
        MessageBrokerClient client = getMessageBrokerClient(clientName);
        client.sendMessageToTopic(cdr, Optional.of(topicName));
    }

    @Then("^create message from file \"([^\"]*)\" as \"([^\"]*)\" and send using \"([^\"]*)\" to queue \"([^\"]*)\"")
    public void createMessageFromFileSendToQueue(String fileName, String alias, String clientName, String queueName) throws EtiqetException {
        this.handlers.createMessageFromFile(fileName, alias);
        final Cdr cdr = handlers.getSentMessage(alias);
        MessageBrokerClient client = getMessageBrokerClient(clientName);
        client.sendMessageToQueue(cdr, queueName);
    }

    @Then("check that \"([^\"]*)\" has received (\\d+) messages from topic \"([^\"]*)\"$")
    public void checkNumberOfMessagesReceivedOnTopic(String clientName, int numMessages, String topicName) {
        MessageBrokerClient client = getMessageBrokerClient(clientName);
        List<Cdr> receivedMessages = client.getReceivedMessagesFromTopic(topicName);
        assertEquals(numMessages, receivedMessages.size());
    }

    @Then("check that \"([^\"]*)\" has received (\\d+) messages from queue \"([^\"]*)\"$")
    public void checkNumberOfMessagesReceivedOnQueue(String clientName, int numMessages, String queueName) {
        MessageBrokerClient client = getMessageBrokerClient(clientName);
        List<Cdr> receivedMessages = client.getReceivedMessagesFromQueue(queueName);
        assertEquals(numMessages, receivedMessages.size());
    }

    @Then("check that last message received by \"([^\"]*)\" from topic \"([^\"]*)\" contains \"([^\"]*)\"$")
    public void checkLastTopicMessageContent(String clientName, String topicName, String params) {
        MessageBrokerClient client = getMessageBrokerClient(clientName);

        Optional<Cdr> lastMessage = client.getLastMessageFromTopic(topicName);
        assertTrue("No message found for topic", lastMessage.isPresent());
        handlers.checkMessageContent(lastMessage.get(), params);
    }

    @Then("check that last message received by \"([^\"]*)\" from queue \"([^\"]*)\" contains \"([^\"]*)\"$")
    public void checkLastQueueMessageContent(String clientName, String queueName, String params) {
        MessageBrokerClient client = getMessageBrokerClient(clientName);

        Optional<Cdr> lastMessage = client.getLastMessageFromQueue(queueName);
        assertTrue("No message found for queue", lastMessage.isPresent());
        handlers.checkMessageContent(lastMessage.get(), params);
    }

    /**
     * Consumes and acknowledges messages from the queue specified on the client. This will continue consuming messages
     * from the queue until we receive nothing for a 5 second period.
     *
     * @param queueName  name of the queue to clear
     * @param clientName name of the client
     * @throws EtiqetException when an error occurs trying to consume / acknowledge the messages
     */
    @Then("clear the queue \"([^\"]*)\" for client \"([^\"]*)\"$")
    public void clearQueue(String queueName, String clientName) throws EtiqetException {
        MessageBrokerClient client = getMessageBrokerClient(clientName);
        client.clearQueue(queueName);
    }

    private MessageBrokerClient getMessageBrokerClient(final String clientName) {
        Client client = handlers.getClient(clientName);
        assertTrue(client instanceof MessageBrokerClient);
        return (MessageBrokerClient) client;
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
