package com.neueda.etiqet.orderbook.etiqetorderbook.entity;

public class Order {
    private String orderID;
    private String time;
    private Double size;
    private Double price;
    private String clientID;
    private String timeInForce;
    private boolean removed;

    public Order(String orderID, String time, Double size, Double price, String clientID, String timeInForce) {
        this.orderID = orderID;
        this.time = time;
        this.size = size;
        this.price = price;
        this.clientID = clientID;
        this.timeInForce = timeInForce;
        this.removed = false;
    }

    public Order(){
        this.orderID = "";
        this.time = "";
        this.size = 0d;
        this.price = 0d;
        this.clientID = "";
        this.timeInForce = "1";
        this.removed = false;
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

    public String getTimeInForce() {
        return timeInForce;
    }

    public void setTimeInForce(String timeInForce) {
        this.timeInForce = timeInForce;
    }

    public synchronized void setRemoved(boolean removed) {
        this.removed = removed;
    }

    public synchronized boolean isRemoved() {
        return removed;
    }

    @Override
    public String toString() {
        return "Order{" +
            "orderID='" + orderID + '\'' +
            ", time='" + time + '\'' +
            ", size=" + size +
            ", price=" + price +
            ", clientID='" + clientID + '\'' +
            ", timeInForce=" + timeInForce +
            '}';
    }


}
