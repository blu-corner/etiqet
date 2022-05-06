package com.neueda.etiqet.orderbook.etiqetorderbook.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import jfxtras.scene.control.LocalDateTimePicker;
import jfxtras.scene.control.LocalTimePicker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class DateTimePickerController implements Initializable {


    private Logger logger = LoggerFactory.getLogger(DateTimePickerController.class);

    public LocalDateTimePicker localDateTimePicker;
    public LocalTimePicker localTimePicker;
    private MainController mainController;


    public void injectMainController(MainController mainController){
        this.mainController = mainController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void showDateTime(ActionEvent actionEvent) {
        this.logger.info(localDateTimePicker.getLocalDateTime().toString());
        this.logger.info(localTimePicker.getLocalTime().toString());
    }
}
