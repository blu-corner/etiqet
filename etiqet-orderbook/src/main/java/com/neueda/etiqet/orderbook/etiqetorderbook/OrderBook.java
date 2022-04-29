package com.neueda.etiqet.orderbook.etiqetorderbook;

import com.neueda.etiqet.orderbook.etiqetorderbook.controllers.MainController;
import com.neueda.etiqet.orderbook.etiqetorderbook.entity.Action;
import com.neueda.etiqet.orderbook.etiqetorderbook.entity.Order;
import com.neueda.etiqet.orderbook.etiqetorderbook.utils.Constants;
import com.neueda.etiqet.orderbook.etiqetorderbook.utils.Utils;
import javafx.application.Platform;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.field.TimeInForce;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.stream.Collectors;

public class OrderBook implements Runnable {

    private Logger logger = LoggerFactory.getLogger(OrderBook.class);

    private final MainController mainController;

    public OrderBook(MainController mainController) {
        this.mainController = mainController;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(1000);
                Platform.runLater(() -> {
                    addActions();
                    this.mainController.getSell().forEach(s -> this.logger.info(s.getOrderID() + ": " + s.isRemoved()));
                    this.mainController.getBuy().removeIf(Order::isRemoved);
                    this.mainController.orderBookBuyTableView.getItems().removeIf(Order::isRemoved);
                    this.mainController.getSell().removeIf(Order::isRemoved);
                    this.mainController.orderBookSellTableView.getItems().removeIf(Order::isRemoved);
                    this.mainController.getBuy().forEach(this::removeIfTimeInForceDay);
                    this.mainController.getSell().forEach(this::removeIfTimeInForceDay);
                });

                if (this.mainController.isChanged()) {
                    System.out.print("\n\n");
                    Constants.orderBook.info("=================================================================================");
                    Constants.orderBook.info(".....................................BID.........................................");
                    this.mainController.getBuy().stream().sorted(Comparator.comparing(Order::getPrice)).forEach(System.out::println);
                    Constants.orderBook.info(".....................................OFFER.......................................");
                    this.mainController.getSell().stream().sorted(Comparator.comparing(Order::getPrice, Comparator.reverseOrder())).forEach(System.out::println);
                    Constants.orderBook.info("=================================================================================\n\n");
                    this.mainController.setChanged(false);
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void addActions() {
        for (Order order : this.mainController.getBuy().stream().filter(Order::isRemoved).collect(Collectors.toList())){
            Action action = new Action(Action.Type.CANCELED, order.getOrderID(), "", order.getClientID(), "",Utils.getFormattedStringDate(), order.getSize(), null, 0d, 0d);
            this.mainController.actionTableView.getItems().add(action);
            this.mainController.reorderActionTableView();
        }
        for (Order order : this.mainController.getSell().stream().filter(Order::isRemoved).collect(Collectors.toList())){
            Action action = new Action(Action.Type.CANCELED,  "", order.getOrderID(), "",order.getClientID(), Utils.getFormattedStringDate(),  null,order.getSize(), 0d, 0d);
            this.mainController.actionTableView.getItems().add(action);
            this.mainController.reorderActionTableView();
        }

    }

    private void removeIfTimeInForceDay(Order order) {
        String endTime;
        switch (order.getTimeInForce()) {
            case TimeInForce.DAY:
                endTime = Utils.getConfig(Constants.ACCEPTOR_ROLE, Constants.CONF_END_TIME);
                break;
            case TimeInForce.AT_THE_OPENING:
                endTime = Utils.getConfig(Constants.ACCEPTOR_ROLE, Constants.CONF_START_TIME);
                break;
            default:
                endTime = null;
        }

        if (endTime != null) {
            String[] endTimeSplit = endTime.split(":");
            LocalDate localDateNow = LocalDate.now();
            LocalTime localTimeNow = LocalTime.now();
            LocalDateTime localDateTimeNow = LocalDateTime.of(localDateNow, localTimeNow);
            LocalDateTime limitTime = LocalDateTime.of(localDateNow, LocalTime.of(Integer.parseInt(endTimeSplit[0]), Integer.parseInt(endTimeSplit[1])));
            this.logger.info("Now {} -> EndOfDay -> {}", localDateTimeNow, limitTime);
            if (localDateTimeNow.isAfter(limitTime)) {
                order.setRemoved(true);
            }
        }
    }
}
