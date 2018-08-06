package com.neueda.etiqet.fixture;

import com.google.api.client.http.HttpStatusCodes;
import com.neueda.etiqet.core.common.cdr.Cdr;
import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import com.neueda.etiqet.core.util.ParserUtils;
import com.neueda.etiqet.core.util.StringUtils;
import cucumber.api.java.en.Then;

import static com.neueda.etiqet.fixture.EtiqetHandlers.DEFAULT_CLIENT_NAME;
import static com.neueda.etiqet.fixture.EtiqetHandlers.DEFAULT_MESSAGE_NAME;

public class RestFixtures {

    private final EtiqetHandlers handlers;

    public RestFixtures(EtiqetHandlers handlers) { this.handlers = handlers; }

    @Then("send a \"(GET|PUT|POST|DELETE)\" message with headers \"(.+)\" and payload \"(.+)\" to \"(.+)\" as \"(.+)\"")
    public void sendNamedRestMessageWithPayloadHeaders
            (String httpVerb, String headers, String payload, String endpoint, String messageName) throws EtiqetException {
        if(endpoint == null) {
            throw new EtiqetException("Cannot send REST request without an endpoint");
        }

        Cdr restMsg = ParserUtils.stringToCdr(httpVerb, payload);
        if(headers != null) {
            String[] splitHeaders = headers.split(";");
            for (String headerKVPair : splitHeaders) {
                String headerName = "$header." + headerKVPair.split("=")[0];
                String headerValue = headerKVPair.split("=")[1];
                restMsg.set(headerName, headerValue);
            }
        }
        if(StringUtils.isNullOrEmpty(messageName)) {
            messageName = DEFAULT_MESSAGE_NAME;
        }
        restMsg.set("$httpEndpoint", endpoint);
        handlers.addMessage(messageName, restMsg);
        handlers.sendMessage(messageName, DEFAULT_CLIENT_NAME);
    }

    @Then("send a \"(GET|PUT|POST|DELETE)\" message with payload \"(.+)\" and headers \"(.+)\" to \"(.+)\"")
    public void sendRestMessageWithPayloadHeaders(String httpVerb, String payload, String headers, String endpoint)
            throws EtiqetException {
        sendNamedRestMessageWithPayloadHeaders(httpVerb, headers, payload, endpoint, DEFAULT_MESSAGE_NAME);
    }

    @Then("send a \"(GET|PUT|POST|DELETE)\" message with body \"(.+)\" to \"(.+)\"")
    public void sendRestMessageWithPayload(String httpVerb, String payload, String endpoint) throws EtiqetException {
        sendNamedRestMessageWithPayloadHeaders(httpVerb, null, payload, endpoint, DEFAULT_MESSAGE_NAME);
    }

    @Then("send a \"(GET|PUT|POST|DELETE)\" message with headers \"(.+)\" to \"(.+)\"")
    public void sendRestMessageWithHeaders(String httpVerb, String headers, String endpoint) throws EtiqetException {
        sendNamedRestMessageWithPayloadHeaders(httpVerb, headers, null, endpoint, DEFAULT_MESSAGE_NAME);
    }

    @Then("send a \"(GET|PUT|POST|DELETE)\" message to \"(.+)\"")
    public void sendRestMessage(String httpVerb, String endpoint) throws EtiqetException {
        sendNamedRestMessageWithPayloadHeaders(httpVerb, null, null, endpoint, DEFAULT_MESSAGE_NAME);
    }

    @Then("check the response for \"(.+)\" from client \"(.+)\" has status code \"(\\d+)\" with fields \"(.+)\"")
    public void checkNamedHttpResponseOnClient
            (String messageName, String clientName, Integer statusCode, String responseParams) throws EtiqetException {
        handlers.waitForResponseOfType(messageName, clientName, statusCode.toString());
        handlers.removeDefaultSentMessage();
        if(!StringUtils.isNullOrEmpty(responseParams)) {
            handlers.checkResponseKeyPresenceAndValue(messageName, responseParams);
        }
    }

    @Then("check the response for \"(.+)\" has status code \"(\\d+)\" with fields \"(.+)\"")
    public void checkHttpResponseForNamedMessage(String messageName, Integer statusCode, String responseParams)
            throws EtiqetException {
        checkNamedHttpResponseOnClient(messageName, DEFAULT_CLIENT_NAME, statusCode, responseParams);
    }

    @Then("check the response has status code \"(\\d+)\" with fields \"(.+)\"")
    public void checkHttpResponse(Integer statusCode, String responseParams) throws EtiqetException {
        checkHttpResponseForNamedMessage(DEFAULT_MESSAGE_NAME, statusCode, responseParams);
    }

    @Then("check the response \"(.+)\" from client \"(.+)\" has status code \"(\\d+)\" and fields \"(.+)\" are present")
    public void checkNamedHttpResponseAndFieldPresenceOnClient
            (String messageName, String clientName, Integer statusCode, String expectedFields) throws EtiqetException {
        handlers.waitForResponseOfType(messageName, clientName, statusCode.toString());
        handlers.removeDefaultSentMessage();
        handlers.checkFieldPresence(messageName, expectedFields);
    }

    @Then("check the response \"(.+)\" has status code \"(\\d+)\" and fields \"(.+)\" are present")
    public void checkNamedHttpResponseAndFieldPresence(String messageName, Integer statusCode, String expectedFields)
            throws EtiqetException {
        checkNamedHttpResponseAndFieldPresenceOnClient(messageName, DEFAULT_CLIENT_NAME, statusCode, expectedFields);
    }

    @Then("check the response has status code \"(\\d+)\" and fields \"(.+)\" are present")
    public void checkHttpResponseAndFieldPresence(Integer statusCode, String expectedFields) throws EtiqetException {
        checkNamedHttpResponseAndFieldPresence(DEFAULT_MESSAGE_NAME, statusCode, expectedFields);
    }

    @Then("check the response has a status code \"(\\d+)\"")
    public void checkHttpStatusCode(Integer statusCode) throws EtiqetException {
        checkHttpResponse(statusCode, null);
    }

    @Then("check the response has fields \"(.+)\"")
    public void checkHttpResponseParameters(String responseParams) throws EtiqetException {
        // assumes that the response code is 200 - OK
        checkHttpResponse(HttpStatusCodes.STATUS_CODE_OK, responseParams);
    }

}
