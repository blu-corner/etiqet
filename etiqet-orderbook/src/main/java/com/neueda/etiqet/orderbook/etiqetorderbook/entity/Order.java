package com.neueda.etiqet.orderbook.etiqetorderbook.entity;

public class Order {
    private String orderID;
    private String time;
    private Double size;
    private Double price;
    private String clientID;

    public Order(String orderID, String time, Double size, Double price, String clientID) {
        this.orderID = orderID;
        this.time = time;
        this.size = size;
        this.price = price;
        this.clientID = clientID;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
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

    public String getClientID() {
        return clientID;
    }

    public void setClientID(String clientID) {
        this.clientID = clientID;
    }

    @Override
    public String toString() {
        return  "OrderID=" + orderID +
                ".....Time=" + time +
                ".....Size=" + size +
                ".....Price=" + price;

    }
}
