package com.neueda.etiqet.websocket.client;

import com.neueda.etiqet.core.common.cdr.Cdr;
import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import com.neueda.etiqet.core.common.exceptions.EtiqetRuntimeException;
import com.neueda.etiqet.core.json.JsonUtils;
import com.neueda.etiqet.core.util.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.StatusCode;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import java.util.concurrent.BlockingQueue;


@WebSocket(maxTextMessageSize = 64 * 1024)
public class WebSocketSession
{
    private static final Logger LOG = LogManager.getLogger(WebSocketSession.class);

    private Session session;

    private Boolean connected;

    private BlockingQueue<Cdr> msgQueue;

    public WebSocketSession(BlockingQueue<Cdr> msgQueue)
    {
        this.connected = false;
        this.msgQueue = msgQueue;
    }

    @OnWebSocketConnect
    public void onConnect(Session session)
    {
        this.session = session;
        this.connected = true;
    }

    public boolean getConnected() {
        return this.connected;
    }

    public void close() throws EtiqetException {
        try
        {
            session.close(StatusCode.NORMAL, "Closing");
        }
        catch (Throwable t)
        {
            t.printStackTrace();
            throw new EtiqetException("Failed to close websocket connection");
        }
    }

    @OnWebSocketMessage
    public void onMessage(String msg)
    {
        LOG.info("Exchange message: " + msg);
        receiveMsg(this.msgQueue, msg);
    }

    private void receiveMsg(BlockingQueue<Cdr> queue, String msg) {
        try {
            Cdr responseData;
            if(StringUtils.isNullOrEmpty(msg)) {
                responseData = new Cdr(Cdr.class.getName());
            } else {
                Cdr parsedData = JsonUtils.jsonToCdr(msg);
                if (parsedData.containsKey("MessageName")) {
                    responseData = new Cdr(parsedData.getAsString("MessageName"));
                    responseData.update(parsedData);
                } else {
                    responseData = new Cdr(Cdr.class.getName());
                }
            }
            queue.add(responseData);
        } catch (Exception e) {
            throw new EtiqetRuntimeException(e);
        }
    }
}
