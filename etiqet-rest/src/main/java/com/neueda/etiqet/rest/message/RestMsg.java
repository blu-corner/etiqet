package com.neueda.etiqet.rest.message;

import com.neueda.etiqet.core.common.cdr.Cdr;
import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import com.neueda.etiqet.core.common.exceptions.SerializeException;
import com.neueda.etiqet.core.config.GlobalConfig;
import com.neueda.etiqet.core.config.dtos.Message;
import com.neueda.etiqet.core.message.config.ProtocolConfig;
import com.neueda.etiqet.core.util.StringUtils;
import com.neueda.etiqet.rest.config.RestConfigConstants;
import com.neueda.etiqet.rest.json.JsonUtils;
import com.neueda.etiqet.rest.message.impl.HttpRequestMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class RestMsg extends Cdr {

    private static final Logger LOG = LoggerFactory.getLogger(RestMsg.class);

    private HttpRequestMsg instance;

    public RestMsg(String msgType) throws EtiqetException {
        super(msgType);
    }

    /**
     * Tramsforms the CDR provided into an HttpRequestMsg
     * @param cdr CDR to be serialised
     * @return HTTP Request Message that can be used by the RestClient
     * @throws EtiqetException when we can't serialise the Cdr into an HTTP request
     */
    public HttpRequestMsg serialize(Cdr cdr) throws EtiqetException {
        try {
            update(cdr);
            Message messageConfig = getMessage();
            if(messageConfig == null) {
                LOG.error("Message type {} not recognised", msgType);
                throw new EtiqetException("Message type " + msgType + " not recognised");
            }

            instance = (HttpRequestMsg) Class.forName(messageConfig.getImplementation()).getConstructor().newInstance();

            if(Arrays.asList("GET", "POST", "PUT", "DELETE").contains(msgType)) {
                handleHttpRequest();
            } else {
                LOG.error("Message type {} not recognised", msgType);
                throw new EtiqetException("Message type " + msgType + " not recognised");
            }
        } catch (EtiqetException e) {
            LOG.error("Exception thrown serializing RestMsg", e);
            throw e;
        } catch (Exception e) {
            LOG.error("Exception thrown serializing RestMsg", e);
            throw new SerializeException(e);
        }

        return instance;
    }

    /**
     * Gets the Message configuration from the ProtocolConfig. Abstracted to help with unit testing
     * @return message configuration for {@link #msgType}
     * @throws EtiqetException when the protocol config can't be found
     */
    Message getMessage() throws EtiqetException {
        ProtocolConfig protocolConfig = GlobalConfig.getInstance().getProtocol(RestConfigConstants.DEFAULT_PROTOCOL_NAME);
        return protocolConfig.getMessage(msgType);
    }

    /**
     * Handles an Http Request
     */
    private void handleHttpRequest() {
        HttpRequestMsg request = instance;
        request.setVerb(msgType);

        getItems().entrySet().stream()
            .filter(entry -> entry.getKey().startsWith("$header."))
            .forEach(entry -> request.addHeader(entry.getKey().replace("$header.", ""), entry.getValue().getStrval()));

        request.setUrl(getAsString("$httpEndpoint"));

        Cdr payloadCdr = new Cdr(msgType);
        getItems().entrySet().stream()
                  .filter(entry -> !entry.getKey().startsWith("$"))
                  .forEach(entry -> payloadCdr.setItem(entry.getKey(), entry.getValue()));

        String payload = JsonUtils.cdrToJson(payloadCdr);
        if(!"GET".equals(msgType) && !StringUtils.isNullOrEmpty(payload) && !"{}".equals(payload)) {
            request.setPayload(payload);
        }
    }

}
