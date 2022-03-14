package com.neueda.etiqet.orderbook.etiqetorderbook;

import java.time.LocalDateTime;

public class Action {
    private Type type;
    private String buyID;
    private String sellID;
    private String origID;
    private LocalDateTime time;
    private Double size;
    private Double price;

    enum Type{
        PARTIAL_FILL,
        FILL,
        CANCELED,
        REPLACED
    }

    public Action(Type type, String orderIDBuy, String orderIDSell, String origOrderID, LocalDateTime time, Double size, Double price) {
        this.type = type;
        this.buyID = orderIDBuy;
        this.sellID = orderIDSell;
        this.origID = origOrderID;
        this.time = time;
        this.size = size;
        this.price = price;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getBuyID() {
        return buyID;
    }

    public void setBuyID(String buyID) {
        this.buyID = buyID;
    }

    public String getOrigID() {
        return origID;
    }

    public void setOrigID(String origID) {
        this.origID = origID;
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

    public String getSellID() {
        return sellID;
    }

    public void setSellID(String sellID) {
        this.sellID = sellID;
    }

    @Override
    public String toString() {
        return "Action{" +
                "type=" + type +
                ", orderIDBuy='" + buyID + '\'' +
                ", orderIDSell='" + sellID + '\'' +
                ", origOrderID='" + origID + '\'' +
                ", time=" + time +
                ", size=" + size +
                ", price=" + price +
                '}';
    }
}
