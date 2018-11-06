package com.neueda.etiqet.fixture;

import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import com.neueda.etiqet.core.transform.Transformable;
import com.neueda.etiqet.fix.transform.FixTransformer;
import java.util.ArrayList;

/**
 * This class is responsible for parsing flows in declared in text format.
 *
 * @see FlowParserDelegate
 */
class FlowParser {

  private static final String STAGE_SEPARATOR = "->";

  private FlowParserDelegate delegate;

  /**
   * Constructor.
   *
   * @param delegate delegate fo tthis arser.
   */
  public FlowParser(FlowParserDelegate delegate) {
    this.delegate = delegate;
  }

  /**
   * Extract the parameters of a given stage.
   *
   * @param parameters the parameters to be extracted.
   * @return the parameters of a given stage.
   */
  private Object[] extractParams(String parameters) {
    Object[] params = parameters.split(",");
    ArrayList<Object> normParams = new ArrayList<>(params.length);

    // Normalise params
    for (Object param : params) {
      // Expand variable
      String strParam = param.toString().trim();
      // Skip empty params
      if (!strParam.isEmpty()) {
        if (strParam.startsWith("${")) {
          String paramKey = strParam.substring(2, strParam.indexOf("}"));
          if (delegate != null) {
            normParams.add(delegate.getParam(paramKey));
          }
        } else {
          normParams.add(param);
          if (delegate != null) {
            delegate.addParam(strParam, param);
          }
        }
      }
    }

    return normParams.toArray();
  }

  /**
   * Creates a stage in a given flow from a given stage description. See examples below:<br>
   *
   * SeparatorNormaliser(^): Here the stage is the SeparatorNormaliser and the param is ^.
   * TagKeeper(8, 34, 52): Here the stage is TagKeeper and the parameters are 8, 34, 52.
   *
   * @param stage the stage description.
   */
  private FixTransformer createStage(String stage) throws EtiqetException {
    FixTransformer t = null;
    try {
      int paramsIdx = stage.indexOf("(");
      String transf =
          "com.neueda.etiqet.fix.transform." + stage.substring(0, paramsIdx) + "Transformer";
      Object[] params = extractParams(stage.substring(paramsIdx + 1, stage.indexOf(")")));
      Class<?> cl = Class.forName(transf);
      switch (params.length) {
        case 0: // Empty constructor
          t = (FixTransformer) cl.getConstructor().newInstance();
          break;
        case 1: // One parameter constructor
          t = (FixTransformer) cl.getConstructor(String.class).newInstance(params[0].toString());
          break;
        default: // Varargs constructor
          t = (FixTransformer) cl.getConstructor(params.getClass()).newInstance((Object) params);
          break;
      }
    } catch (Exception e) {
      throw new EtiqetException("Cannot create stage. Reason: " + e.getMessage());
    }
    return t;
  }

  /**
   * Creates a flow from a string formatted flow, and tag it with the given alias. Example of flow
   * could be described as: StageName(param1, param2, ...) -> StageName(param1, param2, ...)-> ...
   * Where StageName is an alias for the stage and param1, ... is the name of the parameter. Note
   * that composite stage are possible (e.g. Merge) where params could also be stages.
   *
   * @param formattedFlow stages in a string format.
   */
  public Transformable<String, String> createFlow(String formattedFlow) throws EtiqetException {
    String[] stages = formattedFlow.split(STAGE_SEPARATOR);
    Transformable<String, String> flow = createStage(stages[0]);
    for (int idx = 1; idx < stages.length; idx++) {
      flow.append(createStage(stages[idx]));
    }
    return flow;
  }


}
