package com.neueda.etiqet.fix.transform;

import com.neueda.etiqet.fix.message.FIXUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * This transformer keeps all the tags from the message for the given list of tags.
 */
public class TagKeeperTransformer extends TagTransformer {

    private static final Logger logger = LogManager.getLogger(TagKeeperTransformer.class);

    public TagKeeperTransformer(Object... tags) {
        super(Arrays.stream(tags).map(Object::toString).collect(Collectors.joining()));
    }

    @Override
    public String transform(String msg) {
        StringBuilder sb = new StringBuilder();
        for (String tuple : msg.split(FIXUtils.SOH_STR)) {
            if (tags.contains(tuple.split("=")[0])) {
                sb.append(tuple.trim()).append(FIXUtils.SOH_STR);
            }
        }
        String trans = sb.toString();
        logger.debug(
                "Keeping fields {" + String.join(",", tags) + "] from [" + msg + "] -> [" + trans + "]");
        return super.transform(trans);
    }
}
