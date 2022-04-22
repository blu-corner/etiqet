package com.neueda.etiqet.orderbook.etiqetorderbook.controllers;

import com.neueda.etiqet.orderbook.etiqetorderbook.entity.Tag;
import com.neueda.etiqet.orderbook.etiqetorderbook.utils.AutoCompleteComboBoxListener;
import com.neueda.etiqet.orderbook.etiqetorderbook.utils.Constants;
import com.neueda.etiqet.orderbook.etiqetorderbook.utils.Utils;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import quickfix.*;
import quickfix.field.*;
import quickfix.fix44.MessageFactory;
import quickfix.fix44.NewOrderSingle;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.*;
import java.io.File;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import static com.neueda.etiqet.orderbook.etiqetorderbook.fix.Initator.price;
import static com.neueda.etiqet.orderbook.etiqetorderbook.fix.Initator.side;
import static com.neueda.etiqet.orderbook.etiqetorderbook.utils.Utils.getConfig;

public class AdvancedRequestController implements Initializable {

    public ComboBox<String> comboFieldTags;
    public TableView<Tag> tableViewTags;
    public TableColumn<Tag, String> tableColumnKey;
    public TableColumn<Tag, String> tableColumnField;
    public TableColumn<Tag, String> tableColumnValue;

    private final Logger logger = LoggerFactory.getLogger(AdvancedRequestController.class);
    public TextArea textAreaFix;
    public TextField arTextFieldValue;
    public ComboBox<String> comboKeyTags;
    public ComboBox<String> comboStoredOrigID;
    private List<String> storedOrigIDs;

    private MainController mainController;

    private Set<String> tags;

    public void injectMainController(MainController mainController) {
        this.mainController = mainController;
        init();
    }

    private void init(){
        Tag senderInDefaultList = Constants.defaultTags.stream().filter(tag -> tag.getField().equals(Constants.CONF_SENDER)).findFirst().get();
        senderInDefaultList.setValue(getConfig(Constants.INITIATOR_ROLE, Constants.CONF_SENDER ) + this.mainController.getConnectedPort());
        tableViewTags.getItems().addAll(Constants.defaultTags);
        textAreaFix.appendText(Utils.fixEncoder(Constants.defaultTags));
        tags = new HashSet<>();
        storedOrigIDs = new ArrayList<>();
        xmlReader("./spec/FIX44.xml");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        tableColumnKey.setCellValueFactory(new PropertyValueFactory<>("Key"));
        tableColumnField.setCellValueFactory(new PropertyValueFactory<>("Field"));
        tableColumnValue.setCellValueFactory(new PropertyValueFactory<>("Value"));
        tableColumnValue.setCellFactory(TextFieldTableCell.forTableColumn());
        tableColumnValue.setOnEditCommit(data -> {
            this.logger.info("Edit request: old value -> {}", data.getOldValue());
            this.logger.info("Edit request: new value -> {}", data.getNewValue());

            if (data.getRowValue().getField().equals("SendingTime")) {

            }
            substituteTag(data.getRowValue().getKey(), data.getNewValue());
        });
        tableViewTags.setEditable(true);
        comboStoredOrigID.getItems().add("");
        comboStoredOrigID.getItems().add("New");
    }



    private void xmlReader(String file) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newDefaultInstance();
        try {
            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new File(file));
            doc.getDocumentElement().normalize();

            NodeList list = doc.getElementsByTagName("field");
            for (int i = 0; i < list.getLength(); i++) {
                Node field = list.item(i).getAttributes().item(0);
                tags.add(field.getTextContent());
                this.logger.info("Field added: {}", field.getTextContent());
            }
            List<String> sortedTags = tags.stream().sorted().collect(Collectors.toList());
            sortedTags.forEach(t -> {
                comboFieldTags.getItems().add(t);
                comboKeyTags.getItems().add(Utils.getKeyFromValue(t));
            });
            comboFieldTags.getSelectionModel().select(0);
            comboKeyTags.getSelectionModel().select(0);
            Platform.runLater(() -> {
                new AutoCompleteComboBoxListener<>(comboFieldTags);
            });

