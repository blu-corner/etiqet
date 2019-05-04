package com.neueda.etiqet.rest.client;

import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.neueda.etiqet.core.client.Client;
import com.neueda.etiqet.core.client.delegate.SinkClientDelegate;
import com.neueda.etiqet.core.common.Environment;
import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import com.neueda.etiqet.core.config.GlobalConfig;
import com.neueda.etiqet.core.config.dtos.Message;
import com.neueda.etiqet.core.message.cdr.Cdr;
import com.neueda.etiqet.core.message.config.ProtocolConfig;
import com.neueda.etiqet.core.util.Config;
import com.neueda.etiqet.core.util.PropertiesFileReader;
import com.neueda.etiqet.rest.config.RestConfigConstants;
import com.neueda.etiqet.rest.message.impl.HttpRequestMsg;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestClient extends Client {

  private static final Logger LOG = LoggerFactory.getLogger(RestClient.class);

  private HttpRequestFactory requestFactory;

  private String activeConfig;

  public RestClient(String clientConfig) throws EtiqetException {
    this(clientConfig, null);
  }

  public RestClient(String primaryConfig, String secondaryConfig) throws EtiqetException {
    this(primaryConfig, secondaryConfig,
        GlobalConfig.getInstance().getProtocol(RestConfigConstants.DEFAULT_PROTOCOL_NAME));
  }

  public RestClient(String primaryClientConfig, String secondaryClientConfig,
      ProtocolConfig protocol)
      throws EtiqetException {
    super(primaryClientConfig, secondaryClientConfig, protocol);
    // Delegate isn't needed for the rest client to pre-process the messages
    setDelegate(new SinkClientDelegate());
    this.activeConfig = primaryClientConfig;
  }

  @Override
  public void launchClient(String configPath) throws EtiqetException {
    super.launchClient(configPath);
    requestFactory = getHttpRequestFactory();
  }

  @Override
  public void send(Cdr msg) throws EtiqetException {
    if (getRequestFactory() == null) {
      launchClient();
    }

    HttpRequestMsg httpReqMsg = (HttpRequestMsg) transport.getCodec().encode(msg);
    try {
      String baseUrl = getClientConfig().getString("baseUrl");
      httpReqMsg.setResponse(httpReqMsg.createHttpRequest(requestFactory, baseUrl).execute());
      msgQueue.add((Cdr) transport.getCodec().decode(httpReqMsg));
    } catch (IOException e) {
      LOG.error("Error sending HTTP Request", e);
      throw new EtiqetException("Error sending HTTP Request", e);
    }
  }

  /**
   * Sends a message based on the client / message configuation
   *
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
   *
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
    msgQueue.clear();
  }

  @Override
  public boolean isLoggedOn() {
    return false;
  }

  @Override
  public String getMsgType(String messageType) {
    return getMsgName(messageType);
  }

  @Override
  public String getMsgName(String messageName) {
    String msgName;
    Message message = getProtocolConfig().getMessage(messageName);
    if (message != null) {
      msgName = message.getName();
    } else {
      msgName = messageName;
    }
    return msgName;
  }

  @Override
  public Cdr waitForNoMsgType(String msgType, Integer timeoutMillis) throws EtiqetException {
    return waitForNoMsg(msgQueue, timeoutMillis);
  }

  /**
   * Returns HttpRequestFactory for making the HTTP requests. Abstracted to function to allow for
   * easy stubbing
   *
   * @return HttpRequestFactory
   */
  HttpRequestFactory getHttpRequestFactory() {
    return new NetHttpTransport().createRequestFactory();
  }

  HttpRequestFactory getRequestFactory() {
    return requestFactory;
  }

  String getPrimaryConfig() {
    return primaryConfig;
  }

  String getSecondaryConfig() {
    return secondaryConfig;
  }
}
