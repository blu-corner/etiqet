package com.neueda.etiqet.fix.transform;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Processes the messages which tag values' are different from the given ones.
 */
public class TagValueFilterOutTransformer extends TagValueFilterTransformer {

  private static final Logger logger = LogManager.getLogger(TagValueFilterOutTransformer.class);

  /**
   * Constructor.
   */
  public TagValueFilterOutTransformer(String tag, String value) {
    super(tag, value);
  }

  @Override
  public String transform(String msg) {
    return findTagValue(msg).equals(value) ? msg : super.transform(msg);
  }
}
