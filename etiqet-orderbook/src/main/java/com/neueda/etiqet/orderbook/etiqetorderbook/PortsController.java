package com.neueda.etiqet.orderbook.etiqetorderbook;

import com.neueda.etiqet.orderbook.etiqetorderbook.utils.Constants;
import com.neueda.etiqet.orderbook.etiqetorderbook.utils.Utils;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class PortsController implements Initializable {


    public TextArea textAreaRangeA;
    public TextArea textAreaRangeB;
    public Button buttonListenPorts;
    private MainController mainController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void injectMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void listenOnPorts(ActionEvent actionEvent) {
        String portsA = textAreaRangeA.getText();
        String portsB= textAreaRangeB.getText();
        final Node source = (Node) actionEvent.getSource();
        final Stage stage = (Stage) source.getScene().getWindow();
        if (Utils.isNumber(portsA) && Utils.isNumber(portsB)){
            this.mainController.startAcceptor(portsA, portsB,  stage);
        }else{
            stage.setAlwaysOnTop(false);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("PORTS RANGE ERROR");
            alert.setHeaderText("Bad ports range");
            alert.setContentText("Ports must be integer values");
            alert.showAndWait();
        }

    }
}
