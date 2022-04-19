package com.neueda.etiqet.orderbook.etiqetorderbook;

import com.neueda.etiqet.orderbook.etiqetorderbook.controllers.MainController;
import com.neueda.etiqet.orderbook.etiqetorderbook.entity.Action;
import com.neueda.etiqet.orderbook.etiqetorderbook.entity.Order;
import com.neueda.etiqet.orderbook.etiqetorderbook.utils.Constants;
import com.neueda.etiqet.orderbook.etiqetorderbook.utils.Utils;
import javafx.application.Platform;
import javafx.concurrent.Task;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.*;
import quickfix.field.*;
import quickfix.fix44.ExecutionReport;
import quickfix.fix44.OrderCancelReject;

import java.time.LocalDateTime;
import java.util.*;

public class Acceptor implements Application {

    private final MainController mainController;
    Logger logger = LoggerFactory.getLogger(Acceptor.class);

    public Acceptor(MainController mainController) {
        this.mainController = mainController;
    }

    @Override
    public void onCreate(SessionID sessionID) {
        //this.logTextArea.appendText(String.format("onCreate -> sessionID: %s\n", sessionID));
        this.logger.info("onCreate -> sessionID: {}", sessionID);
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
        this.onMessage(message, sessionID);
    }

    public void onMessage(Message message, SessionID sessionID) throws FieldNotFound, IncorrectTagValue {
//        StringField beginString = message.getHeader().getField(new BeginString());
        StringField msgType = message.getHeader().getField(new MsgType());
        StringField clOrdID = message.getField(new ClOrdID());
        StringField symbol = message.getField(new Symbol());
        CharField side = message.getField(new Side());
        String orderId = RandomStringUtils.randomAlphanumeric(5);
        String execId = RandomStringUtils.randomAlphanumeric(5);
        List<Message> reports = new ArrayList<>();

        DoubleField ordQty;
        CharField ordType;
        DoubleField price;
        StringField origClOrdID;

        switch (msgType.getValue()) {
            case MsgType.ORDER_SINGLE:
                price = message.getField(new Price());
                ordQty = message.getField(new OrderQty());
                ordType = message.getField(new OrdType());

                if (ordType.getValue() != OrdType.LIMIT) {
                    throw new IncorrectTagValue(ordType.getField());
                }
                if (duplicatedOrderId(clOrdID)) {
                    logger.info("################################ NEW ORDER REJECTED: DUPLICATED ORDER ID");
                    reports.add(generateExecReport(ExecType.REJECTED, clOrdID, orderId, execId, symbol, side, new DoubleField(0), new DoubleField(0)));
                } else {
                    reports.add(generateExecReport(ExecType.PENDING_NEW, clOrdID, Constants.NEW, Constants.NEW, symbol, side, price, ordQty));
                    reports.add(generateExecReport(ExecType.NEW, clOrdID, orderId, execId, symbol, side, price, ordQty));
                    logger.info("################################ NEW ORDER SINGLE");
                    addNewOrder(side, new Order(clOrdID.getValue(), LocalDateTime.now(), ordQty.getValue(), price.getValue()));
                    ExecutionReport finalExecutionReport = lookForNewTrade(clOrdID, orderId, execId, symbol, side, price, ordQty);
                    if (finalExecutionReport != null) {
                        reports.add(finalExecutionReport);
                    }
                }

                break;
            case MsgType.ORDER_CANCEL_REQUEST:
                origClOrdID = message.getField(new OrigClOrdID());
                if (duplicatedOrderId(clOrdID)) {
                    reports.add(rejectOrder(CxlRejResponseTo.ORDER_CANCEL_REQUEST, CxlRejReason.DUPLICATE_CLORDID_RECEIVED, clOrdID.getValue()));
                } else {
                    if (!containsOrder(side, origClOrdID.getValue())) {
                        reports.add(rejectOrder(CxlRejResponseTo.ORDER_CANCEL_REQUEST, CxlRejReason.UNKNOWN_ORDER, clOrdID.getValue()));
                    } else {
                        logger.info("################################ ORDER CANCEL REQUEST");
                        //clOrdID, orderId, execId, symbol, side, price, ordQty
                        reports.add(generateExecReport(ExecType.PENDING_CANCEL, clOrdID, Constants.NEW, Constants.NEW, symbol, side, new DoubleField(0), new DoubleField(0)));
                        reports.add(generateExecReport(ExecType.CANCELED, clOrdID, orderId, execId, symbol, side, new DoubleField(0), new DoubleField(0)));
                        ExecutionReport tradeWhenCancelling = cancelOrder(origClOrdID, clOrdID, orderId, execId, symbol, side, null, null);
                        if (tradeWhenCancelling != null){
                            reports.add(tradeWhenCancelling);
                        }
                    }
                }

                break;
            case MsgType.ORDER_CANCEL_REPLACE_REQUEST:
                origClOrdID = message.getField(new OrigClOrdID());
                price = message.getField(new Price());
                ordQty = message.getField(new OrderQty());
                ordType = message.getField(new OrdType());
                if (ordType.getValue() != OrdType.LIMIT) {
                    throw new IncorrectTagValue(ordType.getField());
                }
                if (duplicatedOrderId(clOrdID)) {
                    reports.add(rejectOrder(CxlRejResponseTo.ORDER_CANCEL_REPLACE_REQUEST, CxlRejReason.DUPLICATE_CLORDID_RECEIVED, clOrdID.getValue()));
                } else {
                    if (!containsOrder(side, origClOrdID.getValue())) {
                        reports.add(rejectOrder(CxlRejResponseTo.ORDER_CANCEL_REPLACE_REQUEST, CxlRejReason.UNKNOWN_ORDER, clOrdID.getValue()));
                    } else {
                        logger.info("################################ ORDER CANCEL REPLACE REQUEST");
                        Order order = new Order(origClOrdID.getValue(), LocalDateTime.now(), ordQty.getValue(), price.getValue());
                        reports.add(generateExecReport(ExecType.PENDING_REPLACE, clOrdID, Constants.NEW, Constants.NEW, symbol, side, ordQty, new DoubleField(0)));
                        reports.add(generateExecReport(ExecType.REPLACED, clOrdID, orderId, execId, symbol, side, ordQty, price));
                        ExecutionReport tradeWhenReplacing = replaceOrder(origClOrdID, clOrdID, orderId, execId, symbol, side, ordQty, price, order);
                        if (tradeWhenReplacing != null){
                            reports.add(tradeWhenReplacing);
                        }
                    }
                }
                break;
            default:
                break;
        }

        reports.forEach(r -> {
            try {
                Session.sendToTarget(r, sessionID);
            } catch (SessionNotFound e) {
                e.printStackTrace();
            }
        });
    }

