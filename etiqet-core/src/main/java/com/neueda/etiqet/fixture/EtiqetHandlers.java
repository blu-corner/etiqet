package com.neueda.etiqet.fixture;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.neueda.etiqet.core.client.Client;
import com.neueda.etiqet.core.client.ClientFactory;
import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import com.neueda.etiqet.core.config.GlobalConfig;
import com.neueda.etiqet.core.config.dtos.Field;
import com.neueda.etiqet.core.config.dtos.Message;
import com.neueda.etiqet.core.config.dtos.UrlExtension;
import com.neueda.etiqet.core.json.JsonUtils;
import com.neueda.etiqet.core.message.cdr.Cdr;
import com.neueda.etiqet.core.message.cdr.CdrItem;
import com.neueda.etiqet.core.message.config.ProtocolConfig;
import com.neueda.etiqet.core.server.Server;
import com.neueda.etiqet.core.server.ServerFactory;
import com.neueda.etiqet.core.util.ArrayUtils;
import com.neueda.etiqet.core.util.ParserUtils;
import com.neueda.etiqet.core.util.Separators;
import com.neueda.etiqet.core.util.StringUtils;
import gherkin.deps.com.google.gson.Gson;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.net.ssl.HttpsURLConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EtiqetHandlers {

    public static final String DEFAULT_CLIENT_NAME = "default";
    public static final String DEFAULT_MESSAGE_NAME = "default";
    public static final String DEFAULT_SERVER_NAME = "default";
    public static final String DEFAULT_PARAMS = "";
    public static final String DEFAULT_SESSION = null;
    public static final String DEFAULT_EXCEPTION = "default";
    public static final String RESPONSE = "response";
    public static final int NOT_SET = -999999999;
    public static final int MILLI_NANO_CONVERSION = 1000000;
    public static final String DEFAULT_EXTENSIONS_NAME = "neueda";
    public static final String HTTP_POST = "POST";
    public static final String PURGE_ORDERS = "requests/purge_orders";
    public static final String SET_TRADE_PHASE = "requests/set_trading_phase";
    public static final String REMOVE_ORDERS = "requests/remove_orders";
    public static final String HALT_ASSET = "requests/halt_asset";
    public static final String RESUME_ASSET = "requests/resume_asset";
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
    public static final String ERROR_FIELD_COMPARISON_MILLIS =
        ERROR_FIELD_COMPARISON + " by no more than %sms";
    public static final String CLIENT_THREAD_PREFIX = "clientThread-%s";
    private static final Logger LOG = LoggerFactory.getLogger(EtiqetHandlers.class);
    boolean expectException = false;
    Map<String, String> cukeVariables = new HashMap<>();
    GlobalConfig globalConfig;
    private List<RuntimeException> exceptionsList = new ArrayList<>();
    private Map<String, Client> clientMap = new HashMap<>();
    private Map<String, Server> serverMap = new HashMap<>();
    private NavigableMap<String, Cdr> messageMap = new TreeMap<>();

    private NavigableMap<String, Cdr> responseMap = new TreeMap<>();
    private NavigableMap<String, String> variableMap = new TreeMap<>();

    private Set<String> filteredMsgs = ConcurrentHashMap.newKeySet();

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
            if (!StringUtils.isNullOrEmpty(serverConfig)) {
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
            client = clientMap.containsKey(clientName) ? clientMap.get(clientName)
                : createClient(impl, clientName);
            client.setClientConfig(extraConfig, null);

            Thread thread = new Thread(client, String.format(CLIENT_THREAD_PREFIX, clientName));
            thread.start();

        } catch (EtiqetException e) {
            LOG.error(String.format(ERROR_CLIENT_NOT_STARTED, clientName), e.getCause());
        }
    }

    public void startClientWithFailover(String impl, String clientName, String primaryConfig,
                                        String secondaryConfig) {
        Client client;
        try {
            client = clientMap.containsKey(clientName) ? clientMap.get(clientName)
                : createClientWithFailover(impl, clientName, primaryConfig, secondaryConfig);
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
     * @param impl the name of the client type.
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
     */
    public Client createClientWithFailover(String clientType, String clientName, String primaryConfig,
                                           String secondaryConfig) throws EtiqetException {
        if (StringUtils.isNullOrEmpty(secondaryConfig)) {
            throw new EtiqetException(
                "Secondary Config must be provided when trying to create a client with failover capabilities");
        }
        Client client = ClientFactory.create(clientType, primaryConfig, secondaryConfig);
        addClient(clientName, client);
        return client;
    }

    /**
     * Cause the client to failover from Primary to Secondary config if enabled
     */
    public void failover(String clientName) throws EtiqetException {
        Client client = clientMap.get(clientName);
        if (client.canFailover()) {
            client.stop();
            client.failover();
            client.start();
        } else {
            throw new EtiqetException("Client: " + clientName + " not enabled for failover");
        }
    }

    /**
     * Adds a client to the clientMap. Abstracted to assist in unit testing
     *
     * @param clientName name of the client to be added
     * @param client client to be added
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
        Thread clientThread = new Thread(getClient(clientName),
            String.format(CLIENT_THREAD_PREFIX, clientName));
        clientThread.start();
    }

    public void startClient(String impl, String clientName) throws EtiqetException {
        Client client = clientMap.containsKey(clientName) ? clientMap.get(clientName)
            : createClient(impl, clientName);
        Thread clientThread = new Thread(client, String.format(CLIENT_THREAD_PREFIX, clientName));
        clientThread.start();
    }

    public void isClientLoggedOn(String name) {
        Client client = getClient(name);
        assertNotNull(String.format(ERROR_CLIENT_NOT_FOUND, name), client);

        boolean loggedOn = client.isLoggedOn();
        handleError(String.format(ERROR_CLIENT_NOT_LOGGED_ON, name), loggedOn,
            "ClientNoLoggedOnException");
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
        handleError("waitForClientLogon: timeout waiting for logon", client.waitForLogon(),
            "ClientNoLoggedOnException");
    }

    /**
     * Gets the value from a Cdr object based on a given key. Allows use of nested elements to return a value.
     *
     * @param tree string representing tree field (e.g. parent->child)
     * @param cdr Cdr object that
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

    private String checkArrayIndex(String param) {
        String result = "";
        try {
            int index = Integer.parseInt(param);
            if (index > -1) {
                result = param;
            }
        } catch (NumberFormatException e) {
            // Ignored
        }
        return result;
    }

    /**
     * Method to prepare params correctly before set them into message.
     *
     * @param params param list comma separated.
     * @return dealt param list.
     */
    String preTreatParams(String params) {
        StringBuilder preTreatedParams = new StringBuilder();

        if (StringUtils.isNullOrEmpty(params) || params.length() < 3) {
            // minimum 3 characters (e.g. x=y), or array index (e.g 0)
            return checkArrayIndex(params);
        }

        String[] parameList = params.trim().split(Separators.PARAM_SEPARATOR);
        for (String param : parameList) {
            if (!param.contains(Separators.KEY_VALUE_SEPARATOR)) {
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
     */
    private String searchRhs(String rhs) {
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
        if (StringUtils.isNullOrEmpty(dateString)) {
            return 0;
        }
        //Need to ensure the nano secs are full for correct manipulation
        String pad = "000000000";
        if (dateString.contains(".")) {
            dateString += pad;
        } else {
            dateString = dateString + "." + pad;
        }

        //first convert date portion to millis
        Date date;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HH:mm:ss");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            date = sdf.parse(dateString.substring(0, dateString.indexOf('.')));
        } catch (ParseException e) {
            LOG.error("Error parsing date: " + e);
            return -1;
        }

        //Remove the extra zeros from the nano portion
        long nano = Long
            .parseLong(dateString.substring(dateString.indexOf('.') + 1, dateString.indexOf('.') + 10));
        LOG.info(
            String.format("time: %d %s", date.getTime() * MILLI_NANO_CONVERSION + nano, dateString));
        //convert millis to nano by multipling by 1 million
        return date.getTime() * MILLI_NANO_CONVERSION + (nano);
    }

    public String getValueFromField(String messageAlias, String field) {
        String sValue = (preTreatParams("=" + messageAlias + "->" + field)).substring(1);
        assertNotNull(field + " in " + messageAlias + " is null.", sValue);
        assertFalse(field + " in " + messageAlias + " is an empty string.", sValue.equals(""));
        return sValue;
    }

    public void compareValuesEqual(String firstField, String firstMessageAlias, String secondField,
                                   String secondMessageAlias) {
        String sFirstValue = getValueFromField(firstMessageAlias, firstField);
        String sSecondValue = getValueFromField(secondMessageAlias, secondField);
        assertTrue("Test for equality: " + firstField + " in " + firstMessageAlias +
            " is " + sFirstValue + ", in " + secondMessageAlias +
            " it is " + sSecondValue, sFirstValue.equals(sSecondValue));
    }

    public void compareValuesNotEqual(String firstField, String firstMessageAlias, String secondField,
                                      String secondMessageAlias) {
        String sFirstValue = getValueFromField(firstMessageAlias, firstField);
        String sSecondValue = getValueFromField(secondMessageAlias, secondField);
        assertFalse("Test for inequality: " + firstField + " in " + firstMessageAlias +
            " is " + sFirstValue + ", in " + secondMessageAlias +
            " it is " + sSecondValue, sFirstValue.equals(sSecondValue));
    }

    public void compareValues(String firstField, String firstMessageAlias, String secondField,
                              String secondMessageAlias, Long millis) {
        String sFirstValue = getValueFromField(firstMessageAlias, firstField);
        assertFalse("Only timestamps and numeric values can be compared for greater/lesser than",
            sFirstValue.contains("[a-zA-Z]+"));
        String sSecondValue = getValueFromField(secondMessageAlias, secondField);
        assertFalse("Only timestamps and numeric values can be compared for greater/lesser than",
            sSecondValue.contains("[a-zA-Z]+"));
        double firstToCompare = NOT_SET;
        double secondToCompare = NOT_SET;
        try {
            firstToCompare = Double.parseDouble(sFirstValue);
            secondToCompare = Double.parseDouble(sSecondValue);
        } catch (NumberFormatException e) {
            compareTimestamps(sFirstValue, sSecondValue, firstField, firstMessageAlias, secondField,
                secondMessageAlias, millis);
        }
        if (firstToCompare != NOT_SET && secondToCompare != NOT_SET) {
            assertTrue(String.format(ERROR_FIELD_COMPARISON, firstField, firstMessageAlias, secondField,
                    secondMessageAlias),
                firstToCompare >= secondToCompare);
            if (millis != null) {
                assertTrue(String
                        .format(ERROR_FIELD_COMPARISON_MILLIS, firstField, firstMessageAlias, secondField,
                            secondMessageAlias, millis),
                    (firstToCompare - secondToCompare) < millis);
            }
        }
    }

    /**
     * Method to compare timestamps of two separate messages
     *
     * @param firstField field value to find from first message
     * @param firstMessageAlias alias to find first message
     * @param secondField field value to find from second message
     * @param secondMessageAlias alias to find second message
     * @param millis optional time-frame for checking response time
     */
    public void compareTimestamps(String firstValue, String secondValue, String firstField,
                                  String firstMessageAlias, String secondField, String secondMessageAlias, Long millis) {
        Long lFirstTimestamp = dateToNanos(firstValue);
        assertTrue("Could not read " + firstField + " in " + firstMessageAlias, lFirstTimestamp != -1);

        Long lSecondTimestamp = dateToNanos(secondValue);
        assertTrue("Could not read " + secondField + " in " + secondMessageAlias,
            lSecondTimestamp != -1);

        assertTrue(String.format(ERROR_FIELD_COMPARISON, firstField, firstMessageAlias, secondField,
                secondMessageAlias),
            lFirstTimestamp >= lSecondTimestamp);
        if (millis != null) {
            assertTrue(String
                    .format(ERROR_FIELD_COMPARISON_MILLIS, firstField, firstMessageAlias, secondField,
                        secondMessageAlias, millis),
                (lFirstTimestamp - lSecondTimestamp) < millis);
        }
    }

    /**
     * Method to extract the timestamp from a message and the timestamp from cucumber and convert to long millis
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
        try {
            lFirstValue = Long.parseLong(sFirstTimestamp);
            lSecondValue = Long.parseLong(sSecondTimestamp);
        } catch (NumberFormatException e) {
            lFirstValue = dateToNanos(sFirstTimestamp);
            lSecondValue = dateToNanos(sSecondTimestamp);
        }
        return new Long[]{lFirstValue, lSecondValue};
    }

    public void compareTimestampGreaterCukeVar(String field, String messageAlias,
                                               String sSecondTimestamp) {
        Long[] timestamps = extractTimestampAndCukeVar(field, messageAlias, sSecondTimestamp);
        assertTrue(timestamps[0] > timestamps[1]);
    }

    public void compareTimestampLesserCukeVar(String field, String messageAlias,
                                              String sSecondTimestamp) {
        Long[] timestamps = extractTimestampAndCukeVar(field, messageAlias, sSecondTimestamp);
        assertTrue(timestamps[0] < timestamps[1]);
    }

    public void compareTimestampEqualsCukeVar(String field, String messageAlias,
                                              String sSecondTimestamp) {
        String sFirstTimestamp = null == messageAlias ?
            responseMap.lastEntry().getValue().getAsString(field) :
            (preTreatParams("=" + messageAlias + "->" + field)).substring(1);
        if (sFirstTimestamp.equalsIgnoreCase("null")) {
            assertTrue(StringUtils.isNullOrEmpty(sSecondTimestamp));
        } else {
            assertEquals(sSecondTimestamp, sFirstTimestamp);
        }
    }

    /**
     * Method to validate timestamp from message against given format
     *
     * @param formatParam format inputted to match against timestamp
     * @param messageAlias alias of message to find on map
     * @param field field to find value of from message
     */
    public void validateTimestampAgainstFormatParam(String formatParam, String messageAlias,
                                                    String field) {
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
            String errorMsg =
                "Error parsing date from field " + field + " in message " + messageAlias + ".";
            LOG.error(errorMsg, e);
            fail(errorMsg);
        }
    }

    /**
     * Method to create a message.
     *
     * @param msgType message type
     * @param clientName the name of the client.
     * @param messageName name to index the message.
     * @param params list of params with pattern "field1=value1,field2=value2,...,fieldN=valueN"
     */
    public void createMessageForClient(String msgType, String clientName, String messageName, Optional<String> params) throws EtiqetException {
        String pretreatedParams;
        if (params.isPresent()) {
            pretreatedParams = handleScenarioVariables(params.get());
            pretreatedParams = preTreatParams(pretreatedParams);
        } else {
            pretreatedParams = DEFAULT_PARAMS;
        }
        Client client = getClient(clientName);
        ProtocolConfig config = client.getProtocolConfig();
        Cdr message = ParserUtils.stringToCdr(msgType, pretreatedParams);
        assertNotNull(String.format(ERROR_CLIENT_NOT_FOUND, clientName), client);
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

    public void createMessageFromFile(String fileName, String alias) throws EtiqetException {
        final String content;
        try {
            content = new String(Files.readAllBytes(Paths.get(fileName)));
        } catch (IOException e) {
            addException(new RuntimeException(e), EtiqetHandlers.DEFAULT_EXCEPTION);
            throw new EtiqetException(e);
        }
        Cdr message = JsonUtils.jsonToCdr(content);
        LOG.info("Payload: " + message);
        messageMap.put(alias, message);
    }

    /**
     * Method to create a message.
     *
     * @param msgType message type
     * @param protocol to send the message.
     * @param messageName name to index the message.
     * @param params list of params with pattern "field1=value1,field2=value2,...,fieldN=valueN"
     */
    public void createMessage(String msgType, String protocol, String messageName, String params)
        throws EtiqetException {
        ProtocolConfig config = globalConfig.getProtocol(protocol);
        String pretreatedParams = handleScenarioVariables(params);
        Cdr message = ParserUtils.stringToCdr(msgType, preTreatParams(pretreatedParams));
        assertNotNull("Could find protocol " + protocol, config);
        ParserUtils.fillDefault(config.getMessage(msgType), message);
        messageMap.put(messageName, message);
    }

    /**
     * Method to add a message to the queue to be sent
     *
     * @param messageName name to index the message
     * @param message message implementation to be sent
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
     * @param clientName the key to find the client from client map.
     */
    public void sendMessage(String messageName, String clientName) throws EtiqetException {
        // Get the stored client, it must be logged on.
        sendMessage(messageName, clientName, null);
    }

    /**
     * Method to send a message by name using a client.
     *
     * @param messageName the key to find the message from message map.
     * @param clientName the key to find the client from client map.
     * @param sessionName string identifier for the session.
     */
    public void sendMessage(String messageName, String clientName, String sessionName)
        throws EtiqetException {
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

    public void waitForResponse(String messageType, String clientName, int milliseconds)
        throws EtiqetException {
        waitForResponseOfType(DEFAULT_MESSAGE_NAME, clientName, messageType, milliseconds, false);
    }

    public void waitForResponse(String messageName, String clientName) throws EtiqetException {
        waitForResponse(messageName, clientName, 5000);
    }

    /**
     * Method to send a message by name using a client.
     *
     * @param messageName the key to store the message as the response map.
     * @param clientName the key to find the client from client map.
     * @param messageType the key to find the message from message map.
     * @param milliseconds milliseconds to wait
     * @param skipOther skip any other messages found in the queue, rather than stopping and failing the test.
     */
    public void waitForResponseOfType(String messageName, String clientName, String messageType,
                                      int milliseconds, boolean skipOther)
        throws EtiqetException {
        Client client = getClient(clientName);
        assertNotNull(String.format(ERROR_CLIENT_NOT_FOUND, clientName), client);

        long timeout = System.currentTimeMillis() + milliseconds;
        do {
            int remainingMs = Math.max(1, (int) (timeout - System.currentTimeMillis()));
            Cdr rsp = client.waitForMsgType(messageType, remainingMs);
            String receivedMsgType = rsp.getType();

            if (!filteredMsgs.contains(receivedMsgType) && (!skipOther || (skipOther && receivedMsgType
                .equals(messageType)))) {
                if (!DEFAULT_MESSAGE_NAME.equals(messageType)) {
                    handleError(
                        "Expected message '" + messageType + "' but found message '" + receivedMsgType + "'.",
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

    public void waitForResponseOfType(String messageName, String clientName, String msgType)
        throws EtiqetException {
        waitForResponseOfType(messageName, clientName, msgType, 5000, false);
    }

    public void waitForNoResponse(String messageName, String clientName, String messageType,
                                  int milliseconds)
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

    public void validateMessage(String messageName, String clientName, String messageType,
                                Boolean checkValuesMatch) {
        Client client = getClient(clientName);
        assertNotNull(String.format(ERROR_CLIENT_NOT_FOUND, clientName), client);

        Message message = client.getProtocolConfig().getMessage(messageType);
        if (message != null && message.getFields() != null && message.getFields() != null) {
            for (Field field : message.getFields()) {
                if (field != null && (field.getRequired() != null)
                    && (field.getRequired().equalsIgnoreCase("Y"))) {
                    checkResponseKeyPresenceAndValue(messageName, field.getName(), checkValuesMatch);
                }
            }
        }
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
        assertTrue("There should be exceptions to check for, none were found",
            !this.getExceptions().isEmpty());
    }

    public void addException(RuntimeException e, String cukeException) {
        if (!expectException && !e.toString().endsWith(cukeException)) {
            LOG.error("Unexpected Exception: " + e);
            throw e;
        }
        exceptionsList.add(e);
        LOG.info("Exception caught: " + e);
    }

    public void checkMessageContent(Cdr message, String params) {
        Cdr expectedCdr = ParserUtils.stringToCdr("NOTYPE", preTreatParams(params));
        expectedCdr.getItems().forEach(
            (key, value) -> assertEquals(value, message.getItem(key))
        );
    }

    public void checkResponseKeyPresenceAndValue(String messageName, String params,
                                                 List<String> values, String part, int position, boolean checkValuesMatch) {
        // Check if there are some params to check
        assertTrue("checkResponseKeyPresenceAndValue: Nothing to match",
            !StringUtils.isNullOrEmpty(params));

        String preTreatedParams = handleScenarioVariables(params);
        preTreatedParams = preTreatParams(preTreatedParams);

        Cdr response = getResponse(messageName);
        assertNotNull("checkResponseKeyPresenceAndValue: response for " + messageName + " not found",
            response);

        // Check if all params are into response message
        Map<String, String> notMatched = checkMsgContainsKeysAndValues(response, preTreatedParams,
            values, part, position, checkValuesMatch);
        for (Map.Entry<String, String> entry : notMatched.entrySet()) {
            assertTrue("checkResponseKeyPresenceAndValue: " + messageName + " Msg: '" + entry.getKey()
                    + "' found, expected: '" + entry.getValue() + "'",
                StringUtils.isNullOrEmpty(entry.getKey()));
        }
    }

    public void checkResponseKeyPresenceAndValue(String messageName, String params) {
        checkResponseKeyPresenceAndValue(messageName, params, null, null, -1, true);
    }

    public void checkResponseKeyPresenceAndValue(String messageName, String params,
                                                 boolean checkValuesMatch) {
        checkResponseKeyPresenceAndValue(messageName, params, null, null, -1, checkValuesMatch);
    }

    public void checkFieldPresence(String messageName, String params) {
        // Check if there are some params to check
        assertTrue("checkFieldPresence: Nothing to match", !StringUtils.isNullOrEmpty(params));

        Cdr response = getResponse(messageName);
        assertNotNull("checkFieldPresence: response for " + messageName + " not found", response);

        String notMatched = checkMsgContainsKeys(response, params);
        assertTrue(
            "checkResponseKeyPresenceAndValue: params '" + notMatched + "' don't match with message "
                + messageName, StringUtils.isNullOrEmpty(notMatched));
    }

    public void stopClient(String clientName) {
        Client client = getClient(clientName);
        assertNotNull(String.format(ERROR_CLIENT_NOT_FOUND, clientName), client);

        client.initiateStop();
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
     * @param msg message to check.
     * @param params list of params.
     * @param values optional list of values, will override values in the params.
     * @param split optional if set, split the value with this result.
     * @param position optional if split set, compare the result at this position from the split value.
     * @param checkValuesMatch check the values match, or only that it exists.
     * @return string with param_value that don't match.
     */
    private Map<String, String> checkMsgContainsKeysAndValues(Cdr msg, String params,
                                                              List<String> values, String split, int position, boolean checkValuesMatch) {
        Map<String, String> unmatched = new HashMap<>();
        String[] pairs = params.trim().split(Separators.PARAM_SEPARATOR);
        if (pairs.length > 0) {
            for (int i = 0; i < pairs.length; i++) {
                String[] keyValue = pairs[i].split(Separators.KEY_VALUE_SEPARATOR);

                String key = keyValue[0];
                String paramValue = keyValue.length == 2 ? keyValue[1] : null;
                String value = values == null ? paramValue : values.get(i);
                value = splitValue(value, split, position);

                String msgValue = splitValue(getValueFromTree(key, msg), split, position);

                if (msgValue == null || (checkValuesMatch && !value.equals(msgValue))) {
                    unmatched.put(String.format("%s=%s", key, msgValue), pairs[i]);
                }
            }
        }
        return unmatched;
    }

    /**
     * Method to split a value if the string to split on exists in the value, otherwise return the original value
     *
     * @param value string to split
     * @param split optional if set, split the value with this result.
     * @param position optional if split set, compare the result at this position from the split value.
     * @return params that are not in the message.
     */
    private String splitValue(String value, String split, int position) {
        if (!StringUtils.isNullOrEmpty(split) && position >= 0) {
            if (value.contains(split)) {
                value = value.split(split)[position];
            }
        }
        return value;
    }

    /**
     * Method to check if a list of params comma separated is in message.
     *
     * @param msg message to check
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
     * @param messageName name of the sent message.
     * @param responseList list of responses.
     * @param fieldName name of the field to match
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
        LOG.info("Found value '{}' from field '{}' in message named '{}'", value, fieldName,
            messageName);

        String[] responseListArray = responseList.trim().split(Separators.PARAM_SEPARATOR);

        List<Cdr> candidateResponses = responseMap.entrySet().stream()
            .filter(map -> (Arrays.asList(responseListArray)).contains(map.getKey())
                && map.getValue().containsKey(fieldName) && value
                .equals(map.getValue().getAsString(fieldName)))
            .map(Map.Entry::getValue)
            .collect(Collectors.toList());
        assertTrue(
            "There are not matches for responses '" + responseList + "' with same value of Field '"
                + fieldName + "' as message '" + messageName + "'",
            !ArrayUtils.isNullOrEmpty(candidateResponses));
        LOG.info("Matched response message: " + candidateResponses.get(0).toString());
        addResponse(responseName, candidateResponses.get(0));
    }

    /**
     * Method to check if a list of params has the same value in a list of messages.
     *
     * @param paramList list of params comma separated.
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

        String messagesNotFound = StringUtils
            .removeTrailing(messageCheck.toString(), Separators.PARAM_SEPARATOR);

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
     *
     * @param params fields to check
     * @param messageFieldValues message / value array
     * @return String containing the unmatched fields
     */
    public String checkParamsMatch(String[] params, String[][] messageFieldValues) {
        int paramIndex = 0;
        StringBuilder noMatch = new StringBuilder();
        while (paramIndex < params.length) {
            String[] paramsValues = messageFieldValues[paramIndex];
            List<String> distinctParamValue = Arrays.stream(paramsValues)
                .filter(x -> x != null && !x.equals(paramsValues[0]))
                .collect(Collectors.toList());
            if (!ArrayUtils.isNullOrEmpty(distinctParamValue)) {
                noMatch.append(params[paramIndex])
                    .append(Separators.PARAM_SEPARATOR);
            }
            paramIndex++;
        }

        return StringUtils.removeTrailing(noMatch.toString(), Separators.PARAM_SEPARATOR);
    }

    /**
     * Determine if the both messages are the same and if so, do the params match
     *
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
                + (noMatchFound ? ("Params '" + noMatch + "' do not match in messages '" + messageList
                + "'") : "")
            , !messagesNoExist && !noMatchFound);
    }


    /**
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
                if (!messageParam1.contains(Separators.LEVEL_SEPARATOR) || !messageParam2
                    .contains(Separators.LEVEL_SEPARATOR)) {
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

    private boolean isMatched(String messageParam1, String messageParam2) {
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
            if (StringUtils.isNullOrEmpty(value1) || StringUtils.isNullOrEmpty(value2) || !value1
                .equals(value2)) {
                match = false;
            }
        }
        return match;
    }

    public void consumeNamedResponse(String responseName) {
        assertTrue("Reponse " + responseName + " does not exist",
            responseMap.containsKey(responseName));
        responseMap.remove(responseName);
    }

    public void startHandleExceptions() {
        tryOn = true;
    }

    public void checkHandledExceptions(String exceptionList) {
        assertTrue(
            "The scenario is not configured to handle exeptions, use 'try' verb before execute critical verbs",
            tryOn);
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
                clientMap.values().forEach(Client::initiateStop);
            }
        } catch (Exception lex) {
            // Log the caught exception.
            LOG.error("Error closing clients", lex);
        }
    }

    public void closeAllServers() {
        try {
            if (!ArrayUtils.isNullOrEmpty(serverMap)) {
                serverMap.values().forEach(Server::stopServer);
            }
        } catch (Exception e) {
            LOG.error("Error closing servers", e);
        }
    }

    public void closeServer(String name) {
        Server server = getServer(name);
        if (server != null) {
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

    public void checkExtensionsEnabled(String extensionsName, String clientName) {
        assertNotNull("Extensions are not enabled - please enable to use this function",
            getNamedExtension(getClient(clientName).getUrlExtensions(), extensionsName));
    }

    String getJson(String exchange, String auctionPhase) {
        Map<String, String> map = new HashMap<>();
        map.put("exchange", exchange);
        map.put("phase", auctionPhase);
        Gson gson = new Gson();
        return gson.toJson(map);
    }

    String getExchangeSymbolJson(String exchange, String symbol) {
        Map<String, String> map = new HashMap<>();
        map.put("exchange", exchange);
        map.put("symbol", symbol);
        Gson gson = new Gson();
        return gson.toJson(map);
    }

    public Map<String, String> getDefaultHeader() {
        Map<String, String> map = new HashMap<>();
        map.put("Content-Type", "application/json");
        return map;
    }

    public UrlExtension getNamedExtension(List<UrlExtension> urlExtensions, String name) {
        assertFalse("No url extensions specified in client", ArrayUtils.isNullOrEmpty(urlExtensions));
        assertFalse("Client name required to elicit urlExtension", StringUtils.isNullOrEmpty(name));

        UrlExtension ext = urlExtensions.stream().
            filter(x -> x.getName().equalsIgnoreCase(name)).
            findFirst().orElse(null);
        assertNotNull(String.format("Extension: %s Not found", name), ext);

        return ext;
    }

    public UrlExtension getExtension(String clientName, String uri) {
        assertFalse("Must provide client name to acquire URL", StringUtils.isNullOrEmpty(clientName));
        assertFalse("Must provide extensions name to acquire URL", StringUtils.isNullOrEmpty(uri));
        Client client = getClient(clientName);
        assertFalse("Client not found: " + clientName, client == null);
        List<UrlExtension> extensions = client.getUrlExtensions();
        assertFalse("No Extensions URL found in client " + clientName, (extensions.isEmpty()));

        return getNamedExtension(extensions, uri);

    }

    public void sendNamedRestMessageWithPayloadHeaders
        (String httpVerb, Map<String, String> headers, String payload, String endpoint,
         UrlExtension extensionsUrl)
        throws EtiqetException, IOException {
        if (endpoint == null) {
            throw new EtiqetException("Cannot send REST request without an endpoint");
        }

        URL url = getFullExtensionsUrl(extensionsUrl.getUri(), endpoint);
        HttpURLConnection con;
        if (url.toString().startsWith("https")) {
            con = (HttpsURLConnection) url.openConnection();
        } else {
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

        if (con.getResponseCode() != 200) {
            String resMsg = con.getResponseMessage();
            throw new EtiqetException(
                "Did not receive 200 (OK) response. Response from server: " + resMsg);
        }
        LOG.info("Rest request: " + httpVerb + " to: " + extensionsUrl + endpoint + payload);
    }

    /**
     * Provides the fully qualified URL object for opening a connection. Abstracted for help with unit testing
     *
     * @param extensionsUrl base URL for the extensions
     * @param endpoint Endpoint to hit
     * @return fully qualified URL
     * @throws MalformedURLException when URL is malformed
     */
    public URL getFullExtensionsUrl(String extensionsUrl, String endpoint)
        throws MalformedURLException {
        return new URL(String.format("%s%s", extensionsUrl, endpoint));
    }

    public void checkPrecision(int precision, String timestamp) {
        if (precision == 0 && timestamp.contains(".")) {
            assertFalse("Precision doesn't match, expected 0 precision after seconds",
                timestamp.length() - 1 > timestamp.indexOf('.'));
        } else if (precision != 0) {
            int calcPrecision = (timestamp.length() - 1) - timestamp.lastIndexOf('.');
            assertEquals("Precision doesn't match -", precision, calcPrecision);
        }
    }

    public void checkTimeStampPrecision(String field, String messageName, String precision) {
        if (StringUtils.isNullOrEmpty(precision)) {
            fail("Level of time precision must be provided e.g 0,3,6,9,second,milli,micro or nano");
        }

        int timePrecision = -1;
        try {
            timePrecision = Integer.parseInt(precision);
        } catch (NumberFormatException e) {
            switch (precision.toLowerCase()) {
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

    /**
     * @param messageName key of the CDR message in responseMap
     * @param field Long field of CDR containing the bitmap to be checked
     * @param value Expected value of bits at given indexes
     * @param indexes Comma separated integers representing the indexes of the bitmap to check
     */
    void checkMessageNumericFieldBitValues(String messageName, String field, boolean value,
                                           String indexes) {
        long bitmap = getResponse(messageName).getItem(field).getIntval();

        List<Integer> parsedIndexs = new ArrayList<>();
        for (String indexString : indexes.split(",")) {
            parsedIndexs.add(Integer.parseInt(indexString.trim()));
        }

        for (Integer index : parsedIndexs) {
            assert (((1 << index & bitmap) != 0) == value) : String.format(
                "bit %s of number '%s' is not %s ", index, bitmap, value
            );
        }
    }

    public void checkFieldAbsence(String messageName, String params) {
        // Check if there are some params to check
        assertTrue("checkFieldPresence: Nothing to match", !StringUtils.isNullOrEmpty(params));

        Cdr response = getResponse(messageName);
        assertNotNull("checkFieldPresence: response for " + messageName + " not found", response);

        for (String param : params.trim().split(Separators.PARAM_SEPARATOR)) {
            assertTrue(!response.containsKey(param));
        }
    }


    public void setVariable(String variable, String content) {

        String messageValue;
        if (content.contains(Separators.LEVEL_SEPARATOR)) {
            messageValue = searchRhs(content);
        } else {
            messageValue = content;
        }

        this.variableMap.put(variable, getScenarioVariableContent(messageValue));
    }

    private String getScenarioVariableContent(String content) {
        while (content.contains("${")) {
            int startingIndex = content.indexOf("${") + 2;
            int endIndex = content.indexOf("}", startingIndex);
            String substring = content.substring(startingIndex, endIndex);
            String value = this.variableMap.get(substring);
            String subtituted = content.substring(startingIndex - 2, endIndex + 1);
            content = content.replace(subtituted, value);
        }
        return content;

    }

    protected String handleScenarioVariables(String preTreatedParams) {
        preTreatedParams = getScenarioVariableContent(preTreatedParams);

        if (this.variableMap.containsKey(preTreatedParams)) {
            preTreatedParams = this.variableMap.get(preTreatedParams);
        }
        return preTreatedParams;
    }
}
