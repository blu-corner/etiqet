package com.neueda.etiqet.orderbook.etiqetorderbook.controllers;

import com.neueda.etiqet.orderbook.etiqetorderbook.OrderBook;
import com.neueda.etiqet.orderbook.etiqetorderbook.entity.Action;
import com.neueda.etiqet.orderbook.etiqetorderbook.entity.Order;
import com.neueda.etiqet.orderbook.etiqetorderbook.entity.OrderXML;
import com.neueda.etiqet.orderbook.etiqetorderbook.entity.Tag;
import com.neueda.etiqet.orderbook.etiqetorderbook.fix.Acceptor;
import com.neueda.etiqet.orderbook.etiqetorderbook.fix.FixSession;
import com.neueda.etiqet.orderbook.etiqetorderbook.fix.Initator;
import com.neueda.etiqet.orderbook.etiqetorderbook.utils.Constants;
import com.neueda.etiqet.orderbook.etiqetorderbook.utils.Utils;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventTarget;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import javafx.stage.Stage;
import jfxtras.scene.control.LocalDateTimePicker;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.Dictionary;
import quickfix.*;
import quickfix.field.*;
import quickfix.fix44.Logon;
import quickfix.fix44.Logout;
import quickfix.fix44.SequenceReset;

import java.awt.*;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.neueda.etiqet.orderbook.etiqetorderbook.utils.Utils.getConfig;

/**
 * @author enol.cacheroramirez@version1.com
 * Handles rest of controllers and interacts with
 * Acceptor and Initiator classes
 */
public class MainController implements Initializable {
    private static String port;
    private static Thread orderBook;
    public TableView<Order> orderBookBuyTableView;
    public TableView<Order> orderBookSellTableView;
    public TableView<Action> actionTableView;
    public Circle circleStartAcceptor;
    public TextField textFieldSize;
    public TextField textFieldPrice;
    public TextField textFieldOrderID;
    public TextField textFieldOrigOrderID;
    public Button buttonSendOrder;
    public ListView listViewLog;
    public ListView listViewActions;
    public Menu menuItemMessagePort;
    public TableColumn<Order, String> orderIDBuyTableColumn;
    public TableColumn<Order, String> timeBuyTableColumn;
    public TableColumn<Order, String> sizeBuyTableColumn;
    public TableColumn<Order, String> priceBuyTableColumn;
    public TableColumn<Order, String> orderIDSellTableColumn;
    public TableColumn<Order, String> timeSellTableColumn;
    public TableColumn<Order, String> sizeSellTableColumn;
    public TableColumn<Order, String> priceSellTableColumn;
    public TableColumn<Order, String> clientIDBuyTableColumn;
    public TableColumn<Order, String> clientIDSellTableColumn;
    public TableColumn<Order, String> timeInForceSellTableColumn;
    public TableColumn<Order, String> timeInForceBuyTableColumn;
    public TableColumn<Action, String> actionTypeTableColumn;
    public TableColumn<Action, String> actionTypeClientIdBuyTableColumn;
    public TableColumn<Action, String> actionTypeClientIdSellTableColumn;
    public TableColumn<Action, String> actionTimeInForceBuyTableColumn;
    public TableColumn<Action, String> actionTimeInForceSellTableColumn;
    public TableColumn<Action, String> actionOrderIdBuyTableColumn;
    public TableColumn<Action, String> actionOrderIdSellTableColumn;
    public TableColumn<Action, String> actionTimeTableColumn;
    public TableColumn<Action, String> actionBuySizeTableColumn;
    public TableColumn<Action, String> actionSellSizeTableColumn;
    public TableColumn<Action, String> actionLeaveQtyTableColumn;
    public TableColumn<Action, String> actionAgreedPriceTableColumn;
    public Circle circle;
    public MenuItem menuItemImport;
    public MenuItem menuItemExport;
    public CheckMenuItem checkMenuItemExportOnClose;
    public ComboBox<String> comboBoxTimeInForce;
    public Label labelClock;
    public TextField textFieldExpireDate;
    public List<FixSession> fixSessions;
    Logger logger = LoggerFactory.getLogger(MainController.class);
    private SocketInitiator socketInitiator;
    private SessionID sessionId;
    private Initator initator;
    private List<Order> buy;
    private List<Order> sell;
    private boolean changed;
    private boolean useDefaultPort;
    @FXML
    private Circle circleStartInitiator;
    @FXML
    private MenuItem startAcceptor;
    @FXML
    private MenuItem startInitiator;
    @FXML
    private MenuBar menuBarGeneral;
    @FXML
    private Tab tabAcceptor;
    @FXML
    private Tab tabInitiator;
    @FXML
    private ComboBox<String> comboOrders;
    @FXML
    private ComboBox<String> comboSide;
    @FXML
    private TabPane mainTabPane;


    public String getConnectedPort() {
        return port;
    }

    public boolean isChanged() {
        return changed;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }

