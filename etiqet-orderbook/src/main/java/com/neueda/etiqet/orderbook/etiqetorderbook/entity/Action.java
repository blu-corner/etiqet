package com.neueda.etiqet.orderbook.etiqetorderbook.entity;

import java.time.LocalDateTime;

public class Action {
    public Type type;
    private String buyID;
    private String sellID;
    private String buyClientID;
    private String sellClientID;
    private String time;
    private Double buySize;
    private Double sellSize;
    private Double leaveQty;
    private Double agreedPrice;


    public enum Type{
        PARTIAL_FILL,
        FILL,
        CANCELED,
        REPLACED
    }

    public Action(Type type, String buyID, String sellID, String buyClientID, String sellClientID, String time, Double buySize, Double sellSize, Double leaveQty, Double agreedPrice) {
        this.type = type;
        this.buyID = buyID;
        this.buyClientID = buyClientID;
        this.sellClientID = sellClientID;
        this.sellID = sellID;
        this.time = time;
        this.buySize = buySize;
        this.sellSize = sellSize;
        this.leaveQty = leaveQty;
        this.agreedPrice = agreedPrice;
    }

    public Action(){}

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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Double getBuySize() {
        return buySize;
    }

    public void setBuySize(Double buySize) {
        this.buySize = buySize;
    }

    public Double getLeaveQty() {
        return leaveQty;
    }

    public void setLeaveQty(Double leaveQty) {
        this.leaveQty = leaveQty;
    }

    public Double getSellSize() {
        return sellSize;
    }

    public void setSellSize(Double sellSize) {
        this.sellSize = sellSize;
    }

    public Double getAgreedPrice() {
        return agreedPrice;
    }

    public void setAgreedPrice(Double agreedPrice) {
        this.agreedPrice = agreedPrice;
    }

    public String getSellID() {
        return sellID;
    }

    public void setSellID(String sellID) {
        this.sellID = sellID;
    }

    public String getBuyClientID() {
        return buyClientID;
    }

    public void setBuyClientID(String buyClientID) {
        this.buyClientID = buyClientID;
    }

    public String getSellClientID() {
        return sellClientID;
    }

    public void setSellClientID(String sellClientID) {
        this.sellClientID = sellClientID;
    }

    @Override
    public String toString() {
        return "Action{" +
            "type=" + type +
            ", buyID='" + buyID + '\'' +
            ", sellID='" + sellID + '\'' +
            ", buyClientID='" + buyClientID + '\'' +
            ", sellClientID='" + sellClientID + '\'' +
            ", time='" + time + '\'' +
            ", buySize=" + buySize +
            ", sellSize=" + sellSize +
            ", leaveQty=" + leaveQty +
            ", agreedPrice=" + agreedPrice +
            '}';
    }
}
