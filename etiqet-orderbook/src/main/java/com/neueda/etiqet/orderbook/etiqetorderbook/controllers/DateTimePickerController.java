package com.neueda.etiqet.orderbook.etiqetorderbook.controllers;

import com.neueda.etiqet.orderbook.etiqetorderbook.entity.Tag;
import com.neueda.etiqet.orderbook.etiqetorderbook.utils.Utils;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Dialog;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import jfxtras.scene.control.LocalDateTimePicker;
import jfxtras.scene.control.LocalTimePicker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.time.ZoneId;
import java.util.Date;
import java.util.ResourceBundle;

public class DateTimePickerController implements Initializable {


    private Logger logger = LoggerFactory.getLogger(DateTimePickerController.class);

    public LocalDateTimePicker localDateTimePicker;
    private MainController mainController;

    @FXML
    private AdvancedRequestController advancedRequestController;

    @FXML
    private TableView<Tag> tableView;


    public void injectMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void injectRequestController(AdvancedRequestController advancedRequestController) {
        this.advancedRequestController = advancedRequestController;
    }

    public void injectTable(TableView<Tag> tableView){
        this.tableView = tableView;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void showDateTime(ActionEvent actionEvent) {
        try{
            String date = Utils.getFormattedDateFromLocalDateTime(localDateTimePicker.getLocalDateTime());
            this.logger.info(date);


//            FXMLLoader fxmlLoader = new FXMLLoader();
//            fxmlLoader.setLocation(getClass().getResource("/fxml/advancedRequest.fxml"));
//            Parent root = fxmlLoader.load();
//            AdvancedRequestController controller = fxmlLoader.getController();
//            TableView<Tag> tableViewTags = controller.tableViewTags;
//            String field = tableViewTags.getSelectionModel().getSelectedItem().getField();

        }catch (Exception ex){
            this.logger.warn("Exception in showDateTime -> {}", ex.getLocalizedMessage());
        }
    }

    public void cancelDateTime(ActionEvent actionEvent) {
        Utils.getStage(actionEvent).close();
    }
}
