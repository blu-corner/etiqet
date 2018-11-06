package com.neueda.etiqet.fix.transform;

import com.neueda.etiqet.core.transform.Transformable;
import com.neueda.etiqet.fix.message.FIXUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Calculates the length of the message and replace it on the transformed message.
 */
public class LengthTransformer extends FixTransformer {

  private static final Logger logger = LogManager.getLogger(LengthTransformer.class);

  /**
   * Constructor.
   */
  public LengthTransformer() {
    super();
  }

  /**
   * Constructor.
   *
   * @param next the next transformer in the chain to be processed.
   */
  public LengthTransformer(Transformable<String, String> next) {
    super(next);
  }

  @Override
  public String transform(String msg) {
    logger.debug("Calculating the length of the message: " + msg);
    int endOfLengthTag = msg.indexOf(FIXUtils.SOH_STR, msg.indexOf(FIXUtils.SOH_STR) + 1);
    int length = msg.lastIndexOf(FIXUtils.SOH_STR, msg.length() - 2) - endOfLengthTag;
    String[] superHeader = msg.substring(0, endOfLengthTag).split(FIXUtils.SOH_STR);
    String result =
        superHeader[0] + FIXUtils.SOH_STR + "9=" + length + msg.substring(endOfLengthTag);
    return super.transform(result);
  }
}
