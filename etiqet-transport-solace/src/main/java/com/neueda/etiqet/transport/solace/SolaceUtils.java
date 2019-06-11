package com.neueda.etiqet.transport.solace;

import com.solacesystems.jms.SolXAConnectionFactory;
import com.solacesystems.jms.SolXAConnectionFactoryImpl;

public class SolaceUtils {

    private SolaceUtils() {}

    public static SolXAConnectionFactory getConnectionFactory(String host, String vpn, String user, String pass) {
        SolXAConnectionFactory connectionFactory = new SolXAConnectionFactoryImpl();
        connectionFactory.setHost(host);
        connectionFactory.setVPN(vpn);
        connectionFactory.setUsername(user);
        connectionFactory.setPassword(pass);
        connectionFactory.setXmlPayload(false);
        return connectionFactory;
    }

}
