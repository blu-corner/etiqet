package com.neueda.etiqet.core.testing.server;

import com.neueda.etiqet.core.common.cdr.Cdr;
import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import com.neueda.etiqet.core.server.Server;

public class TestServer extends Server {

    public static final String DEFAULT_CONFIG
            = "etiqet-core/src/test/resources/properties/testDefaultServer.properties";

    private boolean isStarted = false;

    public TestServer() {
        this(DEFAULT_CONFIG);
    }

    public TestServer(String configPath) {
        super(configPath);
    }

    public TestServer(String configPath, String extraConfigPath) {
        super(configPath, extraConfigPath);
    }

    @Override
    public void launchServer() {
        isStarted = true;
    }

    @Override
    public Boolean send(Cdr msg) throws EtiqetException {
        return null;
    }

    @Override
    public void stopServer() {
        isStarted = false;
    }

    public boolean isStarted() {
        return isStarted;
    }

}
