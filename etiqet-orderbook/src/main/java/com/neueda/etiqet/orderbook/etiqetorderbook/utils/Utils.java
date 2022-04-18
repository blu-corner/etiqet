package com.neueda.etiqet.orderbook.etiqetorderbook.utils;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;
import quickfix.Message;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Utils {

    public static String replaceSOH(Message message) {
        String content = message.toString();
        return content.replace(Constants.SOH, Constants.VERTICAL_BAR);
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
}
