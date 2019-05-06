package com.neueda.etiqet.websocket;

import com.neueda.etiqet.core.config.annotations.Configuration;
import com.neueda.etiqet.core.config.annotations.EtiqetProtocol;
import com.neueda.etiqet.core.config.dtos.Client;
import com.neueda.etiqet.core.transport.ConsoleTransport;
import com.neueda.etiqet.core.transport.ToStringCodec;
import com.neueda.etiqet.websocket.client.WebSocketClient;

@Configuration
public class WebSocketConfiguration {

    @EtiqetProtocol("websocket")
    public com.neueda.etiqet.core.config.dtos.Protocol getProtocol() {
        com.neueda.etiqet.core.config.dtos.Protocol protocol = new com.neueda.etiqet.core.config.dtos.Protocol();
        protocol.setClient(getWebSocketClient());
        return protocol;
    }

    private Client getWebSocketClient() {
        Client client = new Client();
        client.setImplementationClass(WebSocketClient.class);
        client.setDefaultConfig(getClass().getClassLoader().getResource("config/client.cfg").getPath());
        client.setTransportImpl(ConsoleTransport.class.getName());
        client.setCodecImpl(ToStringCodec.class.getName());
        return client;
    }
}
