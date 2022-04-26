package com.neueda.etiqet.orderbook.etiqetorderbook.controllers;

import com.neueda.etiqet.orderbook.etiqetorderbook.*;
import com.neueda.etiqet.orderbook.etiqetorderbook.entity.Action;
import com.neueda.etiqet.orderbook.etiqetorderbook.entity.Order;
import com.neueda.etiqet.orderbook.etiqetorderbook.fix.FixSession;
import com.neueda.etiqet.orderbook.etiqetorderbook.fix.Initator;
import com.neueda.etiqet.orderbook.etiqetorderbook.utils.Constants;
import com.neueda.etiqet.orderbook.etiqetorderbook.utils.TooltipTableRow;
import com.neueda.etiqet.orderbook.etiqetorderbook.utils.Utils;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventTarget;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.*;
import quickfix.Dictionary;
import quickfix.Message;
import quickfix.MessageFactory;
import quickfix.field.*;
import quickfix.fix44.*;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import static com.neueda.etiqet.orderbook.etiqetorderbook.utils.Utils.getConfig;

public class MainController implements Initializable {


    Logger logger = LoggerFactory.getLogger(MainController.class);
    private List<Order> buy;
    private List<Order> sell;
    private boolean changed;
    private boolean useDefaultPort;
    private String changedDefaultPort = "";
    private static String port;

    public String getConnectedPort(){
        return port;
    }

    public boolean isChanged() {
        return changed;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }

    @FXML public TableView<Order> orderBookBuyTableView;
    @FXML public TableView<Order> orderBookSellTableView;
    @FXML public TableView<Action> actionTableView;
    @FXML public Circle circleStartAcceptor;
    @FXML private Circle circleStartInitiator;
    @FXML private MenuItem startAcceptor;
    @FXML private MenuItem startInitiator;
    @FXML private MenuBar menuBarGeneral;
    @FXML private Tab tabAcceptor;
    @FXML private Tab tabInitiator;
    @FXML private ComboBox comboOrders;
    @FXML private ComboBox comboSide;
    @FXML private TextArea logTextArea;
    @FXML private TabPane mainTabPane;
    @FXML public TextField textFieldSize;
    @FXML public TextField textFieldPrice;
    @FXML public TextField textFieldOrderID;
    @FXML public TextField textFieldOrigOrderID;
    @FXML public Button buttonSendOrder;
    @FXML public ListView listViewLog;
    @FXML public ListView listViewActions;
    @FXML public CheckMenuItem checkMenuItemRemPort;
    @FXML public Menu menuItemMessagePort;
    @FXML public CheckMenuItem menuItemDecodeOnClick;

    @FXML
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

    public TableColumn<Action, String> actionTypeTableColumn;
    public TableColumn<Action, String> actionOrderIdBuyTableColumn;
    public TableColumn<Action, String> actionOrderIdSellTableColumn;
    public TableColumn<Action, String> actionTimeTableColumn;
    public TableColumn<Action, String> actionBuySizeTableColumn;
    public TableColumn<Action, String> actionSellSizeTableColumn;
    public TableColumn<Action, String> actionLeaveQtyTableColumn;
    public TableColumn<Action, String> actionAgreedPriceTableColumn;

    public Circle circle;
    private static Thread orderBook;
    private SocketInitiator socketInitiator;
    private SessionID sessionId;
    private Initator initator;
    private List<FixSession> fixSessions;

