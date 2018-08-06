package com.neueda.etiqet.core.server;

import com.neueda.etiqet.core.common.cdr.Cdr;
import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import com.neueda.etiqet.core.util.Config;
import com.neueda.etiqet.core.util.PropertiesFileReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class Server implements Runnable {
	
	/** Attribute LOG for logging executions. */
	private static final Logger LOG = LogManager.getLogger(Server.class);
	
	/** Attribute serverConfig. */
	protected String serverConfig;
	
	/** Attribute sessionQueue. */
	protected BlockingQueue<Cdr> sessionQueue;
	
	/** Attribute msgQueue. */
	protected BlockingQueue<Cdr> msgQueue;

	private Config config;

	/**
	 * Default constructor.
	 * @param configPath configuration path.
	 */
	public Server(String configPath) {
		commonInit(configPath);
	}

	/**
	 * Constructor with configuration path and extra configuration path.
	 * @param configPath configuration path
	 * @param extraConfigPath extra configuration path.
	 */
	public Server(String configPath, String extraConfigPath) {
		commonInit(configPath);
		try {
			this.setConfig(PropertiesFileReader.loadPropertiesFile(extraConfigPath));
		} catch (Exception e) {
				LOG.warn(String.format("Could not read %s, defaulting to empty configuration", extraConfigPath));
			this.setConfig(new Config());
		}
	}

	/**
	 * Method with common initialization.
	 * @param config configuration path.
	 */
	private void commonInit(String config) {
		serverConfig = config;

		sessionQueue = new LinkedBlockingQueue<>();
		msgQueue = new LinkedBlockingQueue<>();
	}

	@Override
	public void run() {
		launchServer();
	}
	
	/**
	 * Method to start client.
	 * It must be implemented.
	 */
	public abstract void launchServer();

	/**
	 * Metodo to send a message.
	 * It must to be implemented.
	 * @param msg message to be sent
	 * @return operation result (true/false)
	 * @throws EtiqetException exception.
	 */
	public abstract Boolean send(Cdr msg) throws EtiqetException;

	/**
	 * Method to stop client.
	 * Must be implemented
	 */
	public abstract void stopServer();

	/** Attribute config. */
	public Config getConfig() {
		return config;
	}

	public void setConfig(Config config) {
		this.config = config;
	}
}
