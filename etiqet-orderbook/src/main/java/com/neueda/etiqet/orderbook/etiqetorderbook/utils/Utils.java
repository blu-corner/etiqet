package com.neueda.etiqet.orderbook.etiqetorderbook.utils;

import com.neueda.etiqet.orderbook.etiqetorderbook.controllers.ConfigController;
import com.neueda.etiqet.orderbook.etiqetorderbook.entity.Tag;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;
import quickfix.Message;

import javax.sound.sampled.AudioFormat;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.neueda.etiqet.orderbook.etiqetorderbook.controllers.ConfigController.readConfigFile;

public class Utils {

    static DecimalFormat integerFormat = new DecimalFormat("#");
    static DecimalFormat decimalFormat = new DecimalFormat("#.0###");

    public static String replaceSOH(Message message) {
        String content = message.toString();
        return content.replace(Constants.SOH, Constants.VERTICAL_BAR);
    }

    public static String replaceVerticalBar(String message) {
        return message.replace(Constants.VERTICAL_BAR, Constants.SOH) + Constants.SOH;
    }

    public static boolean isNumber(String value){
        try{
            return StringUtils.isNumeric(value);
        }catch (Exception ex){
            return false;
        }
    }

    public static boolean availablePort(int port){
        ServerSocket socket = null;
        DatagramSocket datagramSocket = null;
        try{
            socket = new ServerSocket(port);
            socket.setReuseAddress(true);
            datagramSocket = new DatagramSocket(port);
            datagramSocket.setReuseAddress(true);
            return true;
        }catch (Exception e){}
        finally {
            if (datagramSocket != null){
                datagramSocket.close();
            }
            if (socket != null){
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }


    public static Stage getStage(ActionEvent actionEvent) {
        final Node source = (Node) actionEvent.getSource();
        final Stage stage = (Stage) source.getScene().getWindow();
        return stage;
    }


    public static String getConfig(String role, String field) {
        try {
            List<String> lines;
            if (role.equals(Constants.ACCEPTOR_ROLE)) {
                lines = readConfigFile(ConfigController.ConfigType.SERVER);
            }
            else {
                lines = readConfigFile(ConfigController.ConfigType.CLIENT);
            }
            List<String> filteredLines = lines.stream()
                .filter(l -> !l.trim().startsWith("#"))
                .collect(Collectors.toList());
            return getValueFromConfig(filteredLines, field);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return StringUtils.EMPTY;
    }

    private static String getValueFromConfig(List<String> lines, String field) {
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


    public static int getComboConfigValue(String role, String field) {
        String value = getConfig(role, field);
        if (StringUtils.isEmpty(value)) return -1;
        if (field.equals(Constants.CONF_DATA_DIC)) {
            value = value.substring(value.length() - 6, value.length() - 4);
            return Constants.FIX_VERSIONS_COMBO.indexOf(value);
        } else {
            return Constants.Y_N.indexOf(value);
        }
    }

    public static String getKeyFromValue(String value){
        Optional<Integer> key = Constants.hmTagValue.entrySet()
            .stream()
            .filter(entry -> Objects.equals(entry.getValue(), value))
            .map(Map.Entry::getKey)
            .findFirst();

        if (key.isPresent()){
            return String.valueOf(key.get());
        }else{
            return "-1";
        }
    }

    public static String fixEncoder(List<Tag>tags){
        StringBuilder encodedFix = new StringBuilder();
        String beginStringTag = tags.stream().filter(t -> t.getKey().equals(Constants.KEY_BEGIN_STRING)).findFirst().get().getValue();
        encodedFix.append("8=").append(beginStringTag).append(Constants.VERTICAL_BAR);
        int bodyLengthTag = bodyLenghtCalculator(tags);
        encodedFix.append("9=").append(bodyLengthTag).append(Constants.VERTICAL_BAR);
        String msgTypeag = tags.stream().filter(t -> t.getKey().equals("35")).findFirst().get().getValue();
        encodedFix.append("35=").append(msgTypeag).append(Constants.VERTICAL_BAR);

        for (Tag tag: tags){
            if (!tag.getKey().equals(Constants.KEY_BEGIN_STRING) && !tag.getKey().equals(Constants.KEY_BODY_LENGTH) && !tag.getKey().equals(Constants.KEY_MSG_TYPE)){
                String keyValue = tag.getKey() + "=" + tag.getValue() + Constants.VERTICAL_BAR;
                encodedFix.append(keyValue);
            }

        }
        String checksum = checksumCalculator(encodedFix.toString());
        encodedFix.append("10=").append(checksum);
        return encodedFix.toString();
    }

    public static int bodyLenghtCalculator(List<Tag>tags){
        int acum = 0;
        for(Tag tag: tags){
            if (!tag.getKey().equals(Constants.KEY_BEGIN_STRING) && !tag.getKey().equals(Constants.KEY_BODY_LENGTH)){
                String keyValue = tag.getKey() + "=" + tag.getValue() + Constants.VERTICAL_BAR;
                acum += keyValue.length();
            }
        }
        return acum;
    }


    public static String checksumCalculator(String fixMessage){
        int acum = 0, checksum = 0;
        String replaced = fixMessage.replace(Constants.VERTICAL_BAR, Constants.SOH);
        byte[] bytes = replaced.getBytes(StandardCharsets.UTF_8);
        for (int i = 0 ; i < bytes.length; i++){
            acum += bytes[i];
        }
        checksum = acum % 256;
        return StringUtils.leftPad(String.valueOf(checksum), 3, '0');

    }


    public static void configureTextFieldToAcceptOnlyIntegerValues(TextField textField) {
        if (textField != null)
            textField.setTextFormatter(integerTextFormatter);
    }
    public static void configureTextFieldToAcceptOnlyDecimalValues(TextField textField) {
        if (textField != null)
            textField.setTextFormatter(decimalTextFormatter);
    }


    private static final TextFormatter<Object> integerTextFormatter = new TextFormatter<>(change -> {
        if (change.getControlNewText().isEmpty()) {
            return change;
        }
        ParsePosition parsePosition = new ParsePosition(0);
        Object object = integerFormat.parse(change.getControlNewText(), parsePosition);

        if (object == null || parsePosition.getIndex() < change.getControlNewText().length()) {
            return null;
        } else {
            return change;
        }
    });
    private static final TextFormatter<Object> decimalTextFormatter = new TextFormatter<>(change -> {
        if (change.getControlNewText().isEmpty()) {
            return change;
        }
        ParsePosition parsePosition = new ParsePosition(0);
        Object object = decimalFormat.parse(change.getControlNewText(), parsePosition);

        if (object == null || parsePosition.getIndex() < change.getControlNewText().length()) {
            return null;
        } else {
            return change;
        }
    });


    public static String getFormattedDate() {
        String pattern = "yyyyMMdd-HH:mm:ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String date = simpleDateFormat.format(new Date());
        return date;
    }

    public void launchDatepicker() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/fxml/datepicker.fxml"));
            Parent root = fxmlLoader.load();
//            DatepickerController advancedRequestController = fxmlLoader.getController();
//            advancedRequestController.injectMainController(this);
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setAlwaysOnTop(true);
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
