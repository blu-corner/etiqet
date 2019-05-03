package com.neueda.etiqet.core.config.annotations.impl;

import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import com.neueda.etiqet.core.config.annotations.Configuration;
import com.neueda.etiqet.core.config.annotations.EtiqetProtocol;
import com.neueda.etiqet.core.config.annotations.NamedClient;
import com.neueda.etiqet.core.config.annotations.NamedServer;
import com.neueda.etiqet.core.config.dtos.Client;
import com.neueda.etiqet.core.config.dtos.ClientImpl;
import com.neueda.etiqet.core.config.dtos.Protocol;
import com.neueda.etiqet.core.config.dtos.ServerImpl;
import com.neueda.etiqet.core.testing.client.TestClient;
import com.neueda.etiqet.core.testing.message.TestDictionary;
import com.neueda.etiqet.core.testing.server.TestServer;

@Configuration
public class ExampleConfiguration {

    @EtiqetProtocol("testProtocol")
    public Protocol getTestProtocol() throws EtiqetException {
        Protocol protocol = new Protocol();
        protocol.setClient(getFixClient());
        protocol.setDictionary(TestDictionary.class);
        String messageConfiguration = getClass().getClassLoader().getResource("protocols/testMessages.xml").getPath();
        protocol.setMessages(messageConfiguration);
        return protocol;
    }

    private Client getFixClient() {
        Client client = new Client();
        client.setImplementationClass(TestClient.class);
        client.setDefaultConfig("${user.dir}/src/test/resources/properties/test.properties");
        client.setTransportImpl("");
        client.setCodecImpl("");
        return client;
    }

    @NamedClient(name = "testClient1", impl = "testProtocol")
    public ClientImpl getClient1() {
        ClientImpl client = new ClientImpl();
        String config = getClass().getClassLoader().getResource("properties/test.properties").getPath();
        client.setPrimaryConfig(config);
        return client;
    }

    @NamedClient(name = "testClient2", impl = "testProtocol")
    public ClientImpl getClient2() {
        ClientImpl client = new ClientImpl();
        String config = getClass().getClassLoader().getResource("properties/test.properties").getPath();
        String config2 = getClass().getClassLoader().getResource("properties/testConfig.properties").getPath();
        client.setPrimaryConfig(config);
        client.setSecondaryConfig(config2);
        return client;
    }

    @NamedServer(
        name = "testServer",
        impl = TestServer.class,
        config = "${user.dir}/src/test/resources/properties/testConfig.properties"
    )
    public ServerImpl getServer() {
        return new ServerImpl();
    }

}
