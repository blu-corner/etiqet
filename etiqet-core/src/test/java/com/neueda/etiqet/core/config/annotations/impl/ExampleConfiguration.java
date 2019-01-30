package com.neueda.etiqet.core.config.annotations.impl;

import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import com.neueda.etiqet.core.config.annotations.Configuration;
import com.neueda.etiqet.core.config.annotations.Protocol;
import com.neueda.etiqet.core.config.annotations.Server;
import com.neueda.etiqet.core.config.dtos.*;
import com.neueda.etiqet.core.testing.client.TestClient;
import com.neueda.etiqet.core.testing.message.TestDictionary;
import com.neueda.etiqet.core.testing.server.TestServer;

import java.util.Collections;
import java.util.List;

@Configuration
public class ExampleConfiguration {

    @Protocol("testProtocol")
    public com.neueda.etiqet.core.config.dtos.Protocol getTestProtocol() throws EtiqetException {
        com.neueda.etiqet.core.config.dtos.Protocol protocol = new com.neueda.etiqet.core.config.dtos.Protocol();
        protocol.setClient(getFixClient());
        protocol.setDictionary(new Dictionary(TestDictionary.class));
        String messageConfiguration = getClass().getClassLoader().getResource("protocols/testMessages.xml").getPath();
        protocol.setMessages(messageConfiguration);
        return protocol;
    }

    private Client getFixClient() {
        Client client = new Client();
        client.setImplementationClass(TestClient.class);
        client.setDefaultConfig("${etiqet.directory}/etiqet-core/src/test/resources/properties/test.properties");
        client.setDelegates(getDelegates());
        return client;
    }

    private List<Delegate> getDelegates() {
        return Collections.emptyList();
    }

    @com.neueda.etiqet.core.config.annotations.Client(
        name = "testClient1",
        impl = "testProtocol"
    )
    public ClientImpl getClient1() {
        ClientImpl client = new ClientImpl();
        String config = getClass().getClassLoader()
            .getResource("properties/test.properties")
            .getPath();
        client.setPrimaryConfig(config);
        return client;
    }

    @com.neueda.etiqet.core.config.annotations.Client(
        name = "testClient2",
        impl = "testProtocol"
    )
    public ClientImpl getClient2() {
        ClientImpl client = new ClientImpl();
        String config = getClass().getClassLoader()
            .getResource("properties/test.properties")
            .getPath();
        String config2 = getClass().getClassLoader()
            .getResource("properties/testConfig.properties")
            .getPath();
        client.setPrimaryConfig(config);
        client.setSecondaryConfig(config2);
        return client;
    }

    @Server(name = "testServer",
        impl = TestServer.class,
        config = "${user.dir}/src/test/resources/fix-config/testServer.cfg")
    public ServerImpl getServer() {
        return new ServerImpl();
    }

}
