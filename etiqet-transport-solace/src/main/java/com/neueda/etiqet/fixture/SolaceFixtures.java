package com.neueda.etiqet.fixture;

import com.neueda.etiqet.core.client.Client;
import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import com.neueda.etiqet.core.message.cdr.Cdr;
import com.neueda.etiqet.transport.solace.client.SolaceClient;
import cucumber.api.java.en.And;

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
    public void listenToTopicFromClient(String clientName, String topic, int seconds, String alias) throws EtiqetException {
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
    public void listenToQueueFromClient(String clientName, String queue, int seconds, String alias) throws EtiqetException {
        Client solace = handlers.getClient(clientName);
        assertTrue(solace instanceof SolaceClient);
        SolaceClient solaceClient = (SolaceClient) solace;
        Cdr cdr = solaceClient.waitForMsgOnQueue(queue, seconds * 1000);
        assertNotNull(cdr);
        handlers.addResponse(alias, cdr);
    }

}