    public SessionID getSessionId() {
        return this.sessionId;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.buy = new ArrayList<>();
        this.sell = new ArrayList<>();
        this.changed = true;
        actionTableView.setStyle("-fx-selection-bar: green; -fx-selection-bar-non-focused: green;");
        orderBookBuyTableView.setStyle("-fx-selection-bar: green; -fx-selection-bar-non-focused: green;");
        orderBookSellTableView.setStyle("-fx-selection-bar: green; -fx-selection-bar-non-focused: green;");

        orderBookBuyTableView.setContextMenu(getOrderContextMenu(Constants.SIDE_ENUM.BUY));
        orderBookSellTableView.setContextMenu(getOrderContextMenu(Constants.SIDE_ENUM.SELL));

        listViewLog.setContextMenu(getLogContextMenu());

        orderIDBuyTableColumn.setCellValueFactory(new PropertyValueFactory<>("ClOrdID"));
        timeBuyTableColumn.setCellValueFactory(new PropertyValueFactory<>("Time"));
        sizeBuyTableColumn.setCellValueFactory(new PropertyValueFactory<>("orderQty"));
        priceBuyTableColumn.setCellValueFactory(new PropertyValueFactory<>("Price"));
        clientIDBuyTableColumn.setCellValueFactory(new PropertyValueFactory<>("ClientID"));
        timeInForceBuyTableColumn.setCellValueFactory(new PropertyValueFactory<>("TimeInForce"));

        orderIDSellTableColumn.setCellValueFactory(new PropertyValueFactory<>("ClOrdID"));
        timeSellTableColumn.setCellValueFactory(new PropertyValueFactory<>("Time"));
        sizeSellTableColumn.setCellValueFactory(new PropertyValueFactory<>("orderQty"));
        priceSellTableColumn.setCellValueFactory(new PropertyValueFactory<>("Price"));
        clientIDSellTableColumn.setCellValueFactory(new PropertyValueFactory<>("ClientID"));
        timeInForceSellTableColumn.setCellValueFactory(new PropertyValueFactory<>("TimeInForce"));

        actionTypeTableColumn.setCellValueFactory(new PropertyValueFactory<>("Type"));
        actionTypeClientIdBuyTableColumn.setCellValueFactory(new PropertyValueFactory<>("BuyClientID"));
        actionTypeClientIdSellTableColumn.setCellValueFactory(new PropertyValueFactory<>("SellClientID"));
        actionTimeInForceBuyTableColumn.setCellValueFactory(new PropertyValueFactory<>("TimeInForceBuy"));
        actionTimeInForceSellTableColumn.setCellValueFactory(new PropertyValueFactory<>("TimeInForceSell"));
        actionOrderIdBuyTableColumn.setCellValueFactory(new PropertyValueFactory<>("BuyID"));
        actionOrderIdSellTableColumn.setCellValueFactory(new PropertyValueFactory<>("SellID"));
        actionTimeTableColumn.setCellValueFactory(new PropertyValueFactory<>("Time"));
        actionBuySizeTableColumn.setCellValueFactory(new PropertyValueFactory<>("BuySize"));
        actionSellSizeTableColumn.setCellValueFactory(new PropertyValueFactory<>("SellSize"));
        actionLeaveQtyTableColumn.setCellValueFactory(new PropertyValueFactory<>("LeaveQty"));
        actionAgreedPriceTableColumn.setCellValueFactory(new PropertyValueFactory<>("AgreedPrice"));

        comboOrders.getItems().addAll(Constants.COMBO_NEW_ORDER, Constants.COMBO_CANCEL, Constants.COMBO_REPLACE);
        comboOrders.getSelectionModel().select(0);

        comboSide.getItems().addAll(Constants.SIDE);
        comboSide.getSelectionModel().select(0);
        initator = new Initator(listViewActions, listViewLog, textFieldOrderID);

        setUseDefaultPort(true);

        fixSessions = new ArrayList<>();

        Utils.configureTextFieldToAcceptOnlyIntegerValues(textFieldSize);
        Utils.configureTextFieldToAcceptOnlyDecimalValues(textFieldPrice);

        menuItemImport.setDisable(true);
        menuItemExport.setDisable(true);

        checkMenuItemExportOnClose.setSelected(true);
        comboBoxTimeInForce.getItems().addAll(Constants.TIME_IN_FORCE.getContents());
        comboBoxTimeInForce.getSelectionModel().select(0);

    }

    private void showFixFields() {
        try {
            String targetString = listViewLog.getSelectionModel().getSelectedItem().toString();
            List<Tag> tagList = fixDecoderToTag(targetString);
            launchDecoderWindow(tagList);
        } catch (Exception ex) {
            this.logger.warn("Exception showFixFields -> {}", ex.getLocalizedMessage());
        }

    }


