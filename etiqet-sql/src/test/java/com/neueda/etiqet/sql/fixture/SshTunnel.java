package com.neueda.etiqet.sql.fixture;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "SshTunnel")
public class SshTunnel {

    @XmlElement(name = "username")
    private String userName;
    @XmlElement(name = "local-port")
    private int localPort;
    @XmlElement(name = "remote-host")
    private String remoteHost;
    @XmlElement(name = "remote-port")
    private int remotePort;
    @XmlElement(name = "password")
    private String password;
    @XmlElement(name = "key")
    private String keyPath;

    private SshTunnel(){}

    public SshTunnel(String userName, int localPort, String remoteHost, int remotePort,
                     @Nullable String password, @Nullable String keyPath) {
        this.userName = userName;
        this.localPort = localPort;
        this.remoteHost = remoteHost;
        this.remotePort = remotePort;
        this.password = password;
        this.keyPath = keyPath;
    }

    public String getUserName() {
        return userName;
    }

    public int getLocalPort() {
        return localPort;
    }

    public String getRemoteHost() {
        return remoteHost;
    }

    public int getRemotePort() {
        return remotePort;
    }

    public String getPassword() {
        return password;
    }

    public String getKeyPath() {
        return keyPath;
    }

    @Override
    public String toString() {
        return "SshTunnel{" +
            "userName='" + userName + '\'' +
            ", localPort=" + localPort +
            ", remoteHost='" + remoteHost + '\'' +
            ", remotePort=" + remotePort +
            ", password='" + password + '\'' +
            ", keyPath='" + keyPath + '\'' +
            '}';
    }
}
