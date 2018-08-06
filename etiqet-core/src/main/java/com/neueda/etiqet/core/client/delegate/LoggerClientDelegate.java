package com.neueda.etiqet.core.client.delegate;

import com.neueda.etiqet.core.common.cdr.Cdr;
import com.neueda.etiqet.core.common.exceptions.StopEncodingException;
import com.neueda.etiqet.core.common.exceptions.StopStringDecodingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Client that logs the message by using toString method.
 * @param <U> The class containing the unmarshalled message.
 * @param <M> The class containing the marshalled message.
 */
public class LoggerClientDelegate<U, M> extends BaseClientDelegate<U, M> {
    private static final Logger LOG = LogManager.getLogger(LoggerClientDelegate.class);

    /**
     * Constructor.
     */
    public LoggerClientDelegate() {
        super();
    }

    /**
     * Constructor.
     * @param next the next delegate on the chain to process the message.
     */
    public LoggerClientDelegate(ClientDelegate<U, M> next) {
        super(next);
    }

    @Override
    public Cdr transformBeforeSendMessage(Cdr msg) {
        LOG.info("transformBeforeSendUnmarshalled received for message: " + msg.toString());
        return super.transformBeforeSendMessage(msg);
    }

    @Override
    public Cdr transformAfterReceiveMessage(Cdr msg) {
        LOG.info("transformAfterReceiveUnmarshalled received for message: " + msg.toString());
        return super.transformAfterReceiveMessage(msg);
    }

    @Override
    public U transformBeforeEncoding(U msg) throws StopEncodingException {
        LOG.info("transformBeforeEncoding received for message: " + msg.toString());
        return super.transformBeforeEncoding(msg);
    }

    @Override
    public M transformAfterEncoding(M msg) throws StopEncodingException {
        LOG.info("transformAfterEncoding received for message: " + msg);
        return super.transformAfterEncoding(msg);
    }

    @Override
    public M transformBeforeDecoding(M msg) throws StopStringDecodingException {
        LOG.info("transformBeforeDecoding received for message: " + msg);
        return super.transformBeforeDecoding(msg);
    }

    @Override
    public U transformAfterDecoding(U msg) throws StopStringDecodingException {
        LOG.info("transformAfterDecoding received for message: " + msg.toString());
        return super.transformAfterDecoding(msg);
    }
}