            Platform.runLater(() -> {
                new AutoCompleteComboBoxListener<>(comboKeyTags);
            });


        } catch (Exception ex) {
            this.logger.error("Exception in xmlReader: {}", ex.getMessage());
        }
    }

    public void arButtonAdd(ActionEvent actionEvent) {
        try{
            Platform.runLater(() -> {
                String text = arTextFieldValue.getText();
                String selectedItem = comboFieldTags.getSelectionModel().getSelectedItem();
                Optional<Tag> any = tableViewTags.getItems().stream().filter(t -> t.getField().equals(selectedItem)).findAny();
                if (any.isEmpty()) {
                    Tag tag = new Tag(Utils.getKeyFromValue(selectedItem), selectedItem, text);
                    if (isAddableItem(tag) && !StringUtils.isEmpty(text.trim())) {
                        tableViewTags.getItems().add(tag);
                        updateFixTextArea();
                    }
                } else {
                    substituteTag(any.get().getKey(), text);
                }
            });
        }catch (Exception ex){
            this.logger.warn("Exception arButtonAdd -> {}", ex.getMessage());
        }
    }

    public void substituteTag(String tagKey, String newValue) {
        Optional<Tag> target = tableViewTags.getItems().stream().filter(t -> t.getKey().equals(tagKey)).findFirst();
        if (target.isPresent()) {
            Tag modified = target.get();
            modified.setValue(newValue);
            List<Tag> newTagList = new ArrayList<>(tableViewTags.getItems());
            tableViewTags.getItems().clear();
            tableViewTags.getItems().addAll(newTagList);
            updateFixTextArea();
        }
    }

    private void updateFixTextArea() {
        try {
            textAreaFix.setText(Utils.fixEncoder(tableViewTags.getItems()));
        } catch (Exception ex) {
            this.logger.warn("Exception updateFixTextArea -> {}", ex.getMessage());
        }
    }

    public void arButtonSend(ActionEvent actionEvent) {
        String msgType = tableViewTags.getItems().stream().filter(t -> t.getKey().equals(Constants.KEY_MSG_TYPE)).findFirst().get().getValue();
        try {
            MessageFactory messageFactory = new MessageFactory();
            Message message = messageFactory.create("FIX.4.4", msgType);
            message.fromString(Utils.replaceVerticalBar(textAreaFix.getText()), null, false, true);
            Session.sendToTarget(message, this.mainController.getSessionId());
        } catch (Exception ex) {
            this.logger.error("Exception in arButtonSend: {}", ex.getMessage());
        }
    }

    public void arButtonRemove(ActionEvent actionEvent) {
        Tag selectedItem = tableViewTags.getSelectionModel().getSelectedItem();
        if (isRemovableItem(selectedItem)) {
            Platform.runLater(() -> {
                tableViewTags.getItems().remove(selectedItem);
                updateFixTextArea();
            });

        }
    }

    public void arButtonCancel(ActionEvent actionEvent) {
        Utils.getStage(actionEvent).close();
    }

    private boolean isRemovableItem(Tag tag) {
        if (tag == null) return false;
        boolean isNotBeginString = !tag.getKey().equals(Constants.KEY_BEGIN_STRING);
        boolean isNotBodyLength = !tag.getKey().equals(Constants.KEY_BODY_LENGTH);
        boolean isNotMessageType = !tag.getKey().equals(Constants.KEY_MSG_TYPE);
        return isNotBeginString && isNotBodyLength && isNotMessageType;
    }

    private boolean isAddableItem(Tag tag) {
        if (tag == null) return false;
        boolean isNotTarget= !tag.getKey().equals(Constants.KEY_TARGET);
        boolean isNotSender= !tag.getKey().equals(Constants.KEY_SENDER);
        boolean isNotBeginString = !tag.getKey().equals(Constants.KEY_BEGIN_STRING);
        boolean isNotBodyLength = !tag.getKey().equals(Constants.KEY_BODY_LENGTH);
        boolean isNotMessageType = !tag.getKey().equals(Constants.KEY_MSG_TYPE);
        boolean isNotChecksum = !tag.getKey().equals(Constants.KEY_CHECKSUM);
        boolean existingKey = Constants.hmTagValue.containsKey(Integer.parseInt(tag.getKey()));
        boolean existingField = !Utils.getKeyFromValue(tag.getField()).equals("-1");
        return isNotTarget && isNotSender && isNotBeginString && isNotBodyLength && isNotMessageType && isNotChecksum && existingKey && existingField;
    }

    public void arButtonClear(ActionEvent actionEvent) {
        Platform.runLater(() -> {
            tableViewTags.getItems().removeIf(this::isRemovableItem);
            updateFixTextArea();
        });
    }

    public void arButtonUpdate(ActionEvent actionEvent) {
        String[] fields = textAreaFix.getText().split("\\|");
        List<String> fieldList = List.of(fields);
        List<Tag> fixToListTags = new ArrayList<>();
        for (String field : fieldList) {
            String[] keyValue = field.split("=");
            Tag tag = new Tag();
            tag.setKey(keyValue[0]);
            if (!NumberUtils.isDigits(tag.getKey()) || !Constants.hmTagValue.containsKey(Integer.parseInt(tag.getKey()))) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Bad format");
                alert.setHeaderText(Constants.TAGS_MUST_BE_NUMERIC_AND_VALID_FIX_PROTOCOL_KEYS);
                alert.setContentText(String.format("Bad format: %s", tag.getKey()));
                alert.initOwner(Utils.getStage(actionEvent));
                alert.showAndWait();
                return;
            }
            tag.setValue(keyValue[1]);
            tag.setField(Constants.hmTagValue.get(Integer.parseInt(tag.getKey())));
            fixToListTags.add(tag);
        }
        arButtonClear(actionEvent);
        Platform.runLater(() -> {
            for (Tag tag : fixToListTags) {
                if (isAddableItem(tag)) {
                    tableViewTags.getItems().add(tag);
                }
            }
            updateFixTextArea();
        });

    }


    public void actionComboFieldTag(ActionEvent actionEvent) {
        this.logger.info(comboFieldTags.getValue());
        comboKeyTags.getSelectionModel().select(Utils.getKeyFromValue(comboFieldTags.getValue()));
    }

    public void actionComboKeysTag(ActionEvent actionEvent) {
        try{
            this.logger.info(comboKeyTags.getValue());
            comboFieldTags.getSelectionModel().select(Constants.hmTagValue.get(Integer.parseInt(comboKeyTags.getValue())));
        }catch (Exception ex){
            this.logger.warn("Exception in actionComboKeysTag -> {}", ex.getLocalizedMessage());
        }

    }

    public void setFocusedAllText(MouseEvent mouseEvent) {
        if (mouseEvent.getSource().toString().contains("comboKeyTags")) {
            Platform.runLater(() -> {
                comboKeyTags.getEditor().selectAll();
            });
        } else {
            Platform.runLater(() -> {
                comboFieldTags.getEditor().selectAll();
            });
        }

    }


    public void actionComboStoredOrigID(ActionEvent actionEvent) {
        comboStoredID();
    }

    public void actionComboMouseStoredOrigID(MouseEvent mouseEvent) {
        comboStoredID();
    }

    private void comboStoredID(){
        try{
            if (StringUtils.isEmpty(comboStoredOrigID.getValue())){
                return;
            }
            tableViewTags.getItems().removeIf(tag -> tag.getKey().equals(Constants.KEY_ORIG_CL_ORD_ID));
            Tag tag = new Tag();
            tag.setKey(Constants.KEY_ORIG_CL_ORD_ID);
            tag.setField("OrigClOrdID");
            String newOrigOrdId = RandomStringUtils.randomAlphanumeric(8);
            if (comboStoredOrigID.getValue().equals(Constants.COMBO_NEW_ORDER_ID)){
                tag.setValue(newOrigOrdId);
                tableViewTags.getItems().add(tag);
                storedOrigIDs.add(newOrigOrdId);
                comboStoredOrigID.getItems().add(newOrigOrdId);

            }else{
                tag.setValue(comboStoredOrigID.getValue());
                tableViewTags.getItems().add(tag);
            }
            updateFixTextArea();
        }catch (Exception ex){
            this.logger.warn("Exception in comboStoredID -> {}", ex.getLocalizedMessage());
        }

    }
}
