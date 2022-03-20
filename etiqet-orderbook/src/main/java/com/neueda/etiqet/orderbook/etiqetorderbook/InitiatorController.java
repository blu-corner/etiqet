package com.neueda.etiqet.orderbook.etiqetorderbook;

import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public class InitiatorController implements Initializable {
    private MainController mainController;

    public void init(MainController mainController){
        this.mainController = mainController;
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
