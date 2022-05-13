package com.neueda.etiqet.orderbook.etiqetorderbook.controllers;

import com.neueda.etiqet.orderbook.etiqetorderbook.entity.Tag;
import com.neueda.etiqet.orderbook.etiqetorderbook.utils.Constants;
import com.neueda.etiqet.orderbook.etiqetorderbook.utils.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import jfxtras.scene.control.LocalTimePicker;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static com.neueda.etiqet.orderbook.etiqetorderbook.utils.Utils.getConfig;

/**
 * @author enol.cacheroramirez@version1.com
 * Used by acceptorConfigWindow.fxml and initiatorConfigWindow.fxml
 * Launched from MainController
 */
public class ConfigController implements Initializable {
    public LocalTimePicker acceptorStartTime;
    public LocalTimePicker acceptorEndTime;
    public LocalTimePicker initiatorStartTime;
    public LocalTimePicker initiatorEndTime;
    public TextField initiatorBeginString;
    public TextField initiatorSender;
    public TextField initiatorTarget;
    public ComboBox<String> initiatorDataDictionary;
    public TextField initiatorConnectHost;
    public TextField initiatorConnectPort;
    public TextField initiatorStorePath;
    public TextField initiatorLogPath;
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
    public ComboBox<String> acceptorUseDataDic;
    public TextField acceptorHeartBeat;
    public ComboBox<String> acceptorResetOnLogon;
    public ComboBox<String> acceptorResetOnLogout;
    public ComboBox<String> acceptorResetOnDisconnect;
    private final Logger logger = LoggerFactory.getLogger(ConfigController.class);
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
            acceptorStartTime.setLocalTime(LocalTime.parse(getConfig(Constants.ACCEPTOR_ROLE, Constants.CONF_START_TIME)));
            acceptorEndTime.setLocalTime(LocalTime.parse(getConfig(Constants.ACCEPTOR_ROLE, Constants.CONF_END_TIME)));
            acceptorHeartBeat.setText(getConfig(Constants.ACCEPTOR_ROLE, Constants.CONF_HEART_BT_INT));
            acceptorFromPort.setText(getConfig(Constants.ACCEPTOR_ROLE, Constants.ACC_ACCEPT_PORT));

            String accSocketAcceptorPortRangeLimit = getConfig(Constants.ACCEPTOR_ROLE, Constants.ACC_SOCKET_ACCEPT_PORT_RANGE_LIMIT);
            if (StringUtils.isEmpty(accSocketAcceptorPortRangeLimit)) {
                acceptorToPort.setText(getConfig(Constants.ACCEPTOR_ROLE, Constants.ACC_ACCEPT_PORT));
            } else {
                acceptorToPort.setText(accSocketAcceptorPortRangeLimit);
            }

