package com.neueda.etiqet.fixture;

public interface FlowParserDelegate {

  /**
   * Returns the parameter for its given alias. This will be used by the parser to get information
   * about previously stored parameters.
   *
   * @param alias the alias name of the requested parameter.
   * @return the object instance associated to the alias or null if not found.
   */
  Object getParam(String alias);

  /**
   * Adds a parameter tagged with an alias. This will be invoked from the parser when a parameter
   * has been parsed in order to be stored. Note that getParam will be invoked to get the parameter
   * (see {@link #getParam(String)}) that were previously stored through this method.
   *
   * @param alias the alias to tag the parameter object.
   * @param param the parameter to be inserted.
   */
  void addParam(String alias, Object param);

}
