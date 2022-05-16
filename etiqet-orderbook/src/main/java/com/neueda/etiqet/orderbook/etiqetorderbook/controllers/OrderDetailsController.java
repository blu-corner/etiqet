package com.neueda.etiqet.orderbook.etiqetorderbook.controllers;

import com.neueda.etiqet.orderbook.etiqetorderbook.entity.Field;
import com.neueda.etiqet.orderbook.etiqetorderbook.entity.Order;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Shows all properties of the specified Order
 */
public class OrderDetailsController implements Initializable {

    public TableView<Field> tableViewDetails;
    public TableColumn<Field, String> columnField;
    public TableColumn<Field, String> columnValue;

    private MainController mainController;

    public void injectMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        columnField.setCellValueFactory(new PropertyValueFactory<>("Field"));
        columnValue.setCellValueFactory(new PropertyValueFactory<>("Value"));
    }

    /**
     * @param order
     */
    public void injectOrder(Order order) {
        tableViewDetails.getItems().addAll(List.of(
            new Field("ClOrdID", order.getClOrdID()),
            new Field("Time", order.getTime()),
            new Field("OrderQty", String.valueOf(order.getOrderQty())),
            new Field("Price", String.valueOf(order.getPrice())),
            new Field("Symbol", order.getSymbol()),
            new Field("Side", String.valueOf(order.getSide())),
            new Field("ClientID", order.getClientID()),
            new Field("TimeInForce", order.getTimeInForce()),
            new Field("Will be canceled", String.valueOf(order.isRemoved())),
            new Field("SessionID", order.getSessionID()),
            new Field("Time to be canceled", order.getTimeToBeRemoved())));

    }


}
