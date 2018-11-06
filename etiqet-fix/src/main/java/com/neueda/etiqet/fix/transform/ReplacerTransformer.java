package com.neueda.etiqet.fix.transform;

import com.neueda.etiqet.core.transform.Transformable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ReplacerTransformer extends FixTransformer {

  private static final Logger logger = LogManager.getLogger(ReplacerTransformer.class);
  private final String msg;

  /**
   * Constructor.
   *
   * @param msg the message to replace with the transformation.
   */
  public ReplacerTransformer(String msg) {
    this(null, msg);
  }


  /**
   * Constructor.
   *
   * @param next the next transformer in the chain to be processed.
   * @param msg the message to replace with the transformation.
   */
  public ReplacerTransformer(Transformable<String, String> next, String msg) {
    super(next);
    this.msg = msg;
  }

  @Override
  public String transform(String msg) {
    logger.debug("Replacing msg [" + msg + "] by [" + this.msg + "]");
    return super.transform(this.msg);
  }
}
