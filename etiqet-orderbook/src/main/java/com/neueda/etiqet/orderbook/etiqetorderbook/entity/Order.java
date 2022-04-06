package com.neueda.etiqet.orderbook.etiqetorderbook.entity;

import java.time.LocalDateTime;

public class Order {
    private String orderID;
    private LocalDateTime time;
    private Double size;
    private Double price;

    public Order(String orderID, LocalDateTime time, Double size, Double price) {
        this.orderID = orderID;
        this.time = time;
        this.size = size;
        this.price = price;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public Double getSize() {
        return size;
    }

    public void setSize(Double size) {
        this.size = size;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return  "OrderID=" + orderID +
                ".....Time=" + time +
                ".....Size=" + size +
                ".....Price=" + price;

    }
}
