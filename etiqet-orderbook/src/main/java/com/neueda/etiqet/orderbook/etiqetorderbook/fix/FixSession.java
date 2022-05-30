package com.neueda.etiqet.orderbook.etiqetorderbook.fix;

import com.neueda.etiqet.orderbook.etiqetorderbook.controllers.MainController;
import com.neueda.etiqet.orderbook.etiqetorderbook.utils.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.*;
import quickfix.field.BeginString;
import quickfix.field.SenderCompID;
import quickfix.field.TargetCompID;

/**
 * Used by Acceptor class to handle multiple quickfix acceptor creation
 * Contains multiple properties related to every acceptor
 */
public class FixSession {
    private final MainController mainController;
    private Logger logger = LoggerFactory.getLogger(FixSession.class);
    private Thread orderBook;
    private Integer port;
    private SessionID sessionID;
    private SocketAcceptor socketAcceptor;

    public FixSession(MainController mainController, Thread orderBook) {
        this.mainController = mainController;
        this.orderBook = orderBook;
    }

    public Logger getLogger() {
        return logger;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public MainController getMainController() {
        return mainController;
    }

    public Thread getOrderBook() {
        return orderBook;
    }

    public void setOrderBook(Thread orderBook) {
        this.orderBook = orderBook;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public SessionID getSessionID() {
        return sessionID;
    }

    public void setSessionID(SessionID sessionID) {
        this.sessionID = sessionID;
    }

    public SocketAcceptor getSocketAcceptor() {
        return socketAcceptor;
    }

    public void setSocketAcceptor(SocketAcceptor socketAcceptor) {
        this.socketAcceptor = socketAcceptor;
    }

    /**
     * TODO check if it is better to read default values from cfg file
     * Start acceptor, sets default values
     * @param port
     */
    public void start(Integer port) {
        this.port = port;
        SessionSettings sessionSettings = new SessionSettings();
        sessionID = new SessionID(
            new BeginString(Constants.DEFAULT_FIX_VERSION),
            new SenderCompID(Constants.DEFAULT_SERVER_COMP_ID),
            new TargetCompID(Constants.DEFAULT_TARGET_COMP_ID + port));
        Dictionary dictionary = new Dictionary();
        dictionary.setString(Constants.CONF_CONNECTION_TYPE, Constants.ACCEPTOR_ROLE.toLowerCase());
        dictionary.setString(Constants.ACC_ACCEPT_PORT, String.valueOf(port));
        dictionary.setString(Constants.CONF_FILE_STORE_PATH, Constants.DEFAULT_CONF_FILE_STORE_PATH + port);
        dictionary.setString(Constants.CONF_FILE_LOG_PATH, Constants.DEFAULT_CONF_FILE_LOG_PATH + port);
        dictionary.setString(Constants.CONF_DATA_DIC, Constants.DEFAULT_CONF_DATA_DIC);
        dictionary.setString(Constants.CONF_START_TIME, Constants.DEFAULT_CONF_TIME);
        dictionary.setString(Constants.CONF_END_TIME, Constants.DEFAULT_CONF_TIME);
        dictionary.setString(Constants.CONF_USE_DATA_DIC, Constants.Y);
        dictionary.setString(Constants.CONF_RESET_ON_LOGON, Constants.N);
        try {
            sessionSettings.set(sessionID, dictionary);
            MessageStoreFactory messageStoreFactory = new FileStoreFactory(sessionSettings);
            LogFactory logFactory = new FileLogFactory(sessionSettings);
            MessageFactory messageFactory = new DefaultMessageFactory();
            com.neueda.etiqet.orderbook.etiqetorderbook.fix.Acceptor acceptor = new Acceptor(this.mainController);
            socketAcceptor = new SocketAcceptor(acceptor, messageStoreFactory, sessionSettings, logFactory, messageFactory);
            socketAcceptor.start();
            this.logger.info("============New acceptor listening on {}", this.port);

        } catch (ConfigError e) {
            e.printStackTrace();
        }
    }

    /**
     * Stops current acceptor
     */
    public void stop() {
        try {
            this.socketAcceptor.stop();
        } catch (Exception e) {
            this.logger.warn("Exception in FixSession::stop -> {}", e.getLocalizedMessage());
        }
    }

}
