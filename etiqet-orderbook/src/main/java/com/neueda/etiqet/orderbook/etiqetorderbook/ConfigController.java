package com.neueda.etiqet.orderbook.etiqetorderbook;

import com.neueda.etiqet.orderbook.etiqetorderbook.utils.Constants;
import com.neueda.etiqet.orderbook.etiqetorderbook.utils.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class ConfigController implements Initializable {


    public TextField initiatorBeginString;
    public TextField initiatorSender;
    public TextField initiatorTarget;
    public ComboBox<String> initiatorDataDictionary;
    public TextField initiatorConnectHost;
    public TextField initiatorConnectPort;
    public TextField initiatorStorePath;
    public TextField initiatorLogPath;
    public TextField initiatorStartTime;
    public TextField initiatorEndTime;
    public ComboBox<String> initiatorUseDataDic;
    public TextField initiatorHeartBeat;
    public ComboBox<String> initiatorResentOnLogon;
    public ComboBox<String> initiatorResentOnLogout;
    public ComboBox<String> initiatorResentOnDisconnect;

    public TextField acceptorBeginString;
    public TextField acceptorSender;
    public TextField acceptorTarget;
    public ComboBox<String> acceptorDataDictionary;
    public TextField acceptorFromPort;
    public TextField acceptorToPort;
    public TextField acceptorStorePath;
    public TextField acceptorLogPath;
    public TextField acceptorStartTime;
    public TextField acceptorEndTime;
    public ComboBox<String> acceptorUseDataDic;
    public TextField acceptorHeartBeat;
    public ComboBox<String> acceptorResetOnLogon;
    public ComboBox<String> acceptorResetOnLogout;
    public ComboBox<String> acceptorResetOnDisconnect;

    private MainController mainController;
    private String role;

    public void injectMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void injectRole(String role){
        this.role = role;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (acceptorBeginString != null){
            acceptorBeginString.setText(getConfig(Constants.ACCEPTOR_ROLE, Constants.CONF_BEGIN_STRING));
            acceptorSender.setText(getConfig(Constants.ACCEPTOR_ROLE, Constants.CONF_SENDER));
            acceptorTarget.setText(getConfig(Constants.ACCEPTOR_ROLE, Constants.CONF_TARGET));
            acceptorStorePath.setText(getConfig(Constants.ACCEPTOR_ROLE, Constants.CONF_FILE_STORE_PATH));
            acceptorLogPath.setText(getConfig(Constants.ACCEPTOR_ROLE, Constants.CONF_FILE_LOG_PATH));
            acceptorStartTime.setText(getConfig(Constants.ACCEPTOR_ROLE, Constants.CONF_START_TIME));
            acceptorEndTime.setText(getConfig(Constants.ACCEPTOR_ROLE, Constants.CONF_END_TIME));
            acceptorHeartBeat.setText(getConfig(Constants.ACCEPTOR_ROLE, Constants.CONF_HEART_BT_INT));


            configComboBox(acceptorDataDictionary, Constants.FIX_VERSIONS, 4);
            configComboBox(acceptorUseDataDic, Constants.Y_N, 0);
            configComboBox(acceptorResetOnLogon, Constants.Y_N,0);
            configComboBox(acceptorResetOnLogout, Constants.Y_N,1);
            configComboBox(acceptorResetOnDisconnect, Constants.Y_N,1);
        }else{
            initiatorBeginString.setText(getConfig(Constants.INITIATOR_ROLE, Constants.CONF_BEGIN_STRING));
            initiatorSender.setText(getConfig(Constants.INITIATOR_ROLE, Constants.CONF_SENDER));
            initiatorTarget.setText(getConfig(Constants.INITIATOR_ROLE, Constants.CONF_TARGET));
            initiatorStorePath.setText(getConfig(Constants.INITIATOR_ROLE, Constants.CONF_FILE_STORE_PATH));
            initiatorLogPath.setText(getConfig(Constants.INITIATOR_ROLE, Constants.CONF_FILE_LOG_PATH));
            initiatorStartTime.setText(getConfig(Constants.INITIATOR_ROLE, Constants.CONF_START_TIME));
            initiatorEndTime.setText(getConfig(Constants.INITIATOR_ROLE, Constants.CONF_END_TIME));
            initiatorHeartBeat.setText(getConfig(Constants.INITIATOR_ROLE, Constants.CONF_HEART_BT_INT));

            configComboBox(initiatorDataDictionary, Constants.FIX_VERSIONS, 4);
            configComboBox(initiatorUseDataDic, Constants.Y_N,0);
            configComboBox(initiatorResentOnLogon, Constants.Y_N,0);
            configComboBox(initiatorResentOnLogout, Constants.Y_N,1);
            configComboBox(initiatorResentOnDisconnect, Constants.Y_N,1);
        }
    }

    public String getConfig(String role, String field) {
        List<String> writtenLines = new ArrayList<>();
        try {
            if (role.equals(Constants.ACCEPTOR_ROLE)) {
                Path path = Paths.get("src/main/resources/server.cfg");
                List<String> lines = Files.readAllLines(path);
                String value = "";
                for (String line : lines) {
                    if (!line.contains("#") && line.contains(field)) {
                        value = line.substring(line.indexOf('=') + 1);
                        return value;
                    }
                }
            } else {
                Path path = Paths.get("src/main/resources/client.cfg");
                List<String> lines = Files.readAllLines(path);
                String value = "";
                for (String line : lines) {
                    if (!line.contains("#") && line.contains(field)) {
                        value = line.substring(line.indexOf('=') + 1);
                        return value;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public void setConfig(String role, String field, String value) {
        List<String> writtenLines = new ArrayList<>();
        int index = 0, portIndex = 0;
        try {
            if (role.equals(Constants.ACCEPTOR_ROLE)) {
                Path path = Paths.get("src/main/resources/server.cfg");
                List<String> lines = Files.readAllLines(path);
                String v = "";
                for (String line : lines) {
                    if (!line.contains("#") && line.contains(field)) {
                        v = line.substring(line.indexOf('=') + 1);
                        line = line.replace(v, "");
                        portIndex = index;
                    }
                    writtenLines.add(line);
                    index++;
                }
                String current = writtenLines.get(portIndex);
                writtenLines.set(portIndex, current + field);
                Files.write(Paths.get("src/main/resources/server.cfg"), writtenLines);
            } else {
                Path path = Paths.get("src/main/resources/client.cfg");
                List<String> lines = Files.readAllLines(path);
                String v = "";
                for (String line : lines) {
                    if (!line.contains("#") && line.contains(field)) {
                        v = line.substring(line.indexOf('=') + 1);
                        line = line.replace(v, "");
                        portIndex = index;
                    }
                    writtenLines.add(line);
                    index++;
                }
                String current = writtenLines.get(portIndex);
                writtenLines.set(portIndex, current + v);
                Files.write(Paths.get("src/main/resources/client.cfg"), writtenLines);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void configComboBox(ComboBox<String> comboBox, List<String> data, int selected) {
        comboBox.getItems().addAll(data);
        comboBox.getSelectionModel().select(selected);
    }

    public void closeAcceptorConfig(ActionEvent actionEvent) {
        Utils.getStage(actionEvent).close();
    }

    public void saveAcceptorConfig(ActionEvent actionEvent) {
    }

    public void closeInitiatorConfig(ActionEvent actionEvent) {
        Utils.getStage(actionEvent).close();
    }

    public void saveInitiatorConfig(ActionEvent actionEvent) {
    }
}
