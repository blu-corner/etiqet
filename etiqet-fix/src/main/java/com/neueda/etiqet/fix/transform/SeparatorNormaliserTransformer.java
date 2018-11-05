package com.neueda.etiqet.fix.transform;

import com.neueda.etiqet.core.transform.Transformable;
import com.neueda.etiqet.fix.message.FIXUtils;

/**
 * Class normalising the separator of a message.
 */
public class SeparatorNormaliserTransformer extends FixTransformer {

    private final String tagSeparator;

    /**
     * Constructor.
     */
    public SeparatorNormaliserTransformer() {
        this(null, "\\^A");
    }

    /**
     * Constructor.
     */
    public SeparatorNormaliserTransformer(String separator) {
        this(null, separator);
    }

    /**
     * Constructor.
     *
     * @param next      the next transformer in the chain to be processed.
     * @param separator the separator to be replaced by the FIX message one.
     */
    public SeparatorNormaliserTransformer(Transformable<String, String> next, String separator) {
        super(next);
        tagSeparator = separator;
    }

    @Override
    public String transform(String msg) {
        return super.transform(msg.replace(tagSeparator, FIXUtils.SOH_STR));
    }
}
