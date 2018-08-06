package com.neueda.etiqet.rest.client;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.neueda.etiqet.core.client.Client;
import com.neueda.etiqet.core.client.delegate.SinkClientDelegate;
import com.neueda.etiqet.core.common.Environment;
import com.neueda.etiqet.core.common.cdr.Cdr;
import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import com.neueda.etiqet.core.config.GlobalConfig;
import com.neueda.etiqet.core.message.config.ProtocolConfig;
import com.neueda.etiqet.core.util.Config;
import com.neueda.etiqet.core.util.PropertiesFileReader;
import com.neueda.etiqet.rest.config.RestConfigConstants;
import com.neueda.etiqet.rest.json.JsonUtils;
import com.neueda.etiqet.rest.message.RestMsg;
import com.neueda.etiqet.rest.message.impl.HttpRequestMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

public class RestClient extends Client<HttpRequestMsg, String> {

    private static final Logger LOG = LoggerFactory.getLogger(RestClient.class);

    private HttpRequestFactory requestFactory;

    private String activeConfig;

    public RestClient(String clientConfig) throws EtiqetException {
        this(clientConfig, null);
    }

    public RestClient(String primaryConfig, String secondaryConfig) throws EtiqetException {
        this(primaryConfig, secondaryConfig, GlobalConfig.getInstance().getProtocol(RestConfigConstants.DEFAULT_PROTOCOL_NAME));
    }

    public RestClient(String primaryClientConfig, String secondaryClientConfig, ProtocolConfig protocol)
    throws EtiqetException {
        super(primaryClientConfig, secondaryClientConfig, protocol);
        // Delegate isn't needed for the rest client to pre-process the messages
        this.delegate = new SinkClientDelegate<>();
        this.activeConfig = primaryClientConfig;
    }

    @Override
    public boolean isAdmin(String msgType) { return false; }

    @Override
    public void launchClient() throws EtiqetException {
        launchClient(primaryConfig);
    }

    @Override
    public void launchClient(String configPath) throws EtiqetException {
        requestFactory = getHttpRequestFactory();
        this.activeConfig = configPath;
        this.config = PropertiesFileReader.loadPropertiesFile(Environment.resolveEnvVars(activeConfig));
    }

    @Override
    public void failover() throws EtiqetException {
        if(this.activeConfig.equals(primaryConfig)) {
            launchClient(secondaryConfig);
        } else {
            launchClient();
        }
    }

    @Override
    public String getDefaultSessionId() { return ""; }

    @Override
    public void send(Cdr msg) throws EtiqetException {
        if(getRequestFactory() == null) {
            launchClient();
        }

        HttpRequestMsg httpRequestMsg = encode(msg);
        try {
            String baseUrl = getClientConfig().getString("baseUrl");
            HttpRequest request = httpRequestMsg.createHttpRequest(requestFactory, baseUrl);

            HttpResponse httpResponse = request.execute();
            Cdr responseData = JsonUtils.jsonToCdr(httpResponse.parseAsString());
            for(Map.Entry<String, Object> header : httpResponse.getHeaders().entrySet()) {
                responseData.set("$header." + header.getKey(), String.valueOf(header.getValue()));
            }

            msgQueue.add(new RestMsg(String.valueOf(httpResponse.getStatusCode())).update(responseData));
        } catch (IOException e) {
            LOG.error("Error sending HTTP Request", e);
            throw new EtiqetException("Error sending HTTP Request", e);
        }
    }

    /**
     * Sends a message based on the client / message configuation
     * @param msg message to be sent
     * @param sessionId Not used by RestClient
     * @throws EtiqetException when an error is thrown sending the message
     */
    @Override
    public void send(Cdr msg, String sessionId) throws EtiqetException {
        send(msg);
    }

    /**
     * Gets the client configuration from the {@link #activeConfig} path
     * @return client configuration
     */
    Config getClientConfig() {
        return this.config;
    }

    @Override
    public Cdr waitForMsgType(String msgType, Integer timeoutMillis) throws EtiqetException {
        return waitForMsg(msgQueue, timeoutMillis);
    }

    @Override
    public void stop() {
        /**
         * Required to meet extension req's however unrequired method therefore not currently implemented
         */
    }

    @Override
    public boolean isLoggedOn() { return false; }

    @Override
    public String getMsgType(String messageName) {
        return getProtocolConfig().getMsgType(messageName);
    }

    @Override
    public String getMsgName(String messageType) {
        return getProtocolConfig().getMsgName(messageType);
    }

    @Override
    public Cdr waitForNoMsgType(String msgType, Integer timeoutMillis) throws EtiqetException {
        return waitForNoMsg(msgQueue, timeoutMillis);
    }

    /**
     * Returns HttpRequestFactory for making the HTTP requests. Abstracted to function to allow for easy stubbing
     * @return HttpRequestFactory
     */
    HttpRequestFactory getHttpRequestFactory() {
        return new NetHttpTransport().createRequestFactory();
    }

    public HttpRequestFactory getRequestFactory() {
        return requestFactory;
    }

    @Override
    public HttpRequestMsg encode(Cdr message) throws EtiqetException {
        return new RestMsg(message.getType()).serialize(message);
    }

    @Override
    public Cdr decode(HttpRequestMsg message) throws EtiqetException {
        return msgQueue.iterator().next();
    }
}
