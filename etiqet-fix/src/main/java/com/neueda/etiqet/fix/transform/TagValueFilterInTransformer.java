package com.neueda.etiqet.fix.transform;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Processes the messages which tag values' are the same than the given ones.
 */
public class TagValueFilterInTransformer extends TagValueFilterTransformer {

  private static final Logger logger = LogManager.getLogger(TagValueFilterInTransformer.class);

  /**
   * Constructor.
   */
  public TagValueFilterInTransformer(String tag, String value) {
    super(tag, value);
  }

  @Override
  public String transform(String msg) {
    return findTagValue(msg).equals(value) ? super.transform(msg) : msg;
  }
}
