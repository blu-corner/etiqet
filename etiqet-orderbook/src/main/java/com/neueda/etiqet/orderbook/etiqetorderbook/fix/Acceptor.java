package com.neueda.etiqet.orderbook.etiqetorderbook.fix;

import com.neueda.etiqet.orderbook.etiqetorderbook.controllers.MainController;
import com.neueda.etiqet.orderbook.etiqetorderbook.entity.Action;
import com.neueda.etiqet.orderbook.etiqetorderbook.entity.Order;
import com.neueda.etiqet.orderbook.etiqetorderbook.utils.Constants;
import com.neueda.etiqet.orderbook.etiqetorderbook.utils.Utils;
import javafx.application.Platform;
import javafx.concurrent.Task;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.*;
import quickfix.field.*;
import quickfix.fix44.ExecutionReport;
import quickfix.fix44.OrderCancelReject;

import javax.print.DocFlavor;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;

public class Acceptor implements Application {

    private final MainController mainController;
    Logger logger = LoggerFactory.getLogger(Acceptor.class);


    public Acceptor(MainController mainController) {
        this.mainController = mainController;
    }

    public static ExecutionReport generateExecReport(char execType, StringField clOrdID, String ordId, String execId, StringField symbol, CharField side, DoubleField price, DoubleField ordQty) {
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
                executionReport.setField(new OrdStatus(OrdStatus.REPLACED));
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

    public void sendExecutionReportAfterCanceling(Order order, FixSession fixSession) {
        List<Message> reports = new ArrayList<>();
        String orderId = RandomStringUtils.randomAlphanumeric(5);
        String execId = RandomStringUtils.randomAlphanumeric(5);

        reports.add(generateExecReport(ExecType.PENDING_CANCEL, new ClOrdID(order.getClOrdID()), Constants.NEW, Constants.NEW, new Symbol(order.getSymbol()), new Side(order.getSide()), new DoubleField(0), new DoubleField(0)));
        reports.add(generateExecReport(ExecType.CANCELED, new ClOrdID(order.getClOrdID()), orderId, execId, new Symbol(order.getSymbol()), new Side(order.getSide()), new DoubleField(0), new DoubleField(0)));
        ExecutionReport tradeWhenCancelling = cancelOrder(null, new ClOrdID(order.getClOrdID()), orderId, execId, new Symbol(order.getSymbol()), new Side(order.getSide()), null, null);
        if (tradeWhenCancelling != null) {
            reports.add(tradeWhenCancelling);
        }
        reports.forEach(r -> {
            try {
                Session.sendToTarget(r, fixSession.getSessionID());
            } catch (SessionNotFound e) {
                e.printStackTrace();
            }
        });
    }

    public void onMessage(Message message, SessionID sessionID) throws FieldNotFound, IncorrectTagValue {
        StringField msgType = message.getHeader().getField(new MsgType());
        StringField clOrdID = message.getField(new ClOrdID());
        StringField symbol = message.getField(new Symbol());
        CharField side = message.getField(new Side());
        String orderId = RandomStringUtils.randomAlphanumeric(5);
        String execId = RandomStringUtils.randomAlphanumeric(5);
        List<Message> reports = new ArrayList<>();
        String clientID = message.getHeader().getField(new SenderCompID()).getValue();
        CharField timeInFoce;
        DoubleField ordQty;
        CharField ordType;
        DoubleField price;
        StringField origClOrdID;

        switch (msgType.getValue()) {
            case MsgType.ORDER_SINGLE:
                price = message.getField(new Price());
                ordQty = message.getField(new OrderQty());
                ordType = message.getField(new OrdType());
                timeInFoce = message.getField(new TimeInForce());
                StringField expireDate = null;
                if (timeInFoce.getValue() == TimeInForce.GOOD_TILL_DATE){
                    expireDate =  message.getField(new ExpireDate());
                }
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

                    Order order = new Order.OrderBuilder()
                        .clOrdID(clOrdID.getValue())
                        .time(Utils.getFormattedStringDate())
                        .orderQty(ordQty.getValue())
                        .price(price.getValue())
                        .symbol(symbol.getValue())
                        .side(side.getValue())
                        .clientID(clientID)
                        .timeInForce(Constants.TIME_IN_FORCE.getContent(timeInFoce.getValue()))
                        .timeToBeRemoved(getTimeToBeRemoved(timeInFoce.getValue(), expireDate))
                        .sessionID(sessionID.toString())
                        .build();

                    addNewOrder(side, order);
                    ExecutionReport finalExecutionReport = lookForNewTrade(clOrdID, orderId, execId, symbol, side, price, ordQty);
                    if (finalExecutionReport != null) {
//                        if (finalExecutionReport.getExecType().equals(new ExecType(ExecType.CANCELED)) &&
//                            (timeInFoce.equals(new TimeInForce(TimeInForce.FILL_OR_KILL)) ||
//                                timeInFoce.equals(new TimeInForce(TimeInForce.IMMEDIATE_OR_CANCEL)))) {
//                            order.setRemoved(true);
//                        }
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
                        if (tradeWhenCancelling != null) {
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
                        // TODO CHECK TIMEINFORCE
                        Order order = new Order.OrderBuilder()
                            .clOrdID(clOrdID.getValue())
                            .time(Utils.getFormattedStringDate())
                            .orderQty(ordQty.getValue())
                            .price(price.getValue())
                            .symbol(symbol.getValue())
                            .side(side.getValue())
                            .clientID(clientID)
                            .timeInForce(null)
                            .sessionID(sessionID.toString())
                            .build();
                        reports.add(generateExecReport(ExecType.PENDING_REPLACE, clOrdID, Constants.NEW, Constants.NEW, symbol, side, ordQty, new DoubleField(0)));
                        reports.add(generateExecReport(ExecType.REPLACED, clOrdID, orderId, execId, symbol, side, ordQty, price));
                        ExecutionReport tradeWhenReplacing = replaceOrder(origClOrdID, clOrdID, orderId, execId, symbol, side, ordQty, price, order);
                        if (tradeWhenReplacing != null) {
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

    private String getTimeToBeRemoved(char timeInForce, StringField expireDate) {
        String timeToBeRemoved = StringUtils.EMPTY;
        String[] endTimeSplit;
        LocalDateTime limitTime;
        String endTime;
        LocalDate localDateNow = LocalDate.now();
        LocalTime localTimeNow = LocalTime.now();
        switch (timeInForce){
            case TimeInForce.DAY:
                endTime = Utils.getConfig(Constants.ACCEPTOR_ROLE, Constants.CONF_END_TIME);
                endTimeSplit = endTime.split(":");
                limitTime = LocalDateTime.of(localDateNow, LocalTime.of(Integer.parseInt(endTimeSplit[0]), Integer.parseInt(endTimeSplit[1])));
                timeToBeRemoved = Utils.getFormattedDateFromLocalDateTime(limitTime);
                break;
                case TimeInForce.GOOD_TILL_CANCEL:
                case TimeInForce.AT_THE_CLOSE:
                case TimeInForce.GOOD_THROUGH_CROSSING:
                    // If at the close, it will be canceled when the application is stopped
                    // Good through crossing is not considered
                    break;
                case TimeInForce.AT_THE_OPENING:
                    endTime = Utils.getConfig(Constants.ACCEPTOR_ROLE, Constants.CONF_START_TIME);
                    endTimeSplit = endTime.split(":");
                    limitTime = LocalDateTime.of(localDateNow.plusDays(1), LocalTime.of(Integer.parseInt(endTimeSplit[0]), Integer.parseInt(endTimeSplit[1])));
                    timeToBeRemoved = Utils.getFormattedDateFromLocalDateTime(limitTime);
                    break;
                case TimeInForce.IMMEDIATE_OR_CANCEL:
                case TimeInForce.FILL_OR_KILL:
                    limitTime = LocalDateTime.of(localDateNow, localTimeNow);
                    timeToBeRemoved = Utils.getFormattedDateFromLocalDateTime(limitTime);
                    break;
                case TimeInForce.GOOD_TILL_DATE:
                    timeToBeRemoved = expireDate.getValue();
                    break;
        }
        return timeToBeRemoved;
    }


    private boolean containsOrder(CharField side, String orderId) {
        if (side.getValue() == (Side.BUY)) {
            return this.mainController.getBuy().stream().anyMatch(b -> b.getClOrdID().equals(orderId));
        } else {
            return this.mainController.getSell().stream().anyMatch(b -> b.getClOrdID().equals(orderId));
        }
    }

    private boolean duplicatedOrderId(StringField clOrdID) {
        return this.mainController.getBuy().stream().anyMatch(b -> b.getClOrdID().equals(clOrdID.getValue()))
            || this.mainController.getSell().stream().anyMatch(s -> s.getClOrdID().equals(clOrdID.getValue()))
            || this.mainController.actionTableView.getItems().stream().anyMatch(s -> s.getBuyID().equals(clOrdID.getValue())
            || s.getSellID().equals(clOrdID.getValue()));
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


    private void addNewOrder(CharField side, Order order) {
        if (side.getValue() == (Side.BUY)) {
            this.mainController.addBuy(order);
        } else {
            this.mainController.addSell(order);
        }
        this.mainController.setChanged(true);
    }

    private void removeOrder(CharField side, Order order) {
        if (side.getValue() == (Side.BUY)) {
            this.mainController.removeBuy(order);
        } else {
            this.mainController.removeSell(order);
        }
        this.mainController.setChanged(true);
    }

    public ExecutionReport cancelOrder(StringField origClOrdID, StringField clOrdID, String orderId, String execId, StringField symbol, CharField side, DoubleField size, DoubleField price) {
        if (side.getValue() == (Side.BUY)) {
            if (origClOrdID == null) {
                this.mainController.getBuy().removeIf(b -> b.getClOrdID().equals(clOrdID.getValue()));
            } else {
                this.mainController.getBuy().removeIf(b -> b.getClOrdID().equals(origClOrdID.getValue()));
            }
            this.mainController.reorderBookBuyTableView();

        } else {
            if (origClOrdID == null) {
                this.mainController.getSell().removeIf(b -> b.getClOrdID().equals(clOrdID.getValue()));
            } else {
                this.mainController.getSell().removeIf(s -> s.getClOrdID().equals(origClOrdID.getValue()));
            }
            this.mainController.reorderBookSellTableView();
        }
        this.mainController.setChanged(true);
        // StringField clOrdID, String ordId, String execId, StringField symbol, CharField side, DoubleField price, DoubleField ordQty
        return lookForNewTrade(clOrdID, orderId, execId, symbol, side, size, price);
    }

    private ExecutionReport replaceOrder(StringField origClOrdID, StringField clOrdID, String orderId, String execId, StringField symbol,
                                         CharField side, DoubleField size, DoubleField priceOrder, Order order) {
        if (side.getValue() == Side.BUY) {
            Optional<Order> toReplace = this.mainController.getBuy().stream().filter(buy -> buy.getClOrdID().equals(origClOrdID.getValue())).findFirst();
            if (toReplace.isPresent()){
                toReplace.get().setPrice(order.getPrice());
                toReplace.get().setOrderQty(order.getOrderQty());
                this.mainController.reorderBookBuyTableView();
            }

        } else {
            Optional<Order> toReplace = this.mainController.getSell().stream().filter(sell -> sell.getClOrdID().equals(origClOrdID.getValue())).findFirst();
            if (toReplace.isPresent()){
                toReplace.get().setPrice(order.getPrice());
                toReplace.get().setOrderQty(order.getOrderQty());
                this.mainController.reorderBookSellTableView();
            }
;
        }
        this.mainController.setChanged(true);
        return lookForNewTrade(clOrdID, orderId, execId, symbol, side, size, priceOrder);
    }


    private ExecutionReport lookForNewTrade(StringField clOrdID, String ordId, String execId, StringField symbol, CharField side, DoubleField price, DoubleField ordQty) {
        try {
            Order topBuy = this.mainController.getBuy().size() > 0 ? this.mainController.getOrderedBuy().get(0) : null;
            Order topSell = this.mainController.getSell().size() > 0 ? this.mainController.getOrderedSell().get(0) : null;
            String topBuyClientID = topBuy != null ? topBuy.getClientID() : "";
            String topSellClientID = topSell != null ? topSell.getClientID() : "";
            if (topBuy != null && topSell != null) {
                if (topBuy.getPrice().equals(topSell.getPrice())) {
                    this.mainController.setChanged(true);
                    if (topBuy.getOrderQty().equals(topSell.getOrderQty())) {
                        return handleFullFill(clOrdID, ordId, execId, symbol, side, price, ordQty, topBuy, topSell, topBuyClientID, topSellClientID);
                    } else {
                        return handlePartialFill(clOrdID, ordId, execId, symbol, side, price, topBuy, topSell, topBuyClientID, topSellClientID);
                    }
                } else if (areTimeInForceFOKorIOC(topBuy, topSell)) {
//                    Action action = new Action(Constants.Type.CANCELED, topBuy.getClOrdID(), topSell.getClOrdID(), topBuyClientID,
//                        topBuy.getTimeInForce(), topSell.getTimeInForce(), topSellClientID, Utils.getFormattedStringDate(), 0d, topSell.getOrderQty(), 0d, topSell.getPrice());
                    Action action = new Action.ActionBuilder()
                        .type(Constants.Type.CANCELED)
                        .buyID(topBuy.getClOrdID())
                        .sellID(topSell.getClOrdID())
                        .buyClientID(topBuyClientID)
                        .sellClientID(topSellClientID)
                        .timeInForceBuy(topBuy.getTimeInForce())
                        .timeInForceSell(topSell.getTimeInForce())
                        .time(Utils.getFormattedStringDate())
                        .sellSize(topSell.getOrderQty())
                        .agreedPrice(topSell.getPrice())
                        .build();

                    addAction(topBuy, topSell, action);
                    topBuy.setRemoved(true);
                    topSell.setRemoved(true);
                    return generateExecReport(ExecType.CANCELED, clOrdID, ordId, execId, symbol, side, price, new DoubleField(0));
                }
            } else {
                if (topBuy != null && isTimeInForceFOKorIOC(topBuy)) {
                    Action action = new Action.ActionBuilder()
                        .type(Constants.Type.CANCELED)
                        .buyID(topBuy.getClOrdID())
                        .buyClientID(topBuyClientID)
                        .sellClientID(topSellClientID)
                        .timeInForceBuy(topBuy.getTimeInForce())
                        .time(Utils.getFormattedStringDate())
                        .buySize(topBuy.getOrderQty())
                        .agreedPrice(topBuy.getPrice())
                        .build();
                    topBuy.setRemoved(true);
                    addAction(topBuy, new Order(), action);
                    return generateExecReport(ExecType.CANCELED, clOrdID, ordId, execId, symbol, side, price, new DoubleField(0));

                } else if (topSell != null && isTimeInForceFOKorIOC(topSell)) {
                    Action action = new Action.ActionBuilder()
                        .type(Constants.Type.CANCELED)
                        .sellID(topSell.getClOrdID())
                        .sellClientID(topSellClientID)
                        .buyClientID(topBuyClientID)
                        .timeInForceSell(topSell.getTimeInForce())
                        .time(Utils.getFormattedStringDate())
                        .sellSize(topSell.getOrderQty())
                        .build();

                    addAction(new Order(), topSell, action);
                    topSell.setRemoved(true);
                    return generateExecReport(ExecType.CANCELED, clOrdID, ordId, execId, symbol, side, price, new DoubleField(0));
                }
            }
        } catch (NoSuchElementException n) {
            this.logger.info("Not possible Best Bid Offer");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private ExecutionReport handlePartialFill(StringField clOrdID, String ordId, String execId, StringField symbol, CharField side,
                                              DoubleField price, Order topBuy, Order topSell, String topBuyClientID, String topSellClientID) {
        logger.info("################################ TRADE PARTIAL FILL");
        Double leaveQty = 0d;
        Action action;

        if (isTimeInForceFOK(topBuy)) {
            action = new Action.ActionBuilder()
                .type(Constants.Type.CANCELED)
                .buyID(topBuy.getClOrdID())
                .buyClientID(topBuyClientID)
                .sellClientID(topSellClientID)
                .timeInForceBuy(topBuy.getTimeInForce())
                .time(Utils.getFormattedStringDate())
                .buySize(topBuy.getOrderQty())
                .build();

            addAction(topBuy, new Order(), action);
            topBuy.setRemoved(true);
            return generateExecReport(ExecType.CANCELED, clOrdID, ordId, execId, symbol, side, price, new DoubleField(leaveQty.intValue()));
        } else if (isTimeInForceFOK(topSell)) {
            action = new Action.ActionBuilder()
                .type(Constants.Type.CANCELED)
                .sellID(topSell.getClOrdID())
                .buyClientID(topBuyClientID)
                .sellClientID(topSellClientID)
                .timeInForceSell(topSell.getTimeInForce())
                .time(Utils.getFormattedStringDate())
                .sellSize(topSell.getOrderQty())
                .build();
            addAction(new Order(), topSell, action);
            topSell.setRemoved(true);
            return generateExecReport(ExecType.CANCELED, clOrdID, ordId, execId, symbol, side, price, new DoubleField(leaveQty.intValue()));
        } else {
            if (topBuy.getOrderQty() > topSell.getOrderQty()) {
                Double originalBuySize = topBuy.getOrderQty();
                leaveQty = topBuy.getOrderQty() - topSell.getOrderQty();

                topBuy.setOrderQty(leaveQty);
                this.mainController.getSell().remove(topSell);
                this.mainController.orderBookSellTableView.getItems().remove(topSell);
                mainController.orderBookBuyTableView.getItems().remove(0);
                mainController.orderBookBuyTableView.getItems().add(topBuy);

                action = new Action.ActionBuilder()
                    .type(Constants.Type.PARTIAL_FILL)
                    .buyID(topBuy.getClOrdID())
                    .sellID(topSell.getClOrdID())
                    .buyClientID(topBuyClientID)
                    .sellClientID(topSellClientID)
                    .timeInForceBuy(topBuy.getTimeInForce())
                    .timeInForceSell(topSell.getTimeInForce())
                    .time(Utils.getFormattedStringDate())
                    .buySize(originalBuySize)
                    .sellSize(topSell.getOrderQty())
                    .leaveQty(leaveQty)
                    .agreedPrice(topSell.getPrice())
                    .build();

            } else {
                Double originalSellSize = topSell.getOrderQty();
                leaveQty = topSell.getOrderQty() - topBuy.getOrderQty();
                topSell.setOrderQty(leaveQty);
                this.mainController.getBuy().remove(topBuy);
                mainController.orderBookBuyTableView.getItems().remove(topBuy);
                mainController.orderBookSellTableView.getItems().remove(0);
                mainController.orderBookSellTableView.getItems().add(topSell);

                action = new Action.ActionBuilder()
                    .type(Constants.Type.PARTIAL_FILL)
                    .buyID(topBuy.getClOrdID())
                    .sellID(topSell.getClOrdID())
                    .buyClientID(topBuyClientID)
                    .sellClientID(topSellClientID)
                    .timeInForceBuy(topBuy.getTimeInForce())
                    .timeInForceSell(topSell.getTimeInForce())
                    .time(Utils.getFormattedStringDate())
                    .buySize(topBuy.getOrderQty())
                    .sellSize(originalSellSize)
                    .leaveQty(leaveQty)
                    .agreedPrice(topSell.getPrice())
                    .build();

            }
        }
        addAction(topBuy, topSell, action);
        return generateExecReport(ExecType.PARTIAL_FILL, clOrdID, ordId, execId, symbol, side, price, new DoubleField(leaveQty.intValue()));
    }

    public void addAction(Order topBuy, Order topSell, Action action) {
        this.mainController.actionTableView.getItems().add(action);
        this.mainController.reorderActionTableView();
        this.printTrade(topBuy, topSell);
        this.mainController.reorderBookBuyTableView();
        this.mainController.reorderBookSellTableView();
    }

    private boolean isTimeInForceFOK(Order order) {
        if (order == null || order.getTimeInForce() == null) return false;
        return order.getTimeInForce().equals(Constants.TIME_IN_FORCE.getContent(TimeInForce.FILL_OR_KILL));
    }

    private boolean isTimeInForceIOC(Order order) {
        if (order == null || order.getTimeInForce() == null) return false;
        return order.getTimeInForce().equals(Constants.TIME_IN_FORCE.getContent(TimeInForce.IMMEDIATE_OR_CANCEL));
    }

    private boolean areTimeInForceFOKorIOC(Order buy, Order sell) {
        return isTimeInForceFOK(buy) || isTimeInForceFOK(sell) || isTimeInForceIOC(buy) || isTimeInForceIOC(sell);
    }

    private boolean isTimeInForceFOKorIOC(Order order) {
        return isTimeInForceFOK(order) || isTimeInForceIOC(order);
    }

    private ExecutionReport handleFullFill(StringField clOrdID, String ordId, String execId, StringField symbol, CharField side,
                                           DoubleField price, DoubleField ordQty, Order topBuy, Order topSell, String topBuyClientID, String topSellClientID) {
        logger.info("################################ TRADE FILL");
        this.mainController.getBuy().remove(topBuy);
        this.mainController.orderBookBuyTableView.getItems().remove(topBuy);
        this.mainController.getSell().remove(topSell);
        this.mainController.orderBookSellTableView.getItems().remove(topSell);
        printTrade(topBuy, topSell);
        this.mainController.orderBookSellTableView.getItems().remove(topSell);

        Action action = new Action.ActionBuilder()
            .type(Constants.Type.FILL)
            .buyID(topBuy.getClOrdID())
            .sellID(topSell.getClOrdID())
            .buyClientID(topBuyClientID)
            .sellClientID(topSellClientID)
            .timeInForceBuy(topBuy.getTimeInForce())
            .timeInForceSell(topSell.getTimeInForce())
            .time(Utils.getFormattedStringDate())
            .buySize(topBuy.getOrderQty())
            .sellSize(topSell.getOrderQty())
            .agreedPrice(topSell.getPrice())
            .build();
        this.mainController.actionTableView.getItems().add(action);
        this.mainController.reorderActionTableView();
        this.mainController.reorderBookBuyTableView();
        this.mainController.reorderBookSellTableView();
        return generateExecReport(ExecType.FILL, clOrdID, ordId, execId, symbol, side, ordQty, price);
    }

    private String getClientID(Order topBuy) {
        return topBuy.getClientID();
    }

    private void printTrade(Order buy, Order sell) {
        System.out.print("\n\n");
        Constants.orderBook.info("=================================================================================");
        Constants.orderBook.info(".....................................BID.........................................");
        Constants.orderBook.info(buy != null ? buy.toString(): "");
        Constants.orderBook.info(".....................................ASK.........................................");
        Constants.orderBook.info(sell != null ? sell.toString() : "");
        Constants.orderBook.info("=================================================================================\n\n");
    }

    public void messageAnalizer(Message message, String direction) {
        Task<Void> task = new Task<>() {
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