    private List<Tag> fixDecoderToTag(String targetString) {
        targetString = removeOutInInfoFromFixString(targetString);
        String[] fields = targetString.split("\\|");
        List<String> fieldList = List.of(fields);
        List<Tag> tagList = new ArrayList<>();
        for (String field : fieldList) {
            String[] keyValue = field.split("=");
            Tag newTag = new Tag();
            String key = keyValue[0];
            String value = !key.equals("58") ? keyValue[1] : String.format("%s=%s", keyValue[1] ,keyValue[2]);
            newTag.setKey(key);
            newTag.setField(Constants.hmTagValue.get(Integer.valueOf(key)));
            newTag.setValue(value);
            newTag.setMeaning(getAdditinalInfo(key, value));
            tagList.add(newTag);

        }
        return tagList;
    }

    private String removeOutInInfoFromFixString(String targetString) {
        if (targetString.contains(Constants.OUT)) {
            targetString = targetString.replace(Constants.OUT + StringUtils.SPACE, "");
        } else {
            targetString = targetString.replace(Constants.IN + StringUtils.SPACE, "");
        }
        return targetString;
    }


    private String getAdditinalInfo(String tag, String value) {
        String additionalInfo = "";
        switch (tag) {
            case Constants.MSG_TYPE:
                additionalInfo = StringUtils.SPACE + Constants.hmMsgType.get(value);
                break;
        }
        return additionalInfo != null ? additionalInfo : "";
    }


    public void setUseDefaultPort(boolean useDefaultPort) {
        this.useDefaultPort = useDefaultPort;
    }

    private void deleteDir(String directory) {
        try {
            Files.walk(Path.of(directory))
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
        } catch (Exception e) {
            this.logger.error(e.getLocalizedMessage());
        }
    }

