package com.neueda.etiqet.fix.transform;

import com.neueda.etiqet.core.transform.Transformable;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChecksumTransformer extends FixTransformer {

  private static final Logger logger = LogManager.getLogger(ChecksumTransformer.class);

  /**
   * Calculates and overrides the checksum for the message to transform.
   */
  public ChecksumTransformer() {
    this(null);
  }

  /**
   * Constructor.
   *
   * @param next the next transformer in the chain to be processed.
   */
  public ChecksumTransformer(Transformable<String, String> next) {
    super(next);
  }

  @Override
  public String transform(String msg) {
    int checksum = msg.substring(0, msg.length() - 7).chars().sum() & 0xFF;
    logger.debug("Overriding checksum tag with [" + checksum + "] for message [" + msg + "]");
    return super.transform(
        msg.substring(0, msg.length() - 4) + StringUtils.leftPad("" + checksum, 3, "0") + "\u0001");
  }
}
