package com.neueda.etiqet.orderbook.etiqetorderbook.utils;

import quickfix.Message;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Utils {

    public static final char SOH = '\u0001';
    public static final char VERTICAL_BAR = '\u007C';

    public static String replaceSOH(Message message) {
        String content = message.toString();
        return content.replace(SOH, VERTICAL_BAR);
    }


    private void killProcessByPort(int port) {
        try {
            ArrayList<Long> pids = new ArrayList<Long>();
            Stream<ProcessHandle> processStream = ProcessHandle.allProcesses();

            List<ProcessHandle> processHandleList = processStream.collect(Collectors.toList());

            for (ProcessHandle p: processHandleList) {
                if (p.isAlive()){
                    ProcessHandle.Info info = p.info();
                    Optional<String> command = p.info().command();
                    if (command.get().contains("java")){
                        String doso = "";
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
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
}
