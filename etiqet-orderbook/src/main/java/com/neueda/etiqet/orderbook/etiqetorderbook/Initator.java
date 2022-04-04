package com.neueda.etiqet.orderbook.etiqetorderbook;

import com.neueda.etiqet.orderbook.etiqetorderbook.utils.Constants;
import com.neueda.etiqet.orderbook.etiqetorderbook.utils.Utils;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.*;
import quickfix.field.*;
import quickfix.fix44.NewOrderSingle;

import java.io.IOException;

public class Initator implements Application{

    private final ListView listViewActions;
    private final ListView listViewLog;
    private Logger logger = LoggerFactory.getLogger(Initator.class);

    public static String size;
    public static String price;
    public static String orderOd;
    public static String origOrderOd;
    public static boolean autoGen;
    public static boolean restSeq;
    public static char side;
    private TextArea logTextArea;
    private SessionID sessionID;

    public Initator(ListView listViewActions, ListView listViewLog) {
        this.listViewActions = listViewActions;
        this.listViewLog= listViewLog;
    }

    @Override
    public void onCreate(SessionID sessionID) {
        //this.logTextArea.appendText(String.format("onCreate -> sessionID: %s\n", sessionID));
        this.logger.info("onCreate -> sessionID: {}", sessionID);
        this.sessionID = sessionID;
    }

    @Override
    public void onLogon(SessionID sessionID) {
        //this.logTextArea.appendText(String.format("onLogon -> sessionID: %s\n", sessionID));
        this.logger.info("onLogon -> sessionID: {}", sessionID);
    }

    @Override
    public void onLogout(SessionID sessionID) {
        //this.logTextArea.appendText(String.format("onLogout -> sessionID: %s\n", sessionID));
        this.logger.info("onLogout -> sessionID: {}", sessionID);
    }

    @Override
    public void toAdmin(Message message, SessionID sessionID) {
//        message.setBoolean(ResetSeqNumFlag.FIELD, true);
        //this.logTextArea.appendText(String.format("[>>>>OUT>>>]:toAdmin -> message: %s / sessionID: %s\n", Utils.replaceSOH(message), sessionID));
        this.logger.info("[>>>>OUT>>>]:toAdmin -> message: {} / sessionID: {}", Utils.replaceSOH(message), sessionID);
        this.messageAnalizer(message, Constants.OUT);
    }

    @Override
    public void fromAdmin(Message message, SessionID sessionID) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, RejectLogon {
        //this.logTextArea.appendText(String.format("[>>>>IN>>>]:fromAdmin -> message: %s / sessionID: %s\n", Utils.replaceSOH(message), sessionID));
        this.logger.info("[<<<<<IN<<<]:fromAdmin -> message: {} / sessionID: {}", Utils.replaceSOH(message), sessionID);
        this.messageAnalizer(message, Constants.IN);
    }

    @Override
    public void toApp(Message message, SessionID sessionID) throws DoNotSend {
        //this.logTextArea.appendText(String.format("[>>>>OUT>>>]:toApp -> message: %s / sessionID: %s\n", Utils.replaceSOH(message), sessionID));
        this.logger.info("[>>>>OUT>>>]:toApp -> message: {} / sessionID: {}", Utils.replaceSOH(message), sessionID);
        this.messageAnalizer(message, Constants.OUT);
    }

    @Override
    public void fromApp(Message message, SessionID sessionID) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType {
        //this.logTextArea.appendText(String.format("[>>>>IN>>>]:fromApp -> message: %s / sessionID: %s\n", Utils.replaceSOH(message), sessionID));
        this.logger.info("[<<<<<IN<<<]:fromApp -> message: {} / sessionID: {}", Utils.replaceSOH(message), sessionID);
        this.messageAnalizer(message, Constants.IN);
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

//    @Override
//    public void run() {
//        try {
//            while(true){
//                Thread.sleep(5000);
//                System.out.println("INITIATOR RUNNING");
//            }
//
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }


    public void messageAnalizer(Message message, String direction){
        try {
            IntField msgSeqNum = message.getHeader().getField(new MsgSeqNum());
            String strMsgSeqNum = "[" + msgSeqNum + "]" ;
            StringField msgType = message.getHeader().getField(new MsgType());
            String msgTypeDescription = Constants.hmMsgType.get(msgType.getValue());
            String text = getText(message);
            if (msgType.getValue().equals(Constants.EXECUTION_REPORT)) {
                CharField ordStatus = message.getField(new OrdStatus());
                CharField execType = message.getField(new ExecType());
                String ordStatusDescription = Constants.hmOrdStatus.get(String.valueOf(ordStatus.getValue()));
                String execTypeDescription = Constants.hmExecType.get(String.valueOf(execType.getValue()));

                msgTypeDescription += " -> " + execTypeDescription + " : " + ordStatusDescription;
            }
            msgTypeDescription = StringUtils.isEmpty(text) ? msgTypeDescription : msgTypeDescription + text;
            String finalMsgTypeDescription = msgTypeDescription;
            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    Platform.runLater( () -> {
                        listViewLog.getItems().add(String.format("%s %s",direction, Utils.replaceSOH(message)));
                        listViewActions.getItems().add(String.format("%s %s %s",direction,strMsgSeqNum, finalMsgTypeDescription));
                    });
                    return null;
                }
            };

            task.run();

        } catch (FieldNotFound e) {
            e.printStackTrace();
        }

    }

    private String getText(Message message) throws FieldNotFound {
        try{
            StringField text = message.getField(new Text());
            return " : " + text.getValue();
        }catch (Exception e){
            return "";
        }

    }

}
