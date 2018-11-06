package com.neueda.etiqet.fix.transform;

import com.neueda.etiqet.core.transform.Transformable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class to merge several transformers into one.
 */
public class MergerTransformer extends FixTransformer {

  private static final Logger logger = LogManager.getLogger(MergerTransformer.class);

  private final Set<Transformable<String, String>> transformers = new HashSet<>();

  /**
   * Constructor.
   *
   * @param transformers list of transformers to be merged.
   */
  public MergerTransformer(Object... transformers) {
    super(null);
    this.transformers.addAll(Arrays.stream(transformers)
        .filter(obj -> obj instanceof FixTransformer)
        .map(obj -> (FixTransformer) obj).collect(Collectors.toList()));
  }


  /**
   * Constructor.
   *
   * @param next the next transformer in the chain to be processed.
   * @param transformers list of transformers to be merged.
   */
  public MergerTransformer(Transformable<String, String> next, FixTransformer... transformers) {
    super(next);
    this.transformers.addAll(Arrays.asList(transformers));
  }

  @Override
  public String transform(String msg) {
    logger.debug("Merging messages");
    TagValueReplacerTransformer r = new TagValueReplacerTransformer();
    transformers.stream().forEach(t -> r.addValuesFromMessage(t.transform(msg)));
    return super.transform(r.transform(msg));
  }

  @Override
  public Transformable<String, String> find(Class<? extends Transformable<String, String>> trans) {
    return transformers.stream().filter(t -> t.find(trans) != null).findFirst().orElse(null);
  }
}
