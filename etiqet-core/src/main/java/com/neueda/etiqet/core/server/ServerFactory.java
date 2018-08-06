package com.neueda.etiqet.core.server;

import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Factory to create a Server implementation
 * @author Neueda
 *
 */
public class ServerFactory {

	private static final Logger LOG = LogManager.getLogger(ServerFactory.class);
    private static final String ERROR_CREATING_SERVER = "Error creating server of type: ";
	private ServerFactory() {}
	
	/**
	 * Instantiates the server specified in parameters
	 * @param serverType Type of server required
	 * @return An instance of Server
	 * @throws EtiqetException if server type is not found.
	 */
	public static Server create(String serverType) throws EtiqetException {
        Server instance;
        try {
            instance = (Server) Class.forName(serverType).newInstance();
        } catch (Exception e) {
            LOG.error(ERROR_CREATING_SERVER+ serverType, e);
            throw new EtiqetException(ERROR_CREATING_SERVER + serverType, e);
        }
        return instance;
	}

	/**
	 * Instantiates the server specified in parameters
	 * @param serverType Type of server required
     * @param config path to configuration file for the server
	 * @return An instance of Server
	 * @throws EtiqetException if server type is not found.
	 */
	public static Server create(String serverType, String config) throws EtiqetException {
        Server instance;
        try {
            instance = (Server) Class.forName(serverType).getConstructor(String.class).newInstance(config);
        } catch (Exception e) {
            LOG.error(ERROR_CREATING_SERVER + serverType, e);
            throw new EtiqetException(ERROR_CREATING_SERVER + serverType, e);
        }
        return instance;
	}

    /**
     * Instantiates the server specified with the config and extra config files provided
     * @param serverType Type of the server required
     * @param config path to configuration file for the server
     * @param extraConfig path to extra configuration file for the server
     * @return An instance of Server
     * @throws EtiqetException when serverType can't be instantiated correctly
     */
	public static Server create(String serverType, String config, String extraConfig) throws EtiqetException {
        Server instance;
        try {
            instance = (Server) Class.forName(serverType)
                                     .getConstructor(String.class, String.class)
                                     .newInstance(config, extraConfig);
        } catch (Exception e) {
            LOG.error(ERROR_CREATING_SERVER + serverType, e);
            throw new EtiqetException(ERROR_CREATING_SERVER + serverType, e);
        }
        return instance;
    }

}
