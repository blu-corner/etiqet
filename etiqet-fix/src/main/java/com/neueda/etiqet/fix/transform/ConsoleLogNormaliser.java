package com.neueda.etiqet.fix.transform;

import com.neueda.etiqet.core.transform.Transformable;
import com.neueda.etiqet.fix.message.FIXUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class normalises a console log fix into a fix message.
 */
public class ConsoleLogNormaliser extends FixTransformer {

  private static final Logger logger = LogManager.getLogger(ConsoleLogNormaliser.class);

  /**
   * Constructor.
   *
   * @param next the next transformer in the chain to be processed.
   */
  public ConsoleLogNormaliser(Transformable<String, String> next) {
    super(next);
  }

  @Override
  public String transform(String msg) {
    StringBuilder sb = new StringBuilder();
    for (String tuple : msg.split(FIXUtils.LOG_SEPARATOR)) {
      tuple = tuple.trim();
      int endOfTag = tuple.indexOf(")");
      sb.append(tuple, tuple.indexOf("(") + 1, endOfTag)
          .append(tuple.substring(endOfTag + 1))
          .append(FIXUtils.SOH_STR);
    }
    String trans = sb.toString();
    logger.debug("Normalising message [" + msg + "] into [" + trans + "]");
    return super.transform(trans);
  }
}
