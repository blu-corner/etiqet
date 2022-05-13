package com.neueda.etiqet.orderbook.etiqetorderbook.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Used to import/export XML file with orderbook data
 */
public class OrderXML {
    List<Order> buyOrders;
    List<Order> sellOrders;
    List<Action> actions;

    public OrderXML() {
        this.buyOrders = new ArrayList<>();
        this.sellOrders = new ArrayList<>();
        this.actions = new ArrayList<>();
    }

    public List<Order> getBuyOrders() {
        return buyOrders;
    }

    public void setBuyOrders(List<Order> buyOrders) {
        this.buyOrders = buyOrders;
    }

    public List<Order> getSellOrders() {
        return sellOrders;
    }

    public void setSellOrders(List<Order> sellOrders) {
        this.sellOrders = sellOrders;
    }

    public List<Action> getActions() {
        return actions;
    }

    public void setActions(List<Action> actions) {
        this.actions = actions;
    }

    @Override
    public String toString() {
        return "OrderXML{" +
            "buyOrders=" + buyOrders +
            ", sellOrders=" + sellOrders +
            ", actions=" + actions +
            '}';
    }
}
