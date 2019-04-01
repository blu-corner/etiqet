package com.neueda.etiqet.fix.server;

import com.neueda.etiqet.core.common.Environment;
import com.neueda.etiqet.core.common.cdr.Cdr;
import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import com.neueda.etiqet.core.common.exceptions.EtiqetRuntimeException;
import com.neueda.etiqet.core.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.*;

import java.io.File;

public class FixServer extends Server {

	/** Attribute LOG for logging executions. */
	private static final Logger LOG = LoggerFactory.getLogger(FixServer.class);
	
	private SocketAcceptor socketAcceptor;

    public FixServer(String configPath) {
		super(configPath);
	}
	
	public FixServer(String configPath, String extraConfigPath) {
		super(configPath, extraConfigPath);
	}

	@Override
	public void launchServer() {
		try {
			File f = new File(Environment.resolveEnvVars(serverConfig));
			if (!f.exists()) {
				LOG.error("Server config {} not found", serverConfig);
				throw new EtiqetException("Could not find server configuration file " + serverConfig);
			}
			
			SessionSettings settings = new SessionSettings(Environment.resolveEnvVars(serverConfig));
			ScreenLogFactory screenLogFactory = new ScreenLogFactory(settings);
			DefaultMessageFactory messageFactory = new DefaultMessageFactory();
			FileStoreFactory fileStoreFactory = new FileStoreFactory(settings);

			socketAcceptor = new SocketAcceptor(
			    new FixServerApp(),
                fileStoreFactory,
                settings,
                screenLogFactory,
                messageFactory
            );
			socketAcceptor.start();
		} catch (Exception exp) {
			LOG.error("Error launching server", exp);
			throw new EtiqetRuntimeException("Error launching server", exp);
		}
	}

	@Override
	public Boolean send(Cdr msg) throws EtiqetException {
		throw new EtiqetException("This method has not been implemented for class: " + this.getClass().getName());
	}

	@Override
	public void stopServer() {
		if (socketAcceptor != null) {
			socketAcceptor.stop();
		}
	}

}
