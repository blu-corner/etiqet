package com.neueda.etiqet.orderbook.etiqetorderbook.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

public class Constants {
    public static final Logger orderBookLooger = LoggerFactory.getLogger("ORDER BOOK");
    public static final String OUT = "[>>OUT>>]";
    public static final String IN = "[<<<.IN<<]";
    public static final HashMap<String,String> hmMsgType;
    public static final HashMap<String,String> hmOrdStatus;
    public static final HashMap<String,String> hmExecType;
    public static final HashMap<String,String> hmTags;
    public static final String EXECUTION_REPORT = "8";
    public static final String NEW = "0000";
    public static final String NONE = "NONE";
    public static final String SOCKET_ACCEPTOR_PORT = "SocketAcceptPort";
    public static final String ACCEPTOR_PORT_DIALOG_TITLE = "ACCEPTOR PORT";
    public static final String ACCEPTOR_PORT_DIALOG_HEADER = "Introduce port to listen on";
    public static final String ACCEPTOR_PORT_DIALOG_TEXT = "Port:";
    public static final String ACCEPTOR_ROLE = "ACCEPTOR";
    public static final String INITIATOR_ROLE = "INITIATOR";
    public static final String CLIENT_CFG = "client.cfg";
    public static final String SERVER_CFG = "server.cfg";
    public static final String SOCKET_INITIATOR_PORT = "SocketConnectPort";
    public static final String INITIATOR_PORT_DIALOG_TITLE = "INITIATOR PORT";
    public static final String INITIATOR_PORT_DIALOG_HEADER = "Introduce port to connect";
    public static final String INITIATOR_PORT_DIALOG_TEXT = "Port:";
    public static final String SET_DEFAULT_PORT = "Set default port: ";
    public static final String COMBO_NEW_ORDER = "NEW ORDER";
    public static final String COMBO_CANCEL = "CANCEL";
    public static final String COMBO_REPLACE = "REPLACE";

    public static final String MSG_TYPE = "35";

    static{
        hmTags = new HashMap<>();
        hmTags.put("1", "Account");
        hmTags.put("6", "AvgPx");
        hmTags.put("8", "BeginString");
        hmTags.put("9", "BodyLength");
        hmTags.put("10", "CheckSum");
        hmTags.put("11", "ClOrdID");
        hmTags.put("14", "CumQty");
        hmTags.put("17", "ExecID");
        hmTags.put("31", "LastPx");
        hmTags.put("37", "OrderID");
        hmTags.put("34", "MsgSeqNum");
        hmTags.put(MSG_TYPE, "MsgType");
        hmTags.put("36", "NewSeqNo");
        hmTags.put("38", "OrderQty");
        hmTags.put("39", "OrdStatus");
        hmTags.put("40", "OrdType");
        hmTags.put("41", "OrigClOrdID");
        hmTags.put("44", "Price");
        hmTags.put("49", "SenderCompID");
        hmTags.put("52", "SendingTime");
        hmTags.put("54", "Side");
        hmTags.put("55", "Symbol");
        hmTags.put("56", " TargetCompID");
        hmTags.put("58", " Text");
        hmTags.put("60", " TransactTime");
        hmTags.put("150", "ExecType");
        hmTags.put("151", "LeavesQty");
        hmTags.put("192", "OrderQty2");
        hmTags.put("354", "EncodedTextLen");

    }

    static {
        hmMsgType = new HashMap<>();
        hmMsgType.put("0", "HEARTBEAT");
        hmMsgType.put("1", "TEST REQUEST");
        hmMsgType.put("2", "RESEND REQUEST");
        hmMsgType.put("3", "REJECT");
        hmMsgType.put("4", "SEQUENCE RESET");
        hmMsgType.put("5", "LOGOUT");
        hmMsgType.put("6", "INDICATION OF INTEREST");
        hmMsgType.put("7", "ADVERTISEMENT");
        hmMsgType.put(EXECUTION_REPORT, "EXECUTION REPORT");
        hmMsgType.put("9", "ORDER CANCEL REJECT");
        hmMsgType.put("A", "LOGON");
        hmMsgType.put("B", "NEWS");
        hmMsgType.put("C", "EMAIL");
        hmMsgType.put("D", "NEW ORDER SINGLE");
        hmMsgType.put("E", "NEW ORDER LIST");
        hmMsgType.put("F", "ORDER CANCEL REQUEST");
        hmMsgType.put("G", "ORDER CANCEL/REPLACE REQUEST");
    }

    static {
        hmOrdStatus = new HashMap<>();
        hmOrdStatus.put("0", "NEW");
        hmOrdStatus.put("1", "PARTIALLY FILLED");
        hmOrdStatus.put("2", "FILLED");
        hmOrdStatus.put("3", "DONE FOR DAY");
        hmOrdStatus.put("4", "CANCELED");
        hmOrdStatus.put("5", "");//removed in FIX 44
        hmOrdStatus.put("6", "PENDING CANCEL");
        hmOrdStatus.put("7", "STOPPED");
        hmOrdStatus.put("8", "REJECTED");
        hmOrdStatus.put("9", "SUSPENDED");
        hmOrdStatus.put("A", "PENDING NEW");
        hmOrdStatus.put("B", "CALCULATED");
        hmOrdStatus.put("C", "EXPIRED");
        hmOrdStatus.put("D", "ACCEPTED FOR BIDDING");
        hmOrdStatus.put("E", "PENDING REPLACE");
    }

    static {
        hmExecType = new HashMap<>();
        hmExecType.put("0", "NEW");
        hmExecType.put("3", "DONE FOR DAY");
        hmExecType.put("4", "CANCELED");
        hmExecType.put("5", "REPLACE");
        hmExecType.put("6", "PENDING CANCEL");
        hmExecType.put("7", "STOPPED");
        hmExecType.put("8", "REJECTED");
        hmExecType.put("9", "SUSPENDED");
        hmExecType.put("A", "PENDING NEW");
        hmExecType.put("B", "CALCULATED");
        hmExecType.put("C", "EXPIRED");
        hmExecType.put("D", "RESTARTED");
        hmExecType.put("E", "PENDING REPLACE");
        hmExecType.put("F", "TRADE");
        hmExecType.put("G", "TRADE CORRECT");
        hmExecType.put("H", "TRADE CANCEL");
        hmExecType.put("I", "ORDER STATUS");
    }




}
