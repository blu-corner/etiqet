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
import org.apache.commons.lang3.StringUtils;

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
        if (!StringUtils.isEmpty(portsA) && StringUtils.isEmpty(portsB)){
            portsB = portsA;
        }
        final Node source = (Node) actionEvent.getSource();
        final Stage stage = (Stage) source.getScene().getWindow();
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("PORTS RANGE ERROR");
        alert.setHeaderText("Bad ports range");
        if (!Utils.isNumber(portsA) || !Utils.isNumber(portsB)){
            stage.setAlwaysOnTop(false);
            alert.setContentText("Ports must be integer values");
            alert.showAndWait();
        }else if (Integer.parseInt(portsA) > Integer.parseInt(portsB)){
            stage.setAlwaysOnTop(false);
            alert.setContentText("First set of port has to be equal or lower then second (e.g) [55555 to 55560]");
            alert.showAndWait();
        }else{
            this.mainController.startAcceptor(portsA, portsB,  stage);
        }

    }
}
