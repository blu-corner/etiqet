package com.neueda.etiqet.db.fixture;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class PortForward {

    @XmlElement(name = "local_port")
    private int localPort;
    @XmlElement(name = "remote_server")
    private String remoteServer;
    @XmlElement(name = "destination_port")
    private int destinationPort;

    private PortForward(){}

    public PortForward(int localPort, String remoteServer, int destinationPort) {
        this.localPort = localPort;
        this.remoteServer = remoteServer;
        this.destinationPort = destinationPort;
    }
}
