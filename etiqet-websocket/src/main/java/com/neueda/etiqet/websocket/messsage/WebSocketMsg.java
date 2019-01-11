package com.neueda.etiqet.websocket.messsage;

import com.neueda.etiqet.core.common.exceptions.EtiqetRuntimeException;
import com.neueda.etiqet.core.message.cdr.Cdr;
import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import com.neueda.etiqet.core.config.GlobalConfig;
import com.neueda.etiqet.core.config.dtos.Message;
import com.neueda.etiqet.core.json.JsonUtils;
import com.neueda.etiqet.core.message.config.ProtocolConfig;
import com.neueda.etiqet.websocket.config.WebSocketConfigConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WebSocketMsg extends Cdr {

    private static final Logger LOG = LogManager.getLogger(WebSocketMsg.class);

    public WebSocketMsg(String msgType, Cdr cdr) throws EtiqetException {
        super(msgType);
        update(cdr);
    }

    @Override
    public String toString() {
        return serialize();
    }

    public String serialize() throws EtiqetRuntimeException {

        String result = "";
        try {
            ProtocolConfig protocolConfig = GlobalConfig.getInstance().getProtocol(WebSocketConfigConstants.DEFAULT_PROTOCOL_NAME);
            Message messageConfig = protocolConfig.getMessage(msgType);

            if(messageConfig == null) {
                LOG.error("Message type {} not recognised", msgType);
                throw new EtiqetException("Message type " + msgType + " not recognised");
            }

            Cdr resultCdr = new Cdr(msgType);

            getItems().entrySet().stream()
                    .forEach(entry -> resultCdr.setItem(entry.getKey(), entry.getValue()));

            result = JsonUtils.cdrToJson(resultCdr);
        } catch (EtiqetException e) {
            LOG.error("Exception thrown serializing WebSocketMsg", e);
            throw new EtiqetRuntimeException(e);
        }
        return result;
    }

}
