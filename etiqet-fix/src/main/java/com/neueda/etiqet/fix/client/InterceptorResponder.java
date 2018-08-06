package com.neueda.etiqet.fix.client;

import com.neueda.etiqet.core.client.delegate.ClientDelegate;
import com.neueda.etiqet.core.common.exceptions.StopEncodingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import quickfix.Message;
import quickfix.Responder;

/**
 * This class follows the decorator pattern to intercept and process messages before being sent to the server.
 */
public class InterceptorResponder implements Responder{
    private static final Logger LOG = LogManager.getLogger(InterceptorResponder.class);

    private Responder responder;
    private ClientDelegate<Message, String> delegate;

    /**
     * Constructor.
     * @param responder the responder to be decorated.
     * @param delegate the delegate handling the interception of the sending.
     */
    InterceptorResponder(Responder responder, ClientDelegate<Message, String> delegate) {
        this.responder = responder;
        this.delegate = delegate;
    }

    @Override
    public boolean send(String data) {
        try {
            return responder.send(delegate.transformAfterEncoding(data));
        } catch (StopEncodingException e) {
            LOG.error(e.getMessage());
        }
        return false;
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
