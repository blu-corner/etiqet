package com.neueda.etiqet.orderbook.etiqetorderbook.utils;

import quickfix.Message;

public class Utils {

    public static final char SOH = '\u0001';
    public static final char VERTICAL_BAR = '\u007C';

    public static String replaceSOH(Message message) {
        String content = message.toString();
        return content.replace(SOH, VERTICAL_BAR);
    }
}
