package com.neueda.etiqet.orderbook.etiqetorderbook.utils;

import com.neueda.etiqet.orderbook.etiqetorderbook.entity.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static com.neueda.etiqet.orderbook.etiqetorderbook.utils.Utils.getConfig;

public class Constants {
    public static final Logger orderBookLogger = LoggerFactory.getLogger("ORDER BOOK");

    public static final char SOH = '\u0001';
    public static final char VERTICAL_BAR = '\u007C';
    public static final String OUT = "[>>OUT>>]";
    public static final String IN = "[<<<.IN<<]";
    public static final HashMap<String,String> hmMsgType;
    public static final HashMap<String,String> hmOrdStatus;
    public static final HashMap<String,String> hmExecType;
    public static final HashMap<String,String> hmTags;
    public static final HashMap<String,String> hmFixVersions;
    public static final String EXECUTION_REPORT = "8";
    public static final String NEW = "0000";
    public static final String NONE = "NONE";

    public static final String CONF_BEGIN_STRING = "BeginString";
    public static final String CONF_SENDER = "SenderCompID";
    public static final String CONF_TARGET = "TargetCompID";
    public static final String CONF_CONNECTION_TYPE = "ConnectionType";
    public static final String CONF_RECONNECT_INTERVAL = "ReconnectInterval";
    public static final String CONF_FILE_STORE_PATH = "FileStorePath";
    public static final String CONF_FILE_LOG_PATH= "FileLogPath";
    public static final String CONF_START_TIME = "StartTime";
    public static final String CONF_END_TIME = "EndTime";
    public static final String CONF_USE_DATA_DIC = "UseDataDictionary";
    public static final String CONF_DATA_DIC = "DataDictionary";
    public static final String CONF_RESET_ON_LOGON = "ResetOnLogon";
    public static final String CONF_RESET_ON_LOGOUT = "ResetOnLogout";
    public static final String CONF_RESET_ON_DISCONNECT = "ResetOnDisconnect";
    public static final String CONF_HEART_BT_INT= "HeartBtInt";
    public static final String INI_CONNECT_HOST = "SocketConnectHost";
    public static final String INI_CONNECT_PORT= "SocketConnectPort";
    public static final String ACC_ACCEPT_PORT = "SocketAcceptPort";
    public static final String ACC_SOCKET_ACCEPT_PORT_RANGE_LIMIT = "PortRangeLimit";

    public static final String ACCEPTOR_PORT_DIALOG_TITLE = "ACCEPTOR PORT";
    public static final String ACCEPTOR_PORT_DIALOG_HEADER = "Introduce port to listen on";
    public static final String ACCEPTOR_PORT_DIALOG_TEXT = "Port:";
    public static final String ACCEPTOR_ROLE = "ACCEPTOR";
    public static final String INITIATOR_ROLE = "INITIATOR";
    public static final String CLIENT_CFG = "client.cfg";
    public static final String SERVER_CFG = "server.cfg";
    public static final String INITIATOR_PORT_DIALOG_TITLE = "INITIATOR PORT";
    public static final String INITIATOR_PORT_DIALOG_HEADER = "Introduce port to connect";
    public static final String INITIATOR_PORT_DIALOG_TEXT = "Port:";
    public static final String SET_DEFAULT_PORT = "Set default port: ";
    public static final String COMBO_NEW_ORDER = "NEW ORDER";
    public static final String COMBO_CANCEL = "CANCEL";
    public static final String COMBO_REPLACE = "REPLACE";

