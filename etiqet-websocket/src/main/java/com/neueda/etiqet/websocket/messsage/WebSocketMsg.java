package com.neueda.etiqet.websocket.messsage;

import com.neueda.etiqet.core.common.cdr.Cdr;
import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import com.neueda.etiqet.core.config.GlobalConfig;
import com.neueda.etiqet.core.config.dtos.Message;
import com.neueda.etiqet.core.json.JsonUtils;
import com.neueda.etiqet.core.message.config.ProtocolConfig;
import com.neueda.etiqet.websocket.config.WebSocketConfigConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebSocketMsg extends Cdr {

    private static final Logger LOG = LoggerFactory.getLogger(WebSocketMsg.class);

    public WebSocketMsg(String msgType, Cdr cdr) {
        super(msgType);
        update(cdr);
    }

    public String serialize() throws EtiqetException {
        try {
            ProtocolConfig protocolConfig = GlobalConfig.getInstance().getProtocol(WebSocketConfigConstants.DEFAULT_PROTOCOL_NAME);
            Message messageConfig = protocolConfig.getMessage(msgType);

            if(messageConfig == null) {
                LOG.error("Message type {} not recognised", msgType);
                throw new EtiqetException("Message type " + msgType + " not recognised");
            }

            Cdr resultCdr = new Cdr(msgType);

            getItems().entrySet().forEach(entry -> resultCdr.setItem(entry.getKey(), entry.getValue()));

            return JsonUtils.cdrToJson(resultCdr);
        } catch (EtiqetException e) {
            LOG.error("Exception thrown serializing WebSocketMsg", e);
            throw e;
        }
    }

}
