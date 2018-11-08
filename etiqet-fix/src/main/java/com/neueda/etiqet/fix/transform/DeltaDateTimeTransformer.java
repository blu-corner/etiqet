package com.neueda.etiqet.fix.transform;

import com.neueda.etiqet.core.transform.Transformable;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Replaces the values for the given tags at the transformed message.
 */
public class DeltaDateTimeTransformer extends FixTransformer {

  private static final Logger logger = LogManager.getLogger(DeltaDateTimeTransformer.class);
  private final String tag;
  private final SimpleDateFormat formatter;
  private Instant previous;

  /**
   * Constructor.
   */
  public DeltaDateTimeTransformer() {
    this("54");
  }

  /**
   * Constructor.
   *
   * @param tag the tag containing the date time value to be considered for the delta operation.
   */
  public DeltaDateTimeTransformer(String tag) {
    this(tag, null);
  }

  /**
   * Constructor.
   *
   * @param tag the tag containing the date time value to be considered for the delta operation.
   * @param next the next transformer in the chain to be processed.
   */
  public DeltaDateTimeTransformer(String tag, Transformable<String, String> next) {
    super(new DelayedTransformer(next));
    this.tag = tag;
    this.previous = null;
    this.formatter = new SimpleDateFormat("yyyyMMdd-HH:mm:ss.SSS");
  }

  @Override
  public String transform(String msg) {
    DelayedTransformer dt = find(DelayedTransformer.class);
    if (dt != null) {
      dt.setMillis(calculateDelay(msg));
    }
    return super.transform(msg);
  }

  /**
   *
   * @param msg
   * @return
   */
  private long calculateDelay(String msg) {
    long delay = 0;
    Instant current = null;
    try {
      current = formatter.parse(getTagValue(tag, msg)).toInstant();
      if ((previous != null) && (previous != current)) {
        delay = Duration.between(previous, current).toMillis();
      }
    } catch (Exception e) {
      logger.error("Could not calculate the delta. Reason: " + e.getMessage());
    }
    previous = current;
    return delay;
  }
}
