package com.neueda.etiqet.orderbook.etiqetorderbook.entity;

/**
 * Order sent by an Initiator
 * Shown in tableView
 */
public class Order {
    private String clOrdID;
    private String time;
    private Double orderQty;
    private Double price;
    private String symbol;
    private Character side;
    private String clientID;
    private String timeInForce;
    private boolean removed;
    private String sessionID;
    private String timeToBeRemoved;

    public Order(OrderBuilder orderBuilder) {
        this.clOrdID = orderBuilder.clOrdID;
        this.time = orderBuilder.time;
        this.orderQty = orderBuilder.orderQty;
        this.price = orderBuilder.price;
        this.symbol = orderBuilder.symbol;
        this.side = orderBuilder.side;
        this.clientID = orderBuilder.clientID;
        this.timeInForce = orderBuilder.timeInForce;
        this.removed = orderBuilder.removed;
        this.sessionID = orderBuilder.sessionID;
        this.timeToBeRemoved = orderBuilder.timeToBeRemoved;
    }

    public Order() {
        this.clOrdID = "";
        this.time = "";
        this.orderQty = 0d;
        this.price = 0d;
        this.symbol = "";
        this.side = ' ';
        this.clientID = "";
        this.timeInForce = "1";
        this.removed = false;
        this.sessionID = "";
        this.timeToBeRemoved = "";
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

    public synchronized boolean isRemoved() {
        return removed;
    }

    public synchronized void setRemoved(boolean removed) {
        this.removed = removed;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Character getSide() {
        return side;
    }

    public void setSide(Character side) {
        this.side = side;
    }

    public String getSessionID() {
        return sessionID;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }

    public String getTimeToBeRemoved() {
        return timeToBeRemoved;
    }

    public void setTimeToBeRemoved(String timeToBeRemoved) {
        this.timeToBeRemoved = timeToBeRemoved;
    }

    @Override
    public String toString() {
        return "Order{" +
            "clOrdID='" + clOrdID + '\'' +
            ", time='" + time + '\'' +
            ", orderQty=" + orderQty +
            ", price=" + price +
            ", symbol='" + symbol + '\'' +
            ", side=" + side +
            ", clientID='" + clientID + '\'' +
            ", timeInForce='" + timeInForce + '\'' +
            ", removed=" + removed +
            ", sessionID='" + sessionID + '\'' +
            ", timeToBeRemoved='" + timeToBeRemoved + '\'' +
            '}';
    }

    public static class OrderBuilder {
        private String clOrdID;
        private String time;
        private Double orderQty;
        private Double price;
        private String symbol;
        private Character side;
        private String clientID;
        private String timeInForce;
        private boolean removed;
        private String sessionID;
        private String timeToBeRemoved;

        public OrderBuilder clOrdID(String clOrdID) {
            this.clOrdID = clOrdID;
            return this;
        }

        public OrderBuilder time(String time) {
            this.time = time;
            return this;
        }

        public OrderBuilder orderQty(Double orderQty) {
            this.orderQty = orderQty;
            return this;
        }

        public OrderBuilder symbol(String symbol) {
            this.symbol = symbol;
            return this;
        }

        public OrderBuilder side(Character side) {
            this.side = side;
            return this;
        }

        public OrderBuilder price(Double price) {
            this.price = price;
            return this;
        }

        public OrderBuilder clientID(String clientID) {
            this.clientID = clientID;
            return this;
        }

        public OrderBuilder timeInForce(String timeInForce) {
            this.timeInForce = timeInForce;
            return this;
        }

        public OrderBuilder removed(Boolean removed) {
            this.removed = removed;
            return this;
        }

        public OrderBuilder sessionID(String sessionID) {
            this.sessionID = sessionID;
            return this;
        }

        public OrderBuilder timeToBeRemoved(String timeToBeRemoved) {
            this.timeToBeRemoved = timeToBeRemoved;
            return this;
        }

        public Order build() {
            return new Order(this);
        }


    }
}
