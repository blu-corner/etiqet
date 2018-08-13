package com.neueda.etiqet.fixture;

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

    @Then("\"(GET|PUT|POST|DELETE)\" a \"(\\S+)\"(?: with headers \"?([^\"]*)\"??)?(?: (?:with|and) payload )?\"?([^\"]*)?\"? (?:to|from|at) \"(\\S+)\"(?: as )?\"?([^\"]*)?\"?")
    public void testMethod(String httpVerb, String testName, String headers, String payload, String endpoint, String responseName) throws EtiqetException {
        if(StringUtils.isNullOrEmpty(httpVerb))
            throw new EtiqetException("Cannot send REST request without a HTTP verb");
        if(StringUtils.isNullOrEmpty(testName))
            throw new EtiqetException("Cannot send REST request without a test name");
        if(StringUtils.isNullOrEmpty(endpoint))
            throw new EtiqetException("Cannot send REST request without an endpoint");

        Cdr restMsg = ParserUtils.stringToCdr(testName, payload);
        if(!StringUtils.isNullOrEmpty(headers)) {
            String[] splitHeaders = headers.split(";");
            for (String headerKVPair : splitHeaders) {
                String headerName = "$header." + headerKVPair.split("=")[0];
                String headerValue = headerKVPair.split("=")[1];
                restMsg.set(headerName, headerValue);
            }
        }

        responseName = StringUtils.isNullOrEmpty(responseName) ? DEFAULT_MESSAGE_NAME : responseName;

        restMsg.set("$httpEndpoint", endpoint);
        restMsg.set("$httpVerb", httpVerb);
        handlers.addMessage(responseName, restMsg);
        handlers.sendMessage(responseName, DEFAULT_CLIENT_NAME);
    }

    @Then("check that ?\"?([^\"]*)?\"? (?:has )?status code \"(\\d+)\"")
    public void checkHttpResponse(String responseName, Integer statusCode) throws EtiqetException {
        responseName = StringUtils.isNullOrEmpty(responseName) ? DEFAULT_MESSAGE_NAME : responseName;
        handlers.waitForResponseOfType(responseName, DEFAULT_CLIENT_NAME, statusCode.toString());
        handlers.removeDefaultSentMessage();
    }

    @Then("check that ?\"?([^\"]*)?\"? contains \"(\\S+)\"")
    public void checkHttpResponseForFields(String responseName, String responseFields) throws EtiqetException {
        responseName = StringUtils.isNullOrEmpty(responseName) ? DEFAULT_MESSAGE_NAME : responseName;
        handlers.checkFieldPresence(responseName, responseFields);
    }

    @Then("check that ?\"?([^\"]*)?\"? has \"(\\S+)\"")
    public void checkHttpResponseForValues(String responseName, String responseParams) throws EtiqetException {
        responseName = StringUtils.isNullOrEmpty(responseName) ? DEFAULT_MESSAGE_NAME : responseName;
        handlers.checkResponseKeyPresenceAndValue(responseName, responseParams);
    }

}
