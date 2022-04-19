package com.neueda.etiqet.orderbook.etiqetorderbook.controllers;

import com.neueda.etiqet.orderbook.etiqetorderbook.entity.Tag;
import com.neueda.etiqet.orderbook.etiqetorderbook.utils.AutoCompleteComboBoxListener;
import com.neueda.etiqet.orderbook.etiqetorderbook.utils.Constants;
import com.neueda.etiqet.orderbook.etiqetorderbook.utils.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class AdvancedRequestController implements Initializable {

    public ComboBox<String> comboTags;
    public TableView<Tag> tableViewTags;
    public TableColumn<Tag, String> tableColumnKey;
    public TableColumn<Tag, String> tableColumnField;
    public TableColumn<Tag, String>  tableColumnValue;

    private final Logger logger = LoggerFactory.getLogger(AdvancedRequestController.class);
    public TextArea textAreaFix;
    public TextField arTextFieldValue;

    private MainController mainController;

    private Set<String> tags;

    public void injectMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tableColumnKey.setCellValueFactory(new PropertyValueFactory<>("Key"));
        tableColumnField.setCellValueFactory(new PropertyValueFactory<>("Field"));
        tableColumnValue.setCellValueFactory(new PropertyValueFactory<>("Value"));
        tableViewTags.getItems().addAll(Constants.defaultTags);
        textAreaFix.appendText(Utils.fixEncoder(Constants.defaultTags));
        tags = new HashSet<>();
        xmlReader("./spec/FIX44.xml");


    }




    private void xmlReader(String file){
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newDefaultInstance();
        try{
            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new File(file));
            doc.getDocumentElement().normalize();

            NodeList list = doc.getElementsByTagName("field");
            for (int i = 0; i < list.getLength(); i++){
                Node field = list.item(i).getAttributes().item(0);
                tags.add(field.getTextContent());
                this.logger.info("Field added: {}", field.getTextContent());
            }
            List<String> sortedTags = tags.stream().sorted().collect(Collectors.toList());
            comboTags.getItems().addAll(sortedTags);
            comboTags.getSelectionModel().select(0);
            new AutoCompleteComboBoxListener<>(comboTags);



        }catch (Exception ex){
            this.logger.error("Exception in xmlReader: {}", ex.getMessage());
        }
    }

    public void arButtonAdd(ActionEvent actionEvent) {
        String text = arTextFieldValue.getText();
        String selectedItem = comboTags.getSelectionModel().getSelectedItem();
        Optional<Tag> any = tableViewTags.getItems().stream().filter(t -> t.getField().equals(selectedItem)).findAny();
        if (any.isEmpty()){
            if (!StringUtils.isEmpty(text.trim())){
                Tag tag = new Tag(Utils.getKeyFromValue(selectedItem), selectedItem, text);
                tableViewTags.getItems().add(tag);
                textAreaFix.setText(Utils.fixEncoder(tableViewTags.getItems()));
            }
        }


    }
}
