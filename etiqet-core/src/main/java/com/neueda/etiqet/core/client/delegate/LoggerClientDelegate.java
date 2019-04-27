package com.neueda.etiqet.core.client.delegate;

import com.neueda.etiqet.core.message.cdr.Cdr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Client that logs the message by using toString method.
 */
public class LoggerClientDelegate extends BaseClientDelegate {

    private static final Logger logger = LoggerFactory.getLogger(LoggerClientDelegate.class);

    /**
     * Constructor.
     */
    public LoggerClientDelegate() {
        super();
    }

    /**
     * Constructor.
     *
     * @param next the next delegate on the chain to process the message.
     */
    public LoggerClientDelegate(ClientDelegate next) {
        super(next);
    }

    @Override
    public Cdr processMessage(Cdr msg) {
        logger.info("processMessage received for message: " + msg.toString());
        return super.processMessage(msg);
    }
}
