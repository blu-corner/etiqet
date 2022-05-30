package com.neueda.etiqet.orderbook.etiqetorderbook.fix;

import com.neueda.etiqet.orderbook.etiqetorderbook.utils.Constants;
import com.neueda.etiqet.orderbook.etiqetorderbook.utils.Utils;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.*;
import quickfix.field.*;
import quickfix.fix44.NewOrderSingle;
import quickfix.fix44.OrderCancelReplaceRequest;
import quickfix.fix44.OrderCancelRequest;

/**
 * Initiator class: implement quickfix Application interface
 */
public class Initator implements Application {

    public static String size;
    public static String price;
    public static String orderOd;
    public static String origOrderOd;
    public static boolean autoGen;
    public static boolean restSeq;
    public static char side;
    private final ListView listViewActions;
    private final ListView listViewLog;
    private final TextField origOrderID;
    private final Logger logger = LoggerFactory.getLogger(Initator.class);
    private TextArea logTextArea;
    private SessionID sessionID;


    public Initator(ListView listViewActions, ListView listViewLog, TextField origOrderID) {
        this.listViewActions = listViewActions;
        this.listViewLog = listViewLog;
        this.origOrderID = origOrderID;
    }

    @Override
    public void onCreate(SessionID sessionID) {
        this.logger.info("onCreate -> sessionID: {}", sessionID);
        this.sessionID = sessionID;
    }

    @Override
    public void onLogon(SessionID sessionID) {
        this.logger.info("onLogon -> sessionID: {}", sessionID);
    }

    @Override
    public void onLogout(SessionID sessionID) {
        this.logger.info("onLogout -> sessionID: {}", sessionID);
    }

    @Override
    public void toAdmin(Message message, SessionID sessionID) {
        this.logger.info("[>>>>OUT>>>]:toAdmin -> message: {} / sessionID: {}", Utils.replaceSOH(message), sessionID);
        this.addsMessageToLogView(message, Constants.OUT);
    }

    @Override
    public void fromAdmin(Message message, SessionID sessionID) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, RejectLogon {
        this.logger.info("[<<<<<IN<<<]:fromAdmin -> message: {} / sessionID: {}", Utils.replaceSOH(message), sessionID);
        this.addsMessageToLogView(message, Constants.IN);
    }

    @Override
    public void toApp(Message message, SessionID sessionID) throws DoNotSend {
        this.logger.info("[>>>>OUT>>>]:toApp -> message: {} / sessionID: {}", Utils.replaceSOH(message), sessionID);
        this.addsMessageToLogView(message, Constants.OUT);
    }

    @Override
    public void fromApp(Message message, SessionID sessionID) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType {
        this.logger.info("[<<<<<IN<<<]:fromApp -> message: {} / sessionID: {}", Utils.replaceSOH(message), sessionID);
        this.addsMessageToLogView(message, Constants.IN);
    }

    /**
     * Analyzes and adds message to Log view
     * @param message
     * @param direction
     */
    public void addsMessageToLogView(Message message, String direction) {
        try {
            IntField msgSeqNum = message.getHeader().getField(new MsgSeqNum());
            String strMsgSeqNum = "[" + msgSeqNum + "]";
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
                    Platform.runLater(() -> {
                        listViewLog.getItems().add(String.format("%s %s", direction, Utils.replaceSOH(message)));
                        listViewActions.getItems().add(String.format("%s %s %s", direction, strMsgSeqNum, finalMsgTypeDescription));
                    });
                    return null;
                }
            };

            task.run();

        } catch (FieldNotFound e) {
            e.printStackTrace();
        }

    }

    /**
     * Gets text for msgTypeDescription
     * @param message
     * @return
     * @throws FieldNotFound
     */
    private String getText(Message message) throws FieldNotFound {
        try {
            StringField text = message.getField(new Text());
            return " : " + text.getValue();
        } catch (Exception e) {
            return "";
        }

    }

    /**
     * Sends New Order Single Request to Acceptor
     * @param size
     * @param price
     * @param orderId
     * @param side
     * @param comboTimeInForceValue
     * @param expireDate
     */
    public void sendNewOrderSingle(String size, String price, String orderId, char side, char comboTimeInForceValue, String expireDate) {
        NewOrderSingle newOrderSingle = new NewOrderSingle();
        newOrderSingle.set(new OrderQty(Double.parseDouble(size)));
        newOrderSingle.set(new Price(Double.parseDouble(price)));
        newOrderSingle.set(new ClOrdID(orderId));
        newOrderSingle.set(new Side(side));
        newOrderSingle.set(new OrdType(OrdType.LIMIT));
        newOrderSingle.set(new HandlInst('3'));
        newOrderSingle.set(new TransactTime());
        newOrderSingle.set(new Symbol("N/A"));
        newOrderSingle.set(new TimeInForce(comboTimeInForceValue));
        if (comboTimeInForceValue == TimeInForce.GOOD_TILL_DATE) {
            newOrderSingle.set(new ExpireDate(expireDate));
        }

        try {
            Session.sendToTarget(newOrderSingle, sessionID);
        } catch (SessionNotFound sessionNotFound) {
            sessionNotFound.printStackTrace();
        }
    }

    /**
     *  Sends Order Cancel Request to Acceptor
     * @param orderOd
     * @param origOrderOd
     * @param side
     */
    public void sendOrderCancelRequest(String orderOd, String origOrderOd, char side) {
        OrderCancelRequest orderCancelRequest = new OrderCancelRequest();
        orderCancelRequest.set(new Side(side));
        orderCancelRequest.set(new OrigClOrdID(origOrderOd));
        orderCancelRequest.set(new ClOrdID(orderOd));
        orderCancelRequest.set(new TransactTime());
        orderCancelRequest.set(new Symbol("N/A"));
        try {
            Session.sendToTarget(orderCancelRequest, sessionID);
        } catch (SessionNotFound e) {
            e.printStackTrace();
        }

    }

    /**
     * Sends Order Cancel/Replace Request to Acceptor
     * @param size
     * @param price
     * @param orderOd
     * @param origOrderOd
     * @param side
     */
    public void sendOrderCancelReplaceRequest(String size, String price, String orderOd, String origOrderOd, char side) {
        OrderCancelReplaceRequest orderCancelReplaceRequest = new OrderCancelReplaceRequest();
        orderCancelReplaceRequest.set(new Side(side));
        orderCancelReplaceRequest.set(new OrdType(OrdType.LIMIT));
        orderCancelReplaceRequest.set(new ClOrdID(orderOd));
        orderCancelReplaceRequest.set(new OrigClOrdID(origOrderOd));
        orderCancelReplaceRequest.set(new TransactTime());
        orderCancelReplaceRequest.set(new Symbol("N/A"));
        orderCancelReplaceRequest.set(new Price(Double.parseDouble(price)));
        orderCancelReplaceRequest.set(new OrderQty(Double.parseDouble(size)));
        try {
            Session.sendToTarget(orderCancelReplaceRequest, sessionID);
        } catch (SessionNotFound e) {
            e.printStackTrace();
        }
    }

}