    public SessionID getSessionId(){
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

        actionTableView.setOnMouseClicked(this::cellToClipBoard);
        orderBookBuyTableView.setOnMouseClicked(this::cellToClipBoard);
        orderBookSellTableView.setOnMouseClicked(this::cellToClipBoard);

        listViewLog.setOnMouseClicked(this::showFixFields);
        menuItemDecodeOnClick.setSelected(true);

        orderIDBuyTableColumn.setCellValueFactory(new PropertyValueFactory<>("OrderID"));
        timeBuyTableColumn.setCellValueFactory(new PropertyValueFactory<>("Time"));
        sizeBuyTableColumn.setCellValueFactory(new PropertyValueFactory<>("Size"));
        priceBuyTableColumn.setCellValueFactory(new PropertyValueFactory<>("Price"));
        clientIDBuyTableColumn.setCellValueFactory(new PropertyValueFactory<>("ClientID"));

        orderIDSellTableColumn.setCellValueFactory(new PropertyValueFactory<>("OrderID"));
        timeSellTableColumn.setCellValueFactory(new PropertyValueFactory<>("Time"));
        sizeSellTableColumn.setCellValueFactory(new PropertyValueFactory<>("Size"));
        priceSellTableColumn.setCellValueFactory(new PropertyValueFactory<>("Price"));
        clientIDSellTableColumn.setCellValueFactory(new PropertyValueFactory<>("ClientID"));

        actionTypeTableColumn.setCellValueFactory(new PropertyValueFactory<>("Type"));
        actionOrderIdBuyTableColumn.setCellValueFactory(new PropertyValueFactory<>("BuyID"));
        actionOrderIdSellTableColumn.setCellValueFactory(new PropertyValueFactory<>("SellID"));
        actionTimeTableColumn.setCellValueFactory(new PropertyValueFactory<>("Time"));
        actionBuySizeTableColumn.setCellValueFactory(new PropertyValueFactory<>("BuySize"));
        actionSellSizeTableColumn.setCellValueFactory(new PropertyValueFactory<>("SellSize"));
        actionLeaveQtyTableColumn.setCellValueFactory(new PropertyValueFactory<>("LeaveQty"));
        actionAgreedPriceTableColumn.setCellValueFactory(new PropertyValueFactory<>("AgreedPrice"));

        comboOrders.getItems().addAll(Constants.COMBO_NEW_ORDER, Constants.COMBO_CANCEL, Constants.COMBO_REPLACE);
        comboOrders.getSelectionModel().select(0);

        comboSide.getItems().addAll("BUY", "SELL");
        comboSide.getSelectionModel().select(0);
        initator = new Initator(listViewActions, listViewLog, textFieldOrderID);

        setUseDefaultPort(true);

        fixSessions = new ArrayList<>();

        Utils.configureTextFieldToAcceptOnlyIntegerValues(textFieldSize);
        Utils.configureTextFieldToAcceptOnlyDecimalValues(textFieldPrice);
    }

    private void showFixFields(MouseEvent e) {
        try {
            if (menuItemDecodeOnClick.isSelected()) {
                String targetString = e.getTarget().toString();
                StringBuilder result = fixDecoder(targetString);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText(result.toString());
                alert.setTitle("Decoder");
                alert.setHeaderText("FIX message decoded");
                alert.showAndWait();
            } else {
                cellToClipBoard(e);
            }

        } catch (Exception ex) {
        }

    }

