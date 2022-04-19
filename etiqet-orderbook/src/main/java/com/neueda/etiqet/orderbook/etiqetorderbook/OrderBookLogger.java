package com.neueda.etiqet.orderbook.etiqetorderbook;

import com.neueda.etiqet.orderbook.etiqetorderbook.controllers.MainController;
import com.neueda.etiqet.orderbook.etiqetorderbook.entity.Order;
import com.neueda.etiqet.orderbook.etiqetorderbook.utils.Constants;

import java.util.Comparator;

public class OrderBookLogger implements Runnable{

    private final MainController mainController;

    public OrderBookLogger(MainController mainController) {
        this.mainController = mainController;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(1000);
                if (this.mainController.isChanged()) {
                    System.out.print("\n\n");
                    Constants.orderBookLogger.info("=================================================================================");
                    Constants.orderBookLogger.info(".....................................BID.........................................");
                    this.mainController.getBuy().stream().sorted(Comparator.comparing(Order::getPrice)).forEach(System.out::println);
                    Constants.orderBookLogger.info(".....................................OFFER.......................................");
                    this.mainController.getSell().stream().sorted(Comparator.comparing(Order::getPrice, Comparator.reverseOrder())).forEach(System.out::println);
                    Constants.orderBookLogger.info("=================================================================================\n\n");
                    this.mainController.setChanged(false);
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
