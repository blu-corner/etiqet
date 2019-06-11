package com.neueda.etiqet.fixture;

import com.neueda.etiqet.core.client.Client;
import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import com.neueda.etiqet.core.json.JsonCodec;
import com.neueda.etiqet.core.message.cdr.Cdr;
import com.neueda.etiqet.transport.solace.client.SolaceClient;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;

import java.io.IOException;

import static org.junit.Assert.*;

public class SolaceFixtures {

    private final EtiqetHandlers handlers;

    public SolaceFixtures(EtiqetHandlers handlers) {
        this.handlers = handlers;
    }

    @And("^wait for a message on topic \"([^\"]*)\"")
    public void listenToTopic(String topic) throws EtiqetException {
        listenToTopicFromClient(EtiqetHandlers.DEFAULT_CLIENT_NAME, topic, 5, EtiqetHandlers.DEFAULT_MESSAGE_NAME);
    }

    @And("^wait for \"([^\"]*)\" to receive a message on topic \"([^\"]*)\"$")
    public void listenToTopicFromClient(String clientName, String topic) throws EtiqetException {
        listenToTopicFromClient(clientName, topic, 5, EtiqetHandlers.DEFAULT_MESSAGE_NAME);
    }

    @And("^wait for \"([^\"]*)\" to receive a message on topic \"([^\"]*)\" within (\\d+) seconds as \"([^\"]*)\"")
    public void listenToTopicFromClient(String clientName,
                                        String topic,
                                        int seconds,
                                        String alias) throws EtiqetException {
        Client client = handlers.getClient(clientName);
        assertTrue(client instanceof SolaceClient);
        SolaceClient solaceClient = (SolaceClient) client;
        Cdr cdr = solaceClient.waitForMsgOnTopic(topic, seconds * 1000);
        assertNotNull(cdr);
        handlers.addResponse(alias, cdr);
    }

    @And("^wait for a message on queue \"([^\"]*)\"")
    public void listenToQueue(String queue) throws EtiqetException {
        listenToQueueFromClient(EtiqetHandlers.DEFAULT_CLIENT_NAME, queue, 5, EtiqetHandlers.DEFAULT_MESSAGE_NAME);
    }

    @And("^wait for \"([^\"]*)\" to receive a message on queue \"([^\"]*)\"$")
    public void listenToQueueFromClient(String clientName, String topic) throws EtiqetException {
        listenToQueueFromClient(clientName, topic, 5, EtiqetHandlers.DEFAULT_MESSAGE_NAME);
    }

    @And("^wait for \"([^\"]*)\" to receive a message on queue \"([^\"]*)\" within (\\d+) seconds as \"([^\"]*)\"")
    public void listenToQueueFromClient(String clientName,
                                        String queue,
                                        int seconds,
                                        String alias) throws EtiqetException {
        Client solace = handlers.getClient(clientName);
        assertTrue(solace instanceof SolaceClient);
        SolaceClient solaceClient = (SolaceClient) solace;
        Cdr cdr = solaceClient.waitForMsgOnQueue(queue, seconds * 1000);
        assertNotNull(cdr);
        handlers.addResponse(alias, cdr);
    }


    // Sending to a Queue


    @Then("^send an? \"([^\"]*)\" message with \"([^\"]*)\" using \"([^\"]*)\" to queue \"([^\"]*)\" as \"([^\"]*)\"$")
    public void sendAMsgToQueueWithParamsAndAlias(String msgType,
                                                  String params,
                                                  String clientName,
                                                  String queueName,
                                                  String alias) throws EtiqetException {
        handlers.createMessageForClient(msgType, clientName, alias, params);
        handlers.sendMessage(alias, clientName, queueName);
    }

    @Then("^send an? \"([^\"]*)\" message with \"([^\"]*)\" using \"([^\"]*)\" to queue \"([^\"]*)\"")
    public void sendAMsgToQueueWithParams(String msgType,
                                          String params,
                                          String clientName,
                                          String queueName) throws EtiqetException {
        handlers.createMessageNoAlias(msgType, clientName, params);
        handlers.sendMessage(clientName, queueName);
    }

