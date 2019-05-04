package com.neueda.etiqet.core.transport;

import com.neueda.etiqet.core.client.delegate.ClientDelegate;
import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import com.neueda.etiqet.core.message.cdr.Cdr;

public class EchoTransport<T> implements Transport {

  ClientDelegate delegate;
  Codec<Cdr, T> codec;
  TransportDelegate<String, Cdr> transDel;

  @Override
  public void init(String config) {

  }

  @Override
  public void stop() {

  }

  @Override
  public void send(Cdr msg) throws EtiqetException {
    send(msg, getDefaultSessionId());
  }

  @Override
  public void send(Cdr msg, String sessionId) throws EtiqetException {
    transDel.toApp(codec.decode(codec.encode(msg)), sessionId);
  }

  @Override
  public boolean isLoggedOn() {
    return true;
  }

  @Override
  public String getDefaultSessionId() {
    return "ECHO-SESSION";
  }

  @Override
  public void setTransportDelegate(TransportDelegate<String, Cdr> transDel) {
    this.transDel = transDel;
  }

  @Override
  public Codec getCodec() {
    return codec;
  }

  @Override
  public void setCodec(Codec c) {
    this.codec = c;
  }

  @Override
  public ClientDelegate getDelegate() {
    return delegate;
  }

  @Override
  public void setDelegate(ClientDelegate delegate) {
    this.delegate = delegate;
  }
}
