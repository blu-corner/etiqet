package com.neueda.etiqet.transport.qfj;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.Responder;

/**
 * This class follows the decorator pattern to intercept and process messages before being sent to the server.
 */
public class InterceptorResponder implements Responder {

    private static final Logger LOG = LoggerFactory.getLogger(InterceptorResponder.class);

    private Responder responder;

    /**
     * Constructor.
     *
     * @param responder the responder to be decorated.
     */
    InterceptorResponder(Responder responder) {
        this.responder = responder;
    }

    @Override
    public boolean send(String data) {
        try {
            return responder.send(data);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return false;
        }
    }

    @Override
    public void disconnect() {
        responder.disconnect();
    }

    @Override
    public String getRemoteAddress() {
        return responder.getRemoteAddress();
    }
}
