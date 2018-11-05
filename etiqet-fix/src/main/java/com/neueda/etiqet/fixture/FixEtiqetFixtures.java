package com.neueda.etiqet.fixture;

import cucumber.api.PendingException;
import cucumber.api.java.en.Then;

import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class FixEtiqetFixtures {

  private final FixEtiqetHandlers handlers;

  /**
   * Default constructor.
   */
  public FixEtiqetFixtures(FixEtiqetHandlers handlers) {
    this.handlers = handlers;
  }

  @Then("^create flow \"([^\"]*)\" as \"([^\"]*)\"$")
  public void createAFlowWithStagesAs(String flow, String alias) throws Throwable {
    // Create the flow
    handlers.createFlow(flow, alias);
  }

  @Then("^check flow \"([^\"]*)\"$")
  public void checkFlow(String alias) throws Throwable {
    // Write code here that turns the phrase above into concrete actions
    assertNotNull(handlers.getFlow(alias));
  }

  @Then("^check flow \"([^\"]*)\" does not exist$")
  public void checkFlowDoesNotExist(String alias) throws Throwable {
    // Write code here that turns the phrase above into concrete actions
    assertNull(handlers.getFlow(alias));
  }

  @Then("^send raw message from flow \"([^\"]*)\"$")
  public void sendRawMessageThroughFlow(String alias) throws Throwable {
    // Run the flow and send it
    handlers.runFlow(alias);
  }

}
