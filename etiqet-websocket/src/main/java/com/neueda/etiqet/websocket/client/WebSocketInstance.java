package com.neueda.etiqet.websocket.client;

import com.neueda.etiqet.core.common.cdr.Cdr;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;

import java.net.URI;
import java.util.concurrent.BlockingQueue;

public class WebSocketInstance implements Runnable {

    private String url;

    private BlockingQueue<Cdr> msgQueue;

    public WebSocketInstance(String url, BlockingQueue<Cdr> msgQueue)
    {
        this.url = url;
        this.msgQueue = msgQueue;
    }

    public void run() {
        try {
            org.eclipse.jetty.websocket.client.WebSocketClient client = new org.eclipse.jetty.websocket.client.WebSocketClient();
            WebSocketSession instance = new WebSocketSession(this.msgQueue);
            client.start();
            ClientUpgradeRequest request = new ClientUpgradeRequest();
            client.connect(instance, new URI(this.url), request);
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
    }
}