    private Message rejectOrder(int rejResponseTo, int cxlRejReason, String origClOrdID) {
        String clOrdId = RandomStringUtils.randomAlphanumeric(5);
        OrderCancelReject orderCancelReject = new OrderCancelReject();
        switch (rejResponseTo) {
            case CxlRejResponseTo.ORDER_CANCEL_REQUEST:
                orderCancelReject.setField(new CxlRejResponseTo(CxlRejResponseTo.ORDER_CANCEL_REQUEST));
                orderCancelReject.setField(new CxlRejReason(cxlRejReason));
                logger.info("################################ ORDER CANCEL REQUEST REJECTED!!");
                break;
            case CxlRejResponseTo.ORDER_CANCEL_REPLACE_REQUEST:
                orderCancelReject.setField(new CxlRejResponseTo(CxlRejResponseTo.ORDER_CANCEL_REPLACE_REQUEST));
                orderCancelReject.setField(new CxlRejReason(cxlRejReason));
                logger.info("################################ ORDER CANCEL REPLACE REQUEST REJECTED!!");
                break;
        }
        orderCancelReject.setField(new OrderID(Constants.NONE));
        orderCancelReject.setField(new ClOrdID(clOrdId));
        orderCancelReject.setField(new OrigClOrdID(origClOrdID));
        orderCancelReject.setField(new OrdStatus(OrdStatus.REJECTED));

        return orderCancelReject;
    }

