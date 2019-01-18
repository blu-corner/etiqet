package com.neueda.etiqet.core.transport;

import com.neueda.etiqet.core.common.exceptions.EtiqetException;

public interface TransportDelegate<S, M> {
  void onCreate(S var1);

  void onLogon(S var1);

  void onLogout(S var1);

  void toApp(M var1, S var2) throws EtiqetException;

  void fromApp(M var1, S var2) throws EtiqetException;
}
