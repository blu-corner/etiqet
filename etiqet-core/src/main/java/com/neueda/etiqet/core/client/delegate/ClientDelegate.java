package com.neueda.etiqet.core.client.delegate;

import com.neueda.etiqet.core.common.cdr.Cdr;
import com.neueda.etiqet.core.common.exceptions.StopEncodingException;
import com.neueda.etiqet.core.common.exceptions.StopStringDecodingException;

/**
 * This interface delegates the actions to be customised by the handler at application layer.
 * @param <U> This is the class representing an unmarshalled message.
 * @param <M> This is the class representing an marshalled message.
 */
public interface ClientDelegate<U, M> {

    /**
     * Set the next delegate in the chain to process the messages.
     * @param next next to delegate in the chain to process the messages.
     */
    void setNextDelegate(ClientDelegate<U, M> next);

    /**
     * Iterates on the chain of delegates and returns the first matching the given class.
     * @param delegateClass the delegate to find in the chain.
     * @return the delegate matching the given class or null if not found.
     */
    ClientDelegate<U, M> findDelegate(Class<? extends ClientDelegate<U, M>> delegateClass);

    /**
     * This must be invoked in order to transform the messages before the sending the message to the server.
     * @param msg the generic message to be transformed.
     * @return the transformed message.
     */
    Cdr transformBeforeSendMessage(Cdr msg);

    /**
     * This must be invoked in order to transform the messages after the message is received from the server.
     * @param msg the generic message to be transformed.
     * @return the transformed message.
     */
    Cdr transformAfterReceiveMessage(Cdr msg);

    /**
     * This must be invoked just before marshalling of message is done to provide a way to intercept
     * and process the message before it is sent to the server.
     * @param msg the unmarshalled message
     * @throws StopEncodingException if the handling of the message was not performed
     */
    U transformBeforeEncoding(U msg) throws StopEncodingException;

    /**
     * This must be invoked just after marshalling of message is done to provide a way to intercept
     * and process the message before it is sent to the server.
     * @param msg the unmarshalled message
     * @throws StopEncodingException if the handling of the message was not performed
     */
    M transformAfterEncoding(M msg) throws StopEncodingException;

    /**
     * This must be invoked just before marshalling of message is done to provide a way to intercept
     * and process the message before it is sent to the server.
     * @param msg the unmarshalled message
     * @throws StopStringDecodingException if the handling of the message was not performed
     */
    M transformBeforeDecoding(M msg) throws StopStringDecodingException;

    /**
     * This must be invoked just before marshalling of message is done to provide a way to intercept
     * and process the message before it is sent to the server.
     * @param msg the unmarshalled message
     * @throws StopStringDecodingException if the handling of the message was not performed
     */
    U transformAfterDecoding(U msg) throws StopStringDecodingException;
}
