package com.neueda.etiqet.db.fixture;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "SshTunnel")
public class SshTunnel {

    @XmlElement(name = "PortForward")
    private PortForward portForward;
    @XmlElement(name = "SshServer")
    private SshServer sshServer;

    private SshTunnel(){}

    public SshTunnel(PortForward portForward, SshServer sshServer) {
        this.portForward = portForward;
        this.sshServer = sshServer;
    }
}
