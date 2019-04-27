package com.neueda.etiqet.fixture;

import static com.neueda.etiqet.fixture.EtiqetHandlers.DEFAULT_CLIENT_NAME;
import static com.neueda.etiqet.fixture.EtiqetHandlers.DEFAULT_MESSAGE_NAME;

import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import com.neueda.etiqet.core.config.dtos.Message;
import com.neueda.etiqet.core.message.cdr.Cdr;
import com.neueda.etiqet.core.util.ParserUtils;
import com.neueda.etiqet.core.util.StringUtils;
import cucumber.api.java.en.Then;

public class WebSocketFixtures {

    private final EtiqetHandlers handlers;

    public WebSocketFixtures(EtiqetHandlers handlers) {
        this.handlers = handlers;
    }

    @Then("send a \"(\\S+)\" websocket message with payload (\\S+)\" as \"(\\S+)\" using client \"(\\S+)\"$")
    public void sendMessageWithPayloadAndClient(String testName, String payload, String responseName, String clientName)
        throws EtiqetException {
        Message protocolMsg = handlers.getClient(clientName).getProtocolConfig().getMessage(testName);

        Cdr webSocketMsg = new Cdr(testName);

        ParserUtils.fillDefaultWithParams(protocolMsg, webSocketMsg);
        webSocketMsg = ParserUtils.stringToCdr(webSocketMsg, handlers.preTreatParams(payload));

        responseName = StringUtils.isNullOrEmpty(responseName) ? DEFAULT_MESSAGE_NAME : responseName;
        handlers.addMessage(responseName, webSocketMsg);
        handlers.sendMessage(responseName, clientName);
    }

    @Then("send a \"(\\S+)\" websocket message with payload \"(\\S+)\" as (\\S+)\"$")
    public void sendMessageWithPayload(String testName, String payload, String responseName) throws EtiqetException {
        sendMessageWithPayloadAndClient(testName, payload, responseName, DEFAULT_CLIENT_NAME);
    }

    @Then("send a \"(\\S+)\" websocket message as \"(\\S+)\"$")
    public void sendMessage(String testName, String responseName) throws EtiqetException {
        sendMessageWithPayloadAndClient(testName, "", responseName, DEFAULT_CLIENT_NAME);
    }

    @Then("send a \"(\\S+)\" websocket message as \"(\\S+)\" using client \"(\\S+)\"$")
    public void sendMessageAndClient(String testName, String responseName, String clientName) throws EtiqetException {
        sendMessageWithPayloadAndClient(testName, "", responseName, clientName);
    }

    @Then("^wait for \"([^\"]*)\" to receive a websocket message ? \"([^\"]*)\\\" as \"([^\"]*)\"$")
    public void namedClientExchangeResponse(String clientName, String t, String responseName) throws EtiqetException {
        if (EtiqetHandlers.RESPONSE.equals(t)) {
            handlers.waitForResponse(responseName, clientName);
        } else {
            handlers.waitForResponseOfType(responseName, clientName, t, 10000, true);
        }
    }
}
