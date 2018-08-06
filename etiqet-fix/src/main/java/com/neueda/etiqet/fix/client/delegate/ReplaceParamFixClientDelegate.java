package com.neueda.etiqet.fix.client.delegate;

import com.neueda.etiqet.core.client.delegate.ClientDelegate;
import com.neueda.etiqet.core.common.cdr.CdrItem;
import com.neueda.etiqet.core.common.exceptions.StopEncodingException;
import com.neueda.etiqet.core.config.GlobalConfig;
import com.neueda.etiqet.fix.config.FixConfigConstants;
import quickfix.Message;

/**
 * Delegate for QuickFix client that fills some necessary parameters when sending messages to the server.
 */
public class ReplaceParamFixClientDelegate extends MessageFixClientDelegate {

    /**
     * Constructor.
     */
    public ReplaceParamFixClientDelegate() {
        super();
    }

    /**
     * Constructor.
     *
     * @param next the next delegate on the chain to process the message.
     */
    public ReplaceParamFixClientDelegate(ClientDelegate<Message, String> next) {
        super(next);
    }

    @Override
    public String transformAfterEncoding(String msg) throws StopEncodingException {
        String transformed = msg;

        // If message with params has been defined then replace with message's values.
        if(message != null) {
            try {
                StringBuilder buffer = new StringBuilder();
                for(String entry: msg.split(FIELD_SEPARATOR)) {
                    String[] keyValuePair = entry.split("=");
                    String tagName = GlobalConfig.getInstance().getProtocol(FixConfigConstants.PROTOCOL_NAME)
                                                               .getNameForTag(Integer.parseInt(keyValuePair[0]));
                    CdrItem toReplaceWith = message.getItem(tagName);
                    String value = (toReplaceWith != null)? toReplaceWith.toString(): keyValuePair[1];
                    buffer.append(keyValuePair[0]).append(KEY_VALUE_SEPARATOR).append(value).append(FIELD_SEPARATOR);
                }
                transformed = buffer.toString();
            } catch (Exception e) {
                throw new StopEncodingException(e);
            }
        }
        return super.transformAfterEncoding(transformed);
    }
}
