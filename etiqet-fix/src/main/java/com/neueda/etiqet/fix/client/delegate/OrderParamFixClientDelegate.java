package com.neueda.etiqet.fix.client.delegate;

import com.neueda.etiqet.core.client.delegate.ClientDelegate;
import com.neueda.etiqet.core.common.cdr.CdrItem;
import com.neueda.etiqet.core.common.exceptions.StopEncodingException;
import com.neueda.etiqet.core.config.GlobalConfig;
import com.neueda.etiqet.fix.config.FixConfigConstants;
import quickfix.Message;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Delegate for QuickFix client that fills some necessary parameters when sending messages to the server.
 */
public class OrderParamFixClientDelegate extends MessageFixClientDelegate {

    /**
     * Constructor.
     */
    public OrderParamFixClientDelegate() {
        super();
    }

    /**
     * Constructor.
     *
     * @param next the next delegate on the chain to process the message.
     */
    public OrderParamFixClientDelegate(ClientDelegate<Message, String> next) {
        super(next);
    }

    @Override
    public String transformAfterEncoding(String msg) throws StopEncodingException {
        String transformed = msg;
        Map<Integer, String> map = new LinkedHashMap<>();

        // If message with params has been defined then replace with message's values.
        if(message != null) {
            // Cache the values from message in a key value map
            for(String entry: msg.split(FIELD_SEPARATOR)) {
                String[] keyValuePair = entry.split(KEY_VALUE_SEPARATOR);
                map.put(Integer.parseInt(keyValuePair[0]), keyValuePair[1]);
            }

            try {
                // Build the message considering the parameter's ordering from the given message
                StringBuilder buffer = new StringBuilder();
                for(Map.Entry<String, CdrItem> field: message.getItems().entrySet()) {
                    Integer tag = GlobalConfig.getInstance().getProtocol(FixConfigConstants.PROTOCOL_NAME).getTagForName(field.getKey());
                    if(map.get(tag) != null) {
                        buffer.append(tag).append(KEY_VALUE_SEPARATOR).append(field.getValue()).append(FIELD_SEPARATOR);
                        map.remove(tag);
                    }
                }

                // Add the rest of parameters to the end of the message respecting the ordering
                for(Map.Entry<Integer, String> keyValue: map.entrySet()) {
                    buffer.append(keyValue.getKey()).append(KEY_VALUE_SEPARATOR).append(keyValue.getValue()).append(FIELD_SEPARATOR);
                }
                transformed = buffer.toString();
            } catch (Exception e) {
                throw new StopEncodingException(e);
            }
        }
        return super.transformAfterEncoding(transformed);
    }
}
