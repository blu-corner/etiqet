package com.neueda.etiqet.orderbook.etiqetorderbook;

import com.neueda.etiqet.orderbook.etiqetorderbook.controllers.MainController;
import com.neueda.etiqet.orderbook.etiqetorderbook.entity.Action;
import com.neueda.etiqet.orderbook.etiqetorderbook.entity.Order;
import com.neueda.etiqet.orderbook.etiqetorderbook.fix.Acceptor;
import com.neueda.etiqet.orderbook.etiqetorderbook.fix.FixSession;
import com.neueda.etiqet.orderbook.etiqetorderbook.utils.Constants;
import com.neueda.etiqet.orderbook.etiqetorderbook.utils.Utils;
import javafx.application.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.field.TimeInForce;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Thread dealing with several tasks related to the orderbook
 * Interacts with Acceptor tableviews and handles life cycle of orders
 */
public class OrderBook implements Runnable {

    private final MainController mainController;
    private final Logger logger = LoggerFactory.getLogger(OrderBook.class);

    public OrderBook(MainController mainController) {
        this.mainController = mainController;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(1000);

                Platform.runLater(() -> {
                    this.mainController.labelClock.setText(Utils.getFormattedTimeFromLocalTime(LocalDateTime.now()));
                    addActions();
                    this.mainController.getSell().forEach(s -> this.logger.info(s.getClOrdID() + ": " + s.isRemoved()));
                    this.mainController.getBuy().removeIf(Order::isRemoved);
                    this.mainController.orderBookBuyTableView.getItems().removeIf(Order::isRemoved);
                    this.mainController.getSell().removeIf(Order::isRemoved);
                    this.mainController.orderBookSellTableView.getItems().removeIf(Order::isRemoved);
                    this.mainController.getBuy().forEach(this::removeIfTimeExpired);
                    this.mainController.getSell().forEach(this::removeIfTimeExpired);
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

    /**
     * If an order was canceled, a new action row is added in the action tableview
     */
    private void addActions() {
        Acceptor acceptor = new Acceptor(this.mainController);
        for (Order order : this.mainController.getBuy().stream().filter(Order::isRemoved).collect(Collectors.toList())) {
            Action action = new Action.ActionBuilder()
                .type(Constants.Type.CANCELED)
                .buyID(order.getClOrdID())
                .buyClientID(order.getClientID())
                .timeInForceBuy(order.getTimeInForce())
                .time(Utils.getFormattedStringDate())
                .buySize(order.getOrderQty())
                .build();
            acceptor.addAction(order, null, action);
            FixSession fixSession = this.mainController.fixSessions.stream().filter(s -> s.getSessionID().toString().equals(order.getSessionID())).findFirst().get();
            acceptor.sendExecutionReportAfterCanceling(order, fixSession);

        }
        for (Order order : this.mainController.getSell().stream().filter(Order::isRemoved).collect(Collectors.toList())) {
            Action action = new Action.ActionBuilder()
                .type(Constants.Type.CANCELED)
                .sellID(order.getClOrdID())
                .sellClientID(order.getClientID())
                .timeInForceSell(order.getTimeInForce())
                .time(Utils.getFormattedStringDate())
                .sellSize(order.getOrderQty())
                .build();
            acceptor.addAction(null, order, action);
            FixSession fixSession = this.mainController.fixSessions.stream().filter(s -> s.getSessionID().toString().equals(order.getSessionID())).findFirst().get();
            acceptor.sendExecutionReportAfterCanceling(order, fixSession);

        }

    }

    /**
     * Checks the order timeToBeRemoved field to determine if particular
     * order should be canceled right now
     * @param order
     */
    private void removeIfTimeExpired(Order order) {
        LocalDate localDateNow = LocalDate.now();
        LocalTime localTimeNow = LocalTime.now();
        LocalDateTime localDateTimeNow = LocalDateTime.of(localDateNow, localTimeNow);
        String formattedDate = Utils.getFormattedDateFromLocalDateTime(localDateTimeNow);
        this.logger.info("Order {} time: {} timeToBeRemoved {} result -> {}", order.getClOrdID(), formattedDate, order.getTimeToBeRemoved(), (order.getTime().compareTo(order.getTimeToBeRemoved())));
        this.logger.info("********************************************************");

        Optional<Character> timeInForce = Optional.ofNullable(Constants.TIME_IN_FORCE.getValue(order.getTimeInForce()));
        if (timeInForce.isPresent()) {
            switch (timeInForce.get()) {
                case TimeInForce.DAY:
                case TimeInForce.IMMEDIATE_OR_CANCEL:
                case TimeInForce.FILL_OR_KILL:
                case TimeInForce.AT_THE_OPENING:
                case TimeInForce.GOOD_TILL_DATE:
                    if (formattedDate.compareTo(order.getTimeToBeRemoved()) > 0) {
                        order.setRemoved(true);
                    }
                    break;
            }

        }

    }
}
