package com.neueda.etiqet.orderbook.etiqetorderbook;

import com.neueda.etiqet.orderbook.etiqetorderbook.utils.Constants;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.*;
import quickfix.Message;
import quickfix.MessageFactory;
import quickfix.field.*;
import quickfix.fix44.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MainController implements Initializable{

    Logger logger = LoggerFactory.getLogger(MainController.class);
    private List<Order> buy;
    private List<Order> sell;
    private boolean changed;

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
    @FXML public CheckBox checkBoxAutoGen;
    @FXML public CheckBox checkBoxResetSeq;
    @FXML public Button buttonSendOrder;
    @FXML public ListView listViewLog;
    @FXML public ListView listViewActions;

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

    public Circle circle;
    private SocketAcceptor socketAcceptor;
    private Thread orderBook;
    private SocketInitiator socketInitiator;
    private SessionID sessionId;
    private Initator initator;


    private void deleteAcceptorStore(){
        try{
            Files.deleteIfExists(Path.of("store"));
        }catch (Exception e){

        }
    }

    public void startAcceptor(ActionEvent actionEvent) {
        URL resource = getClass().getClassLoader().getResource(Constants.SERVER_CFG);
        try {

            SessionSettings sessionSettings = new SessionSettings(new FileInputStream(new File(resource.toURI())));
            setPort(sessionSettings, Constants.ACCEPTOR_ROLE);
            MessageStoreFactory messageStoreFactory = new FileStoreFactory(sessionSettings);
            LogFactory logFactory = new FileLogFactory(sessionSettings);
            MessageFactory messageFactory = new DefaultMessageFactory();
            Acceptor acceptor = new Acceptor(this);
            socketAcceptor = new SocketAcceptor(acceptor, messageStoreFactory, sessionSettings, logFactory, messageFactory);
            socketAcceptor.start();
            OrderBookThread orderBookThread = new OrderBookThread(this);
            orderBook = new Thread(orderBookThread);
            orderBook.setDaemon(true);
            orderBook.start();

            circle.setFill(Color.GREENYELLOW);
            this.startAcceptor.setDisable(true);
            this.startInitiator.setDisable(true);
            this.circleStartAcceptor.setFill(Color.GREENYELLOW);
            this.tabInitiator.setDisable(true);
            this.mainTabPane.getSelectionModel().select(0);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setPort(SessionSettings sessionSettings, String role) {
        TextInputDialog dialog = null;
        if (role.equals(Constants.ACCEPTOR_ROLE)){
            String port = sessionSettings.getDefaultProperties().getProperty(Constants.SOCKET_ACCEPTOR_PORT);
            dialog = new TextInputDialog(port);
            dialog.setTitle(Constants.ACCEPTOR_PORT_DIALOG_TITLE);
//            dialog.initStyle(StageStyle.UTILITY);
            dialog.setHeaderText(Constants.ACCEPTOR_PORT_DIALOG_HEADER);
            dialog.setContentText(Constants.ACCEPTOR_PORT_DIALOG_TEXT);
        }else{
            String port = sessionSettings.getDefaultProperties().getProperty(Constants.SOCKET_INITIATOR_PORT);
            dialog = new TextInputDialog(port);
            dialog.setTitle(Constants.INITIATOR_PORT_DIALOG_TITLE);
//            dialog.initStyle(StageStyle.UTILITY);
            dialog.setHeaderText(Constants.INITIATOR_PORT_DIALOG_HEADER);
            dialog.setContentText(Constants.INITIATOR_PORT_DIALOG_TEXT);
        }

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(s -> {
            sessionSettings.getDefaultProperties().setProperty(Constants.SOCKET_ACCEPTOR_PORT, result.get());
        });
    }

    private void killProcessByPort(int port) {
        try {
            ArrayList<Long> pids = new ArrayList<Long>();
            Stream<ProcessHandle> processStream = ProcessHandle.allProcesses();

            List<ProcessHandle> processHandleList = processStream.collect(Collectors.toList());

            for (ProcessHandle p: processHandleList) {
                if (p.isAlive()){
                    ProcessHandle.Info info = p.info();
                    Optional<String> command = p.info().command();
                    if (command.get().contains("java")){
                        String doso = "";
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        try {
            if (socketInitiator != null){
                socketInitiator.stop();
            }else{
                socketAcceptor.stop();
            }
            this.circle.setFill(Color.RED);
            this.startAcceptor.setDisable(false);
            this.startInitiator.setDisable(false);
            this.circleStartAcceptor.setFill(Color.RED);
            this.circleStartInitiator.setFill(Color.RED);
            this.tabAcceptor.setDisable(false);
            this.tabInitiator.setDisable(false);
            this.orderBook.stop();


        } catch (Exception ex) {
            this.logger.error(ex.getLocalizedMessage());
        }
    }

    public void startInitiator(ActionEvent actionEvent) {
        try {
            URL resource = getClass().getClassLoader().getResource(Constants.CLIENT_CFG);
            SessionSettings initiatorSettings = new SessionSettings(new FileInputStream(new File(resource.toURI())));
            setPort(initiatorSettings, Constants.INITIATOR_ROLE);
            FileStoreFactory fileStoreFactory = new FileStoreFactory(initiatorSettings);
            FileLogFactory fileLogFactory = new FileLogFactory(initiatorSettings);
            MessageFactory messageFactory = new DefaultMessageFactory();
            socketInitiator = new SocketInitiator(initator, fileStoreFactory, initiatorSettings, fileLogFactory, messageFactory);
            socketInitiator.start();

            sessionId = socketInitiator.getSessions().get(0);
//            sendLogoutRequest(sessionId);
            sendLogonRequest(sessionId);

            circle.setFill(Color.GREENYELLOW);
            this.startInitiator.setDisable(true);
            this.startAcceptor.setDisable(true);
            this.circleStartInitiator.setFill(Color.GREENYELLOW);
            this.tabAcceptor.setDisable(true);
            this.mainTabPane.getSelectionModel().select(1);

//            do{
//                Thread.sleep(5000);
//                logger.info("Logged on -> {}", socketInitiator.isLoggedOn());
//            }while (true);

        } catch (ConfigError | IOException | URISyntaxException | SessionNotFound e) {
            e.printStackTrace();
        }


    }

    private void sendLogonRequest(SessionID sessionID)throws SessionNotFound{
        Logon logon = new Logon();
        Message.Header header = logon.getHeader();
        header.setField(new BeginString("FIX.4.4"));
        logon.set(new HeartBtInt(30));
        logon.set(new ResetSeqNumFlag(true));
        logon.set(new EncryptMethod(0));
        logon.set(new ResetSeqNumFlag(true));
        boolean sent = Session.sendToTarget(logon, sessionID);
        logger.info("Logon message sent: {}", sent);
    }


    private void sendSeqReset(SessionID sessionID)throws SessionNotFound{
        SequenceReset sequenceReset = new SequenceReset();
//        GapFillFlag gapFillFlag = new GapFillFlag();
        NewSeqNo newSeqNo = new NewSeqNo(10);
//        sequenceReset.set(gapFillFlag);
        sequenceReset.set(newSeqNo);
        MsgSeqNum msgSeqNum = new MsgSeqNum(10);


        boolean sent = Session.sendToTarget(sequenceReset, sessionID);
        logger.info("Sequence reset message sent: {}", sent);
    }

    private void sendLogoutRequest(SessionID sessionID)throws SessionNotFound{
        Logout logout = new Logout();
        boolean sent = Session.sendToTarget(logout, sessionID);
        logger.info("Logout message sent: {}", sent);
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

        comboOrders.getItems().addAll("NEW ORDER", "CANCEL", "REPLACE");
        comboOrders.getSelectionModel().select(0);

        comboSide.getItems().addAll("BUY", "SELL");
        comboSide.getSelectionModel().select(0);
        initator = new Initator(listViewActions, listViewLog);



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

//    private ObservableList<Order> getOrders() {
//        ObservableList<Order> orders = FXCollections.observableArrayList();
//        orders.add(new Order("4579", LocalDateTime.now(), 34d, 2d));
//        return orders;
//    }

    public List<Order> getBuy() {
        return buy;
    }

    public void addBuy(Order buy) {
        this.buy.add(buy);
        List<Order> tempList = new ArrayList<>(orderBookBuyTableView.getItems());
        tempList.add(buy);
        tempList = tempList.stream().sorted(Comparator.comparing(Order::getPrice)).collect(Collectors.toList());
        orderBookBuyTableView.getItems().clear();
        tempList.forEach(b -> {
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
        tempList.forEach(b -> {
            orderBookSellTableView.getItems().add(b);
        });
        orderBookSellTableView.getSelectionModel().clearAndSelect(0);
    }

    //initator: send order
    public void sendOrder(ActionEvent actionEvent) {
        String textFieldSizeText = this.textFieldSize.getText();
        String textFieldPrice = this.textFieldPrice.getText();
        String textFieldOrderID = this.textFieldOrderID.getText();
        String textFieldOrigOrderID = this.textFieldOrigOrderID.getText();
        boolean checkBoxAutoGenSelected = checkBoxAutoGen.isSelected();
        boolean checkBoxResetSeqSelected = checkBoxResetSeq.isSelected();
        String comboOrdersValue = this.comboOrders.getValue().toString();
        char comboSideValue = this.comboSide.getValue().toString().equals("SELL") ? '2' : '1';

        switch (comboOrdersValue){
            case "NEW ORDER":
                initator.sendNewOrderSingle(textFieldSizeText,textFieldPrice,textFieldOrderID,
                    checkBoxAutoGenSelected, checkBoxResetSeqSelected, comboSideValue);
                break;
        }
    }

}
