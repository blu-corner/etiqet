package com.neueda.etiqet.websocket.fixture;

import com.neueda.etiqet.fixture.EtiqetHandlers;
import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import cucumber.api.java.en.Then;

public class WebSocketFixtures {

    private final EtiqetHandlers handlers;

    public WebSocketFixtures(EtiqetHandlers handlers) {
        this.handlers = handlers;
    }

    @Then("^wait for \"([^\"]*)\" to receive a websocket message ? \"([^\"]*)\\\" as \"([^\"]*)\"$")
    public void namedClientExchangeResponse(String clientName, String t, String responseName) throws EtiqetException {
        if (EtiqetHandlers.RESPONSE.equals(t))
            handlers.waitForResponse(responseName, clientName);
        else
            handlers.waitForResponseOfType(responseName, clientName, t, 10000, true);
    }
}
