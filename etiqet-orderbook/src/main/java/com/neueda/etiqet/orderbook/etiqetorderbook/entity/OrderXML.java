package com.neueda.etiqet.orderbook.etiqetorderbook.entity;

import java.util.ArrayList;
import java.util.List;

public class OrderXML {
    List<Order> orders;
    List<Action> actions;

    public OrderXML(){
        this.orders = new ArrayList<>();
        this.actions = new ArrayList<>();
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public List<Action> getActions() {
        return actions;
    }

    public void setActions(List<Action> actions) {
        this.actions = actions;
    }
}
