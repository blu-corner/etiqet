package com.neueda.etiqet.fix.transform;

import com.neueda.etiqet.core.transform.Transformable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class TagTransformer extends FixTransformer {

    private static final Logger logger = LogManager.getLogger(TagTransformer.class);
    protected final Set<String> tags;

    /**
     * Constructor.
     *
     * @param tags the tags to be cleaned or maintained in the message.
     */
    public TagTransformer(String... tags) {
        this(null, tags);
    }

    /**
     * Constructor.
     *
     * @param next the next transformer in the chain to be processed.
     * @param tags the tags to be cleaned or maintained in the message.
     */
    public TagTransformer(Transformable<String, String> next, String... tags) {
        super(next);
        this.tags = new HashSet<>(Arrays.asList(tags));
    }

    /**
     * Add tags to the list of tags to discard or keep.
     *
     * @param tags the list of tags to discard or keep.
     */
    public void addTags(String... tags) {
        this.tags.addAll(Arrays.asList(tags));
    }

    /**
     * Removes the tags from the list of tags currently considered to discard or keep.
     *
     * @param tags the list of tags to discard or keep.
     */
    public void removeTags(String... tags) {
        this.tags.removeAll(Arrays.asList(tags));
    }

}
