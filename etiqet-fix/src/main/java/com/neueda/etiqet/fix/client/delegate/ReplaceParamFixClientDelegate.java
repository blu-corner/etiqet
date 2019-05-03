package com.neueda.etiqet.fix.client.delegate;

import com.neueda.etiqet.core.client.delegate.ClientDelegate;
import com.neueda.etiqet.core.common.exceptions.StopEncodingException;
import com.neueda.etiqet.core.config.GlobalConfig;
import com.neueda.etiqet.core.message.config.ProtocolConfig;
import com.neueda.etiqet.fix.config.FixConfigConstants;
import com.neueda.etiqet.fix.message.FIXMsg;
import quickfix.DefaultMessageFactory;
import quickfix.Message;
import quickfix.MessageUtils;

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

        // If message with params has been defined then replace with message's values.
        if(message != null) {
            try {
                Message fixMessage = MessageUtils.parse(new DefaultMessageFactory(), null, msg);
                FIXMsg fixMsg = new FIXMsg(fixMessage);
                fixMessage = fixMsg.updateWithCdr(message);

                for (Integer removeTag: fixMsg.getFieldsIgnored()){
                    ProtocolConfig protocolConfig = GlobalConfig.getInstance().getProtocol(FixConfigConstants.PROTOCOL_NAME);
                    if (protocolConfig.isHeaderField(removeTag)){
                        fixMessage.getHeader().removeField(removeTag);
                    } else {
                        fixMessage.removeField(removeTag);
                    }
                }

                msg = fixMessage.toString();
            } catch (Exception e) {
                throw new StopEncodingException(e);
            }
        }
        return super.transformAfterEncoding(msg);
    }
}
