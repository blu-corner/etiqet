package com.neueda.etiqet.fixture;

import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import com.neueda.etiqet.core.transform.Transformable;
import java.util.HashMap;
import java.util.Map;

public class FixEtiqetHandlers extends EtiqetHandlers {

  protected FlowParser flowParser = new FlowParser(new FlowParserDelegate() {
    Map<String, Object> params = new HashMap<>();

    @Override
    public Object getParam(String alias) {
      return params.getOrDefault(alias, null);
    }

    @Override
    public void addParam(String alias, Object param) {
      params.put(alias, param);
    }
  });

  protected Map<String, Transformable<String, String>> flows = new HashMap<>();

  /**
   * Creates a flow from a string formatted flow, and tag it with the given alias. Example of flow
   * could be described as: StageName(param1, param2, ...) -> StageName(param1, param2, ...)-> ...
   * Where StageName is an alias for the stage and param1, ... is the name of the parameter. Note
   * that composite stage are possible (e.g. Merge) where params could also be stages.
   *
   * @param formattedFlow the flow to be created.
   * @param alias the alias for the flow.
   */
  public void createFlow(String formattedFlow, String alias) throws EtiqetException {
    flows.put(alias, flowParser.createFlow(formattedFlow));
  }

  /**
   * Appends a flow, or creates a new one if alias does not exist, from a string formatted flow, and
   * tag it with the given alias. Example of flow could be described as: StageName(param1, param2,
   * ...) -> StageName(param1, param2, ...)-> ... Where StageName is an alias for the stage and
   * param1, ... is the name of the parameter. Note that composite stage are possible (e.g. Merge)
   * where params could also be stages.
   *
   * @param formattedFlow the flow to be created.
   * @param alias the alias for the flow.
   */
  public void appendFlow(String formattedFlow, String alias) throws EtiqetException {
    Transformable<String, String> flow = flowParser.createFlow(formattedFlow);
    Transformable<String, String> t = flows.putIfAbsent(alias, flow);
    if (t != null) {
      flow.append(t);
    }
  }

  /**
   * Returns the flow for a given alias or throw an exception if it does not exists.
   *
   * @param alias the alias of the flow.
   * @return the flow for a given alias or throw an exception if it does not exists.
   */
  public Transformable<String, String> getFlow(String alias) {
    return flows.getOrDefault(alias, null);
  }

  /**
   * Runs the flow associated to the given alias or throw an exception if it does not exists.
   *
   * @param alias the alias for the flow to run.
   * @throws EtiqetException propagated from client send operation.
   */
  public void runFlow(String alias) throws EtiqetException {
    // Check that flow exists
    Transformable<String, String> flow = flows.get(alias);
    if (flow != null) {
      // Register flow for replacement of test request message and sends a test request
      Transformable<String, String> f = flowParser.createFlow("Replacer(35, 1)");
      f.setNext(flow);
      FlowRunner fr = new FlowRunner(f);
      getClient(DEFAULT_CLIENT_NAME).setDelegate(fr);
      sendMessage("TestRequest", DEFAULT_CLIENT_NAME);
    }
  }

  /**
   * Remove the flow tagged with the given alias. If flow alias is not present it just ignores.
   *
   * @param alias the alias linked to the flow.
   */
  public void eraseFlow(String alias) {
    flows.remove(alias);
  }

  /**
   * Remove all the registered flows.
   */
  public void eraseAllFlow() {
    flows.clear();
  }
}
