package com.neueda.etiqet.fix.transform;

import com.neueda.etiqet.fix.message.FIXUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This transformer removes all of the tags from the message for the given list of tags.
 */
public class TagCleanerTransformer extends TagTransformer {

  private static final Logger logger = LogManager.getLogger(TagCleanerTransformer.class);

  @Override
  public String transform(String msg) {
    StringBuilder sb = new StringBuilder();
    for (String tuple : msg.split(FIXUtils.SOH_STR)) {
      if (!tags.contains(tuple.split("=")[0])) {
        sb.append(tuple.trim()).append(FIXUtils.SOH_STR);
      }
    }
    String trans = sb.toString();
    logger.debug(
        "Cleaning fields {" + String.join(",", tags) + "] from [" + msg + "] -> [" + trans + "]");
    return super.transform(trans);
  }
}
