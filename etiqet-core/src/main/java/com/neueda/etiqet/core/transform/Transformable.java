package com.neueda.etiqet.core.transform;

import java.util.List;

public interface Transformable<Source, Target> {

    /**
     * Returns the next transformable object in the chain.
     *
     * @return the next transformable object in the chain.
     */
    Transformable<Source, Target> getNext();

    /**
     * Returns the latest transformable object in the chain.
     *
     * @return the latest transformable object in the chain.
     */
    Transformable<Source, Target> getLast();

    /**
     * Sets the next transformable object in the chain.
     *
     * @param next the next transformable object in the chain.
     */
    void setNext(Transformable<Source, Target> next);

    /**
     * Appends a transformable object at the end of the chain.
     *
     * @param last the object to be placed at the end of the chain.
     */
    void append(Transformable<Source, Target> last);

    /**
     * Inserts a transformable object between this and the next in the chain.
     *
     * @param next the object to be placed in between this and the next in the chain.
     */
    void push(Transformable<Source, Target> next);

    /**
     * Finds a processable element in the chain of transformers for the given class.
     *
     * @param transClass the transformer class to be search into the chain.
     * @return the transformable instance matching the class name or null if not found.
     */
    <C extends Transformable<Source, Target>> C find(Class<C> transClass);

    /**
     * Returns the in order sequence of transformations applied by this chain.
     *
     * @return
     */
    List<Transformable<Source, Target>> getChain();

    /**
     * Message transforms a Source message into a Target one.
     * Notice that Source and target could be the same types and thus transformation could mean
     * just to transform the message into a different one, or even leave it the same.
     *
     * @param msg the message to be transformed.
     * @return the transformed message.
     */
    Target transform(Source msg);
}
