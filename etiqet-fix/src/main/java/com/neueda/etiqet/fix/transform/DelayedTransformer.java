package com.neueda.etiqet.fix.transform;

import com.neueda.etiqet.core.transform.Transformable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Replaces the values for the given tags at the transformed message.
 */
public class DelayedTransformer extends FixTransformer {

  private static final Logger logger = LogManager.getLogger(DelayedTransformer.class);
  private long millis;

  /**
   * Constructor.
   */
  public DelayedTransformer() {
    this(0);
  }

  /**
   * Constructor.
   *
   * @param millis the millis in milliseconds to be applied to the transformation.
   */
  public DelayedTransformer(int millis) {
    this(millis, null);
  }

  /**
   * Constructor.
   *
   * @param next the next transformer in the chain to be processed.
   */
  public DelayedTransformer(Transformable<String, String> next) {
    this(0, null);
  }

  /**
   * Constructor.
   *
   * @param millis the millis in milliseconds to be applied to the transformation.
   * @param next the next transformer in the chain to be processed.
   */
  public DelayedTransformer(int millis, Transformable<String, String> next) {
    super(next);
    this.millis = millis;
  }

  /**
   * Adds a tag and value tuple to be the replacement when processing the message.
   *
   * @param millis the millis in milliseconds to be applied to the transformation.
   */
  public void setMillis(long millis) {
    this.millis = millis;
  }

  @Override
  public String transform(String msg) {
    try {
      logger.debug("Delaying transformation " + millis + " ms.");
      Thread.sleep(millis);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return super.transform(msg);
  }
}