    private ExecutionReport generateExecReport(char execType, StringField clOrdID, String ordId, String execId, StringField symbol, CharField side, DoubleField price, DoubleField ordQty) {
        ExecutionReport executionReport = new ExecutionReport();

        switch (execType) {
            case ExecType.PENDING_NEW:
                executionReport.setField(new ExecType(execType));
                executionReport.setField(new OrdStatus(OrdStatus.PENDING_NEW));
                executionReport.setField(new LeavesQty(ordQty.getValue()));
                break;
            case ExecType.NEW:
                executionReport.setField(new ExecType(execType));
                executionReport.setField(new OrdStatus(OrdStatus.NEW));
                executionReport.setField(new LeavesQty(ordQty.getValue()));
                break;
            case ExecType.FILL:
                executionReport.setField(new ExecType(ExecType.TRADE));
                executionReport.setField(new OrdStatus(OrdStatus.FILLED));
                executionReport.setField(new LeavesQty(ordQty.getValue()));
                break;
            case ExecType.PARTIAL_FILL:
                executionReport.setField(new ExecType(ExecType.TRADE));
                executionReport.setField(new OrdStatus(OrdStatus.PARTIALLY_FILLED));
                executionReport.setField(new LeavesQty(ordQty.getValue()));
                break;
            case ExecType.PENDING_CANCEL:
                executionReport.setField(new ExecType(execType));
                executionReport.setField(new OrdStatus(OrdStatus.PENDING_CANCEL));
                executionReport.setField(new LeavesQty(0));
                break;
            case ExecType.CANCELED:
                executionReport.setField(new ExecType(execType));
                executionReport.setField(new OrdStatus(OrdStatus.CANCELED));
                executionReport.setField(new LeavesQty(0));
                break;
            case ExecType.PENDING_REPLACE:
                executionReport.setField(new ExecType(execType));
                executionReport.setField(new OrdStatus(OrdStatus.PENDING_REPLACE));
                executionReport.setField(new LeavesQty(0));
                break;
            case ExecType.REPLACED:
                executionReport.setField(new ExecType(execType));
                executionReport.setField(new LeavesQty(0));
//                executionReport.setField(new OrdStatus(OrdStatus.REPLACED));
                break;
            case ExecType.REJECTED:
                executionReport.setField(new ExecType(execType));
                executionReport.setField(new OrdStatus(OrdStatus.REJECTED));
                executionReport.setField(new LeavesQty(0));
                break;
            default:
                break;
        }

        executionReport.setField(new OrderID(ordId));
        executionReport.setField(new ExecID(execId));
        executionReport.setField(new Symbol(symbol.getValue()));
        executionReport.setField(new Side(side.getValue()));
        executionReport.setField(new OrderQty(ordQty.getValue()));
        executionReport.setField(new CumQty(ordQty.getValue()));
        executionReport.setField(new AvgPx(price.getValue()));
        executionReport.setField(new LastPx(price.getValue()));
        executionReport.setField(new ClOrdID(clOrdID.getValue()));
        executionReport.setField(new OrderQty2(ordQty.getValue()));

        return executionReport;
    }

    private void addNewOrder(CharField side, Order order) {
        if (side.getValue() == (Side.BUY)) {
            this.mainController.addBuy(order);
        } else {
            this.mainController.addSell(order);
        }
        this.mainController.setChanged(true);
    }

    private ExecutionReport cancelOrder(StringField origClOrdID, StringField clOrdID, String orderId, String execId, StringField symbol, CharField side, DoubleField size, DoubleField price) {
        if (side.getValue() == (Side.BUY)) {
            this.mainController.getBuy().removeIf(b -> b.getOrderID().equals(origClOrdID.getValue()));
            this.mainController.reorderBookBuyTableView();

        } else {
            this.mainController.getSell().removeIf(s -> s.getOrderID().equals(origClOrdID.getValue()));
            this.mainController.reorderBookSellTableView();
        }
        this.mainController.setChanged(true);
        // StringField clOrdID, String ordId, String execId, StringField symbol, CharField side, DoubleField price, DoubleField ordQty
        return lookForNewTrade(clOrdID, orderId, execId, symbol, side, size, price);
    }

    private ExecutionReport replaceOrder(StringField origClOrdID, StringField clOrdID, String orderId, String execId, StringField symbol,
                              CharField side, DoubleField size, DoubleField priceOrder, Order order) {
        if (side.getValue() == (Side.BUY)) {
            for (Order o : this.mainController.getBuy()) {
                String orderID = o.getOrderID();
                if (orderID.equals(order.getOrderID())) {
                    o.setPrice(order.getPrice());
                    o.setSize(order.getSize());
                }
                this.mainController.reorderBookBuyTableView();

            }
        } else {
            for (Order o : this.mainController.getSell()) {
                String orderID = o.getOrderID();
                if (orderID.equals(order.getOrderID())) {
                    o.setPrice(order.getPrice());
                    o.setSize(order.getSize());
                }
            }
            this.mainController.reorderBookSellTableView();
        }
        this.mainController.setChanged(true);
        return lookForNewTrade(clOrdID, orderId, execId, symbol, side, size, priceOrder);
    }

    private boolean containsOrder(CharField side, String orderId) {
        if (side.getValue() == (Side.BUY)) {
            return this.mainController.getBuy().stream().anyMatch(b -> b.getOrderID().equals(orderId));
        } else {
            return this.mainController.getSell().stream().anyMatch(b -> b.getOrderID().equals(orderId));
        }
    }

    private boolean duplicatedOrderId(StringField clOrdID) {
        return this.mainController.getBuy().stream().anyMatch(b -> b.getOrderID().equals(clOrdID.getValue()))
            || this.mainController.getSell().stream().anyMatch(s -> s.getOrderID().equals(clOrdID.getValue()))
            || this.mainController.actionTableView.getItems().stream().anyMatch(s -> s.getBuyID().equals(clOrdID.getValue())
            || s.getSellID().equals(clOrdID.getValue()) );
    }