    public static final String MSG_TYPE = "35";
    public static final String PORTS_RANGE_ERROR = "PORTS RANGE ERROR";
    public static final String BAD_PORTS_RANGE = "Bad ports range";
    public static final String PORTS_IN_USE = "Ports in use";
    public static final String LISTENING_ON_PORTS = "Listening on ports: %s";
    public static final String INVALID_PORTS = "Invalid ports: ";
    public static final String HELP_SITE = "https://btobits.com/fixopaedia/fixdic44/fields_by_tag_.html";
    public static final List<String> Y_N = Arrays.asList("Y", "N");
    public static final String FIX_4_0 = "FIX.4.0";
    public static final String FIX_4_1 = "FIX.4.1";
    public static final String FIX_4_2 = "FIX.4.2";
    public static final String FIX_4_3 = "FIX.4.3";
    public static final String FIX_4_4 = "FIX.4.4";
    public static final String FIX_5_0 = "FIX.5.0";
    public static final String FIX40 = "40";
    public static final String FIX41 = "41";
    public static final String FIX42 = "42";
    public static final String FIX43 = "43";
    public static final String FIX44 = "44";
    public static final String FIX50 = "50";
    public static final List<String> FIX_VERSIONS = Arrays.asList(FIX_4_0, FIX_4_1, FIX_4_2, FIX_4_3, FIX_4_4, FIX_5_0);
    public static final List<String> FIX_VERSIONS_COMBO = Arrays.asList(FIX40, FIX41, FIX42, FIX43, FIX44, FIX50);
    public static final String Y = "Y";
    public static final String N = "N";
    public static final String SRC_MAIN_RESOURCES_SERVER_CFG = "src/main/resources/server.cfg";
    public static final String SRC_MAIN_RESOURCES_CLIENT_CFG = "src/main/resources/client.cfg";
    public static final String INITIATOR_TITLE = "Initiator configuration";
    public static final String ACCEPTOR_TITLE = "Acceptor configuration";
    public static final String ADVANCED_REQUEST_TITLE = "Advanced request";
    public static final List<Tag> defaultTags;


    static {
        hmFixVersions = new HashMap<>();
        hmFixVersions.put(FIX_4_0, "FIX40.xml");
        hmFixVersions.put(FIX_4_1, "FIX41.xml");
        hmFixVersions.put(FIX_4_2, "FIX42.xml");
        hmFixVersions.put(FIX_4_3, "FIX43.xml");
        hmFixVersions.put(FIX_4_4, "FIX44.xml");
        hmFixVersions.put(FIX_5_0, "FIX50.xml");
    }

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

    static{
        defaultTags = new ArrayList<>();
        int dataDic = Utils.getComboConfigValue(Constants.INITIATOR_ROLE, Constants.CONF_DATA_DIC);
        int resetOnLogon = Utils.getComboConfigValue(Constants.INITIATOR_ROLE, Constants.CONF_RESET_ON_LOGON);
        int resetOnLogout = Utils.getComboConfigValue(Constants.INITIATOR_ROLE, Constants.CONF_RESET_ON_LOGOUT);
        int resetOnDisconnect = Utils.getComboConfigValue(Constants.INITIATOR_ROLE, Constants.CONF_RESET_ON_DISCONNECT);
        defaultTags.add(new Tag(Constants.CONF_BEGIN_STRING, getConfig(Constants.INITIATOR_ROLE, Constants.CONF_BEGIN_STRING)));
        defaultTags.add(new Tag(Constants.CONF_SENDER, getConfig(Constants.INITIATOR_ROLE, Constants.CONF_SENDER)));
        defaultTags.add(new Tag(Constants.CONF_TARGET, getConfig(Constants.INITIATOR_ROLE, Constants.CONF_TARGET)));
        defaultTags.add(new Tag(Constants.INI_CONNECT_HOST, getConfig(Constants.INITIATOR_ROLE,Constants.INI_CONNECT_HOST)));
        defaultTags.add(new Tag(Constants.INI_CONNECT_PORT, getConfig(Constants.INITIATOR_ROLE,Constants.INI_CONNECT_PORT)));
        defaultTags.add(new Tag(Constants.CONF_FILE_STORE_PATH, getConfig(Constants.INITIATOR_ROLE, Constants.CONF_FILE_STORE_PATH)));
        defaultTags.add(new Tag(Constants.CONF_FILE_LOG_PATH, getConfig(Constants.INITIATOR_ROLE, Constants.CONF_FILE_LOG_PATH)));
        defaultTags.add(new Tag(Constants.CONF_START_TIME, getConfig(Constants.INITIATOR_ROLE, Constants.CONF_START_TIME)));
        defaultTags.add(new Tag(Constants.CONF_END_TIME, getConfig(Constants.INITIATOR_ROLE, Constants.CONF_END_TIME)));
        defaultTags.add(new Tag(Constants.CONF_USE_DATA_DIC, dataDic == 0 ? Y: N));
        defaultTags.add(new Tag(Constants.CONF_HEART_BT_INT, getConfig(Constants.INITIATOR_ROLE, Constants.CONF_HEART_BT_INT)));
        defaultTags.add(new Tag(Constants.CONF_RESET_ON_LOGON, resetOnLogon == 0 ? Y: N));
        defaultTags.add(new Tag(Constants.CONF_RESET_ON_LOGOUT, resetOnLogout == 0 ? Y: N));
        defaultTags.add(new Tag(Constants.CONF_RESET_ON_DISCONNECT, resetOnDisconnect == 0 ? Y: N));
    }
}
