package com.neueda.etiqet.fix.transform;

import com.neueda.etiqet.core.transform.Transformable;
import com.neueda.etiqet.fix.message.FIXUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Replaces the values for the given tags at the transformed message.
 */
public class TagValueFilterTransformer extends FixTransformer {

    private static final Logger logger = LogManager.getLogger(TagValueFilterTransformer.class);

    protected final String tagLookup;
    protected final String value;

    /**
     * Constructor.
     *
     * @param tag   the tag to be considered for the filtering operation.
     * @param value the value to be considered for the filtering operation.
     */
    public TagValueFilterTransformer(String tag, String value) {
        this(null, tag, value);
    }

    /**
     * Constructor.
     *
     * @param next  the next transformer in the chain to be processed.
     * @param tag   the tag to be considered for the filtering operation.
     * @param value the value to be considered for the filtering operation.
     */
    public TagValueFilterTransformer(Transformable<String, String> next, String tag, String value) {
        super(next);
        this.tagLookup = FIXUtils.SOH_STR + tag + FIXUtils.TAG_VALUE_SEPARATOR;
        this.value = value;
    }

    protected String findTagValue(String msg) {
        return msg.substring(msg.indexOf(tagLookup) + tagLookup.length()).split(FIXUtils.SOH_STR)[0];
    }
}