    private ExecutionReport lookForNewTrade(StringField clOrdID, String ordId, String execId, StringField symbol, CharField side, DoubleField price, DoubleField ordQty) {
        try {
            Order topBuy = this.mainController.getBuy().size() > 0 ? this.mainController.getOrderedBuy().get(0) : null;
            Order topSell = this.mainController.getSell().size() > 0 ? this.mainController.getOrderedSell().get(0) : null;
            if (topBuy != null && topSell != null) {
                if (topBuy.getPrice().equals(topSell.getPrice())) {
                    this.mainController.setChanged(true);
                    //Fill
                    if (topBuy.getSize().equals(topSell.getSize())) {
                        logger.info("################################ TRADE FILL");
                        this.mainController.getBuy().remove(topBuy);
                        this.mainController.orderBookBuyTableView.getItems().remove(topBuy);
                        this.mainController.getSell().remove(topSell);
                        this.mainController.orderBookSellTableView.getItems().remove(topSell);
                        printTrade(topBuy, topSell);
                        this.mainController.orderBookSellTableView.getItems().remove(topSell);
                        //Type type, String orderIDBuy, String orderIDSell, String origOrderID, LocalDateTime time, Double size, Double price
                        Action action = new Action(Action.Type.FILL, topBuy.getOrderID(), topSell.getOrderID(), LocalDateTime.now(), topBuy.getSize(), topSell.getSize(), 0d, topSell.getPrice());
                        this.mainController.actionTableView.getItems().add(action);
                        this.mainController.reorderActionTableView();
                        this.mainController.reorderBookBuyTableView();
                        this.mainController.reorderBookSellTableView();
                        return generateExecReport(ExecType.FILL, clOrdID, ordId, execId, symbol, side, ordQty, price);
                    } else {//Partial fill
                        logger.info("################################ TRADE PARTIAL FILL");
                        Double leaveQty;
                        Action action = null;
                        Double originalSize = 0d;
                        if (topBuy.getSize() > topSell.getSize()) {
                            leaveQty = topBuy.getSize() - topSell.getSize();
                            originalSize = topBuy.getSize();
                            topBuy.setSize(leaveQty);
                            this.mainController.getSell().remove(topSell);
                            this.mainController.orderBookSellTableView.getItems().remove(topSell);
                            mainController.orderBookBuyTableView.getItems().remove(0);
                            mainController.orderBookBuyTableView.getItems().add(topBuy);
                            action = new Action(Action.Type.PARTIAL_FILL, topBuy.getOrderID(), topSell.getOrderID(), LocalDateTime.now(), originalSize, topSell.getSize(), leaveQty, topSell.getPrice());
                        } else {
                            leaveQty = topSell.getSize() - topBuy.getSize();
                            originalSize = topSell.getSize();
                            topSell.setSize(leaveQty);
                            this.mainController.getBuy().remove(topBuy);
                            mainController.orderBookBuyTableView.getItems().remove(topBuy);
                            mainController.orderBookSellTableView.getItems().remove(0);
                            mainController.orderBookSellTableView.getItems().add(topSell);
                            action = new Action(Action.Type.PARTIAL_FILL, topBuy.getOrderID(), topSell.getOrderID(), LocalDateTime.now(),topBuy.getSize(),  originalSize, leaveQty, topSell.getPrice());
                        }

                        this.mainController.actionTableView.getItems().add(action);
                        this.mainController.reorderActionTableView();
                        this.printTrade(topBuy, topSell);
                        this.mainController.reorderBookBuyTableView();
                        this.mainController.reorderBookSellTableView();
                        return generateExecReport(ExecType.PARTIAL_FILL, clOrdID, ordId, execId, symbol, side, price, new DoubleField(leaveQty.intValue()));
                    }
                }
            }
        } catch (NoSuchElementException n) {
            this.logger.info("Not possible Best Bid Offer");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void printTrade(Order buy, Order sell) {
        System.out.print("\n\n");
        Constants.orderBookLooger.info("=================================================================================");
        Constants.orderBookLooger.info(".....................................BID.........................................");
        Constants.orderBookLooger.info(buy.toString());
        Constants.orderBookLooger.info(".....................................ASK.........................................");
        Constants.orderBookLooger.info(sell.toString());
        Constants.orderBookLooger.info("=================================================================================\n\n");
    }

    public void messageAnalizer(Message message, String direction) {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Platform.runLater(() -> {
                    mainController.listViewLog.getItems().add(String.format("%s %s", direction, Utils.replaceSOH(message)));
                });
                return null;
            }
        };
        task.run();
    }

}
