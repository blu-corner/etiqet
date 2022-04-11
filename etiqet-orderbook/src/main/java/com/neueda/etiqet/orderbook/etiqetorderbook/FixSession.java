package com.neueda.etiqet.orderbook.etiqetorderbook;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.*;
import quickfix.field.BeginString;
import quickfix.field.SenderCompID;
import quickfix.field.TargetCompID;

public class FixSession {
    private Logger logger = LoggerFactory.getLogger(FixSession.class);
    private final MainController mainController;
    private Thread orderBook;
    private Integer port;
    private SessionID sessionID;
    private SocketAcceptor socketAcceptor;

    public FixSession(MainController mainController, Thread orderBook){
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

    public void start(Integer port){
        this.port = port;
        SessionSettings sessionSettings = new SessionSettings();
        sessionID = new SessionID(
            new BeginString("FIX.4.4"),
            new SenderCompID("SERVER"),
            new TargetCompID("CLIENT"+port));
        Dictionary dictionary = new Dictionary();
        dictionary.setString("ConnectionType", "acceptor");
        dictionary.setString("SocketAcceptPort", String.valueOf(port));
        dictionary.setString("FileStorePath", "stores/store" + port);
        dictionary.setString("FileLogPath", "logs/log" + port);
        dictionary.setString("DataDictionary", "spec/FIX44.xml");
        dictionary.setString("StartTime", "00:00:00");
        dictionary.setString("EndTime", "00:00:00");
        dictionary.setString("UseDataDictionary", "Y");
        dictionary.setString("ResetOnLogon", "Y");
        try {
            sessionSettings.set(sessionID, dictionary);
            MessageStoreFactory messageStoreFactory = new FileStoreFactory(sessionSettings);
            LogFactory logFactory = new FileLogFactory(sessionSettings);
            MessageFactory messageFactory = new DefaultMessageFactory();
            Acceptor acceptor = new Acceptor(this.mainController);
            socketAcceptor = new SocketAcceptor(acceptor, messageStoreFactory, sessionSettings, logFactory, messageFactory);
            socketAcceptor.start();
            this.logger.info("============New acceptor listening on {}", this.port);

        } catch (ConfigError e) {
            e.printStackTrace();
        }
    }

    public void stop(){
        try{
            this.socketAcceptor.stop();
        }catch (Exception e){

        }
    }

}
