package com.neueda.etiqet.fix.client.delegate;

import com.neueda.etiqet.core.client.delegate.ClientDelegate;
import com.neueda.etiqet.core.client.delegate.LoggerClientDelegate;
import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import com.neueda.etiqet.core.common.exceptions.EtiqetRuntimeException;
import com.neueda.etiqet.core.common.exceptions.StopEncodingException;
import com.neueda.etiqet.core.common.exceptions.StopStringDecodingException;
import com.neueda.etiqet.core.config.GlobalConfig;
import com.neueda.etiqet.core.message.config.ProtocolConfig;
import com.neueda.etiqet.fix.config.FixConfigConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import quickfix.Field;
import quickfix.Message;
import quickfix.StringField;

import java.util.Iterator;

/**
 * Logger for quickfix describing the messages in a more verbose way.
 */
public class FixLoggerClientDelegate extends LoggerClientDelegate<Message, String> {
    private static final Logger LOG = LogManager.getLogger(FixLoggerClientDelegate.class);

    /**
     * Constructor.
     */
    public FixLoggerClientDelegate() {
        super();
    }

    /**
     * Constructor.
     * @param next the next delegate on the chain to process the messages.
     */
    public FixLoggerClientDelegate(ClientDelegate<Message, String> next) {
        super(next);
    }

    @Override
    public Message transformBeforeEncoding(Message msg) throws StopEncodingException {
        printFixMsg(msg, "out");
        return super.transformBeforeEncoding(msg);
    }

    @Override
    public Message transformAfterDecoding(Message msg) throws StopStringDecodingException {
        printFixMsg(msg, "in");
        return super.transformAfterDecoding(msg);
    }

    /**
     *
     * @param itr iterator of fields to be logged.
     * @param direction the direction of the message (inbound, outbound).
     * @param protocolConfig the protocol configuration to extract the name of the fields.
     */
    private void printFields(Iterator<Field<?>> itr, String direction, ProtocolConfig protocolConfig) {
        while (itr.hasNext()) {
            StringField f = (StringField) itr.next();
            LOG.info("    " + direction + ": " + protocolConfig.getNameForTag(f.getTag()) + " = "
                    + f.getValue());
        }
    }

    /**
     * Logs the message in a pretty way.
     * @param msg the message to be logged.
     * @param direction the direction of the message (inbound, outbound).
     */
    private synchronized void printFixMsg(Message msg, String direction) {
        LOG.info("--- msg " + direction + " ---");
        try {
            // Prints header, body and trailer
            ProtocolConfig protocolConfig = GlobalConfig.getInstance().getProtocol(FixConfigConstants.PROTOCOL_NAME);
            printFields(msg.getHeader().iterator(), direction, protocolConfig);
            printFields(msg.iterator(), direction, protocolConfig);
            printFields(msg.getTrailer().iterator(), direction, protocolConfig);
        } catch (EtiqetException e) {
            throw new EtiqetRuntimeException("Exception thrown printing FIX message", e);
        }
    }
}
