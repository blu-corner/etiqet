package com.neueda.etiqet.db.fixture;


import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class SshServer {

    @XmlElement
    private String url;
    @XmlElement
    private int port;
    @XmlElement
    private String user;
    @XmlElement
    private String password;
    @XmlElement
    private String key;

    private SshServer(){}

    public SshServer(String url, int port, String user, String password, String key) {
        this.url = url;
        this.port = port;
        this.user = user;
        this.password = password;
        this.key = key;
    }

    public SshServer(String url, int port, String user, String password) {
        this.url = url;
        this.port = port;
        this.user = user;
        this.password = password;
    }

    public SshServer(String url, int port, String user) {
        this.url = url;
        this.port = port;
        this.user = user;
    }
}
