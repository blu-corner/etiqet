package com.neueda.etiqet.fixture;

import com.neueda.etiqet.core.client.Client;
import com.neueda.etiqet.core.client.ClientFactory;
import com.neueda.etiqet.core.common.cdr.Cdr;
import com.neueda.etiqet.core.common.cdr.CdrItem;
import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import com.neueda.etiqet.core.config.GlobalConfig;
import com.neueda.etiqet.core.message.config.ProtocolConfig;
import com.neueda.etiqet.core.server.Server;
import com.neueda.etiqet.core.server.ServerFactory;
import com.neueda.etiqet.core.util.ArrayUtils;
import com.neueda.etiqet.core.util.ParserUtils;
import com.neueda.etiqet.core.util.Separators;
import com.neueda.etiqet.core.util.StringUtils;
import gherkin.deps.com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.mina.util.ConcurrentHashSet;

import javax.net.ssl.HttpsURLConnection;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class EtiqetHandlers {

    private static final Logger LOG = LogManager.getLogger(EtiqetHandlers.class);

    public static final String DEFAULT_CLIENT_NAME = "default";
    public static final String DEFAULT_MESSAGE_NAME = "default";
    public static final String DEFAULT_SERVER_NAME = "default";
    public static final String DEFAULT_PARAMS = "";
    public static final String DEFAULT_SESSION = null;
    public static final String DEFAULT_EXCEPTION = "default";
    public static final String RESPONSE = "response";
    public static final int NOT_SET = -999999999;
    public static final int MILLI_NANO_CONVERSION = 1000000;

    public static final String HTTP_POST = "POST";
    public static final String PURGE_ORDERS = "requests/purge_orders";
    public static final String SET_TRADE_PHASE = "requests/set_trading_phase";

    public static final String SECONDS_NAME = "seconds";
    public static final String MILLI_TIME_NAME = "milli";
    public static final String MICRO_TIME_NAME = "micro";
    public static final String NANO_TIME_NAME = "nano";

    public static final int SECONDS_TIME_VALUE = 0;
    public static final int MILLI_TIME_VALUE = 3;
    public static final int MICRO_TIME_VALUE = 6;
    public static final int NANO_TIME_VALUE = 9;

    public static final String ERROR_CLIENT_NOT_FOUND = "Client %s must exist";
    public static final String ERROR_CLIENT_NOT_LOGGED_ON = "Client %s must be logged on";
    public static final String ERROR_CLIENT_NOT_LOGGED_OFF = "Client %s must be logged off";
    public static final String ERROR_CLIENT_NOT_STARTED = "Error starting client named: '%s'";
    public static final String ERROR_SERVER_NOT_FOUND = "Server named %s wasn't found";
    public static final String ERROR_SERVER_NOT_CREATED = "Error creating server type %s";
    public static final String ERROR_SERVER_NOT_STARTED = "Error starting server named %s";
    public static final String ERROR_FIELD_COMPARISON = "Expected %s in %s to be greater than or equal to %s in %s";
    public static final String ERROR_FIELD_COMPARISON_MILLIS = ERROR_FIELD_COMPARISON + " by no more than %sms";

    public static final String CLIENT_THREAD_PREFIX = "clientThread-%s";

    boolean expectException = false;
    private List<RuntimeException> exceptionsList = new ArrayList<>();

    Map<String, String> cukeVariables = new HashMap<>();

    GlobalConfig globalConfig;

    private Map<String, Client> clientMap = new HashMap<>();
    private Map<String, Server> serverMap = new HashMap<>();
    private NavigableMap<String, Cdr> messageMap = new TreeMap<>();

    private NavigableMap<String, Cdr> responseMap = new TreeMap<>();

    private Set<String> filteredMsgs = new ConcurrentHashSet<>();

    private boolean tryOn;

    private Map<String, List<String>> exceptionMap = new HashMap<>();

    public EtiqetHandlers() {
        try {
            globalConfig = GlobalConfig.getInstance();
            clientMap.putAll(globalConfig.getClients());
            serverMap.putAll(globalConfig.getServers());
        } catch (EtiqetException e) {
            String msg = "Error reading global configuration: " + e.getMessage();
            LOG.error(msg, e.getCause());
            fail(msg + ", " + e.getCause().getMessage());
        }
    }

    public void startServer(String serverName) {
        Server server = getServer(serverName);
        assertNotNull(String.format(ERROR_SERVER_NOT_FOUND, serverName), server);
        try {
            Thread thread = new Thread(server);
            thread.start();
            Thread.sleep(3000);
        } catch (Exception e) {
            LOG.error(String.format(ERROR_SERVER_NOT_STARTED, serverName), e);
            fail(String.format(ERROR_SERVER_NOT_STARTED, serverName));
        }
    }

    public void createServer(String serverName, String serverClass) {
        createServer(serverName, serverClass, null);
    }

    public void createServer(String serverName, String serverClass, String serverConfig) {
        try {
            Server server;
            if(!StringUtils.isNullOrEmpty(serverConfig)) {
                server = ServerFactory.create(serverClass);
            } else {
                server = ServerFactory.create(serverClass, serverConfig);
            }
            assertNotNull("Could not initialise an instance of " + serverClass, server);
            serverMap.put(serverName, server);
        } catch (EtiqetException e) {
            LOG.error(String.format(ERROR_SERVER_NOT_CREATED, serverClass), e);
            fail(String.format(ERROR_SERVER_NOT_CREATED, serverClass));
        }
    }

    public void startClient(String impl, String clientName, String extraConfig) {
        Client client;
        try {
            client = clientMap.containsKey(clientName) ? clientMap.get(clientName) : createClient(impl, clientName);
            client.setClientConfig(extraConfig, null);

            Thread thread = new Thread(client, String.format(CLIENT_THREAD_PREFIX, clientName));
            thread.start();

        } catch (EtiqetException e) {
            LOG.error(String.format(ERROR_CLIENT_NOT_STARTED, clientName), e.getCause());
        }
    }

    public void startClientWithFailover(String impl, String clientName, String primaryConfig, String secondaryConfig) {
        Client client;
        try {
            client = clientMap.containsKey(clientName) ? clientMap.get(clientName) : createClientWithFailover(impl, clientName, primaryConfig, secondaryConfig);
            client.setClientConfig(primaryConfig, secondaryConfig);
            Thread thread = new Thread(client, String.format(CLIENT_THREAD_PREFIX, clientName));
            thread.start();

        } catch (EtiqetException e) {
            LOG.error(String.format(ERROR_CLIENT_NOT_STARTED, clientName), e.getCause());
        }
    }

    /**
     * Creates a client and add it to the list of clients with the provided name.
     *
     * @param impl       the name of the client type.
     * @param clientName the alias to refer to this client.
     * @throws EtiqetException when client cannot be instantiated.
     */
    public Client createClient(String impl, String clientName) throws EtiqetException {
        Client client = ClientFactory.create(impl);
        addClient(clientName, client);
        return client;
    }

    /**
     * Creates a client with a secondary config which enables failover
     * @param clientType
     * @param primaryConfig
     * @param secondaryConfig
     * @return
     */
    public Client createClientWithFailover(String clientType, String clientName, String primaryConfig, String secondaryConfig)throws EtiqetException{
        if(StringUtils.isNullOrEmpty(secondaryConfig)){
            throw new EtiqetException( "Secondary Config must be provided when trying to create a client with failover capabilities");
        }
        Client client = ClientFactory.create(clientType, primaryConfig, secondaryConfig);
        addClient(clientName, client);
        return client;
    }

    /**
     * Cause the client to failover from Primary to Secondary config if enabled
     * @param clientName
     * @throws EtiqetException
     */
    public void failover(String clientName)throws EtiqetException{
        Client client = clientMap.get(clientName);
        if (client.canFailover()){
            client.failover();
        } else {
            throw new EtiqetException("Client: " + clientName + " not enabled for failover");
        }
    }

    /**
     * Adds a client to the clientMap. Abstracted to assist in unit testing
     *
     * @param clientName name of the client to be added
     * @param client     client to be added
     */
    void addClient(String clientName, Client client) {
        clientMap.put(clientName, client);
    }

    /**
     * Gets a client from the clientMap. Abstracted to assist in unit testing
     *
     * @param clientName name of the client
     * @return Client object matching clientName, or null if not found
     */
    Client getClient(String clientName) {
        return clientMap.get(clientName);
    }

    /**
     * Runs a previously created client with the given name.
     *
     * @param clientName the name of the client.
     * @see #createClient
     */
    public void startClient(String clientName) {
        Thread clientThread = new Thread(getClient(clientName), String.format(CLIENT_THREAD_PREFIX, clientName));
        clientThread.start();
    }

    public void startClient(String impl, String clientName) throws EtiqetException {
        Client client = clientMap.containsKey(clientName) ? clientMap.get(clientName) : createClient(impl, clientName);
        Thread clientThread = new Thread(client, String.format(CLIENT_THREAD_PREFIX, clientName));
        clientThread.start();
    }

    public void isClientLoggedOn(String name) {
        Client client = getClient(name);
        assertNotNull(String.format(ERROR_CLIENT_NOT_FOUND, name), client);

        boolean loggedOn = client.isLoggedOn();
        handleError(String.format(ERROR_CLIENT_NOT_LOGGED_ON, name), loggedOn, "ClientNoLoggedOnException");
    }

    public void isClientLoggedOff(String name) {
        Client client = getClient(name);
        assertNotNull(String.format(ERROR_CLIENT_NOT_FOUND, name), client);

        boolean loggedOff = !client.isLoggedOn();
        assertTrue(String.format(ERROR_CLIENT_NOT_LOGGED_OFF, name), loggedOff);
    }

    public void waitForClientLogon(String clientName) {
        Client client = getClient(clientName);
        assertNotNull(String.format(ERROR_CLIENT_NOT_FOUND, clientName), client);
        handleError("waitForClientLogon: timeout waiting for logon", client.waitForLogon(), "ClientNoLoggedOnException");
    }

    /**
     * Gets the value from a Cdr object based on a given key. Allows use of nested elements to return a value.
     *
     * @param tree string representing tree field (e.g. parent->child)
     * @param cdr  Cdr object that
     * @return value from the tree provided, null if not found
     */
    String getValueFromTree(String tree, Cdr cdr) {
        String[] treeSplit = tree.split(Separators.LEVEL_SEPARATOR);
        if (treeSplit.length == 1) {
            return cdr.getAsString(tree);
        }

        CdrItem cdrItem = cdr.getItem(treeSplit[0]);
        for (int i = 1, len = treeSplit.length; i < len; i++) {
            if (cdrItem != null && cdrItem.getType().equals(CdrItem.CdrItemType.CDR_ARRAY)) {
                String nextChild = treeSplit[i];
                cdrItem = cdrItem.getCdrs().stream()
                            .filter(c -> c.containsKey(nextChild))
                            .findFirst()
                            .map(c -> c.getItem(nextChild))
                            .orElse(null);
            } else {
                cdrItem = null;
                break;
            }
        }

        return cdrItem == null ? null : cdrItem.toString();
    }

    /**
     * Method to prepare params correctly before set them into message.
     *
     * @param params param list comma separated.
     * @return dealt param list.
     */
    String preTreatParams(String params) {
        StringBuilder preTreatedParams = new StringBuilder();

        if (StringUtils.isNullOrEmpty(params) || params.length() < 3) // minimum 3 characters (e.g. x=y)
            return preTreatedParams.toString();

        String[] parameList = params.trim().split(Separators.PARAM_SEPARATOR);
        for (String param : parameList) {
            if(!param.contains(Separators.KEY_VALUE_SEPARATOR)) {
                preTreatedParams.append(param);
                continue;
            }
            String lhs = param.split(Separators.KEY_VALUE_SEPARATOR)[0];
            String rhs = param.split(Separators.KEY_VALUE_SEPARATOR)[1];

            String messageValue;
            if (rhs.contains(Separators.LEVEL_SEPARATOR)) {
                messageValue = searchRhs(rhs);
            } else {
                messageValue = rhs;
            }

            String tempParam = lhs + Separators.KEY_VALUE_SEPARATOR + messageValue;

            if (!StringUtils.isNullOrEmpty(preTreatedParams.toString())) {
                preTreatedParams.append(Separators.PARAM_SEPARATOR);
            }
            if (lhs.equals("TransactTime") && rhs.equals("now")) {
                SimpleDateFormat f = new SimpleDateFormat("yyyyMMdd-HH:mm:ss.SSS");
                f.setTimeZone(TimeZone.getTimeZone("UTC"));
                tempParam = lhs + Separators.KEY_VALUE_SEPARATOR + f.format(new Date());
            }
            if (tempParam.length() > 1) {
                preTreatedParams.append(tempParam);
            }
        }

        return preTreatedParams.toString();
    }

    /**
     * Helper method to drill into elements and return searched for term
     * @param rhs
     * @return
     */
    private String searchRhs(String rhs){
        String[] split = rhs.split(Separators.LEVEL_SEPARATOR, 2);
        String firstElement = split[0];
        String searchTerm = split[1];
        Cdr originMessage = getSentMessage(firstElement);
        if (originMessage == null) {
            originMessage = getResponse(firstElement);
        }
        if (originMessage == null) {
            originMessage = getSentMessage(DEFAULT_MESSAGE_NAME);
            searchTerm = rhs;
        }
        if (originMessage == null) {
            originMessage = getResponse(DEFAULT_MESSAGE_NAME);
        }

        return getValueFromTree(searchTerm, originMessage);
    }

    Cdr getSentMessage(String messageName) {
        return messageMap.get(messageName);
    }

    /**
     * Method to convert the fix message String Date/Time to nanoseconds
     *
     * @param dateString fix message time value
     * @return dateString represented as nanoseconds
     */
    long dateToNanos(String dateString) {
        if(StringUtils.isNullOrEmpty(dateString)){
            return 0;
        }
        //Need to ensure the nano secs are full for correct manipulation
        String pad = "000000000";
        if(dateString.contains(".")) {
            dateString += pad;
        }else {
            dateString = dateString + "." + pad;
        }

        //first convert date portion to millis
        Date date;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HH:mm:ss");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            date = sdf.parse(dateString.substring(0,dateString.indexOf('.')));
        } catch (ParseException e) {
            LOG.error("Error parsing date: " + e);
            return -1;
        }

        //Remove the extra zeros from the nano portion
        long nano = Long.parseLong(dateString.substring(dateString.indexOf('.')+1,dateString.indexOf('.')+10));
        LOG.info (String.format("time: %d %s", date.getTime()*MILLI_NANO_CONVERSION + nano, dateString));
        //convert millis to nano by multipling by 1 million
        return date.getTime()*MILLI_NANO_CONVERSION +(nano);
    }

    public String getValueFromField(String messageAlias,String field){
        String sValue = (preTreatParams("=" + messageAlias + "->" + field)).substring(1);
        assertNotNull(field + " in " + messageAlias + " is null.", sValue);
        assertFalse(field + " in " + messageAlias + " is an empty string.",sValue.equals(""));
        return sValue;
    }

    public void compareValuesEqual(String firstField, String firstMessageAlias, String secondField, String secondMessageAlias){
        String sFirstValue = getValueFromField(firstMessageAlias,firstField);
        String sSecondValue = getValueFromField(secondMessageAlias,secondField);
        assertTrue(firstField +" in " + firstMessageAlias +
                " is " + sFirstValue +" in " + secondMessageAlias +
                " it is " + sSecondValue, sFirstValue.equals(sFirstValue));
    }

    public void compareValues(String firstField, String firstMessageAlias, String secondField, String secondMessageAlias, Long millis){
        String sFirstValue = getValueFromField(firstMessageAlias,firstField);
        assertFalse("Only timestamps and numeric values can be compared for greater/lesser than",sFirstValue.contains("[a-zA-Z]+"));
        String sSecondValue = getValueFromField(secondMessageAlias,secondField);
        assertFalse("Only timestamps and numeric values can be compared for greater/lesser than",sSecondValue.contains("[a-zA-Z]+"));
        double firstToCompare=NOT_SET;
        double secondToCompare=NOT_SET;
        try{
            firstToCompare = Double.parseDouble(sFirstValue);
            secondToCompare = Double.parseDouble(sSecondValue);
        }
        catch (NumberFormatException e) {
            compareTimestamps(sFirstValue,sSecondValue,firstField,firstMessageAlias,secondField,secondMessageAlias,millis);
        }
        if(firstToCompare!=NOT_SET && secondToCompare!=NOT_SET){
            assertTrue(String.format(ERROR_FIELD_COMPARISON, firstField, firstMessageAlias, secondField, secondMessageAlias),
                firstToCompare >= secondToCompare);
            if (millis != null) {
                assertTrue(String.format(ERROR_FIELD_COMPARISON_MILLIS, firstField, firstMessageAlias, secondField, secondMessageAlias, millis) ,
                        (firstToCompare - secondToCompare) < millis);
            }
    }}
    /**
     * Method to compare timestamps of two separate messages
     *
     * @param firstField         field value to find from first message
     * @param firstMessageAlias  alias to find first message
     * @param secondField        field value to find from second message
     * @param secondMessageAlias alias to find second message
     * @param millis             optional time-frame for checking response time
     */
    public void compareTimestamps(String firstValue, String secondValue, String firstField, String firstMessageAlias, String secondField, String secondMessageAlias, Long millis) {
        Long lFirstTimestamp = dateToNanos(firstValue);
        assertTrue("Could not read " + firstField + " in " + firstMessageAlias, lFirstTimestamp != -1);

        Long lSecondTimestamp = dateToNanos(secondValue);
        assertTrue("Could not read " + secondField + " in " + secondMessageAlias,lSecondTimestamp != -1);

        assertTrue(String.format(ERROR_FIELD_COMPARISON, firstField, firstMessageAlias, secondField, secondMessageAlias),
                lFirstTimestamp >= lSecondTimestamp);
        if (millis != null) {
            assertTrue(String.format(ERROR_FIELD_COMPARISON_MILLIS, firstField, firstMessageAlias, secondField, secondMessageAlias, millis),
                (lFirstTimestamp - lSecondTimestamp) < millis);
        }
    }

    /**
     * Method to extract the timestamp from a message and the timestamp from cucumber
     * and convert to long millis
     *
     * @param field field value to find in message
     * @param messageAlias alias for message to find, null for latest response
     * @param sSecondTimestamp timestamp to compare timestamp extracted from message against
     * @return the pair of timestamps as millis
     */
    Long[] extractTimestampAndCukeVar(String field, String messageAlias, String sSecondTimestamp) {
        String sFirstTimestamp = null == messageAlias ?
                responseMap.lastEntry().getValue().getAsString(field) :
                (preTreatParams("=" + messageAlias + "->" + field)).substring(1);
        Long lFirstValue;
        Long lSecondValue;
        try{
            lFirstValue = Long.parseLong(sFirstTimestamp);
            lSecondValue = Long.parseLong(sSecondTimestamp);
        }catch (NumberFormatException e){
            lFirstValue = dateToNanos(sFirstTimestamp);
            lSecondValue = dateToNanos(sSecondTimestamp);
        }
        return new Long[]{lFirstValue, lSecondValue};
    }

    public void compareTimestampGreaterCukeVar(String field, String messageAlias, String sSecondTimestamp) {
        Long[] timestamps = extractTimestampAndCukeVar(field, messageAlias, sSecondTimestamp);
        assertTrue(timestamps[0] > timestamps[1]);
    }

    public void compareTimestampLesserCukeVar(String field, String messageAlias, String sSecondTimestamp) {
        Long[] timestamps = extractTimestampAndCukeVar(field, messageAlias, sSecondTimestamp);
        assertTrue(timestamps[0] < timestamps[1]);
    }

    public void compareTimestampEqualsCukeVar(String field, String messageAlias, String sSecondTimestamp) {
        String sFirstTimestamp = null == messageAlias ?
                responseMap.lastEntry().getValue().getAsString(field) :
                (preTreatParams("=" + messageAlias + "->" + field)).substring(1);
        if (sFirstTimestamp.equalsIgnoreCase("null"))
            assertTrue (StringUtils.isNullOrEmpty(sSecondTimestamp));
        else
            assertEquals(sFirstTimestamp, sSecondTimestamp);
    }

    /**
     * Method to validate timestamp from message against given format
     *
     * @param formatParam  format inputted to match against timestamp
     * @param messageAlias alias of message to find on map
     * @param field        field to find value of from message
     */
    public void validateTimestampAgainstFormatParam(String formatParam, String messageAlias, String field) {
        try {
            String timestamp = (preTreatParams("=" + messageAlias + "->" + field)).substring(1);
            SimpleDateFormat df = new SimpleDateFormat(formatParam);
            df.setLenient(false);
            df.parse(timestamp);

            String[] timeArr = timestamp.split("((?<=[^\\w'])|(?=[^\\w']))+");
            String[] formatArr = formatParam.split("((?<=[^\\w'])|(?=[^\\w']))+");

            assertEquals(timeArr.length, formatArr.length);
            for (int i = 0; i < timeArr.length; i++) {
                assertEquals(timeArr[i].length(), formatArr[i].length());
                if (!timeArr[i].matches("^[\\pL\\pN]+$")) {
                    assertEquals(timeArr[i], formatArr[i]);
                }
            }
        } catch (ParseException e) {
            String errorMsg = "Error parsing date from field " + field + " in message " + messageAlias + ".";
            LOG.error(errorMsg, e);
            fail(errorMsg);
        }
    }

    /**
     * Method to create a message.
     *
     * @param msgType     message type
     * @param clientName  the name of the client.
     * @param messageName name to index the message.
     * @param params      list of params with pattern "field1=value1,field2=value2,...,fieldN=valueN"
     */
    public void createMessageForClient(String msgType, String clientName, String messageName, String params)
            throws EtiqetException {
        Cdr message = ParserUtils.stringToCdr(msgType, preTreatParams(params));
        Client client = getClient(clientName);
        assertNotNull(String.format(ERROR_CLIENT_NOT_FOUND, clientName), client);
        ProtocolConfig config = client.getProtocolConfig();
        assertNotNull("Could not find protocol " + client.getProtocolName(), config);
        ParserUtils.fillDefault(config.getMessage(msgType), message);
        messageMap.put(messageName, message);
    }

    public void addCukeVariable(String alias, String value) {
        if (value.equals("currentTimestamp")) {
            SimpleDateFormat f = new SimpleDateFormat("yyyyMMdd-HH:mm:ss.SSS");
            f.setTimeZone(TimeZone.getTimeZone("UTC"));
            cukeVariables.put(alias, f.format(new Date()));
        } else {
            cukeVariables.put(alias, value);
        }
    }

    /**
     * Method to create a message.
     *
     * @param msgType     message type
     * @param protocol    to send the message.
     * @param messageName name to index the message.
     * @param params      list of params with pattern "field1=value1,field2=value2,...,fieldN=valueN"
     */
    public void createMessage(String msgType, String protocol, String messageName, String params)
            throws EtiqetException {
        Cdr message = ParserUtils.stringToCdr(msgType, preTreatParams(params));
        ProtocolConfig config = globalConfig.getProtocol(protocol);
        assertNotNull("Could find protocol " + protocol, config);
        ParserUtils.fillDefault(config.getMessage(msgType), message);
        messageMap.put(messageName, message);
    }

    /**
     * Method to add a message to the queue to be sent
     *
     * @param messageName name to index the message
     * @param message     message implementation to be sent
     */
    public void addMessage(String messageName, Cdr message) {
        messageMap.put(messageName, message);
    }

    void addResponse(String responseName, Cdr message) {
        responseMap.put(responseName, message);
    }

    /**
     * Method to send a message by name using a client.
     *
     * @param messageName the key to find the message from message map.
     * @param clientName  the key to find the client from client map.
     */
    public void sendMessage(String messageName, String clientName) throws EtiqetException {
        // Get the stored client, it must be logged on.
        sendMessage(messageName, clientName, null);
    }

    /**
     * Method to send a message by name using a client.
     *
     * @param messageName the key to find the message from message map.
     * @param clientName  the key to find the client from client map.
     * @param sessionName string identifier for the session.
     */
    public void sendMessage(String messageName, String clientName, String sessionName) throws EtiqetException {
        // Get the stored message by name.
        Cdr message = getSentMessage(messageName);
        assertNotNull("sendMessage:Message '" + messageName + "' must exist", message);

        // Get the stored client, it must be logged on.
        Client client = getClient(clientName);
        assertNotNull(String.format(ERROR_CLIENT_NOT_FOUND, clientName), client);

        // Get the session identifier by default
        if (sessionName == null) {
            sessionName = client.getDefaultSessionId();
        }

        // Send the message using the client.
        client.send(message, sessionName);
    }

    public void waitForResponse(String messageType, String clientName, int milliseconds) throws EtiqetException {
        waitForResponseOfType(DEFAULT_MESSAGE_NAME, clientName, messageType, milliseconds);
    }

    public void waitForResponse(String messageName, String clientName) throws EtiqetException {
        waitForResponse(messageName, clientName, 5000);
    }

    public void waitForResponseOfType(String messageName, String clientName, String messageType, int milliseconds)
            throws EtiqetException {
        Client client = getClient(clientName);
        assertNotNull(String.format(ERROR_CLIENT_NOT_FOUND, clientName), client);

        long timeout = System.currentTimeMillis() + milliseconds;
        do {
            int remainingMs = Math.max(1, (int) (timeout - System.currentTimeMillis()));
            Cdr rsp = client.waitForMsgType(messageType, remainingMs);
            String receivedMsgType = client.getMsgName(rsp.getType());

            assertNotNull("Dictionary does not contain a definition for received message type '" + rsp.getType() + "'",
                            receivedMsgType);
            if (!filteredMsgs.contains(receivedMsgType)) {
                if (!DEFAULT_MESSAGE_NAME.equals(messageType)) {
                    handleError("Expected message '" + messageType + "' but found message '" + rsp.getType() + "'.",
                            (receivedMsgType.equals(messageType)), "NoCorrectResponseException");
                }
                LOG.info("Validating msg of type: " + messageType);
                client.validateMsg(receivedMsgType, rsp);
                addResponse(messageName, rsp);
                return;
            } else {
                LOG.warn("Filtering received message " + receivedMsgType);
            }
        } while (true);
    }

    public void waitForResponseOfType(String messageName, String clientName, String msgType) throws EtiqetException {
        waitForResponseOfType(messageName, clientName, msgType, 5000);
    }

    public void waitForNoResponse(String messageName, String clientName, String messageType, int milliseconds)
            throws EtiqetException {
        Client client = getClient(clientName);
        assertNotNull(String.format(ERROR_CLIENT_NOT_FOUND, clientName), client);

        long timeout = System.currentTimeMillis() + milliseconds;
        do {
            int remainingMs = Math.max(1, (int) (timeout - System.currentTimeMillis()));
            Cdr rsp = client.waitForNoMsgType(messageType, remainingMs);
            if (rsp != null) {
                LOG.error("Expected no message response but received: " + rsp);
            }
            addResponse(messageName, rsp);
            return;
        } while (true);
    }

    public void waitForNoResponse(String messageName, String clientName, String messageType)
            throws EtiqetException {
        waitForNoResponse(messageName, clientName, messageType, 5000);
    }

    public void validateMessageTypeExistInResponseMap(String messageName) {
        assertNotNull(getResponse(messageName));
    }

    Cdr getResponse(String messageName) {
        return responseMap.get(messageName);
    }

    public void validateMessageTypeDoesNotExistInResponseMap(String messageName) {
        assertNull(getResponse(messageName));
    }

    List<RuntimeException> getExceptions() {
        return exceptionsList;
    }

    public void expectException() {
        expectException = true;
    }

    public void resetExpectException() {
        expectException = false;
    }

    public void checkForExceptions() {
        resetExpectException();
        assertTrue("There should be exceptions to check for, none were found", !this.getExceptions().isEmpty());
    }

    public void addException(RuntimeException e, String cukeException) {
        if (!expectException && !e.toString().endsWith(cukeException)) {
            LOG.error("Unexpected Exception: " + e);
            throw e;
        }
        exceptionsList.add(e);
        LOG.info("Exception caught: " + e);
    }

    public void checkResponseKeyPresenceAndValue(String messageName, String params) {
        // Check if there are some params to check
        assertTrue("checkResponseKeyPresenceAndValue: Nothing to match", !StringUtils.isNullOrEmpty(params));

        String preTreatedParams = preTreatParams(params);

        Cdr response = getResponse(messageName);
        assertNotNull("checkResponseKeyPresenceAndValue: response for " + messageName + " not found", response);

        // Check if all params are into response message
        Map<String,String> notMatched = checkMsgContainsKeysAndValues(response, preTreatedParams);
        for(Map.Entry<String, String> entry : notMatched.entrySet()) {
            assertTrue("checkResponseKeyPresenceAndValue: "+ messageName + " Msg: '"  + entry.getKey() + "' found, expected: '" + entry.getValue() +"'", StringUtils.isNullOrEmpty(entry.getKey()));
        }
    }

    public void checkFieldPresence(String messageName, String params) {
        // Check if there are some params to check
        assertTrue("checkFieldPresence: Nothing to match", !StringUtils.isNullOrEmpty(params));

        Cdr response = getResponse(messageName);
        assertNotNull("checkFieldPresence: response for " + messageName + " not found", response);

        String notMatched = checkMsgContainsKeys(response, params);
        assertTrue("checkResponseKeyPresenceAndValue: params '" + notMatched + "' don't match with message " + messageName, StringUtils.isNullOrEmpty(notMatched));
    }

    public void stopClient(String clientName) {
        Client client = getClient(clientName);
        assertNotNull(String.format(ERROR_CLIENT_NOT_FOUND, clientName), client);

        client.stop();
    }

    /**
     * Adds a filter to skip the given message.
     *
     * @param msgType the message to be filtered out.
     */
    public void filterMessage(String msgType) {
        filteredMsgs.add(msgType);
    }

    /**
     * Removes a message from being filtered.
     *
     * @param msgType the message to be removed from the filtering.
     */
    public void removeFromFiltered(String msgType) {
        filteredMsgs.remove(msgType);
    }

    /**
     * Remove all of the messages to be filterd out.
     */
    public void cleanFilteredMsgs() {
        filteredMsgs.clear();
    }

    /**
     * Removes the given message name from the (sent) messageMap
     *
     * @param messageName name of the message to be removed
     */
    public void removeSentMessage(String messageName) {
        messageMap.remove(messageName);
    }

    /**
     * Removes the default message from the (sent) messageMap)
     */
    public void removeDefaultSentMessage() {
        removeSentMessage(DEFAULT_MESSAGE_NAME);
    }

    /**
     * Method to check if a list of param=value is into a message.
     *
     * @param msg    message to check.
     * @param params list of params
     * @return string with param_value that don't match.
     */
    private Map<String, String> checkMsgContainsKeysAndValues(Cdr msg, String params) {
        Map<String,String> unmatched = new HashMap<>();
        String[] pairs = params.trim().split(Separators.PARAM_SEPARATOR);
        if (pairs.length > 0) {
            for (String expected : pairs) {
                String[] keyValue = expected.split(Separators.KEY_VALUE_SEPARATOR);

                String key = keyValue[0];
                String value = keyValue[1];

                String msgValue = getValueFromTree(key, msg);
                if (!value.equals(msgValue)) {
                    unmatched.put(String.format("%s=%s", key,msgValue ),expected);
                }
            }
        }
        return unmatched;
    }

    /**
     * Method to check if a list of params comma separated is in message.
     *
     * @param msg  message to check
     * @param list list of params comma separated.
     * @return params that are not in the message.
     */
    private String checkMsgContainsKeys(Cdr msg, String list) {
        StringBuilder notMatched = new StringBuilder();

        String[] params = list.trim().split(Separators.PARAM_SEPARATOR);

        if (params.length > 0) {
            for (String param : params) {

                if (!msg.containsKey(param)) {
                    if (!StringUtils.isNullOrEmpty(notMatched.toString())) {
                        notMatched.append(Separators.PARAM_SEPARATOR);
                    }
                    notMatched.append(param);
                }
            }
        }
        return notMatched.toString();
    }

    /**
     * Method to create a response to a message by name into a response list matching a field value.
     *
     * @param responseName name to give to the found response.
     * @param messageName  name of the sent message.
     * @param responseList list of responses.
     * @param fieldName    name of the field to match
     */
    public void getResponseToMessageFromListByField(
        String responseName,
        String messageName,
        String responseList,
        String fieldName
    ) {
        Cdr sentMessage = getSentMessage(messageName);
        assertNotNull("Could not find sent message '" + messageName + "'", sentMessage);

        String value = (String) ParserUtils.getTagValueFromCdr(fieldName, sentMessage);
        assertTrue("Field '" + fieldName + "' wasn't found in message '" + messageName + "'",
                !StringUtils.isNullOrEmpty(value));
        LOG.info("Found value '{}' from field '{}' in message named '{}'", value, fieldName, messageName);

        String[] responseListArray = responseList.trim().split(Separators.PARAM_SEPARATOR);

        List<Cdr> candidateResponses = responseMap.entrySet().stream()
                .filter(map -> (Arrays.asList(responseListArray)).contains(map.getKey())
                        && map.getValue().containsKey(fieldName) && value.equals(map.getValue().getAsString(fieldName)))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
        assertTrue("There are not matches for responses '" + responseList + "' with same value of Field '" + fieldName + "' as message '" + messageName + "'",
                !ArrayUtils.isNullOrEmpty(candidateResponses));
        LOG.info("Matched response message: " + candidateResponses.get(0).toString());
        addResponse(responseName, candidateResponses.get(0));
    }

    /**
     * Method to check if a list of params has the same value in a list of messages.
     *
     * @param paramList   list of params comma separated.
     * @param messageList list of messages comma separated.
     */
    public void checkThatListOfParamsMatchInListOfMessages(String paramList, String messageList) {
        assertFalse("checkThatListOfParamsMatchInListOfMessages: No params to check",
                        StringUtils.isNullOrEmpty(paramList));
        assertFalse("checkThatListOfParamsMatchInListOfMessages: No messages where to find matches",
                        StringUtils.isNullOrEmpty(messageList));

        StringBuilder messageCheck = new StringBuilder();

        String[] params = paramList.trim().split(Separators.PARAM_SEPARATOR);
        String[] messages = messageList.trim().split(Separators.PARAM_SEPARATOR);
        String[][] messageFieldValues = new String[params.length][messages.length];

        for (int indexMessages = 0; indexMessages < messages.length; indexMessages++) {
            String messageName = messages[indexMessages];
            Cdr message = getSentMessage(messageName);

            // If messageName is not stored in sent messages, try to create from responses.
            if (message == null) {
                message = getResponse(messageName);
            }
            // If messageName is not stored in either messages or responses, flag it as not found
            if (message == null) {
                messageCheck.append(messageName)
                            .append(Separators.PARAM_SEPARATOR);
            } else {
                for (int indexParams = 0; indexParams < params.length; indexParams++) {
                    messageFieldValues[indexParams][indexMessages] = message.getAsString(params[indexParams]);
                }
            }
        }

        String messagesNotFound = StringUtils.removeTrailing(messageCheck.toString(), Separators.PARAM_SEPARATOR);

        String noMatch = checkParamsMatch(params, messageFieldValues);
        assertFoundOrExists(
            !StringUtils.isNullOrEmpty(messagesNotFound),
            !StringUtils.isNullOrEmpty(noMatch),
            messagesNotFound,
            noMatch,
            messageList
        );
    }

    /**
     * Checks whether fields in a series of messages match
     * @param params fields to check
     * @param messageFieldValues message / value array
     * @return String containing the unmatched fields
     */
    public String checkParamsMatch(String[] params, String[][] messageFieldValues){
        int paramIndex = 0;
        StringBuilder noMatch = new StringBuilder();
        while (paramIndex < params.length) {
            String[] paramsValues = messageFieldValues[paramIndex];
            List<String> distinctParamValue = Arrays.stream(paramsValues)
                                                    .filter(x -> x != null && !x.equals(paramsValues[0]))
                                                    .collect(Collectors.toList());
            if(!ArrayUtils.isNullOrEmpty(distinctParamValue)) {
                noMatch.append(params[paramIndex])
                       .append(Separators.PARAM_SEPARATOR);
            }
            paramIndex++;
        }

        return StringUtils.removeTrailing(noMatch.toString(), Separators.PARAM_SEPARATOR);
    }
    /**
     * Determine if the both messages are the same and if so, do the params match
     * @param messagesNoExist Whether any messages where not found
     * @param noMatchFound Whether any fiels weren't matched
     * @param messageCheck messages that weren't found
     * @param noMatch fields that weren't matched
     * @param messageList messages that the fields in noMatch were found in
     */
    void assertFoundOrExists(
        boolean messagesNoExist,
        boolean noMatchFound,
        String messageCheck,
        String noMatch,
        String messageList
    ) {
        assertTrue((messagesNoExist ? ("Messages '" + messageCheck + "' do not exist ") : "")
                        + (messagesNoExist && noMatchFound ? "and " : "")
                        + (noMatchFound ? ("Params '" + noMatch + "' do not match in messages '" + messageList + "'") : "")
                , !messagesNoExist && !noMatchFound);
    }


    /**
     *
     * @param params comma separated field comparisons
     */
    void checkThatMessageParamsMatch(String params) {

        assertFalse("checkThatMessageParamsMatch: Nothing to check", StringUtils.isNullOrEmpty(params));
        StringBuilder noMatch = new StringBuilder();
        String[] couples = params.trim().split(Separators.PARAM_SEPARATOR);

        for (String couple : couples) {
            boolean match;
            if (couple.contains(Separators.KEY_VALUE_SEPARATOR)) {
                String[] messageParams = couple.trim().split(Separators.KEY_VALUE_SEPARATOR);
                String messageParam1 = messageParams[0];
                String messageParam2 = messageParams[1];
                if (!messageParam1.contains(Separators.LEVEL_SEPARATOR) || !messageParam2.contains(Separators.LEVEL_SEPARATOR)) {
                    match = false;
                } else {
                    match = isMatched(messageParam1, messageParam2);
                }
            } else {
                match = false;
            }

            if (!match) {
                noMatch.append(couple).append(Separators.PARAM_SEPARATOR);
            }

        }

        String result = StringUtils.removeTrailing(noMatch.toString(), Separators.PARAM_SEPARATOR);
        assertTrue("No matches found in '" + result + "'", StringUtils.isNullOrEmpty(result));

    }

    private boolean isMatched(String messageParam1, String messageParam2){
        boolean match = true;
        String[] messageParam1Array = messageParam1.trim().split(Separators.LEVEL_SEPARATOR, 2);
        String messageName1 = messageParam1Array[0];
        String paramName1 = messageParam1Array[1];

        String[] messageParam2Array = messageParam2.trim().split(Separators.LEVEL_SEPARATOR, 2);
        String messageName2 = messageParam2Array[0];
        String paramName2 = messageParam2Array[1];

        Cdr message1 = getSentMessage(messageName1);
        Cdr message2 = getSentMessage(messageName2);
        if (message1 == null || message2 == null) {
            match = false;
        } else {
            String value1 = (String) ParserUtils.getFullTagValueFromCdr(paramName1, message1);
            String value2 = (String) ParserUtils.getFullTagValueFromCdr(paramName2, message2);
            if (StringUtils.isNullOrEmpty(value1) || StringUtils.isNullOrEmpty(value2) || !value1.equals(value2)) {
                match = false;
            }
        }
        return match;
    }

    public void consumeNamedResponse(String responseName) {
        assertTrue("Reponse " + responseName + " does not exist", responseMap.containsKey(responseName));
        responseMap.remove(responseName);
    }

    public void startHandleExceptions() {
        tryOn = true;
    }

    public void checkHandledExceptions(String exceptionList) {
        assertTrue("The scenario is not configured to handle exeptions, use 'try' verb before execute critical verbs", tryOn);
        String[] exceptionsKeys = exceptionList.split(Pattern.quote(","));

        assertTrue("No exceptions to check", exceptionsKeys.length > 0);

        Iterator<String> ite = exceptionMap.keySet().iterator();
        boolean anyMatch = false;
        StringBuilder out = new StringBuilder();
        while (ite.hasNext()) {
            String key = ite.next();
            List<String> messages = exceptionMap.get(key);
            if (!ArrayUtils.isNullOrEmpty(messages)) {
                anyMatch = true;
                out.append("\r\n *").append(key).append(":");
                for (String message : messages) {
                    out.append("\r\n\t -").append(message);
                }
            }
        }
        if (anyMatch) {
            LOG.info("Check for errors Result: " + out.toString());
            tryOn = false;
        } else {
            fail("No errors matching: " + exceptionList);
        }
    }

    private void handleError(String message, boolean condition, String error) {
        if (!condition) {
            if (tryOn) {
                exceptionMap.computeIfAbsent(error, k -> new ArrayList<>()).add(message);
            } else {
                fail(message);
            }
        }
    }

    public void closeAllClients() {
        try {
            // Stop all clients using lambda expressions.
            if (!ArrayUtils.isNullOrEmpty(clientMap)) {
                clientMap.values().forEach(Client::stop);
            }
        } catch (Exception lex) {
            // Log the caught exception.
            LOG.error("Error closing clients", lex);
        }
    }

    public void closeAllServers() {
        try {
            if(!ArrayUtils.isNullOrEmpty(serverMap)) {
                serverMap.values().forEach(Server::stopServer);
            }
        } catch (Exception e) {
            LOG.error("Error closing servers", e);
        }
    }

    public void closeServer(String name) {
        Server server = getServer(name);
        if(server != null) {
            server.stopServer();
        }
    }

    Server getServer(String name) {
        return serverMap.get(name);
    }

    public void setActions(String clientName, String actions) throws EtiqetException {
        getClient(clientName).setActions(actions.split(","));
    }

    public void resetActions(String clientName) throws EtiqetException {
        getClient(clientName).setActions(new String[]{});
    }

    public void checkExtensionsEnabled(String clientName) throws EtiqetException{
        if (StringUtils.isNullOrEmpty(getClient(clientName).getExtensionsUrl())){
            throw new EtiqetException("Extensions are not enabled - please enable to use this function");
        }
    }

    String getJson(String exchange, String auctionPhase){
        Map<String, String> map = new HashMap<>();
        map.put("exchange", exchange);
        map.put("phase", auctionPhase);
        Gson gson = new Gson();
        return gson.toJson(map);
    }

    Map<String,String> getDefaultHeader(){
        Map<String, String> map = new HashMap<>();
        map.put("Content-Type", "application/json");
        return map;
    }

    public void sendNamedRestMessageWithPayloadHeaders
            (String httpVerb, Map<String, String> headers, String payload, String endpoint, String clientName)
    throws EtiqetException, IOException {
        if(endpoint == null) {
            throw new EtiqetException("Cannot send REST request without an endpoint");
        }

        String extensionsUrl = getClient(clientName).getExtensionsUrl();
        assertFalse("No Extensions URL found in client " + clientName, StringUtils.isNullOrEmpty(extensionsUrl));

        URL url = getFullExtensionsUrl(extensionsUrl, endpoint);
        HttpURLConnection con;
        if(url.toString().startsWith("https")){
            con = (HttpsURLConnection) url.openConnection();
        }
        else {
            con = (HttpURLConnection) url.openConnection();
        }
        assertNotNull("Couldn't open an HTTP connection", con);
        con.setDoOutput(true);
        con.setRequestMethod(httpVerb);
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            con.setRequestProperty(entry.getKey(), entry.getValue());
        }

        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(payload);
        wr.flush();
        wr.close();

        if (con.getResponseCode() != 200){
            String resMsg = con.getResponseMessage();
            throw new EtiqetException("Did not receive 200 (OK) response. Response from server: " + resMsg);
        }
        LOG.info("Rest request: " + httpVerb + " to: " + extensionsUrl + endpoint + payload );
    }

    /**
     * Provides the fully qualified URL object for opening a connection. Abstracted for help with unit testing
     * @param extensionsUrl base URL for the extensions
     * @param endpoint Endpoint to hit
     * @return fully qualified URL
     * @throws MalformedURLException when URL is malformed
     */
    URL getFullExtensionsUrl(String extensionsUrl, String endpoint) throws MalformedURLException {
        return new URL(String.format("%s%s", extensionsUrl, endpoint));
    }

    public void checkPrecision(int precision, String timestamp) {
        if(precision == 0 && timestamp.contains(".")){
            assertFalse("Precision doesn't match, expected 0 precision after seconds", timestamp.length()-1> timestamp.indexOf('.'));
        } else if (precision!=0){
            int calcPrecision = (timestamp.length()-1) - timestamp.lastIndexOf('.');
            assertEquals("Precision doesn't match -", precision, calcPrecision);
        }
    }

    public void checkTimeStampPrecision(String field, String messageName, String precision) {
        if (StringUtils.isNullOrEmpty(precision))
            fail("Level of time precision must be provided e.g 0,3,6,9,second,milli,micro or nano");

        int timePrecision = -1;
        try {
            timePrecision = Integer.parseInt(precision);
        } catch(NumberFormatException e) {
            switch(precision.toLowerCase()){
                case SECONDS_NAME:
                    timePrecision = SECONDS_TIME_VALUE;
                    break;
                case MILLI_TIME_NAME:
                    timePrecision = MILLI_TIME_VALUE;
                    break;
                case MICRO_TIME_NAME:
                    timePrecision = MICRO_TIME_VALUE;
                    break;
                case NANO_TIME_NAME:
                    timePrecision = NANO_TIME_VALUE;
                    break;
                default:
                    fail("Level of time precision must be provided e.g 0,3,6,9,second,milli,micro or nano");
            }
        }

        Cdr response = getResponse(messageName);
        assertNotNull(String.format("Couldn't find message: %s", messageName), response);
        checkPrecision(timePrecision, response.getItem(field).toString());
    }
}
