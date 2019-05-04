package com.neueda.etiqet.rest;

import com.neueda.etiqet.core.config.annotations.Configuration;
import com.neueda.etiqet.core.config.annotations.EtiqetProtocol;
import com.neueda.etiqet.core.config.dtos.Client;
import com.neueda.etiqet.core.config.dtos.Field;
import com.neueda.etiqet.core.config.dtos.Message;
import com.neueda.etiqet.core.config.dtos.Protocol;
import com.neueda.etiqet.core.transport.ConsoleTransport;
import com.neueda.etiqet.rest.client.RestClient;
import com.neueda.etiqet.rest.message.impl.HttpRequestMsg;
import com.neueda.etiqet.rest.transport.RestCodec;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Configuration
public class RestConfig {

    @EtiqetProtocol("rest")
    public Protocol getRestProtocol() {
        Protocol protocol = new Protocol();
        protocol.setClient(getRestClient());
        protocol.setMessages(getMessages());
        return protocol;
    }

    private Client getRestClient() {
        Client client = new Client();
        client.setImplementationClass(RestClient.class);
        client.setDefaultConfig(getClass().getClassLoader().getResource("config/ok/client.cfg").getPath());
        client.setTransportImpl(ConsoleTransport.class.getName());
        client.setCodecImpl(RestCodec.class.getName());
        return client;
    }

    private List<Message> getMessages() {
        Message basicMsg = new Message();
        basicMsg.setName("test_01");
        basicMsg.setImplementation(HttpRequestMsg.class);

        Field msgField = new Field();
        msgField.setName("test");
        msgField.setValue("value");

        Message message1Header = new Message();
        message1Header.setName("test_02");
        message1Header.setImplementation(HttpRequestMsg.class);
        message1Header.setFields(Collections.singletonList(msgField));

        Message message2Header = new Message();
        Field authHeader = new Field();
        authHeader.setName("Authorization");
        authHeader.setValue("LEGITIMATE_AUTH_PASSWORD");
        message2Header.setName("test_03");
        message2Header.setImplementation(HttpRequestMsg.class);
        message2Header.setHeaders(Collections.singletonList(authHeader));

        return Arrays.asList(basicMsg, message1Header, message2Header);
    }

}
