package com.neueda.etiqet.fix.transform;

import com.neueda.etiqet.core.transform.Transformable;
import com.neueda.etiqet.fix.message.FIXUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * Replaces the values for the given tags at the transformed message.
 */
public class TagValueReplacerTransformer extends FixTransformer {

    private static final Logger logger = LogManager.getLogger(TagValueReplacerTransformer.class);

    protected Map<String, String> fieldsToReplace = new HashMap<>();

    /**
     * Constructor.
     */
    public TagValueReplacerTransformer() {
        this(null);
    }

    /**
     * Constructor.
     */
    public TagValueReplacerTransformer(String msg) {
        this(null, msg);
    }

    /**
     * Constructor.
     *
     * @param next the next transformer in the chain to be processed.
     */
    public TagValueReplacerTransformer(Transformable<String, String> next, String msg) {
        super(next);
        addValuesFromMessage(msg);
    }

    /**
     * Adds a tag and value tuple to be the replacement when processing the message.
     *
     * @param tag   the tag to identify the one in the message for replacement.
     * @param value the value to replace with in the message.
     */
    public void addField(String tag, String value) {
        fieldsToReplace.put(FIXUtils.SOH_STR + tag + "=", value);
    }

    /**
     * Add values from a given FIX mesasge.
     *
     * @param msg message to extract the tags and values for the replacement.
     */
    public void addValuesFromMessage(String msg) {
        if (msg != null) {
            for (String tuple : msg.split(FIXUtils.SOH_STR)) {
                String[] tagValue = tuple.split(FIXUtils.TAG_VALUE_SEPARATOR);
                if (tagValue.length == 2) {
                    addField(tagValue[0], tagValue[1]);
                } else {
                    logger.debug("Wrong tag value at addValuesFromMessage method [" + tuple + "]");
                }
            }
        }
    }

    @Override
    public String transform(String msg) {
        final StringBuilder replaced = new StringBuilder(msg);
        fieldsToReplace.forEach((tagLookup, v) -> {
            int startIdx = replaced.indexOf(tagLookup);
            if (startIdx >= 0) {
                int endIdx = replaced.indexOf(FIXUtils.SOH_STR, startIdx + 1);
                if (endIdx > startIdx) {
                    String tmp = replaced.toString();
                    replaced.setLength(0);
                    replaced.append(tmp, 0, startIdx).append(tagLookup).append(v)
                            .append(tmp.substring(endIdx));
                } else {
                    logger.error("Cannot find termination field for tag " + tagLookup
                            .substring(1, tagLookup.length() - 1));
                }
            } else {
                logger.debug("Cannot replace field. Reason tag " +
                        tagLookup.substring(1, tagLookup.length() - 1) + " is not present.");
            }
        });
        return super.transform(replaced.toString());
    }
}
