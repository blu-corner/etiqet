package com.neueda.etiqet.orderbook.etiqetorderbook;

import com.neueda.etiqet.orderbook.etiqetorderbook.controllers.MainController;
import com.neueda.etiqet.orderbook.etiqetorderbook.entity.Order;
import com.neueda.etiqet.orderbook.etiqetorderbook.utils.Constants;
import com.neueda.etiqet.orderbook.etiqetorderbook.utils.Utils;
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

public class OrderBook implements Runnable{

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
                this.mainController.getBuy().removeIf(Order::isRemoved);
                this.mainController.orderBookBuyTableView.getItems().removeIf(Order::isRemoved);
                this.mainController.getSell().removeIf(Order::isRemoved);
                this.mainController.orderBookSellTableView.getItems().removeIf(Order::isRemoved);
                this.mainController.getBuy().forEach(this::removeIfTimeInForceDay);
                this.mainController.getSell().forEach(this::removeIfTimeInForceDay);


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

    private void removeIfTimeInForceDay(Order order) {
        if (order.getTimeInForce().equals(TimeInForce.DAY)){
            String endTime = Utils.getConfig(Constants.ACCEPTOR_ROLE, Constants.CONF_END_TIME);
            String[] endTimeSplit = endTime.split(":");
            LocalDate localDateNow = LocalDate.now();
            LocalTime localTimeNow = LocalTime.now();
            LocalDateTime localDateTimeNow = LocalDateTime.of(localDateNow, localTimeNow);
            LocalDateTime endOfDay = LocalDateTime.of(localDateNow, LocalTime.of(Integer.parseInt(endTimeSplit[0]), Integer.parseInt(endTimeSplit[1])));
            this.logger.info("Now {} -> EndOfDay -> {}", localDateTimeNow, endOfDay);
            if (localDateTimeNow.isAfter(endOfDay)){
                order.setRemoved(true);
            }
        }
    }
}
