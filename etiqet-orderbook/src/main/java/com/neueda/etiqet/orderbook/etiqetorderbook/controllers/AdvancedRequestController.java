package com.neueda.etiqet.orderbook.etiqetorderbook.controllers;

import com.neueda.etiqet.orderbook.etiqetorderbook.controllers.MainController;

public class AdvancedRequestController {

    private MainController mainController;

    public void injectMainController(MainController mainController) {
        this.mainController = mainController;
    }


}
