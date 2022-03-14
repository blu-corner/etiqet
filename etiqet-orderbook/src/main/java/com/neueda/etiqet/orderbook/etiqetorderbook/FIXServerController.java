package com.neueda.etiqet.orderbook.etiqetorderbook;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.*;
import quickfix.field.*;
import quickfix.fix44.ExecutionReport;
import quickfix.fix44.OrderCancelReject;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class FIXServerController implements Initializable, Runnable, Application {

    public static final String NEW = "0000";
    public static final String NONE = "NONE";
    Logger logger = LoggerFactory.getLogger(FIXServerController.class);
    Logger orderBookLooger = LoggerFactory.getLogger("ORDER BOOK");
    public static final char SOH = '\u0001';
    public static final char VERTICAL_BAR = '\u007C';
    private List<Order> buy;
    private List<Order> sell;
    private boolean changed;

    @FXML
    private TableView<Order> orderBookBuyTableView;

    @FXML
    private TableView<Order> orderBookSellTableView;

    @FXML
    private TableView<Action> actionTableView;

    @FXML
    public TableColumn<Order, String> orderIDBuyTableColumn;
    public TableColumn<Order, String> timeBuyTableColumn;
    public TableColumn<Order, String> sizeBuyTableColumn;
    public TableColumn<Order, String> priceBuyTableColumn;
    public TableColumn<Order, String> orderIDSellTableColumn;
    public TableColumn<Order, String> timeSellTableColumn;
    public TableColumn<Order, String> sizeSellTableColumn;
    public TableColumn<Order, String> priceSellTableColumn;

    public TableColumn<Action, String> actionTypeTableColumn;
    public TableColumn<Action, String> actionOrderIdBuyTableColumn;
    public TableColumn<Action, String> actionOrderIdSellTableColumn;
    public TableColumn<Action, String> actionOrigOrderIDTableColumn;
    public TableColumn<Action, String> actionTimeTableColumn;
    public TableColumn<Action, String> actionSizeTableColumn;
    public TableColumn<Action, String> actionPriceTableColumn;


    public void startAcceptor(ActionEvent actionEvent) {
        URL resource = getClass().getClassLoader().getResource("server.cfg");
        Acceptor acceptor = null;
        try {
            SessionSettings sessionSettings = new SessionSettings(new FileInputStream(new File(resource.toURI())));
            MessageStoreFactory messageStoreFactory = new FileStoreFactory(sessionSettings);
            LogFactory logFactory = new FileLogFactory(sessionSettings);
            MessageFactory messageFactory = new DefaultMessageFactory();
            acceptor = new SocketAcceptor(this, messageStoreFactory, sessionSettings, logFactory, messageFactory);
            acceptor.start();
            Thread thread = new Thread(this);
            thread.start();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.buy = new ArrayList<>();
        this.sell = new ArrayList<>();
        this.changed = true;

        actionTableView.setStyle("-fx-selection-bar: green; -fx-selection-bar-non-focused: green;");
        orderBookBuyTableView.setStyle("-fx-selection-bar: green; -fx-selection-bar-non-focused: green;");
        orderBookSellTableView.setStyle("-fx-selection-bar: green; -fx-selection-bar-non-focused: green;");
//        orderBookBuyTableView.getSelectionModel().setCellSelectionEnabled(true);
//        orderBookBuyTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        orderBookBuyTableView.setOnMouseClicked(e -> {
            cellToClipBoard(e);
        });


        orderIDBuyTableColumn.setCellValueFactory(new PropertyValueFactory<>("OrderID"));
        timeBuyTableColumn.setCellValueFactory(new PropertyValueFactory<>("Time"));
        sizeBuyTableColumn.setCellValueFactory(new PropertyValueFactory<>("Size"));
        priceBuyTableColumn.setCellValueFactory(new PropertyValueFactory<>("Price"));

        orderIDSellTableColumn.setCellValueFactory(new PropertyValueFactory<>("OrderID"));
        timeSellTableColumn.setCellValueFactory(new PropertyValueFactory<>("Time"));
        sizeSellTableColumn.setCellValueFactory(new PropertyValueFactory<>("Size"));
        priceSellTableColumn.setCellValueFactory(new PropertyValueFactory<>("Price"));

        actionTypeTableColumn.setCellValueFactory(new PropertyValueFactory<>("Type"));
        actionOrderIdBuyTableColumn.setCellValueFactory(new PropertyValueFactory<>("BuyID"));
        actionOrderIdSellTableColumn.setCellValueFactory(new PropertyValueFactory<>("SellID"));
        actionOrigOrderIDTableColumn.setCellValueFactory(new PropertyValueFactory<>("OrigID"));
        actionTimeTableColumn.setCellValueFactory(new PropertyValueFactory<>("Time"));
        actionSizeTableColumn.setCellValueFactory(new PropertyValueFactory<>("Size"));
        actionPriceTableColumn.setCellValueFactory(new PropertyValueFactory<>("Price"));


    }

    private void cellToClipBoard(MouseEvent e) {
        try{
            final Clipboard clipboard = Clipboard.getSystemClipboard();
            final ClipboardContent content = new ClipboardContent();
            String targetString = e.getTarget().toString();
            int firstQuote = targetString.indexOf('"');
            int secondQuote = targetString.indexOf('"', firstQuote + 1);
            content.putString(targetString.substring(firstQuote + 1, secondQuote));
            clipboard.setContent(content);
        }catch (Exception ex){
            this.logger.error(ex.getLocalizedMessage());
        }

    }

    private ObservableList<Order> getOrders() {
        ObservableList<Order> orders = FXCollections.observableArrayList();
        orders.add(new Order("4579", LocalDateTime.now(), 34d, 2d));
        return orders;
    }

    public List<Order> getBuy() {
        return buy;
    }

    public void addBuy(Order buy) {
        this.buy.add(buy);
        List<Order> tempList = new ArrayList<>(orderBookBuyTableView.getItems());
        tempList.add(buy);
        tempList = tempList.stream().sorted(Comparator.comparing(Order::getPrice)).collect(Collectors.toList());
        orderBookBuyTableView.getItems().clear();
        tempList.forEach( b -> {
            orderBookBuyTableView.getItems().add(b);
        });
        orderBookBuyTableView.getSelectionModel().clearAndSelect(0);
    }

    public List<Order> getSell() {
        return sell;
    }

    public void addSell(Order sell) {
        this.sell.add(sell);
        List<Order> tempList = new ArrayList<>(orderBookSellTableView.getItems());
        tempList.add(sell);
        tempList = tempList.stream().sorted(Comparator.comparing(Order::getPrice).reversed()).collect(Collectors.toList());
        orderBookSellTableView.getItems().clear();
        tempList.forEach( b -> {
            orderBookSellTableView.getItems().add(b);
        });
        orderBookSellTableView.getSelectionModel().clearAndSelect(0);
    }

    @Override
    public void onCreate(SessionID sessionID) {
        this.logger.info("onCreate -> sessionID: {}", sessionID);
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
        this.logger.info("[>>>>OUT>>>]:toAdmin -> message: {} / sessionID: {}", replaceSOH(message), sessionID);
    }

    @Override
    public void fromAdmin(Message message, SessionID sessionID) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, RejectLogon {
        this.logger.info("[<<<<<IN<<<]:fromAdmin -> message: {} / sessionID: {}", replaceSOH(message), sessionID);
    }

    @Override
    public void toApp(Message message, SessionID sessionID) throws DoNotSend {
        this.logger.info("[>>>>OUT>>>]:toApp -> message: {} / sessionID: {}", replaceSOH(message), sessionID);
    }

    @Override
    public void fromApp(Message message, SessionID sessionID) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType {
        this.logger.info("[<<<<<IN<<<]:fromApp -> message: {} / sessionID: {}", replaceSOH(message), sessionID);
        onMessage(message, sessionID);
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(1000);
                if (this.changed) {
                    System.out.print("\n\n");
                    this.orderBookLooger.info("=================================================================================");
                    this.orderBookLooger.info(".....................................BID.........................................");
                    this.getBuy().stream().sorted(Comparator.comparing(Order::getPrice)).forEach(System.out::println);
                    this.orderBookLooger.info(".....................................OFFER.......................................");
                    this.getSell().stream().sorted(Comparator.comparing(Order::getPrice, Comparator.reverseOrder())).forEach(System.out::println);
                    this.orderBookLooger.info("=================================================================================\n\n");
                    this.changed = false;
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void onMessage(Message message, SessionID sessionID) throws FieldNotFound, IncorrectTagValue {
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
                    reports.add(generateExecReport(ExecType.PENDING_NEW, clOrdID, NEW, NEW, symbol, side, price, ordQty));
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
                    reports.add(rejectDuplicated(CxlRejResponseTo.ORDER_CANCEL_REQUEST, CxlRejReason.DUPLICATE_CLORDID_RECEIVED));
                } else {
                    if (!containsOrder(side, origClOrdID.getValue())) {
                        reports.add(rejectDuplicated(CxlRejResponseTo.ORDER_CANCEL_REQUEST, CxlRejReason.UNKNOWN_ORDER));
                    } else {
                        logger.info("################################ ORDER CANCEL REQUEST");
                        //clOrdID, orderId, execId, symbol, side, price, ordQty
                        cancelOrder(origClOrdID, clOrdID, orderId, execId, symbol, side, null, null );
                        reports.add(generateExecReport(ExecType.PENDING_CANCEL, clOrdID, NEW, NEW, symbol, side, new DoubleField(0), new DoubleField(0)));
                        reports.add(generateExecReport(ExecType.CANCELED, clOrdID, orderId, execId, symbol, side, new DoubleField(0), new DoubleField(0)));

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
                    reports.add(rejectDuplicated(CxlRejResponseTo.ORDER_CANCEL_REPLACE_REQUEST, CxlRejReason.DUPLICATE_CLORDID_RECEIVED));
                } else {
                    if (!containsOrder(side, origClOrdID.getValue())) {
                        reports.add(rejectDuplicated(CxlRejResponseTo.ORDER_CANCEL_REPLACE_REQUEST, CxlRejReason.UNKNOWN_ORDER));
                    } else {
                        logger.info("################################ ORDER CANCEL REPLACE REQUEST");
                        replaceOrder(side, new Order(origClOrdID.getValue(), LocalDateTime.now(), ordQty.getValue(), price.getValue()));
                        reports.add(generateExecReport(ExecType.PENDING_REPLACE, clOrdID, NEW, NEW, symbol, side, ordQty, new DoubleField(0)));
                        reports.add(generateExecReport(ExecType.REPLACED, clOrdID, orderId, execId, symbol, side, ordQty, price));
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



    private Message rejectDuplicated(int rejResponseTo, int cxlRejReason) {
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
        orderCancelReject.setField(new OrderID(NONE));
        orderCancelReject.setField(new ClOrdID(clOrdId));
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
                break;
            case ExecType.CANCELED:
                executionReport.setField(new ExecType(execType));
                executionReport.setField(new OrdStatus(OrdStatus.CANCELED));
                break;
            case ExecType.PENDING_REPLACE:
                executionReport.setField(new ExecType(execType));
                executionReport.setField(new OrdStatus(OrdStatus.PENDING_REPLACE));
                break;
            case ExecType.REPLACED:
                executionReport.setField(new ExecType(execType));
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

    private String replaceSOH(Message message) {
        String content = message.toString();
        return content.replace(SOH, VERTICAL_BAR);
    }

    private void addNewOrder(CharField side, Order order) {
        if (side.getValue() == (Side.BUY)) {
            addBuy(order);
        } else {
            addSell(order);
        }
        this.changed = true;
    }



    private ExecutionReport cancelOrder(StringField origClOrdID, StringField clOrdID, String orderId, String execId, StringField symbol, CharField side, DoubleField size, DoubleField price) {
        if (side.getValue() == (Side.BUY)) {
            this.getBuy().removeIf(b -> b.getOrderID().equals(origClOrdID));
            orderBookBuyTableView.getItems().removeIf(b -> b.getOrderID().equals(origClOrdID));

        } else {
            this.getSell().removeIf(s -> s.getOrderID().equals(origClOrdID));
            orderBookSellTableView.getItems().removeIf(b -> b.getOrderID().equals(origClOrdID));
        }
        this.changed = true;
        // StringField clOrdID, String ordId, String execId, StringField symbol, CharField side, DoubleField price, DoubleField ordQty
        return lookForNewTrade(clOrdID, orderId, execId, symbol, side, size, price);
    }



    private void replaceOrder(CharField side, Order order) {
        if (side.getValue() == (Side.BUY)) {
            for (Order o : this.getBuy()) {
                String orderID = o.getOrderID();
                if (orderID.equals(order.getOrderID())) {
                    this.getBuy().remove(o);
                    this.getBuy().add(order);
                }
            }

        } else {
            for (Order o : this.getSell()) {
                String orderID = o.getOrderID();
                if (orderID.equals(order.getOrderID())) {
                    this.getSell().remove(o);
                    this.getSell().add(order);
                }
            }
        }
        this.changed = true;
    }

    private boolean containsOrder(CharField side, String orderId) {
        if (side.getValue() == (Side.BUY)) {
            return this.getBuy().stream().anyMatch(b -> b.getOrderID().equals(orderId));
        } else {
            return this.getSell().stream().anyMatch(b -> b.getOrderID().equals(orderId));
        }
    }

    private boolean duplicatedOrderId(StringField clOrdID) {
        return this.getBuy().stream().anyMatch(b -> b.getOrderID().equals(clOrdID.getValue()))
            || this.getSell().stream().anyMatch(s -> s.getOrderID().equals(clOrdID.getValue()));
    }


    private ExecutionReport lookForNewTrade(StringField clOrdID, String ordId, String execId, StringField symbol, CharField side, DoubleField price, DoubleField ordQty) {
        try {
            Order topBuy = this.getBuy().stream().max(Comparator.comparing(Order::getPrice)).orElseThrow();
            Order topSell = this.getSell().stream().min(Comparator.comparing(Order::getPrice)).orElseThrow();

            if (topBuy.getPrice().equals(topSell.getPrice())) {
                this.changed = true;
                //Fill
                if (topBuy.getSize().equals(topSell.getSize())) {
                    logger.info("################################ TRADE FILL");
                    this.getBuy().remove(topBuy);
                    orderBookBuyTableView.getItems().remove(topBuy);
                    this.getSell().remove(topSell);
                    printTrade(topBuy, topSell);
                    orderBookSellTableView.getItems().remove(topSell);
                    //Type type, String orderIDBuy, String orderIDSell, String origOrderID, LocalDateTime time, Double size, Double price
                    Action action = new Action(Action.Type.FILL,topBuy.getOrderID(), topSell.getOrderID(), null, LocalDateTime.now(), topBuy.getSize(), topBuy.getPrice());
                    actionTableView.getItems().add(action);
                    actionTableView.getSelectionModel().clearAndSelect(0);
                    return generateExecReport(ExecType.FILL, clOrdID, ordId, execId, symbol, side, ordQty, price);
                } else {//Partial fill
                    logger.info("################################ TRADE PARTIAL FILL");
                    Double leaveQty;
                    if (topBuy.getSize() > topSell.getSize()) {
                        leaveQty = topBuy.getSize() - topSell.getSize();
                        topBuy.setSize(leaveQty);
                        this.getSell().remove(topSell);
                        orderBookSellTableView.getItems().remove(topSell);
                        orderBookBuyTableView.getItems().remove(0);
                        orderBookBuyTableView.getItems().add(topBuy);
                    } else {
                        leaveQty = topSell.getSize() - topBuy.getSize();
                        topSell.setSize(leaveQty);
                        this.getBuy().remove(topBuy);
                        orderBookBuyTableView.getItems().remove(topBuy);
                        orderBookSellTableView.getItems().remove(0);
                        orderBookSellTableView.getItems().add(topSell);
                    }
                    Action action = new Action(Action.Type.PARTIAL_FILL,topBuy.getOrderID(), topSell.getOrderID(), null, LocalDateTime.now(), leaveQty, topBuy.getPrice());
                    actionTableView.getItems().add(action);
                    actionTableView.getSelectionModel().clearAndSelect(0);
                    printTrade(topBuy, topSell);
                    return generateExecReport(ExecType.PARTIAL_FILL, clOrdID, ordId, execId, symbol, side, price, new DoubleField(leaveQty.intValue()));
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
        this.orderBookLooger.info("=================================================================================");
        this.orderBookLooger.info(".....................................BID.........................................");
        this.orderBookLooger.info(buy.toString());
        this.orderBookLooger.info(".....................................ASK.........................................");
        this.orderBookLooger.info(sell.toString());
        this.orderBookLooger.info("=================================================================================\n\n");
    }
}
