package com.neueda.etiqet.core.transport;

import com.neueda.etiqet.core.client.delegate.ClientDelegate;
import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import com.neueda.etiqet.core.message.cdr.Cdr;

public interface Transport {

  /**
   * Initialise the transport.
   * @param config the configuration file path for the transport.
   * @throws EtiqetException
   */
  void init(String config) throws EtiqetException;

  /**
   * Starts the transport
   * @throws EtiqetException
   */
  default void start() throws EtiqetException {}

  /**
   * Stops the transport.
   */
  void stop();

  /**
   * This method sends a message to a remote party.
   * @param msg message to be sent
   * @throws EtiqetException exception.
   */
  void send(Cdr msg) throws EtiqetException;

  /**
   * This method sends a message to a remote party through the given session.
   * @param msg message to be sent
   * @param sessionId String containing the session identifier or null to create the session by default.
   * @throws EtiqetException exception.
   */
  void send(Cdr msg, String sessionId) throws EtiqetException;

  /**
   * Returns true if the transport's connection has been established, false otherwise.
   * @return true if the transport's connection has been established, false otherwise
   */
  boolean isLoggedOn();

  String getDefaultSessionId();

  /**
   * Sets the transport delegate to attend incoming and outgoing messages.
   * @param transDel the transport delegate
   */
  void setTransportDelegate(TransportDelegate<String, Cdr> transDel);

  /**
   * Sets the codec used by the transport.
   * @param c the codec used by the transport.
   */
  void setCodec(Codec c);

  Codec getCodec();

  void setDelegate(ClientDelegate delegate);

  ClientDelegate getDelegate();
}
