package com.neueda.etiqet.orderbook.etiqetorderbook.entity;

public class Order {
    private String clOrdID;
    private String time;
    private Double orderQty;
    private Double price;
    private String clientID;
    private String timeInForce;
    private boolean removed;

    public static class OrderBuilder{
        private String clOrdID;
        private String time;
        private Double orderQty;
        private Double price;
        private String clientID;
        private String timeInForce;
        private boolean removed;

        public OrderBuilder clOrdID(String clOrdID){
            this.clOrdID = clOrdID;
            return this;
        }

        public OrderBuilder time(String time){
            this.time = time;
            return this;
        }

        public OrderBuilder orderQty(Double orderQty){
            this.orderQty = orderQty;
            return this;
        }

        public OrderBuilder price(Double price){
            this.price = price;
            return this;
        }

        public OrderBuilder clientID(String clientID){
            this.clientID = clientID;
            return this;
        }

        public OrderBuilder timeInForce(String timeInForce){
            this.timeInForce = timeInForce;
            return this;
        }

        public OrderBuilder removed(Boolean removed){
            this.removed = removed;
            return this;
        }

        public Order build(){
            return new Order(this);
        }


    }

    public Order(OrderBuilder orderBuilder) {
        this.clOrdID = orderBuilder.clOrdID;
        this.time = orderBuilder.time;
        this.orderQty = orderBuilder.orderQty;
        this.price = orderBuilder.price;
        this.clientID = orderBuilder.clientID;
        this.timeInForce = orderBuilder.timeInForce;
        this.removed = orderBuilder.removed;
    }

    public Order(){
        this.clOrdID = "";
        this.time = "";
        this.orderQty = 0d;
        this.price = 0d;
        this.clientID = "";
        this.timeInForce = "1";
        this.removed = false;
    }

    public String getClOrdID() {
        return clOrdID;
    }

    public void setClOrdID(String clOrdID) {
        this.clOrdID = clOrdID;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Double getOrderQty() {
        return orderQty;
    }

    public void setOrderQty(Double orderQty) {
        this.orderQty = orderQty;
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
            "orderID='" + clOrdID + '\'' +
            ", time='" + time + '\'' +
            ", size=" + orderQty +
            ", price=" + price +
            ", clientID='" + clientID + '\'' +
            ", timeInForce=" + timeInForce +
            '}';
    }


}
