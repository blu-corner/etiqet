package com.neueda.etiqet.orderbook.etiqetorderbook;

import com.neueda.etiqet.orderbook.etiqetorderbook.utils.Utils;
import javafx.scene.control.TextArea;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.*;
import quickfix.field.*;
import quickfix.fix44.NewOrderSingle;

public class Initator implements Application, Runnable{

    private Logger logger = LoggerFactory.getLogger(Initator.class);

    public static String size;
    public static String price;
    public static String orderOd;
    public static String origOrderOd;
    public static boolean autoGen;
    public static boolean restSeq;
    public static char side;
    private final TextArea logTextArea;
    private SessionID sessionID;

    public Initator(TextArea logTextArea) {
        this.logTextArea = logTextArea;
    }

    @Override
    public void onCreate(SessionID sessionID) {
        this.logTextArea.appendText(String.format("onCreate -> sessionID: %s\n", sessionID));
        this.logger.info("onCreate -> sessionID: {}", sessionID);
        this.sessionID = sessionID;
    }

    @Override
    public void onLogon(SessionID sessionID) {
        this.logTextArea.appendText(String.format("onLogon -> sessionID: %s\n", sessionID));
        this.logger.info("onLogon -> sessionID: {}", sessionID);
    }

    @Override
    public void onLogout(SessionID sessionID) {
        this.logTextArea.appendText(String.format("onLogout -> sessionID: %s\n", sessionID));
        this.logger.info("onLogout -> sessionID: {}", sessionID);
    }

    @Override
    public void toAdmin(Message message, SessionID sessionID) {
        this.logTextArea.appendText(String.format("[>>>>OUT>>>]:toAdmin -> message: %s / sessionID: %s\n", Utils.replaceSOH(message), sessionID));
        this.logger.info("[>>>>OUT>>>]:toAdmin -> message: {} / sessionID: {}", Utils.replaceSOH(message), sessionID);
    }

    @Override
    public void fromAdmin(Message message, SessionID sessionID) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, RejectLogon {
        this.logTextArea.appendText(String.format("[>>>>IN>>>]:fromAdmin -> message: %s / sessionID: %s\n", Utils.replaceSOH(message), sessionID));
        this.logger.info("[<<<<<IN<<<]:fromAdmin -> message: {} / sessionID: {}", Utils.replaceSOH(message), sessionID);
    }

    @Override
    public void toApp(Message message, SessionID sessionID) throws DoNotSend {
        this.logTextArea.appendText(String.format("[>>>>OUT>>>]:toApp -> message: %s / sessionID: %s\n", Utils.replaceSOH(message), sessionID));
        this.logger.info("[>>>>OUT>>>]:toApp -> message: {} / sessionID: {}", Utils.replaceSOH(message), sessionID);
    }

    @Override
    public void fromApp(Message message, SessionID sessionID) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType {
        this.logTextArea.appendText(String.format("[>>>>IN>>>]:fromApp -> message: %s / sessionID: %s\n", Utils.replaceSOH(message), sessionID));
        this.logger.info("[<<<<<IN<<<]:fromApp -> message: {} / sessionID: {}", Utils.replaceSOH(message), sessionID);
    }

    public void sendNewOrderSingle(String size, String price, String orderOd, boolean autoGen, boolean resetSeq, char side){
        NewOrderSingle newOrderSingle = new NewOrderSingle();
        newOrderSingle.set(new OrderQty(Double.parseDouble(size)));
        newOrderSingle.set(new Price(Double.parseDouble(price)));
        newOrderSingle.set(new ClOrdID(orderOd));
        newOrderSingle.set(new Side(side));
        newOrderSingle.set(new OrdType(OrdType.LIMIT));
        newOrderSingle.set(new HandlInst('3'));
        newOrderSingle.set(new TransactTime());
        newOrderSingle.set(new Symbol("N/A"));

        try {
            Session.sendToTarget(newOrderSingle, sessionID);
        } catch (SessionNotFound sessionNotFound) {
            sessionNotFound.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            while(true){
                Thread.sleep(5000);
                System.out.println("INITIATOR RUNNING");
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
