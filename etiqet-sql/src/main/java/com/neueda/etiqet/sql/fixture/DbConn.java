package com.neueda.etiqet.sql.fixture;

import org.apache.log4j.Logger;
import org.jooq.SQLDialect;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class DbConn {

    private static Logger logger = Logger.getLogger(DbConn.class);

    @XmlAttribute
    private String name;
    @XmlAttribute(name = "driver_class")
    private String driverClass;
    @XmlAttribute
    private SQLDialect dialect;

    @XmlElement
    private String subprotocol;
    @XmlElement
    private String host;
    @XmlElement
    private int port;
    @XmlElement(name = "db_name")
    private String dbName;
    @XmlElement
    private String user;
    @XmlElement
    private String password;

    @XmlElement(name = "SshTunnel")
    private SshTunnel sshTunnel;

    private DbConn(){}

    public DbConn(String name, String driverClass, SQLDialect dialect, String subprotocol, String host, int port, String dbName, String user, String password) {
        this.name = name;
        this.driverClass = driverClass;
        this.dialect = dialect;
        this.subprotocol = subprotocol;
        this.host = host;
        this.port = port;
        this.dbName = dbName;
        this.user = user;
        this.password = password;
    }

    public DbConn(String name, String driverClass, SQLDialect dialect, String subprotocol, String host, int port, String dbName, String user, String password, SshTunnel sshTunnel) {
        this.name = name;
        this.driverClass = driverClass;
        this.dialect = dialect;
        this.subprotocol = subprotocol;
        this.host = host;
        this.port = port;
        this.dbName = dbName;
        this.user = user;
        this.password = password;
        this.sshTunnel = sshTunnel;
    }

    public String getName() {
        return name;
    }

    public String getDriverClass() {
        return driverClass;
    }

    public SQLDialect getDialect() {
        return dialect;
    }

    public String getHost() {
        return host;
    }

    public String getSubprotocol() {
        return subprotocol;
    }

    public int getPort() {
        return port;
    }

    public String getDbName() {
        return dbName;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public SshTunnel getSshTunnel() {
        return sshTunnel;
    }

    @Override
    public String toString() {
        return "DbConn{" +
            "name='" + name + '\'' +
            ", driverClass='" + driverClass + '\'' +
            ", dialect=" + dialect +
            ", subprotocol='" + subprotocol + '\'' +
            ", host='" + host + '\'' +
            ", port=" + port +
            ", dbName='" + dbName + '\'' +
            ", user='" + user + '\'' +
            ", password='" + password + '\'' +
            ", sshTunnel=" + sshTunnel +
            '}';
    }
}