    @Then("^send an? \"([^\"]*)\" message using \"([^\"]*)\" to queue \"([^\"]*)\" as \"([^\"]*)\"$")
    public void sendAMsgToQueueWithAlias(String msgType,
                                         String clientName,
                                         String queueName,
                                         String alias) throws EtiqetException {
        handlers.createMessageNoParams(msgType, clientName, alias);
        handlers.sendMessage(alias, clientName, queueName);
    }

    @Then("^send an? \"([^\"]*)\" message using \"([^\"]*)\" to queue \"([^\"]*)\"")
    public void sendAMsgToQueue(String msgType,
                                String clientName,
                                String queueName) throws EtiqetException {
        handlers.createMessageNoParamsNoAlias(msgType, clientName);
        handlers.sendMessage(clientName, queueName);
    }


    // Sending to a Topic


    @Then("^send an? \"([^\"]*)\" message with \"([^\"]*)\" using \"([^\"]*)\" to topic \"([^\"]*)\" as \"([^\"]*)\"$")
    public void sendAMsgToTopicWithParamsAndAlias(String msgType,
                                                  String params,
                                                  String clientName,
                                                  String topicName,
                                                  String alias) throws EtiqetException {
        handlers.createMessageForClient(msgType, clientName, alias, params);
        handlers.sendMessage(alias, clientName, topicName);
    }

    @Then("^send an? \"([^\"]*)\" message with \"([^\"]*)\" using \"([^\"]*)\" to topic \"([^\"]*)\"")
    public void sendAMsgToTopicWithParams(String msgType,
                                          String params,
                                          String clientName,
                                          String topicName) throws EtiqetException {
        handlers.createMessageNoAlias(msgType, clientName, params);
        handlers.sendMessage(clientName, topicName);
    }

    @Then("^send an? \"([^\"]*)\" message using \"([^\"]*)\" to topic \"([^\"]*)\" as \"([^\"]*)\"$")
    public void sendAMsgToTopicWithAlias(String msgType,
                                         String clientName,
                                         String topicName,
                                         String alias) throws EtiqetException {
        handlers.createMessageNoParams(msgType, clientName, alias);
        handlers.sendMessage(alias, clientName, topicName);
    }

    @Then("^send an? \"([^\"]*)\" message using \"([^\"]*)\" to topic \"([^\"]*)\"")
    public void sendAMsgToTopic(String msgType,
                                String clientName,
                                String topicName) throws EtiqetException {
        handlers.createMessageNoParamsNoAlias(msgType, clientName);
        handlers.sendMessage(clientName, topicName);
    }

    @Then("^send message \"([^\"]*)\" using \"([^\"]*)\" to topic \"([^\"]*)\"")
    public void sendMessageToTopicAsAlias(String alias, String clientName, String topicName) throws EtiqetException {
        this.handlers.sendMessage(alias, clientName, topicName);
    }

    @Then("^send message \"([^\"]*)\" using \"([^\"]*)\" to queue \"([^\"]*)\"")
    public void sendMessageToQueueAsAlias(String alias, String clientName, String queueName) throws EtiqetException {
        this.handlers.sendMessage(alias, clientName, queueName);
    }

    @Then("^create message from file \"([^\"]*)\" as \"([^\"]*)\" and send using \"([^\"]*)\" to topic \"([^\"]*)\"")
    public void createMessageFromFileSendToTopic(String fileName, String alias, String clientName, String topicName) throws EtiqetException {
        try {
            this.handlers.createMessageFromFile(fileName, alias);
            this.handlers.sendMessage(alias, clientName, topicName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Then("^create message from file \"([^\"]*)\" as \"([^\"]*)\" and send using \"([^\"]*)\" to queue \"([^\"]*)\"")
    public void createMessageFromFileSendToQueue(String fileName, String alias, String clientName, String queueName) throws EtiqetException {
        try {
            this.handlers.createMessageFromFile(fileName, alias);
            this.handlers.sendMessage(alias, clientName, queueName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
