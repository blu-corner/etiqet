package com.neueda.etiqet.websocket.messsage;

import com.neueda.etiqet.core.common.cdr.Cdr;
import com.neueda.etiqet.core.common.exceptions.EtiqetException;

public class WebSocketMsg extends Cdr {

    public WebSocketMsg(String msgType) throws EtiqetException {
        super(msgType);
    }

}
