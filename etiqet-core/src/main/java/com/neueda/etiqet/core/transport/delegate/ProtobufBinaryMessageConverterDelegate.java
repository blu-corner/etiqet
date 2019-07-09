package com.neueda.etiqet.core.transport.delegate;

import com.google.protobuf.Any;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.neueda.etiqet.core.message.config.AbstractDictionary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProtobufBinaryMessageConverterDelegate implements BinaryMessageConverterDelegate<Message> {

    private AbstractDictionary dictionary;

    private static Logger logger = LoggerFactory.getLogger(ProtobufBinaryMessageConverterDelegate.class);

    @Override
    public Message fromByteArray(byte[] binaryMessage) {
        if (dictionary != null) {
            for (String messageName : dictionary.getMessageNames()) {
                Message parsedMessage = parseMessage(dictionary.getMsgType(messageName), binaryMessage);
                if (parsedMessage != null && parsedMessage.getUnknownFields().asMap().size() == 0) {
                    return parsedMessage;
                }
            }
        }
        try {
            return Any.newBuilder().mergeFrom(binaryMessage).build();
        } catch (InvalidProtocolBufferException e) {
            logger.error("Unable to parse received binary message");
            return null;
        }
    }

    private Message parseMessage(final String className, byte[] binaryMessage) {
        try {
            Class messageClass = Class.forName(className);
            return (Message) messageClass.getMethod("parseFrom", byte[].class).invoke(this, binaryMessage);
        } catch (Exception e){
            return null;
        }
    }

    @Override
    public byte[] toByteArray(Message message) {
        return message.toByteArray();
    }

    @Override
    public void setDictionary(AbstractDictionary dictionary) {
        if (dictionary == null) {
            logger.warn("Protobuf dictionary missing");
        } else {
            this.dictionary = dictionary;
        }
    }
}
