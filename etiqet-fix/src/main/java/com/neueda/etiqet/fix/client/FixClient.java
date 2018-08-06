package com.neueda.etiqet.fix.client;

import com.neueda.etiqet.core.client.Client;
import com.neueda.etiqet.core.client.delegate.ClientDelegate;
import com.neueda.etiqet.core.common.Environment;
import com.neueda.etiqet.core.common.cdr.Cdr;
import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import com.neueda.etiqet.core.config.GlobalConfig;
import com.neueda.etiqet.fix.client.delegate.FixClientDelegate;
import com.neueda.etiqet.fix.client.delegate.ReplaceParamFixClientDelegate;
import com.neueda.etiqet.fix.client.logger.LogAdapter;
import com.neueda.etiqet.fix.client.logger.LogAdapterFactory;
import com.neueda.etiqet.fix.config.FixConfigConstants;
import com.neueda.etiqet.fix.message.FIXUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import quickfix.*;

import java.io.File;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * {@inheritDoc}
 * 
 * @author Neueda
 *
 */
public class FixClient extends Client<Message, String> {

	public static final String CONFIG_CLIENT_LOGGON_TIMEOUT = "config.client.loggon.timeout";
	private static final Logger LOG = LogManager.getLogger(FixClient.class.getName());

	protected static final String[] DEFAULT_CLIENT_DELEGATES = {"fix", "fix-logger"};

	private Map<String, SessionID> sessionIds = new TreeMap<>();
	private SocketInitiator socketInitiator;

	/** Attribute sessionQueue. */
    private BlockingQueue<Cdr> sessionQueue;

	private String activeConfig;

	/**
	 * Constructor.
	 * @param clientConfig the client's configuration.
     * @throws EtiqetException when an issue occurs setting up the FixClient
	 */
	public FixClient(String clientConfig) throws EtiqetException {
		this(clientConfig, null);
	}

	/**
	 * Constructor.
	 * @param primaryConfig the client's configuration.
	 * @param secondaryConfig the client's secondary configuration for failover.
     * @throws EtiqetException when an issue occurs setting up the FixClient
	 */
	public FixClient(String primaryConfig, String secondaryConfig) throws EtiqetException {
		super(primaryConfig, secondaryConfig, GlobalConfig.getInstance().getProtocol(FixConfigConstants.PROTOCOL_NAME));
		sessionQueue = new LinkedBlockingQueue<>();
		setActions(DEFAULT_CLIENT_DELEGATES);
	}

	@Override
    public void setDelegate(ClientDelegate<Message, String> delegate) {
        super.setDelegate((delegate instanceof FixClientDelegate)? delegate: new FixClientDelegate(delegate));
    }

	public void stop() {
		if (socketInitiator != null)
			socketInitiator.stop();
	}

    /**
     * Map the sessions so that they can be referred from their ids.
     */
	protected void initSessions() {
		// Map the session ids
		for(SessionID sessionId: socketInitiator.getSessions()) {
			sessionIds.put(sessionId.toString(), sessionId);
		}
	}

	protected void interceptResponders() {
		for(Session session: socketInitiator.getManagedSessions()) {
			session.setResponder(new InterceptorResponder(session.getResponder(), delegate));
		}
	}

	@Override
	public void send(Cdr msg) throws EtiqetException {
		send(msg, getDefaultSessionId());
	}

	@Override
	public void send(Cdr msg, String sessionId) throws EtiqetException {
		try {
			// Update the replace delegate with the message to be sent
			ClientDelegate c = delegate.findDelegate(ReplaceParamFixClientDelegate.class);
			if(c != null) {
				((ReplaceParamFixClientDelegate)c).setMessage(msg);
			}

			// Send the message
			Session.sendToTarget(encode(msg), sessionIds.get(sessionId));
		} catch (SessionNotFound snfe) {
			throw new EtiqetException(snfe);
		}
	}

	@Override
	public boolean isLoggedOn() {
		return socketInitiator != null && socketInitiator.isLoggedOn();
	}

	@Override
	public void launchClient() throws EtiqetException {
		launchClient(primaryConfig);
	}

	@Override
	public void launchClient(String configPath) throws EtiqetException {
		socketInitiator = null;
		setActiveConfig(configPath);
		try {
			if (!new File(Environment.resolveEnvVars(configPath)).exists())
				LOG.fatal(primaryConfig + "not found");

			socketInitiator = getSocketInitiator(configPath);
			socketInitiator.start();
			initSessions();
			int i = 0;
			do {
				Thread.sleep(1000);
				i++;
			} while ((!isLoggedOn()) && (i < this.getConfig().getInteger(CONFIG_CLIENT_LOGGON_TIMEOUT)));
			interceptResponders();
		} catch (Exception exp) {
			LOG.fatal("Error launching FIX client: " + exp.getMessage(), exp);
		}
	}

	protected SocketInitiator getSocketInitiator(String configPath) throws EtiqetException {
        try {
            SessionSettings sessionSettings = new SessionSettings(Environment.fileResolveEnvVars(configPath));

            FixApp application = new FixApp(sessionSettings, sessionQueue, msgQueue, logonEvent);
            application.setDelegate(delegate);
            FileStoreFactory fileStoreFactory = new FileStoreFactory(sessionSettings);
            LogAdapterFactory logFactory = new LogAdapterFactory(LogAdapter.Level.INFO);
            MessageFactory messageFactory = new DefaultMessageFactory();

            return new SocketInitiator(application, fileStoreFactory, sessionSettings, logFactory,
                messageFactory);
        } catch (Exception e) {
            throw new EtiqetException("Error creating SocketInitiator for FixClient", e);
        }
    }

	@Override
	public String getDefaultSessionId() {
		return sessionIds.entrySet().iterator().next().getKey();
	}

	public boolean isAdmin(String msgType) {
		return getProtocolConfig().isAdmin(msgType);
	}

	@Override
	public String getMsgType(String messageName) {		
		return getProtocolConfig().getMsgType(messageName);
	}
	
	@Override
	public String getMsgName(String messageType) {		
		return getProtocolConfig().getMsgName(messageType);
	}

    /**
     * Method to check type of received message
     * @param msgType type of msg requested
     * @param timeoutMillis the maximum timeout to wait for the message in milliseconds
     */
    @Override
    public Cdr waitForMsgType(String msgType, Integer timeoutMillis) throws EtiqetException {
        return waitForMsg(isAdmin(msgType)? sessionQueue: msgQueue, timeoutMillis);
    }

    @Override
	public Cdr waitForNoMsgType(String msgType, Integer timeoutMillis) throws EtiqetException {
		return waitForNoMsg(isAdmin(msgType)? sessionQueue: msgQueue, timeoutMillis);
	}

	@Override
	public Message encode(Cdr cdr) throws EtiqetException {
		return FIXUtils.encode(cdr);
	}

	@Override
	public Cdr decode(Message message) throws EtiqetException {
		return FIXUtils.decode(message);
	}

	@Override
	public void failover() throws EtiqetException {
		if(canFailover()) {
			stop();
			if(getActiveConfig().equals(primaryConfig)) {
				launchClient(secondaryConfig);
			} else {
				launchClient();
			}
		} else {
		    String error = "No secondary config to failover";
		    LOG.error(error);
		    throw new EtiqetException(error);
        }
	}

    public String getActiveConfig() {
        return activeConfig;
    }

    public void setActiveConfig(String activeConfig) {
        this.activeConfig = activeConfig;
    }
}
