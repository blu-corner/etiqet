package com.neueda.etiqet.fixture;

public interface FlowParserDelegate {

  /**
   * Returns the parameter for its given alias.
   *
   * @param alias the alias name of the requested parameter.
   * @return the object instance associated to the alias or null if not found.
   */
  Object getParam(String alias);
  void addParam(String alias, Object param);

}