    private StringBuilder fixDecoder(String targetString) {
        int firstQuote = targetString.indexOf('"');
        int secondQuote = targetString.indexOf('"', firstQuote + 1);
        String content = targetString.substring(firstQuote + 1, secondQuote);
        if (content.contains(Constants.OUT)) {
            content = content.replace(Constants.OUT + StringUtils.SPACE, "");
        } else {
            content = content.replace(Constants.IN + StringUtils.SPACE, "");
        }

        String[] fields = content.split("\\|");
        List<String> fieldList = List.of(fields);
        StringBuilder result = new StringBuilder();
        for (String field : fieldList) {
            String[] keyValue = field.split("=");
            result
                .append(keyValue[0])
                .append(StringUtils.SPACE)
                .append(Constants.hmTagValue.get(Integer.valueOf(keyValue[0])))
                .append(StringUtils.SPACE)
                .append(keyValue[1])
                .append(getAdditinalInfo(keyValue[0], keyValue[1]))
                .append("\n");
        }
        return result;
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

    public boolean isUseDefaultPort() {
        return useDefaultPort;
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
        String portsA = getConfig(Constants.ACCEPTOR_ROLE, Constants.ACC_ACCEPT_PORT);
        String portsB = getConfig(Constants.ACCEPTOR_ROLE, Constants.ACC_SOCKET_ACCEPT_PORT_RANGE_LIMIT);
        if (listenOnPorts(portsA, portsB)){
            OrderBookLogger orderBookLoggerThread = new OrderBookLogger(this);
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
        }
    }

    public void startInitiator(ActionEvent actionEvent) {
        try {
            SessionSettings initiatorSettings = new SessionSettings();
            port = getConfig(Constants.INITIATOR_ROLE, Constants.INI_CONNECT_PORT);
            if (!StringUtils.isEmpty(port)){
                sessionId = new SessionID(
                    new BeginString(getConfig(Constants.INITIATOR_ROLE, Constants.CONF_BEGIN_STRING)),
                    new SenderCompID(getConfig(Constants.INITIATOR_ROLE, Constants.CONF_SENDER) + port),
                    new TargetCompID(getConfig(Constants.INITIATOR_ROLE, Constants.CONF_TARGET)));
                Dictionary dictionary = new Dictionary();
                dictionary.setString(Constants.CONF_CONNECTION_TYPE, StringUtils.lowerCase(Constants.INITIATOR_ROLE));
                dictionary.setString(Constants.INI_CONNECT_PORT,  port);
                dictionary.setString(Constants.INI_CONNECT_HOST,  getConfig(Constants.INITIATOR_ROLE, Constants.INI_CONNECT_HOST));
                dictionary.setString(Constants.CONF_FILE_STORE_PATH,  getConfig(Constants.INITIATOR_ROLE, Constants.CONF_FILE_STORE_PATH) + port);
                dictionary.setString(Constants.CONF_FILE_LOG_PATH,  getConfig(Constants.INITIATOR_ROLE, Constants.CONF_FILE_LOG_PATH) + port);
                dictionary.setString(Constants.CONF_DATA_DIC,  getConfig(Constants.INITIATOR_ROLE, Constants.CONF_DATA_DIC));
                dictionary.setString(Constants.CONF_START_TIME,  getConfig(Constants.INITIATOR_ROLE, Constants.CONF_START_TIME) );
                dictionary.setString(Constants.CONF_END_TIME, getConfig(Constants.INITIATOR_ROLE, Constants.CONF_END_TIME) );
                dictionary.setString(Constants.CONF_USE_DATA_DIC,  getConfig(Constants.INITIATOR_ROLE, Constants.CONF_USE_DATA_DIC) );
                dictionary.setString(Constants.CONF_RESET_ON_LOGON,  getConfig(Constants.INITIATOR_ROLE, Constants.CONF_RESET_ON_LOGON) );
//                dictionary.setString(Constants.CONF_RESET_ON_DISCONNECT,  getConfig(Constants.INITIATOR_ROLE, Constants.CONF_RESET_ON_DISCONNECT) );
                dictionary.setString(Constants.CONF_HEART_BT_INT,  getConfig(Constants.INITIATOR_ROLE, Constants.CONF_HEART_BT_INT) );
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
            }
        } catch (ConfigError | SessionNotFound   e) {
            e.printStackTrace();
        }
    }

    private String getPort() {
        URL resource = getClass().getClassLoader().getResource(Constants.CLIENT_CFG);
        try {
            SessionSettings initiatorSettings = new SessionSettings(new FileInputStream(new File(resource.toURI())));
            TextInputDialog dialog = null;
            String port = initiatorSettings.getDefaultProperties().getProperty(Constants.INI_CONNECT_PORT);
            dialog = new TextInputDialog(port);
            dialog.setTitle(Constants.INITIATOR_PORT_DIALOG_TITLE);
            dialog.setHeaderText(Constants.INITIATOR_PORT_DIALOG_HEADER);
            dialog.setContentText(Constants.INITIATOR_PORT_DIALOG_TEXT);

            Optional<String> result = dialog.showAndWait();
            if (result.isPresent()) {
                return result.get();
            }
        } catch (ConfigError | FileNotFoundException | URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    @FXML
    private void launchPortWindow(ActionEvent actionEvent) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/fxml/portsWindow.fxml"));
            Parent root = fxmlLoader.load();
            PortsController portsController = fxmlLoader.getController();
            portsController.injectMainController(this);
            Stage stage = new Stage();
            stage.setTitle("PORT");
            stage.setScene(new Scene(root));
            stage.setAlwaysOnTop(true);
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        try {
            if (socketInitiator != null) {
                socketInitiator.stop();
            } else {
                for (FixSession fixSession : fixSessions) {
                    fixSession.stop();
                }
            }
        } catch (Exception ex) {
            this.logger.error(ex.getLocalizedMessage());
        }finally {
            stopConfiguration();
        }
    }

    private void stopConfiguration() {
        try{
            this.circle.setFill(Color.RED);
            this.startAcceptor.setDisable(false);
            this.startInitiator.setDisable(false);
            this.circleStartAcceptor.setFill(Color.RED);
            this.circleStartInitiator.setFill(Color.RED);
            this.tabAcceptor.setDisable(false);
            this.tabInitiator.setDisable(false);
            if (orderBook != null){
                orderBook.stop();
            }
            menuItemMessagePort.setText("");
        }catch (Exception e){
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

    private void cellToClipBoard(MouseEvent e) {
        try {
            final Clipboard clipboard = Clipboard.getSystemClipboard();
            final ClipboardContent content = new ClipboardContent();
            String targetString = e.getTarget().toString();
            int firstQuote = targetString.indexOf('"');
            int secondQuote = targetString.indexOf('"', firstQuote + 1);
            content.putString(targetString.substring(firstQuote + 1, secondQuote));
            clipboard.setContent(content);
        } catch (Exception ex) {
            this.logger.error(ex.getLocalizedMessage());
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
        String textFieldSizeText = this.textFieldSize.getText();
        String textFieldPrice = this.textFieldPrice.getText();
        String textFieldOrderID = this.textFieldOrderID.getText();
        if (StringUtils.isEmpty(textFieldOrderID)) {
            return;
        }
        String textFieldOrigOrderID = this.textFieldOrigOrderID.getText();
//        boolean checkBoxAutoGenSelected = checkBoxAutoGen.isSelected();
        String comboOrdersValue = this.comboOrders.getValue().toString();
        char comboSideValue = this.comboSide.getValue().toString().equals("SELL") ? '2' : '1';

        switch (comboOrdersValue) {
            case Constants.COMBO_NEW_ORDER:
                initator.sendNewOrderSingle(textFieldSizeText, textFieldPrice, textFieldOrderID,
                     comboSideValue);
                break;
            case Constants.COMBO_CANCEL:
                initator.sendOrderCancelRequest(textFieldOrderID, textFieldOrigOrderID, comboSideValue);
                break;
            case Constants.COMBO_REPLACE:
                initator.sendOrderCancelReplaceRequest(textFieldSizeText, textFieldPrice, textFieldOrderID, textFieldOrigOrderID,  comboSideValue);
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

    public void setDefaultPort(ActionEvent actionEvent) {
        TextInputDialog dialog = null;
        List<String> writtenLines = new ArrayList<>();
        int index = 0, portIndex = 0;
        try {
            if (tabAcceptor.isSelected()) {
                Path path = Paths.get("src/main/resources/server.cfg");
                List<String> lines = Files.readAllLines(path);
                String port = "";
                for (String line : lines) {
                    if (!line.contains("#") && line.contains(Constants.ACC_ACCEPT_PORT)) {
                        port = line.substring(line.indexOf('=') + 1);
                        line = line.replace(port, "");
                        portIndex = index;
                    }
                    writtenLines.add(line);
                    index++;
                }
                dialog = new TextInputDialog(port);
                dialog.setTitle(Constants.ACCEPTOR_ROLE);
                dialog.setHeaderText(Constants.SET_DEFAULT_PORT);
                dialog.setContentText(Constants.ACCEPTOR_PORT_DIALOG_TEXT);
                Optional<String> result = dialog.showAndWait();
                if (result.isPresent()) {
                    String current = writtenLines.get(portIndex);
                    writtenLines.set(portIndex, current + result.get());
                    Files.write(Paths.get("src/main/resources/server.cfg"), writtenLines);
                    this.changedDefaultPort = result.get();
                }

            } else if (tabInitiator.isSelected()) {
                Path path = Paths.get("src/main/resources/client.cfg");
                List<String> lines = Files.readAllLines(path);
                String port = "";
                for (String line : lines) {
                    if (!line.contains("#") && line.contains(Constants.INI_CONNECT_HOST)) {
                        port = line.substring(line.indexOf('=') + 1);
                        line = line.replace(port, "");
                        portIndex = index;
                    }
                    writtenLines.add(line);
                    index++;
                }
                dialog = new TextInputDialog(port);
                dialog.setTitle(Constants.INITIATOR_ROLE);
                dialog.setHeaderText(Constants.SET_DEFAULT_PORT);
                dialog.setContentText(Constants.ACCEPTOR_PORT_DIALOG_TEXT);
                Optional<String> result = dialog.showAndWait();
                if (result.isPresent()) {
                    String current = writtenLines.get(portIndex);
                    writtenLines.set(portIndex, current + result.get());
                    Files.write(Paths.get("src/main/resources/client.cfg"), writtenLines);
                    this.changedDefaultPort = result.get();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

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
                if (Utils.availablePort(port)){
                    FixSession fixSession = new FixSession(this, orderBook);
                    fixSession.start(port);
                    fixSessions.add(fixSession);
                    validPorts.append(port).append(",");
                }else{
                    existInvalidPorts = true;
                    invalidPorts.append(port).append("\n");
                }
            }
            if (existInvalidPorts){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle(Constants.PORTS_RANGE_ERROR);
                alert.setHeaderText(Constants.PORTS_IN_USE);
                alert.setContentText(invalidPorts.toString());
                alert.showAndWait();
            }
            if (validPorts.length() > 0){
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

    public void launchInitiatorConfigWindow(ActionEvent actionEvent) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/fxml/initiatorConfigWindow.fxml"));
            Parent root = fxmlLoader.load();
            ConfigController configController = fxmlLoader.getController();
            configController.injectMainController(this);
            configController.injectRole(Constants.INITIATOR_ROLE);
            Stage stage = new Stage();
            stage.setTitle(Constants.INITIATOR_TITLE);
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
            fxmlLoader.setLocation(getClass().getResource("/fxml/acceptorConfigWindow.fxml"));
            Parent root = fxmlLoader.load();
            ConfigController configController = fxmlLoader.getController();
            configController.injectMainController(this);
            configController.injectRole(Constants.ACCEPTOR_ROLE);
            Stage stage = new Stage();
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
            fxmlLoader.setLocation(getClass().getResource("/fxml/advancedRequest.fxml"));
            Parent root = fxmlLoader.load();
            AdvancedRequestController advancedRequestController = fxmlLoader.getController();
            advancedRequestController.injectMainController(this);
            Stage stage = new Stage();
            stage.setTitle(Constants.ADVANCED_REQUEST_TITLE);
            stage.setScene(new Scene(root));
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
}