            setConfigComboBox(acceptorDataDictionary, Constants.FIX_VERSIONS, Utils.getComboConfigValue(Constants.ACCEPTOR_ROLE, Constants.CONF_DATA_DIC));
            setConfigComboBox(acceptorUseDataDic, Constants.Y_N, Utils.getComboConfigValue(Constants.ACCEPTOR_ROLE, Constants.CONF_USE_DATA_DIC));
            setConfigComboBox(acceptorResetOnLogon, Constants.Y_N, Utils.getComboConfigValue(Constants.ACCEPTOR_ROLE, Constants.CONF_RESET_ON_LOGON));
            setConfigComboBox(acceptorResetOnLogout, Constants.Y_N, Utils.getComboConfigValue(Constants.ACCEPTOR_ROLE, Constants.CONF_RESET_ON_LOGOUT));
            setConfigComboBox(acceptorResetOnDisconnect, Constants.Y_N, Utils.getComboConfigValue(Constants.ACCEPTOR_ROLE, Constants.CONF_RESET_ON_DISCONNECT));
        } else {
            initiatorBeginString.setText(getConfig(Constants.INITIATOR_ROLE, Constants.CONF_BEGIN_STRING));
            initiatorSender.setText(getConfig(Constants.INITIATOR_ROLE, Constants.CONF_SENDER));
            initiatorTarget.setText(getConfig(Constants.INITIATOR_ROLE, Constants.CONF_TARGET));
            initiatorStorePath.setText(getConfig(Constants.INITIATOR_ROLE, Constants.CONF_FILE_STORE_PATH));
            initiatorLogPath.setText(getConfig(Constants.INITIATOR_ROLE, Constants.CONF_FILE_LOG_PATH));
            initiatorStartTime.setLocalTime(LocalTime.parse(getConfig(Constants.INITIATOR_ROLE, Constants.CONF_START_TIME)));
            initiatorEndTime.setLocalTime(LocalTime.parse(getConfig(Constants.INITIATOR_ROLE, Constants.CONF_END_TIME)));
            initiatorHeartBeat.setText(getConfig(Constants.INITIATOR_ROLE, Constants.CONF_HEART_BT_INT));
            initiatorConnectHost.setText(getConfig(Constants.INITIATOR_ROLE, Constants.INI_CONNECT_HOST));
            initiatorConnectPort.setText(getConfig(Constants.INITIATOR_ROLE, Constants.INI_CONNECT_PORT));


            setConfigComboBox(initiatorDataDictionary, Constants.FIX_VERSIONS, Utils.getComboConfigValue(Constants.INITIATOR_ROLE, Constants.CONF_DATA_DIC));
            setConfigComboBox(initiatorUseDataDic, Constants.Y_N, Utils.getComboConfigValue(Constants.INITIATOR_ROLE, Constants.CONF_USE_DATA_DIC));
            setConfigComboBox(initiatorResetOnLogon, Constants.Y_N, Utils.getComboConfigValue(Constants.INITIATOR_ROLE, Constants.CONF_RESET_ON_LOGON));
            setConfigComboBox(initiatorResetOnLogout, Constants.Y_N, Utils.getComboConfigValue(Constants.INITIATOR_ROLE, Constants.CONF_RESET_ON_LOGOUT));
            setConfigComboBox(initiatorResetOnDisconnect, Constants.Y_N, Utils.getComboConfigValue(Constants.INITIATOR_ROLE, Constants.CONF_RESET_ON_DISCONNECT));
        }
    }

    /**
     * Used by multiple combos to be configured
     * @param comboBox
     * @param data
     * @param selected
     */
    private void setConfigComboBox(ComboBox<String> comboBox, List<String> data, int selected) {
        comboBox.getItems().addAll(data);
        comboBox.getSelectionModel().select(selected);
    }

    /**
     * Close acceptor config view
     * @param actionEvent
     */
    public void closeAcceptorConfig(ActionEvent actionEvent) {
        Utils.getStage(actionEvent).close();
    }

    /**
     * Save info to server.cfg
     * @param actionEvent
     */
    public void saveAcceptorConfig(ActionEvent actionEvent) {
        Utils.getStage(actionEvent).close();
        List<Tag> tagList = new ArrayList<>();
        tagList.add(new Tag(Constants.CONF_BEGIN_STRING, acceptorBeginString.getText()));
        tagList.add(new Tag(Constants.CONF_SENDER, acceptorSender.getText()));
        tagList.add(new Tag(Constants.CONF_TARGET, acceptorTarget.getText()));
        tagList.add(new Tag(Constants.CONF_DATA_DIC, Constants.hmFixVersions.get(acceptorDataDictionary.getSelectionModel().getSelectedItem())));
        tagList.add(new Tag(Constants.ACC_ACCEPT_PORT, acceptorFromPort.getText()));
        tagList.add(new Tag(Constants.ACC_SOCKET_ACCEPT_PORT_RANGE_LIMIT, acceptorToPort.getText()));
        tagList.add(new Tag(Constants.CONF_FILE_STORE_PATH, acceptorStorePath.getText()));
        tagList.add(new Tag(Constants.CONF_FILE_LOG_PATH, acceptorLogPath.getText()));
        tagList.add(new Tag(Constants.CONF_START_TIME, acceptorStartTime.getLocalTime().toString() + ":00"));
        tagList.add(new Tag(Constants.CONF_END_TIME, acceptorEndTime.getLocalTime().toString() + ":00"));
        tagList.add(new Tag(Constants.CONF_USE_DATA_DIC, acceptorUseDataDic.getSelectionModel().getSelectedItem()));
        tagList.add(new Tag(Constants.CONF_HEART_BT_INT, acceptorHeartBeat.getText()));
        tagList.add(new Tag(Constants.CONF_RESET_ON_LOGON, acceptorResetOnLogon.getSelectionModel().getSelectedItem()));
        tagList.add(new Tag(Constants.CONF_RESET_ON_LOGOUT, acceptorResetOnLogout.getSelectionModel().getSelectedItem()));
        tagList.add(new Tag(Constants.CONF_RESET_ON_DISCONNECT, acceptorResetOnDisconnect.getSelectionModel().getSelectedItem()));
        propertiesWriter(tagList, Constants.ACCEPTOR_ROLE);
    }

    /**
     * Close initiator config view
     * @param actionEvent
     */
    public void closeInitiatorConfig(ActionEvent actionEvent) {
        Utils.getStage(actionEvent).close();
    }

    /**
     * Save config to client.cfg
     * @param actionEvent
     */
    public void saveInitiatorConfig(ActionEvent actionEvent) {
        Utils.getStage(actionEvent).close();
        List<Tag> tagList = new ArrayList<>();
        tagList.add(new Tag(Constants.CONF_BEGIN_STRING, initiatorBeginString.getText()));
        tagList.add(new Tag(Constants.CONF_SENDER, initiatorSender.getText()));
        tagList.add(new Tag(Constants.CONF_TARGET, initiatorTarget.getText()));
        tagList.add(new Tag(Constants.CONF_DATA_DIC, Constants.hmFixVersions.get(initiatorDataDictionary.getSelectionModel().getSelectedItem())));
        tagList.add(new Tag(Constants.INI_CONNECT_HOST, initiatorConnectHost.getText()));
        tagList.add(new Tag(Constants.INI_CONNECT_PORT, initiatorConnectPort.getText()));
        tagList.add(new Tag(Constants.CONF_FILE_STORE_PATH, initiatorStorePath.getText()));
        tagList.add(new Tag(Constants.CONF_FILE_LOG_PATH, initiatorLogPath.getText()));
        tagList.add(new Tag(Constants.CONF_START_TIME, initiatorStartTime.getLocalTime().toString()+ ":00"));
        tagList.add(new Tag(Constants.CONF_END_TIME, initiatorEndTime.getLocalTime().toString() + ":00"));
        tagList.add(new Tag(Constants.CONF_USE_DATA_DIC, initiatorUseDataDic.getSelectionModel().getSelectedItem()));
        tagList.add(new Tag(Constants.CONF_HEART_BT_INT, initiatorHeartBeat.getText()));
        tagList.add(new Tag(Constants.CONF_RESET_ON_LOGON, initiatorResetOnLogon.getSelectionModel().getSelectedItem()));
        tagList.add(new Tag(Constants.CONF_RESET_ON_LOGOUT, initiatorResetOnLogout.getSelectionModel().getSelectedItem()));
        tagList.add(new Tag(Constants.CONF_RESET_ON_DISCONNECT, initiatorResetOnDisconnect.getSelectionModel().getSelectedItem()));
        propertiesWriter(tagList, Constants.INITIATOR_ROLE);
    }

    /**
     * Saves config in both acceptor and initiator .cfg files
     * @param fields
     * @param role
     */
    private void propertiesWriter(List<Tag> fields, String role) {
        try {
            Path pathRoot;
            List<String> lines;
            if (role.equals(Constants.ACCEPTOR_ROLE)) {
                pathRoot = Paths.get(Constants.PATH_OUTPUT_SERVER_CONFIG);
                lines = Utils.readConfigFile(Constants.ACCEPTOR_ROLE);
            } else {
                pathRoot = Paths.get(Constants.PATH_OUTPUT_CLIENT_CONFIG);
                lines = Utils.readConfigFile(Constants.INITIATOR_ROLE);
            }

            List<String> newLines = new ArrayList<>();
            for (String line : lines) {
                propertyHandler(fields, newLines, line);
            }
            newPropertiesHandler(fields, newLines);
            Files.write(pathRoot, newLines);
        } catch (Exception e) {
            this.logger.warn("Exception in propertiesWriter: {}", e.getLocalizedMessage());
        }
    }

    /**
     * Extracts tag from a String
     * @param property
     * @return
     */
    private String extractTag(String property) {
        try {
            if (!StringUtils.isEmpty(property)) {
                return property.substring(0, property.indexOf('='));
            }
        } catch (Exception e) {
            this.logger.warn("Exception in extractTag, property -> {} :: exception: {}", property, e.getLocalizedMessage());
        }
        return "";
    }

    /**
     * Turns a Tag List into a String List
     * @param fields
     * @param newLines
     * @param line
     */
    private void propertyHandler(List<Tag> fields, List<String> newLines, String line) {
        List<String> tags = fields.stream().map(Tag::getField).collect(Collectors.toList());
        String tag = extractTag(line);
        String newProperty;
        if (!tags.contains(tag)) {
            newLines.add(line);
        } else if (line.startsWith("#")) {
            newLines.add(line);
        } else {
            Optional<Tag> property = fields.stream().filter(t -> t.getField().equals(tag)).findFirst();
            if (property.isPresent()) {
                Tag newTag = property.get();
                newTag.setUsed();
                String newValue = newTag.getValue();
                if (tag.equals(Constants.CONF_DATA_DIC)) {
                    newProperty = tag + "=spec/" + newValue;
                } else {
                    newProperty = tag + "=" + newValue;
                }
                newLines.add(newProperty);
            }

        }
    }

    /**
     * Deals with the non-used tags
     * @param fields
     * @param newLines
     */
    private void newPropertiesHandler(List<Tag> fields, List<String> newLines) {
        List<Tag> nonUsed = fields.stream().filter(t -> !t.isUsed()).collect(Collectors.toList());
        for (Tag tag : nonUsed) {
            String newProperty;
            if (!StringUtils.isEmpty(tag.getValue())) {
                if (tag.getField().equals(Constants.CONF_DATA_DIC)) {
                    newProperty = tag.getField() + "=spec/" + tag.getValue();
                } else {
                    newProperty = tag.getField() + "=" + tag.getValue();
                }
                newLines.add(newProperty);
            }
        }
    }
}
