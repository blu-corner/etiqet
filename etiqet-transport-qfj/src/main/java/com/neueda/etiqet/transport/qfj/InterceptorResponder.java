package com.neueda.etiqet.transport.qfj;

import com.neueda.etiqet.core.client.delegate.ClientDelegate;
import com.neueda.etiqet.core.message.cdr.Cdr;
import com.neueda.etiqet.core.transport.Codec;
import java.util.ArrayDeque;
import java.util.Deque;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.Message;
import quickfix.Responder;

/**
 * This class follows the decorator pattern to intercept and process messages before being sent to the server.
 */
public class InterceptorResponder implements Responder {

    private static final Logger LOG = LoggerFactory.getLogger(InterceptorResponder.class);

    private Responder responder;
    private Deque<ClientDelegate> delegates;
    private Codec<Cdr, Message> codec;

    /**
     * Constructor.
     *
     * @param responder the responder to be decorated.
     * @param delegate the delegate handling the interception of the sending.
     */
    InterceptorResponder(Responder responder, ClientDelegate delegate, Codec<Cdr, Message> codec) {
        this.responder = responder;
        this.delegates = new ArrayDeque<>();
        delegates.add(delegate);
        this.codec = codec;
    }

    public void addDelegate(ClientDelegate clientDelegate) {
        delegates.push(clientDelegate);
    }

    @Override
    public boolean send(String data) {
        try {
            ClientDelegate del = delegates.pop();
            boolean result = responder
                .send(codec.encode(del.processMessage(codec.decode(new Message(data)))).toString());
            if (delegates.isEmpty()) {
                delegates.push(del);
            }
            return result;
        } catch (Exception e) {
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
