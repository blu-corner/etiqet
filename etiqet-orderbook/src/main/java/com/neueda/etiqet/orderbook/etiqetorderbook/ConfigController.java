package com.neueda.etiqet.orderbook.etiqetorderbook;

import com.neueda.etiqet.orderbook.etiqetorderbook.utils.Constants;
import com.neueda.etiqet.orderbook.etiqetorderbook.utils.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

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
    public ComboBox<String> initiatorResetOnLogon;
    public ComboBox<String> initiatorResetOnLogout;
    public ComboBox<String> initiatorResetOnDisconnect;

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

    public void injectRole(String role) {
        this.role = role;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (acceptorBeginString != null) {
            acceptorBeginString.setText(getConfig(Constants.ACCEPTOR_ROLE, Constants.CONF_BEGIN_STRING));
            acceptorSender.setText(getConfig(Constants.ACCEPTOR_ROLE, Constants.CONF_SENDER));
            acceptorTarget.setText(getConfig(Constants.ACCEPTOR_ROLE, Constants.CONF_TARGET));
            acceptorStorePath.setText(getConfig(Constants.ACCEPTOR_ROLE, Constants.CONF_FILE_STORE_PATH));
            acceptorLogPath.setText(getConfig(Constants.ACCEPTOR_ROLE, Constants.CONF_FILE_LOG_PATH));
            acceptorStartTime.setText(getConfig(Constants.ACCEPTOR_ROLE, Constants.CONF_START_TIME));
            acceptorEndTime.setText(getConfig(Constants.ACCEPTOR_ROLE, Constants.CONF_END_TIME));
            acceptorHeartBeat.setText(getConfig(Constants.ACCEPTOR_ROLE, Constants.CONF_HEART_BT_INT));
            acceptorFromPort.setText(getConfig(Constants.ACCEPTOR_ROLE, Constants.ACC_ACCEPT_PORT));

            String accSocketAcceptorPortRangeLimit = getConfig(Constants.ACCEPTOR_ROLE, Constants.ACC_SOCKET_ACCEPT_PORT_RANGE_LIMIT);
            if (StringUtils.isEmpty(accSocketAcceptorPortRangeLimit)){
                acceptorToPort.setText(getConfig(Constants.ACCEPTOR_ROLE, Constants.ACC_ACCEPT_PORT));
            }else{
                acceptorToPort.setText(accSocketAcceptorPortRangeLimit);
            }

            setConfigComboBox(acceptorDataDictionary, Constants.FIX_VERSIONS, getComboConfigValue(Constants.ACCEPTOR_ROLE, Constants.CONF_DATA_DIC));
            setConfigComboBox(acceptorUseDataDic, Constants.Y_N, getComboConfigValue(Constants.ACCEPTOR_ROLE, Constants.CONF_USE_DATA_DIC));
            setConfigComboBox(acceptorResetOnLogon, Constants.Y_N, getComboConfigValue(Constants.ACCEPTOR_ROLE, Constants.CONF_RESET_ON_LOGON));
            setConfigComboBox(acceptorResetOnLogout, Constants.Y_N, getComboConfigValue(Constants.ACCEPTOR_ROLE, Constants.CONF_RESET_ON_LOGOUT));
            setConfigComboBox(acceptorResetOnDisconnect, Constants.Y_N, getComboConfigValue(Constants.ACCEPTOR_ROLE, Constants.CONF_RESET_ON_DISCONNECT));
        } else {
            initiatorBeginString.setText(getConfig(Constants.INITIATOR_ROLE, Constants.CONF_BEGIN_STRING));
            initiatorSender.setText(getConfig(Constants.INITIATOR_ROLE, Constants.CONF_SENDER));
            initiatorTarget.setText(getConfig(Constants.INITIATOR_ROLE, Constants.CONF_TARGET));
            initiatorStorePath.setText(getConfig(Constants.INITIATOR_ROLE, Constants.CONF_FILE_STORE_PATH));
            initiatorLogPath.setText(getConfig(Constants.INITIATOR_ROLE, Constants.CONF_FILE_LOG_PATH));
            initiatorStartTime.setText(getConfig(Constants.INITIATOR_ROLE, Constants.CONF_START_TIME));
            initiatorEndTime.setText(getConfig(Constants.INITIATOR_ROLE, Constants.CONF_END_TIME));
            initiatorHeartBeat.setText(getConfig(Constants.INITIATOR_ROLE, Constants.CONF_HEART_BT_INT));
            initiatorConnectHost.setText(getConfig(Constants.INITIATOR_ROLE,Constants.INI_CONNECT_HOST));
            initiatorConnectPort.setText(getConfig(Constants.INITIATOR_ROLE,Constants.INI_CONNECT_PORT));


            setConfigComboBox(initiatorDataDictionary, Constants.FIX_VERSIONS, getComboConfigValue(Constants.INITIATOR_ROLE, Constants.CONF_DATA_DIC));
            setConfigComboBox(initiatorUseDataDic, Constants.Y_N, getComboConfigValue(Constants.INITIATOR_ROLE, Constants.CONF_USE_DATA_DIC));
            setConfigComboBox(initiatorResetOnLogon, Constants.Y_N, getComboConfigValue(Constants.INITIATOR_ROLE, Constants.CONF_RESET_ON_LOGON));
            setConfigComboBox(initiatorResetOnLogout, Constants.Y_N, getComboConfigValue(Constants.INITIATOR_ROLE, Constants.CONF_RESET_ON_LOGOUT));
            setConfigComboBox(initiatorResetOnDisconnect, Constants.Y_N, getComboConfigValue(Constants.INITIATOR_ROLE, Constants.CONF_RESET_ON_DISCONNECT));
        }
    }

    public int getComboConfigValue(String role, String field) {
        String value = getConfig(role, field);
        if (StringUtils.isEmpty(value)) return -1;
        if (field.equals(Constants.CONF_DATA_DIC)) {
            value = value.substring(value.length() - 6, value.length() - 4);
            return Constants.FIX_VERSIONS_COMBO.indexOf(value);
        } else {
            return Constants.Y_N.indexOf(value);
        }
    }

    public String getConfig(String role, String field) {
        try {
            Path path = role.equals(Constants.ACCEPTOR_ROLE)
                ? Paths.get(Constants.SRC_MAIN_RESOURCES_SERVER_CFG)
                : Paths.get(Constants.SRC_MAIN_RESOURCES_CLIENT_CFG);

            List<String> lines = Files.readAllLines(path).stream()
                .filter(l -> !l.trim().startsWith("#"))
                .collect(Collectors.toList());
            return getValueFromConfig(lines, field);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return StringUtils.EMPTY;
    }

    private String getValueFromConfig(List<String> lines, String field) {
        String value = StringUtils.EMPTY;
        if (field.equals(Constants.CONF_DATA_DIC)){
            for (String line : lines) {
                if (line.contains(field) && !line.contains(Constants.CONF_USE_DATA_DIC)){
                    value = line.substring(line.indexOf('=') + 1);
                }
            }
        }else{
            for (String line : lines) {
                if (line.contains(field)){
                    value = line.substring(line.indexOf('=') + 1);
                }
            }
        }
        return value;
    }


    private void setValueInConfig(String field, String value, String role) {
        List<String> writtenLines = new ArrayList<>();
        int index = 0, valueIndex = 0;
        boolean found = false;
        String v = "";

        Path path = role.equals(Constants.ACCEPTOR_ROLE)
            ? Paths.get(Constants.SRC_MAIN_RESOURCES_SERVER_CFG)
            : Paths.get(Constants.SRC_MAIN_RESOURCES_CLIENT_CFG);

        List<String> lines;
        try {
            lines = Files.readAllLines(path);
            if (lines != null){
                if (field.equals(Constants.CONF_DATA_DIC)){
                    for (String line : lines) {
                        if (line.contains(field) && !line.contains(Constants.CONF_USE_DATA_DIC)){
                            v = line.substring(line.indexOf('=') + 1);
                            line = line.replace(v, "");
                            valueIndex = index;
                            found = true;
                        }
                        writtenLines.add(line);
                        index++;
                    }
                }else{
                    for (String line : lines) {
                        if (line.contains(field)){
                            v = line.substring(line.indexOf('=') + 1);
                            line = line.replace(v, "");
                            valueIndex = index;
                            found = true;
                        }
                        writtenLines.add(line);
                        index++;
                    }
                }
            }
            if (found){
                String current = writtenLines.get(valueIndex);
                if (field.equals(Constants.CONF_DATA_DIC)){
                    writtenLines.set(valueIndex, current + "spec/" + value);
                }else{
                    writtenLines.set(valueIndex, current + value);
                }

                Files.write(path, writtenLines);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void setConfigComboBox(ComboBox<String> comboBox, List<String> data, int selected) {
        comboBox.getItems().addAll(data);
        comboBox.getSelectionModel().select(selected);
    }

    public void closeAcceptorConfig(ActionEvent actionEvent) {
        Utils.getStage(actionEvent).close();
    }

    public void saveAcceptorConfig(ActionEvent actionEvent) {
        Utils.getStage(actionEvent).close();
        setValueInConfig(Constants.CONF_BEGIN_STRING, acceptorBeginString.getText(), Constants.ACCEPTOR_ROLE);
        setValueInConfig(Constants.CONF_SENDER, acceptorSender.getText(), Constants.ACCEPTOR_ROLE);
        setValueInConfig(Constants.CONF_TARGET, acceptorTarget.getText(), Constants.ACCEPTOR_ROLE);
        setValueInConfig(Constants.CONF_DATA_DIC, Constants.hmFixVersions.get(acceptorDataDictionary.getSelectionModel().getSelectedItem()), Constants.ACCEPTOR_ROLE);
        setValueInConfig(Constants.ACC_ACCEPT_PORT, acceptorFromPort.getText(), Constants.ACCEPTOR_ROLE);
        saveToPort(acceptorToPort.getText());
        setValueInConfig(Constants.CONF_FILE_STORE_PATH, acceptorStorePath.getText(), Constants.ACCEPTOR_ROLE);
        setValueInConfig(Constants.CONF_FILE_LOG_PATH, acceptorLogPath.getText(), Constants.ACCEPTOR_ROLE);
        setValueInConfig(Constants.CONF_START_TIME, acceptorStartTime.getText(), Constants.ACCEPTOR_ROLE);
        setValueInConfig(Constants.CONF_END_TIME, acceptorEndTime.getText(), Constants.ACCEPTOR_ROLE);
        setValueInConfig(Constants.CONF_USE_DATA_DIC, acceptorUseDataDic.getSelectionModel().getSelectedItem(), Constants.ACCEPTOR_ROLE);
        setValueInConfig(Constants.CONF_HEART_BT_INT, acceptorHeartBeat.getText(), Constants.ACCEPTOR_ROLE);
        setValueInConfig(Constants.CONF_RESET_ON_LOGON, acceptorResetOnLogon.getSelectionModel().getSelectedItem(), Constants.ACCEPTOR_ROLE);
        setValueInConfig(Constants.CONF_RESET_ON_LOGOUT, acceptorResetOnLogout.getSelectionModel().getSelectedItem(), Constants.ACCEPTOR_ROLE);
        setValueInConfig(Constants.CONF_RESET_ON_DISCONNECT, acceptorResetOnDisconnect.getSelectionModel().getSelectedItem(), Constants.ACCEPTOR_ROLE);
    }

    private void saveToPort(String port) {
        Path path = Paths.get(Constants.SRC_MAIN_RESOURCES_SERVER_CFG);
        try {
            List<String> lines = Files.readAllLines(path);
            lines.add(Constants.ACC_SOCKET_ACCEPT_PORT_RANGE_LIMIT + port);
            Files.write(path, lines);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void closeInitiatorConfig(ActionEvent actionEvent) {
        Utils.getStage(actionEvent).close();
    }

    public void saveInitiatorConfig(ActionEvent actionEvent) {
        Utils.getStage(actionEvent).close();
        setValueInConfig(Constants.CONF_BEGIN_STRING, initiatorBeginString.getText(), Constants.INITIATOR_ROLE);
        setValueInConfig(Constants.CONF_SENDER, initiatorSender.getText(), Constants.INITIATOR_ROLE);
        setValueInConfig(Constants.CONF_TARGET, initiatorTarget.getText(), Constants.INITIATOR_ROLE);
        setValueInConfig(Constants.CONF_DATA_DIC, Constants.hmFixVersions.get(initiatorDataDictionary.getSelectionModel().getSelectedItem()), Constants.INITIATOR_ROLE);
        setValueInConfig(Constants.INI_CONNECT_HOST, initiatorConnectHost.getText(), Constants.INITIATOR_ROLE);
        setValueInConfig(Constants.INI_CONNECT_PORT, initiatorConnectPort.getText(), Constants.INITIATOR_ROLE);
        setValueInConfig(Constants.CONF_FILE_STORE_PATH, initiatorStorePath.getText(), Constants.INITIATOR_ROLE);
        setValueInConfig(Constants.CONF_FILE_LOG_PATH, initiatorLogPath.getText(), Constants.INITIATOR_ROLE);
        setValueInConfig(Constants.CONF_START_TIME, initiatorStartTime.getText(), Constants.INITIATOR_ROLE);
        setValueInConfig(Constants.CONF_END_TIME, initiatorEndTime.getText(), Constants.INITIATOR_ROLE);
        setValueInConfig(Constants.CONF_USE_DATA_DIC, initiatorUseDataDic.getSelectionModel().getSelectedItem(), Constants.INITIATOR_ROLE);
        setValueInConfig(Constants.CONF_HEART_BT_INT, initiatorHeartBeat.getText(), Constants.INITIATOR_ROLE);
        setValueInConfig(Constants.CONF_RESET_ON_LOGON, initiatorResetOnLogon.getSelectionModel().getSelectedItem(), Constants.INITIATOR_ROLE);
        setValueInConfig(Constants.CONF_RESET_ON_LOGOUT, initiatorResetOnLogout.getSelectionModel().getSelectedItem(), Constants.INITIATOR_ROLE);
        setValueInConfig(Constants.CONF_RESET_ON_DISCONNECT, initiatorResetOnDisconnect.getSelectionModel().getSelectedItem(), Constants.INITIATOR_ROLE);
    }
}