    public void startAcceptor(ActionEvent actionEvent) {
        try {
            String portsA = getConfig(Constants.ACCEPTOR_ROLE, Constants.ACC_ACCEPT_PORT);
            String portsB = getConfig(Constants.ACCEPTOR_ROLE, Constants.ACC_SOCKET_ACCEPT_PORT_RANGE_LIMIT);
            if (listenOnPorts(portsA, portsB)) {
                OrderBook orderBookLoggerThread = new OrderBook(this);
                orderBook = new Thread(orderBookLoggerThread);
                orderBook.setDaemon(true);
                orderBook.start();

                //javafx
                circle.setFill(Color.GREENYELLOW);
                this.startAcceptor.setDisable(true);
                this.startInitiator.setDisable(true);
                this.circleStartAcceptor.setFill(Color.GREENYELLOW);
                this.tabInitiator.setDisable(true);
                this.mainTabPane.getSelectionModel().select(0);
                menuItemImport.setDisable(false);
                menuItemExport.setDisable(false);
                importOrders(new File(Constants.DEFAULT_ORDERS_FILE));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void startInitiator(ActionEvent actionEvent) {
        try {
            SessionSettings initiatorSettings = new SessionSettings();
            port = getConfig(Constants.INITIATOR_ROLE, Constants.INI_CONNECT_PORT);
            if (!StringUtils.isEmpty(port)) {
                sessionId = new SessionID(
                    new BeginString(getConfig(Constants.INITIATOR_ROLE, Constants.CONF_BEGIN_STRING)),
                    new SenderCompID(getConfig(Constants.INITIATOR_ROLE, Constants.CONF_SENDER) + port),
                    new TargetCompID(getConfig(Constants.INITIATOR_ROLE, Constants.CONF_TARGET)));
                Dictionary dictionary = new Dictionary();
                dictionary.setString(Constants.CONF_CONNECTION_TYPE, StringUtils.lowerCase(Constants.INITIATOR_ROLE));
                dictionary.setString(Constants.INI_CONNECT_PORT, port);
                dictionary.setString(Constants.INI_CONNECT_HOST, getConfig(Constants.INITIATOR_ROLE, Constants.INI_CONNECT_HOST));
                dictionary.setString(Constants.CONF_FILE_STORE_PATH, getConfig(Constants.INITIATOR_ROLE, Constants.CONF_FILE_STORE_PATH) + port);
                dictionary.setString(Constants.CONF_FILE_LOG_PATH, getConfig(Constants.INITIATOR_ROLE, Constants.CONF_FILE_LOG_PATH) + port);
                dictionary.setString(Constants.CONF_DATA_DIC, getConfig(Constants.INITIATOR_ROLE, Constants.CONF_DATA_DIC));
                dictionary.setString(Constants.CONF_START_TIME, getConfig(Constants.INITIATOR_ROLE, Constants.CONF_START_TIME));
                dictionary.setString(Constants.CONF_END_TIME, getConfig(Constants.INITIATOR_ROLE, Constants.CONF_END_TIME));
                dictionary.setString(Constants.CONF_USE_DATA_DIC, getConfig(Constants.INITIATOR_ROLE, Constants.CONF_USE_DATA_DIC));
                dictionary.setString(Constants.CONF_RESET_ON_LOGON, getConfig(Constants.INITIATOR_ROLE, Constants.CONF_RESET_ON_LOGON));
//                dictionary.setString(Constants.CONF_RESET_ON_DISCONNECT,  getConfig(Constants.INITIATOR_ROLE, Constants.CONF_RESET_ON_DISCONNECT) );
                dictionary.setString(Constants.CONF_HEART_BT_INT, getConfig(Constants.INITIATOR_ROLE, Constants.CONF_HEART_BT_INT));
                initiatorSettings.set(sessionId, dictionary);
                menuItemMessagePort.setText("Connected to port: " + port);

                FileStoreFactory fileStoreFactory = new FileStoreFactory(initiatorSettings);
                FileLogFactory fileLogFactory = new FileLogFactory(initiatorSettings);
                MessageFactory messageFactory = new DefaultMessageFactory();
                socketInitiator = new SocketInitiator(initator, fileStoreFactory, initiatorSettings, fileLogFactory, messageFactory);
                socketInitiator.start();
                sendLogonRequest();

                circle.setFill(Color.GREENYELLOW);
                this.startInitiator.setDisable(true);
                this.startAcceptor.setDisable(true);
                this.circleStartInitiator.setFill(Color.GREENYELLOW);
                this.tabAcceptor.setDisable(true);
                this.mainTabPane.getSelectionModel().select(1);
                menuItemImport.setDisable(true);
                menuItemExport.setDisable(true);

            }
        } catch (ConfigError | SessionNotFound e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        try {
            if (socketInitiator != null) {
                socketInitiator.stop();
            } else {
                removeAtTheStopAndSendExecReport();
                for (FixSession fixSession : fixSessions) {
                    fixSession.stop();
                }
            }
        } catch (Exception ex) {
            this.logger.error(ex.getLocalizedMessage());
        } finally {
            stopConfiguration();
        }
    }

    private void removeAtTheStopAndSendExecReport() {
        try {
            List<Order> buys = this.getBuy().stream().filter(buy -> buy.getTimeInForce().equals(Constants.TIME_IN_FORCE.AT_THE_CLOSE.getContent())).collect(Collectors.toList());
            Acceptor acceptor = new Acceptor(this);
            buys.forEach(buy -> {
                FixSession fixSession = this.fixSessions.stream().filter(s -> s.getSessionID().toString().equals(buy.getSessionID())).findFirst().get();
                acceptor.sendExecutionReportAfterCanceling(buy, fixSession);
                Action action = new Action.ActionBuilder()
                    .type(Constants.Type.CANCELED)
                    .buyID(buy.getClOrdID())
                    .buyClientID(buy.getClientID())
                    .timeInForceBuy(buy.getTimeInForce())
                    .time(Utils.getFormattedStringDate())
                    .sellSize(buy.getOrderQty())
                    .build();
                acceptor.addAction(buy, null, action);

            });
            List<Order> sells = this.getSell().stream().filter(sell -> sell.getTimeInForce().equals(Constants.TIME_IN_FORCE.AT_THE_CLOSE.getContent())).collect(Collectors.toList());
            sells.forEach(sell -> {
                FixSession fixSession = this.fixSessions.stream().filter(s -> s.getSessionID().toString().equals(sell.getSessionID())).findFirst().get();
                acceptor.sendExecutionReportAfterCanceling(sell, fixSession);
                Action action = new Action.ActionBuilder()
                    .type(Constants.Type.CANCELED)
                    .sellID(sell.getClOrdID())
                    .sellClientID(sell.getClientID())
                    .timeInForceSell(sell.getTimeInForce())
                    .time(Utils.getFormattedStringDate())
                    .sellSize(sell.getOrderQty())
                    .build();
                acceptor.addAction(null, sell, action);
            });
            Thread.sleep(2000);
        } catch (Exception ex) {
            this.logger.warn("Exception in removeAtTheStopOrders -> {}", ex.getLocalizedMessage());
        }


    }

    private void stopConfiguration() {
        try {
            this.circle.setFill(Color.RED);
            this.startAcceptor.setDisable(false);
            this.startInitiator.setDisable(false);
            this.circleStartAcceptor.setFill(Color.RED);
            this.circleStartInitiator.setFill(Color.RED);
            this.tabAcceptor.setDisable(false);
            this.tabInitiator.setDisable(false);
            if (orderBook != null) {
                orderBook.stop();
            }
            menuItemMessagePort.setText("");
            menuItemImport.setDisable(true);
            menuItemExport.setDisable(true);

            if (checkMenuItemExportOnClose.isSelected()) {
                exportOrders(new File(Constants.DEFAULT_ORDERS_FILE));
            }


        } catch (Exception e) {
            this.logger.warn("StopConfiguration exception: {}", e.getMessage());
        }

    }


    private void sendLogonRequest() throws SessionNotFound {
        Logon logon = new Logon();
        Message.Header header = logon.getHeader();
        header.setField(new BeginString("FIX.4.4"));
        logon.set(new HeartBtInt(30));
        logon.set(new EncryptMethod(0));
        boolean sent = Session.sendToTarget(logon, this.sessionId);
        logger.info("Logon message sent: {}", sent);
    }

    private void sendSeqReset() throws SessionNotFound {
        SequenceReset sequenceReset = new SequenceReset();
//        GapFillFlag gapFillFlag = new GapFillFlag();
        NewSeqNo newSeqNo = new NewSeqNo(100);
//        sequenceReset.set(gapFillFlag);
        sequenceReset.set(newSeqNo);
        try {
            Path path = Paths.get("initiatorStore/FIX.4.4-CLIENT-SERVER.senderseqnums");
            Files.write(path, Collections.singleton(" \u0001100"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        boolean sent = Session.sendToTarget(sequenceReset, this.sessionId);

//        MsgSeqNum msgSeqNum = new MsgSeqNum(100);
//          Heartbeat heartbeat = new Heartbeat();
//        heartbeat.set(msgSeqNum);
//        Session.sendToTarget()
        logger.info("Sequence reset message sent: {}", sent);
    }

    private void sendLogoutRequest() throws SessionNotFound {
        Logout logout = new Logout();
        boolean sent = Session.sendToTarget(logout, this.sessionId);
        logger.info("Logout message sent: {}", sent);
    }


    private ContextMenu getLogContextMenu() {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem menuItemCopy = new MenuItem("Copy");
        MenuItem menuItemDecode = new MenuItem("Decode");

        contextMenu.getItems().add(menuItemCopy);
        contextMenu.getItems().add(menuItemDecode);

        menuItemDecode.setOnAction((event) -> showFixFields());

        menuItemCopy.setOnAction(e -> {
            final Clipboard clipboard = Clipboard.getSystemClipboard();
            final ClipboardContent content = new ClipboardContent();
            String row = removeOutInInfoFromFixString(listViewLog.getSelectionModel().getSelectedItem().toString());
            content.putString(row);
            clipboard.setContent(content);
            showCopiedPopUp("Row copied");
        });

        return contextMenu;
    }

    private ContextMenu getOrderContextMenu(Constants.SIDE_ENUM side) {
        ContextMenu contextMenu = new ContextMenu();
        Menu menuCopy = new Menu("Copy");
        MenuItem menuItemCopyOrderID = new MenuItem("ClOrdID");
        MenuItem menuItemCancel = new MenuItem("Cancel Order");

        menuCopy.getItems().add(menuItemCopyOrderID);
        contextMenu.getItems().add(menuCopy);
        contextMenu.getItems().add(menuItemCancel);

        menuItemCancel.setOnAction((event) -> contextMenuCancelAction(side));

        menuItemCopyOrderID.setOnAction(e -> {
            Order selectedOrder;
            final Clipboard clipboard = Clipboard.getSystemClipboard();
            final ClipboardContent content = new ClipboardContent();
            if (side.equals(Constants.SIDE_ENUM.BUY)) {
                selectedOrder = orderBookBuyTableView.getSelectionModel().getSelectedItem();
            } else {
                selectedOrder = orderBookSellTableView.getSelectionModel().getSelectedItem();
            }
            content.putString(selectedOrder.getClOrdID());

            showCopiedPopUp("ClOrdID Copied");

            clipboard.setContent(content);
        });

        return contextMenu;
    }

    private void showCopiedPopUp(String message) {
        Label label = new Label(message);
        label.setStyle("-fx-background-radius: 6;" +
            "-fx-background-color: rgb(45, 45, 50), rgb(60, 60, 65);" +
            "-fx-text-fill: white;");
        label.setPadding(new Insets(10, 10, 10, 10));
        Popup popup = new Popup();
        popup.getContent().add(label);
        label.setMinWidth(100);
        label.setMinHeight(50);
        popup.show(menuBarGeneral.getScene().getWindow());
        popup.setAutoHide(true);
    }

    private void contextMenuCancelAction(Constants.SIDE_ENUM side) {
        Order selectedOrder;
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm order cancellation");
        alert.setHeaderText("Cancel and remove order from orderbook");
        alert.setContentText("An execution report will be sent to the client");
        try {
            Optional<ButtonType> response = alert.showAndWait();
            if (response.isPresent()) {
                ButtonType buttonType = response.get();
                if (buttonType.equals(ButtonType.OK)) {
                    Acceptor acceptor = new Acceptor(this);

                    if (side.equals(Constants.SIDE_ENUM.BUY)) {
                        selectedOrder = orderBookBuyTableView.getSelectionModel().getSelectedItem();
                        FixSession fixSession = fixSessions.stream().filter(s -> s.getSessionID().toString().equals(selectedOrder.getSessionID())).findFirst().get();
                        acceptor.sendExecutionReportAfterCanceling(selectedOrder, fixSession);
                        this.getBuy().remove(selectedOrder);
                        reorderBookBuyTableView();
                    } else {
                        selectedOrder = orderBookSellTableView.getSelectionModel().getSelectedItem();
                        FixSession fixSession = fixSessions.stream().filter(s -> s.getSessionID().toString().equals(selectedOrder.getSessionID())).findFirst().get();
                        acceptor.sendExecutionReportAfterCanceling(selectedOrder, fixSession);
                        this.getSell().remove(selectedOrder);
                        reorderBookSellTableView();
                    }
                    this.logger.info("Deleted");
                } else {
                    this.logger.info("Canceled");
                }
            }
        } catch (Exception e) {
            this.logger.warn("Exception::contextMenuCancelAction -> {}", e.getLocalizedMessage());
        }

    }

    public List<Order> getBuy() {
        return buy;
    }

    public List<Order> getSell() {
        return sell;
    }

    public void addBuy(Order buy) {
        this.buy.add(buy);
        reorderBookBuyTableView();
    }

    public void addSell(Order sell) {
        this.sell.add(sell);
        reorderBookSellTableView();
    }

    public void removeBuy(Order buy) {
        this.buy.remove(buy);
        reorderBookBuyTableView();
    }

    public void removeSell(Order sell) {
        this.sell.remove(sell);
        reorderBookSellTableView();
    }

    public List<Order> getOrderedBuy() {
        return this.getBuy().stream().sorted(Comparator.comparing(Order::getPrice, Comparator.reverseOrder())).collect(Collectors.toList());
    }

    public List<Order> getOrderedSell() {
        return this.getSell().stream().sorted(Comparator.comparing(Order::getPrice)).collect(Collectors.toList());
    }

    public List<Action> getOrderedTrades() {
        return this.actionTableView.getItems().stream().sorted(Comparator.comparing(Action::getTime, Comparator.reverseOrder())).collect(Collectors.toList());
    }

    public void reorderBookBuyTableView() {
        orderBookBuyTableView.getItems().clear();
        orderBookBuyTableView.getItems().addAll(this.getOrderedBuy());
        orderBookBuyTableView.getSelectionModel().clearAndSelect(0);
    }

    public void reorderBookSellTableView() {
        orderBookSellTableView.getItems().clear();
        orderBookSellTableView.getItems().addAll(this.getOrderedSell());
        orderBookSellTableView.getSelectionModel().clearAndSelect(0);
    }

    public void reorderActionTableView() {
        List<Action> orderedTrades = getOrderedTrades();
        actionTableView.getItems().clear();
        actionTableView.getItems().addAll(orderedTrades);
        actionTableView.getSelectionModel().clearAndSelect(0);
    }

    public void sendOrder(ActionEvent actionEvent) {
        String textFieldOrderID = this.textFieldOrderID.getText();
        if (StringUtils.isEmpty(textFieldOrderID)) {
            return;
        }
        String textFieldSizeText = this.textFieldSize.getText();
        String textFieldPrice = this.textFieldPrice.getText();
        String textFieldOrigOrderID = this.textFieldOrigOrderID.getText();
        String comboOrdersValue = this.comboOrders.getValue();
        Character comboTimeInForceValue = Constants.TIME_IN_FORCE.getValue(this.comboBoxTimeInForce.getValue());
        char comboSideValue = this.comboSide.getValue().equals(Constants.SIDE[0]) ? '1' : '2';
        String expireDate = this.textFieldExpireDate.getText();

        switch (comboOrdersValue) {
            case Constants.COMBO_NEW_ORDER:
                initator.sendNewOrderSingle(textFieldSizeText, textFieldPrice, textFieldOrderID,
                    comboSideValue, comboTimeInForceValue == null ? ' ' : comboTimeInForceValue, expireDate);
                break;
            case Constants.COMBO_CANCEL:
                initator.sendOrderCancelRequest(textFieldOrderID, textFieldOrigOrderID, comboSideValue);
                break;
            case Constants.COMBO_REPLACE:
                initator.sendOrderCancelReplaceRequest(textFieldSizeText, textFieldPrice, textFieldOrderID, textFieldOrigOrderID, comboSideValue);
                break;
        }
    }

    public void clearMainLog(ActionEvent event) {
        this.listViewActions.getItems().clear();
    }

    public void clearGlobalLog(ActionEvent event) {
        this.listViewLog.getItems().clear();
    }

    public void setRememberPort(ActionEvent actionEvent) {
        EventTarget target = actionEvent.getTarget();
        String targetString = target.toString();
        setUseDefaultPort(targetString.contains("selected"));
    }

    public void closeApplication(ActionEvent actionEvent) {
        stop();
        Platform.exit();
    }

    public void cleanBid(ActionEvent actionEvent) {
        this.getBuy().clear();
        orderBookBuyTableView.getItems().clear();
    }

    public void cleanOffer(ActionEvent actionEvent) {
        this.getSell().clear();
        orderBookSellTableView.getItems().clear();
    }

    public void cleanBidAndOffer(ActionEvent actionEvent) {
        cleanBid(actionEvent);
        cleanOffer(actionEvent);
    }

    public void cleanTrades(ActionEvent actionEvent) {
        this.actionTableView.getItems().clear();
    }

    public void cleanAll(ActionEvent actionEvent) {
        cleanBidAndOffer(actionEvent);
        cleanTrades(actionEvent);
    }

    public void setAutoGenValue(ActionEvent actionEvent) {
        this.textFieldOrderID.setText(RandomStringUtils.randomAlphanumeric(8));
    }

    public void resetSequenceNumber(ActionEvent actionEvent) {
        try {
            sendLogoutRequest();
//            deleteDir("store");
//            deleteDir("initatorStore");
            sendLogonRequest();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void goToFixDoc(ActionEvent actionEvent) {
        String url = Constants.HELP_SITE;
        try {
            if (java.awt.Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                try {
                    desktop.browse(new URI(url));
                } catch (IOException | URISyntaxException e) {
                    e.printStackTrace();
                }
            } else {
                Runtime runtime = Runtime.getRuntime();
                try {
                    runtime.exec("xdg-open " + url);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startMultipleAcceptor(ActionEvent actionEvent) {
        this.tabInitiator.setDisable(true);
        this.tabAcceptor.setDisable(true);
        this.mainTabPane.getSelectionModel().select(0);

    }

    public boolean listenOnPorts(String testRangeA, String testRangeB) {
        try {
            Integer iTestRangeA = Integer.parseInt(testRangeA);
            Integer iTestRangeB = Integer.parseInt(testRangeB);
            StringBuilder invalidPorts = new StringBuilder();
            StringBuilder validPorts = new StringBuilder();
            invalidPorts.append(Constants.INVALID_PORTS).append("\n");

            boolean existInvalidPorts = false;
            for (int port = iTestRangeA; port <= iTestRangeB; port++) {
                if (Utils.availablePort(port)) {
                    FixSession fixSession = new FixSession(this, orderBook);
//                    fixSessionsList.add(fixSession);
                    fixSession.start(port);
                    fixSessions.add(fixSession);
                    validPorts.append(port).append(",");
                } else {
                    existInvalidPorts = true;
                    invalidPorts.append(port).append("\n");
                }
            }
            if (existInvalidPorts) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle(Constants.PORTS_RANGE_ERROR);
                alert.setHeaderText(Constants.PORTS_IN_USE);
                alert.setContentText(invalidPorts.toString());
                alert.showAndWait();
            }
            if (validPorts.length() > 0) {
                validPorts.deleteCharAt(validPorts.length() - 1);
                menuItemMessagePort.setText(String.format(Constants.LISTENING_ON_PORTS, validPorts));
            }

            return !StringUtils.isEmpty(validPorts);
        } catch (Exception ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(Constants.PORTS_RANGE_ERROR);
            alert.setHeaderText(Constants.BAD_PORTS_RANGE);
            alert.setContentText(ex.getLocalizedMessage());
            alert.showAndWait();
            return false;
        }

    }

    public void launchDecoderWindow(List<Tag> tagList) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource(Constants.FXML_FIXDECODER_FXML));
            Parent root = fxmlLoader.load();
            DecoderController decoderController = fxmlLoader.getController();
            decoderController.injectTags(tagList);
            Stage stage = new Stage();
            stage.setTitle(Constants.FIX_DECODER_TITLE);
            stage.getIcons().add(Constants.ICON);
            stage.setScene(new Scene(root));
            stage.setAlwaysOnTop(true);
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void launchInitiatorConfigWindow(ActionEvent actionEvent) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource(Constants.FXML_INITIATOR_CONFIG_WINDOW_FXML));
            Parent root = fxmlLoader.load();
            ConfigController configController = fxmlLoader.getController();
            configController.injectMainController(this);
            configController.injectRole(Constants.INITIATOR_ROLE);
            Stage stage = new Stage();
            stage.setTitle(Constants.INITIATOR_TITLE);
            stage.getIcons().add(Constants.ICON);
            stage.setScene(new Scene(root));
            stage.setAlwaysOnTop(true);
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void launchAcceptorConfigWindow(ActionEvent actionEvent) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource(Constants.FXML_ACCEPTOR_CONFIG_WINDOW_FXML));
            Parent root = fxmlLoader.load();
            ConfigController configController = fxmlLoader.getController();
            configController.injectMainController(this);
            configController.injectRole(Constants.ACCEPTOR_ROLE);
            Stage stage = new Stage();
            stage.getIcons().add(Constants.ICON);
            stage.setTitle(Constants.ACCEPTOR_TITLE);
            stage.setScene(new Scene(root));
            stage.setAlwaysOnTop(true);
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void launchAdvancedRequest(ActionEvent actionEvent) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource(Constants.FXML_ADVANCED_REQUEST_FXML));
            Parent root = fxmlLoader.load();
            AdvancedRequestController advancedRequestController = fxmlLoader.getController();
            advancedRequestController.injectMainController(this);
            Stage stage = new Stage();
            stage.setTitle(Constants.ADVANCED_REQUEST_TITLE);
            stage.setScene(new Scene(root));
            stage.getIcons().add(Constants.ICON);
            stage.setAlwaysOnTop(true);
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void buttonCopyLast(ActionEvent actionEvent) {
        textFieldOrigOrderID.setText(textFieldOrderID.getText());
    }

    public void actionMenuItemImport(ActionEvent actionEvent) {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialDirectory(new File("."));
            fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("XML Files", "*.xml")
            );
            File selectedFile = fileChooser.showOpenDialog(menuBarGeneral.getScene().getWindow());
            importOrders(selectedFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void importOrders(File selectedFile) {
        try {
            if (selectedFile != null) {
                clearAll();
                this.logger.info(selectedFile.getAbsolutePath());
                FileInputStream fileInputStream = new FileInputStream(selectedFile);
                XMLDecoder xmlDecoder = new XMLDecoder(fileInputStream);
                OrderXML orderXML = (OrderXML) xmlDecoder.readObject();
                this.logger.info(orderXML.toString());

                orderBookSellTableView.getItems().addAll(orderXML.getSellOrders());
                orderBookBuyTableView.getItems().addAll(orderXML.getBuyOrders());
                this.buy.addAll(orderXML.getBuyOrders());
                this.sell.addAll(orderXML.getSellOrders());
                actionTableView.getItems().addAll(orderXML.getActions());
                orderBookSellTableView.getSelectionModel().select(0);
                orderBookBuyTableView.getSelectionModel().select(0);
                actionTableView.getSelectionModel().select(0);
            }
        } catch (FileNotFoundException e) {
            this.logger.warn("orders.xml does no exist in the root directory");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void clearAll() {
        this.orderBookSellTableView.getItems().clear();
        this.orderBookBuyTableView.getItems().clear();
        this.buy.clear();
        this.sell.clear();
        this.actionTableView.getItems().clear();
    }

    public void actionMenuItemExport(ActionEvent actionEvent) {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save");
            fileChooser.setInitialDirectory(new File("."));
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("XML files", "*.xml"));
            File selectedFile = fileChooser.showSaveDialog(menuBarGeneral.getScene().getWindow());
            exportOrders(selectedFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void exportOrders(File selectedFile) throws IOException {
        try {
            if (selectedFile != null) {
                OrderXML orders = new OrderXML();
                orders.setBuyOrders(new ArrayList<>(orderBookBuyTableView.getItems()));
                orders.setSellOrders(new ArrayList<>(orderBookSellTableView.getItems()));
                orders.setActions(new ArrayList<>(actionTableView.getItems()));
                FileOutputStream fileOutputStream = new FileOutputStream(selectedFile);
                XMLEncoder xmlEncoder = new XMLEncoder(fileOutputStream);
                xmlEncoder.setExceptionListener(e -> this.logger.error("Exception! : {}", e.getLocalizedMessage()));
                xmlEncoder.writeObject(orders);
                xmlEncoder.close();
                fileOutputStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void selectAllField(MouseEvent event) {
        textFieldOrigOrderID.selectAll();
    }


    public void launchTimePicker() {
        try {
            final Dialog dialog = new Dialog();
            dialog.setTitle("Date Time Picker");
            dialog.setContentText("Test");
            dialog.initOwner(orderBookBuyTableView.getScene().getWindow());
            AnchorPane pane = new AnchorPane();
            pane.setMinSize(410d, 280d);
            final LocalDateTimePicker localDateTimePicker = new LocalDateTimePicker();
            ButtonType save = new ButtonType("SAVE", ButtonBar.ButtonData.OK_DONE);
            localDateTimePicker.setMinSize(420d, 270d);
            AnchorPane.setLeftAnchor(localDateTimePicker, 10.0);
            AnchorPane.setTopAnchor(localDateTimePicker, 20.0);
            pane.getChildren().add(localDateTimePicker);
            dialog.getDialogPane().setContent(pane);
            dialog.getDialogPane().getButtonTypes().add(save);
            Optional<ButtonType> optional = dialog.showAndWait();
            if (optional.isPresent()) {
                String type = optional.get().getButtonData().getTypeCode();
                if (type.equals("O")) {
                    String date = Utils.getFormattedDateFromLocalDateTime(localDateTimePicker.getLocalDateTime());
                    textFieldExpireDate.setText(date);
                }
            }

        } catch (Exception e) {
            this.logger.warn("Exception launchTimePicker MainController -> {}", e.getLocalizedMessage());
        }

    }

    public void comboTimeInForceAction(ActionEvent actionEvent) {
        if (comboBoxTimeInForce.getValue().contains(Constants.TIME_IN_FORCE.GOOD_TILL_DATE.getContent())) {
            launchTimePicker();
        }
    }

    public void comboTimeInForceMouse(MouseEvent e) {
        if (!comboBoxTimeInForce.isShowing()) {
            if (comboBoxTimeInForce.getValue().contains(Constants.TIME_IN_FORCE.GOOD_TILL_DATE.getContent())) {
                launchTimePicker();
            }
        }
    }
}
