package com.neueda.etiqet.orderbook.etiqetorderbook.controllers;

import com.neueda.etiqet.orderbook.etiqetorderbook.entity.Tag;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * @author enol.cacheroramirez@version1.com
 * FIX decoder view
 * Launched from MainController
 */
public class DecoderController implements Initializable {
    public TableView<Tag> tableViewDecoder;
    public TableColumn<Tag, String> tableColumnKey;
    public TableColumn<Tag, String> tableColumnField;
    public TableColumn<Tag, String> tableColumnValue;
    public TableColumn<Tag, String> tableColumnMeaning;

    private List<Tag> tagList;

    public void injectTags(List<Tag> tagList) {
        this.tagList = tagList;
        this.tableViewDecoder.getItems().addAll(this.tagList);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tableColumnKey.setCellValueFactory(new PropertyValueFactory<>("Key"));
        tableColumnField.setCellValueFactory(new PropertyValueFactory<>("Field"));
        tableColumnValue.setCellValueFactory(new PropertyValueFactory<>("Value"));
        tableColumnMeaning.setCellValueFactory(new PropertyValueFactory<>("Meaning"));

    }
}
