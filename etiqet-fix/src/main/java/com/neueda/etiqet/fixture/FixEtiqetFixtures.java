package com.neueda.etiqet.fixture;

import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertNotNull;

import cucumber.api.java.en.Then;

/**
 * Fixtures specific for fix
 */
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

  @Then("^check flow \"([^\"]*)\" exists$")
  public void checkFlowExists(String alias) throws Throwable {
    assertNotNull(handlers.getFlow(alias));
  }

  @Then("^check flow \"([^\"]*)\" does not exist$")
  public void checkFlowDoesNotExist(String alias) throws Throwable {
    // Check that flows does not exist
    assertNull(handlers.getFlow(alias));
  }

  @Then("^send raw message through \"([^\"]*)\"$")
  public void sendRawMessageThroughFlow(String alias) throws Throwable {
    // Run the flow and send it
    handlers.runFlow(alias);
  }

  @Then("^erase flow \"([^\"]*)\"$")
  public void eraseFlow(String alias) throws Throwable {
    handlers.eraseFlow(alias);
  }

  @Then("^erase all flows$")
  public void eraseAllFlows() throws Throwable {
    handlers.eraseAllFlow();
  }
}
