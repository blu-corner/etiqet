package com.neueda.etiqet.core.client;

import com.neueda.etiqet.core.client.delegate.ClientDelegate;
import com.neueda.etiqet.core.client.delegate.SinkClientDelegate;
import com.neueda.etiqet.core.common.Environment;
import com.neueda.etiqet.core.common.EtiqetEvent;
import com.neueda.etiqet.core.common.cdr.Cdr;
import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import com.neueda.etiqet.core.common.exceptions.EtiqetRuntimeException;
import com.neueda.etiqet.core.common.exceptions.UnhandledMessageException;
import com.neueda.etiqet.core.common.exceptions.UnknownTagException;
import com.neueda.etiqet.core.config.GlobalConfig;
import com.neueda.etiqet.core.config.dtos.Field;
import com.neueda.etiqet.core.config.dtos.Message;
import com.neueda.etiqet.core.message.Codec;
import com.neueda.etiqet.core.message.config.ProtocolConfig;
import com.neueda.etiqet.core.util.Config;
import com.neueda.etiqet.core.util.PropertiesFileReader;
import com.neueda.etiqet.core.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Abstract client
 * @param <U> unmarshalled native message format
 * @param <M> marshalled format of message for Etiqet to handle
 */
public abstract class Client<U, M> implements Codec<U>, Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(Client.class);

    private static final int DEFAULT_TIMEOUT_MILLIS = 5000;
    private static final int LOGON_RETRIES = 3;

    /**
     * Defines the primary configuration
     */
    protected String primaryConfig;

    /**
     * Defines the secondary configuration to be failed over to
     */
    protected String secondaryConfig;

    private String extensionsUrl;

    /** The name of the protocol used by this client */
    private String protocolName;

    /** Attribute msgQueue. */
    protected BlockingQueue<Cdr> msgQueue;

    protected final EtiqetEvent logonEvent = new EtiqetEvent();

    protected Config config;

    private ProtocolConfig protocolConfig;

    /* Delegate for processing messages during encoding and decoding phases */
    protected ClientDelegate<U, M> delegate;

    /**
     * Constructor configuration file path.
     * @param clientConfig path where client configuration file is.
     * @throws EtiqetException when unable to resolve the configuration file
     */
    public Client(String clientConfig) throws EtiqetException {
        this(clientConfig, null);
    }

    /**
     * Constructor with etiqet configuration, and primary/secondary client configuration
     * @param primaryClientConfig path where the primary configuration file is.
     * @param secondaryClientConfig path where the secondary configuration file is (for failover).
     * @throws EtiqetException when unable to resolve the configuration files
     */
    public Client(String primaryClientConfig, String secondaryClientConfig) throws EtiqetException {
        this(primaryClientConfig, secondaryClientConfig, null);
    }

    /**
     * Constructor with primary / secondary client configs and protocol config
     * @param primaryClientConfig path where the primary configuration file is.
     * @param secondaryClientConfig path where the secondary configuration file is (for failover).
     * @param protocol protocol configuration
     * @throws EtiqetException
     */
    public Client(String primaryClientConfig, String secondaryClientConfig, ProtocolConfig protocol) throws EtiqetException {
        commonInit(primaryClientConfig, secondaryClientConfig, protocol);
    }

    /**
     * Sets the delegate to intercept the processing of messages during encoding and decoding phase.
     * @param delegate the delegates providing te processing of the messages during encoding and decoding phase.
     */
    public void setDelegate(ClientDelegate<U, M> delegate) {
        this.delegate = delegate;
    }

    /**
     * Returns the delegate associated to this client.
     * @return the delegate associated to this client.
     */
    public ClientDelegate<U, M> getDelegate() {
        return delegate;
    }

    /**
     * Method with common initialization.
     * @param primaryConfig primary configuration path.
     * @param secondaryConfig secondary configuration path for failover
     * @param protocolConfig protocol configuration for this client
     * @throws EtiqetException when the extra config path can't be found / processed
     */
    private void commonInit(String primaryConfig, String secondaryConfig, ProtocolConfig protocolConfig) throws EtiqetException {
        msgQueue = new LinkedBlockingQueue<>();
        if(protocolConfig != null) {
            setProtocolConfig(protocolConfig);
            setProtocolName(protocolConfig.getProtocolName());
        }
        setClientConfig(primaryConfig, secondaryConfig);
    }

    @Override
    public void run() {
        try {
            launchClient();
        } catch (EtiqetException e) {
            LOG.error("Error starting Client: " + e.getMessage(), e);
            throw new EtiqetRuntimeException("Error starting Client: " + e.getMessage(), e);
        }
    }

    /**
     * Method to create received message.
     * @return received message.
     * @throws EtiqetException when a message can't be found within 5 seconds (default time)
     */
    public Cdr waitForAppMsg() throws EtiqetException {
        return waitForMsg(msgQueue, 5000);
    }

    /**
     * Method to create received message.
     * @param timeoutMillis time out to receive the message.
     * @return received message from server.
     */
    protected Cdr waitForMsg(BlockingQueue<Cdr> queue, Integer timeoutMillis) throws EtiqetException {
        try {
            Cdr msg = queue.poll(timeoutMillis, TimeUnit.MILLISECONDS);
            if(msg == null) {
                throw new EtiqetException("Timeout while waiting for message.");
            }
            return msg;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new EtiqetException("Interrupted while waiting for message.", e);
        }
    }


    /**
     * Method to wait for no message response, when there should be none.
     * @param timeoutMillis time out for receiving message response.
     * @return received message from server if any.
     */
    protected Cdr waitForNoMsg(BlockingQueue<Cdr> queue, Integer timeoutMillis) throws EtiqetException {
        try {
            Cdr msg = queue.poll(timeoutMillis, TimeUnit.MILLISECONDS);
            if(msg != null) {
                throw new EtiqetException("Unexpected message response.");
            }
            return null;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new EtiqetException("Interrupted while waiting for message.", e);
        }
    }

    /**
     * Sets client configuration
     * @param primaryConfig primary configuration to be used by default
     * @param secondaryConfig secondary config to be used for failing over
     * @throws EtiqetException when configuration files can't be resolved
     */
    public void setClientConfig(String primaryConfig, String secondaryConfig) throws EtiqetException {
        if(!StringUtils.isNullOrEmpty(primaryConfig)) {
            this.primaryConfig = Environment.resolveEnvVars(primaryConfig);
            this.setConfig(PropertiesFileReader.loadPropertiesFile(this.primaryConfig));
        }
        if(!StringUtils.isNullOrEmpty(secondaryConfig)) {
            this.secondaryConfig = Environment.resolveEnvVars(secondaryConfig);
        }
    }

    /**
     * Returns true if the given message type is adminstration related, false otherwise.
     * @param msgType the type of message.
     * @return true if the given message type is adminstration related, false otherwise.
     */
    public abstract boolean isAdmin(String msgType);

    /**
     * Method to check type of received message
     * @param msgType type of msg requested
     * @param timeoutMillis the maximum timeout to wait for the message in milliseconds
     */
    public Cdr waitForMsgType(String msgType, Integer timeoutMillis) throws EtiqetException {
        return waitForMsg(msgQueue, timeoutMillis);
    }

    /**
     * Returns true if successfully wait for logon with the default timeout, false otherwise.
     * @return true if successfully wait for logon with the default timeout, false otherwise.
     */
    public Boolean waitForLogon() {
        return waitForLogon(DEFAULT_TIMEOUT_MILLIS);
    }

    /**
     * Returns the number of retries for logon.
     * @return the number of retries for logon.
     */
    private int getLogonRetries() {
        return LOGON_RETRIES;
    }

    /**
     * Returns true if successfully wait for logon with the given timeout, false otherwise.
     * @return true if successfully wait for logon with the given timeout, false otherwise.
     */
    public Boolean waitForLogon(Integer timeoutMillis) {
        try {
            int retries = getLogonRetries();
            while((retries > 0) && !isLoggedOn()) {
                synchronized (logonEvent) {
                    logonEvent.wait(timeoutMillis);
                }
                retries--;
            }
            return logonEvent.getEvent();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    /**
     * Method to start client.
     * It must be implemented.
     */
    public abstract void launchClient() throws EtiqetException;

    /**
     * Method to start client with a configuration file
     * @param configPath String
     */
    public abstract void launchClient(String configPath) throws EtiqetException;

    /**
     * Returns string containing a session identifier by default.
     * @return string containing a session identifier by default.
     */
    public abstract String getDefaultSessionId();

    /**
     * Metodo to send a message.
     * It must to be implemented.
     * @param msg message to be sent
     * @throws EtiqetException exception.
     * @throws UnhandledMessageException exception.
     * @throws UnknownTagException exception.
     */
    public abstract void send(Cdr msg) throws EtiqetException;

    /**
     * Metodo to send a message.
     * It must to be implemented.
     * @param msg message to be sent
     * @param sessionId String containing the session identifier or null to create the session by default.
     * @throws EtiqetException exception.
     * @throws UnhandledMessageException exception.
     * @throws UnknownTagException exception.
     */
    public abstract void send(Cdr msg, String sessionId) throws EtiqetException;

    /**
     * Sets the action to be carried out by the client (chain of delegates).
     * @param actions the set of actions to be processed per message sent / received.
     * @throws EtiqetException throws exception if something went wrong.
     */
    public void setActions(String[] actions) throws EtiqetException {
        ClientDelegate<U, M> del = new SinkClientDelegate<>();

        // Create factory
        ClientDelegateFactory<U, M> cdf = new ClientDelegateFactory<>(getProtocolConfig());

        // Build actions
        for(int pos = actions.length - 1; pos >= 0; pos--) {
            ClientDelegate<U, M> tmp = cdf.create(actions[pos]);
            tmp.setNextDelegate(del);
            del = tmp;
        }
        setDelegate(del);
    }

    /**
    * Method to validate msg returned from server
    * @param cdr message recieved from server in cdr format
    * @throws EtiqetException throws exception if something went wrong.
    */
    public void validateMsg(String msgName, Cdr cdr) throws EtiqetException {
        ProtocolConfig protocolConf;
        if(protocolConfig != null) {
            protocolConf = protocolConfig;
        } else {
            protocolConf = GlobalConfig.getInstance().getProtocol(getProtocolName());
        }
        if(protocolConf == null) return;

        Message message = protocolConf.getMessage(msgName);
        if(message != null) {
            for (Field field: message.getFields().getField()){
                if(field != null && (field.getRequired()!= null )
                    && (field.getRequired().equalsIgnoreCase("Y"))
                    && !cdr.containsKey(field.getName()) ){
                    throw new EtiqetException(String.format("Required field missing: %s", field.getName()));
                }
                if(field != null
                    && (field.getAllowedValues()!=null)
                    && cdr.containsKey(field.getName())
                    && (!Arrays.asList(field.getAllowedValues().split(",")).contains(cdr.getItem(field.getName()).toString()))) {
                    throw new EtiqetException(String.format("Field: %s contains illegal value: %s", field.getName(), cdr.getItem(field.getName())));
                }
            }
        }
    }

    /**
     * Method to stop client.
     * Must be implemented
     */
    public abstract void stop();

    /**
     * Method to create if client is logged on.
     * @return flag that says is client is logged on.
     */
    public abstract boolean isLoggedOn();

    /**
     * Returns the name of the protocol followed by this client.
     * @return the name of the protocol followed by this client.
     */
    public String getProtocolName() {
        return protocolName;
    }

    /**
     * Sets the protocolName
     * @param protocolName name of the protocol for this client.
     */
    public void setProtocolName(String protocolName) {
        this.protocolName = protocolName;
    }

    /**
     * Method to find out the messageType given a message name.
     * @param messageName the name of the message.
     * @return the value of the attribute msgType of the message definition.
     */
    public abstract String getMsgType(String messageName);

    /**
     * Method to find out the messageName given a message type
     * @param messageType type of the message.
     * @return the name of the message.
     */
    public abstract String getMsgName(String messageType);

    /**
     * Method to wait for given time and check that no message was received in that time
     * @param msgType type of the message
     * @param timeoutMillis timeout limit in milliseconds
     * @return CDR object if one is received
     * @throws EtiqetException when an error occurs
     */
    public abstract Cdr waitForNoMsgType(String msgType, Integer timeoutMillis) throws EtiqetException;

    /**
     * Method to switch between the primary and secondary configurations
     * @throws EtiqetException when an error occurs failing over
     */
    public abstract void failover() throws EtiqetException;

    /**
     * Return true/false depending on whether or not secondaryConfig is set
     * @return Boolean canFailover
     */
    public boolean canFailover() {
        return !StringUtils.isNullOrEmpty(this.secondaryConfig);
    }

    public String getExtensionsUrl() {
        return extensionsUrl;
    }

    public void setExtensionsUrl(String extensionsUrl) {
        this.extensionsUrl = extensionsUrl;
    }

    public ProtocolConfig getProtocolConfig() {
        return protocolConfig;
    }

    public void setProtocolConfig(ProtocolConfig protocolConfig) {
        this.protocolConfig = protocolConfig;
    }

    /** Attribute config. */
    public Config getConfig() {
        return config;
    }

    public void setConfig(Config config) {
        this.config = config;
    }
}
