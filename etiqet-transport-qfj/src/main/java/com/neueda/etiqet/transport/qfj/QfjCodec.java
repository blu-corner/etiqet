package com.neueda.etiqet.transport.qfj;

import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import com.neueda.etiqet.core.common.exceptions.EtiqetRuntimeException;
import com.neueda.etiqet.core.common.exceptions.SerializeException;
import com.neueda.etiqet.core.common.exceptions.UnknownTagException;
import com.neueda.etiqet.core.config.GlobalConfig;
import com.neueda.etiqet.core.message.cdr.Cdr;
import com.neueda.etiqet.core.message.cdr.CdrItem;
import com.neueda.etiqet.core.message.config.AbstractDictionary;
import com.neueda.etiqet.core.message.config.ProtocolConfig;
import com.neueda.etiqet.core.transport.Codec;
import com.neueda.etiqet.core.util.IntegerValidator;
import com.neueda.etiqet.core.util.ParserUtils;
import com.neueda.etiqet.fix.message.dictionary.Component;
import com.neueda.etiqet.fix.message.dictionary.FixDictionary;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import quickfix.*;
import quickfix.field.MsgType;

import java.util.*;

public class QfjCodec implements Codec<Cdr, Message> {

    private static final Logger logger = LogManager.getLogger(QfjCodec.class);
    private ProtocolConfig protocolConfig;
    private FixDictionary dictionary;

    /**
     * Constructor for the codec. Suppressing unused warnings as this will typically be instantiated via reflection
     *
     * @throws EtiqetException when we can't get the default FIX protocol
     */
    @SuppressWarnings("unused")
    public QfjCodec() throws EtiqetException {
        protocolConfig = GlobalConfig.getInstance().getProtocol("fix");
    }

    public QfjCodec(ProtocolConfig protocolConfig) {
        this.protocolConfig = protocolConfig;
    }

    /**
     * Encode a msg based on the message type which it is an instance of
     */
    private void encodeKnownMsg(Integer tag, HashMap.Entry<String, CdrItem> entry, FieldMap message)
        throws UnknownTagException {
        // Determine field type and assign correctly
        boolean isHeaderField = protocolConfig.isHeaderField(tag);
        switch (entry.getValue().getType()) {
            case CDR_INTEGER:
                IntField intf = new IntField(tag);
                intf.setValue(entry.getValue().getIntval().intValue());
                if (isHeaderField) {
                    ((Message) message).getHeader().setField(intf);
                } else {
                    message.setField(intf);
                }
                break;
            case CDR_DOUBLE:
                DoubleField df = new DoubleField(tag);
                df.setValue(entry.getValue().getDoubleval());
                if (isHeaderField) {
                    ((Message) message).getHeader().setField(df);
                } else {
                    message.setField(df);
                }
                break;
            case CDR_STRING:
                StringField sf = new StringField(tag, entry.getValue().getStrval());
                if (isHeaderField) {
                    ((Message) message).getHeader().setField(sf);
                } else {
                    message.setField(sf);
                }
                break;
            case CDR_BOOLEAN:
                BooleanField bf = new BooleanField(tag.intValue(), entry.getValue().getBoolVal());
                if (isHeaderField) {
                    ((Message) message).getHeader().setField(bf);
                } else {
                    message.setField(bf);
                }
                break;
            case CDR_ARRAY:
                parseGroup(message, tag, entry.getValue());
                break;
            default:
                throw new UnknownTagException("Unhandled tag " + tag + " with type " + entry.getValue().getType());
        }
    }

    /**
     * Encode msg based on field types
     */
    private void encodeUnknownMsg(HashMap.Entry<String, CdrItem> entry, Integer tag, FieldMap message)
        throws UnknownTagException {
        switch (entry.getValue().getType()) {
            case CDR_INTEGER:
                message.setField(new IntField(tag.intValue(), entry.getValue().getIntval().intValue()));
                break;
            case CDR_DOUBLE:
                message.setField(new DoubleField(tag.intValue(), entry.getValue().getDoubleval()));
                break;
            case CDR_STRING:
                message.setField(new StringField(tag, entry.getValue().getStrval()));
                break;
            case CDR_BOOLEAN:
                message.setField(new BooleanField(tag.intValue(), entry.getValue().getBoolVal()));
                break;
            case CDR_ARRAY:
                parseGroup(message, tag, entry.getValue());
                break;
            default:
                throw new UnknownTagException("Unhandled tag " + tag + " with type " + entry.getValue().getType());
        }
    }

    /**
     * Attempts to parse a group from the CdrItem passed
     *
     * @param message
     * @param tag     Tag that begins the group
     * @param item    CdrItem containing the nested fields
     * @return {@link Group} to be added to the FIX message
     * @throws UnknownTagException when there is no group matching the input tag, or we're unable to parse one of the
     *                             children
     */
    void parseGroup(FieldMap message, Integer tag, CdrItem item) throws UnknownTagException {
        // Iterate through the CDR children to allow for groups within groups
        for (Cdr cdr : item.getCdrs()) {
            Group childGroup = createEmptyGroup(tag);
            for (Map.Entry<String, CdrItem> cdrItemEntry : cdr.getItems().entrySet()) {
                if (!cdrItemEntry.getKey().equals(cdr.getType())) {
                    encodeKnownMsg(getIntegerTag(cdrItemEntry.getKey()), cdrItemEntry, childGroup);
                } else {
                    Map<String, CdrItem> items = cdrItemEntry.getValue().getCdrs().get(0).getItems();
                    for (Map.Entry<String, CdrItem> childEntry : items.entrySet()) {
                        encodeKnownMsg(getIntegerTag(childEntry.getKey()), childEntry, childGroup);
                    }
                }
            }
            message.addGroup(childGroup);
        }
    }

    private Group createEmptyGroup(Integer tag) throws UnknownTagException {
        String fieldName = protocolConfig.getNameForTag(tag);
        AbstractDictionary abstractDictionary = protocolConfig.getDictionary();
        if (!(abstractDictionary instanceof FixDictionary)) {
            throw new EtiqetRuntimeException("QuickFIX protocol is not using a FIXDictionary");
        }
        FixDictionary fixDictionary = (FixDictionary) abstractDictionary;
        Component[] components = fixDictionary.getFixDictionary().getComponents().getComponent();
        com.neueda.etiqet.fix.message.dictionary.Group groupDef
            = Arrays.stream(components)
                    .map(Component::getGroup)
                    .filter(Objects::nonNull)
                    .filter(group -> fieldName.equalsIgnoreCase(group.getName()))
                    .findFirst()
                    .orElseThrow(() -> new UnknownTagException("Unknown group definition for tag " + tag));

        // Delimiter of the group is always the first field
        Integer delimiterTag = protocolConfig.getTagForName(groupDef.getField()[0].getName());
        int[] fieldOrder = new int[groupDef.getField().length];
        for (int i = 0; i < groupDef.getField().length; i++) {
            fieldOrder[i] = protocolConfig.getTagForName(groupDef.getField()[i].getName());
        }

        return new Group(tag, delimiterTag, fieldOrder);
    }


    private int getIntegerTag(String key) throws UnknownTagException {
        int tag;
        if (IntegerValidator.isParseable(key)) {
            tag = IntegerValidator.tryParse(key);
            if (!protocolConfig.tagContains(tag)) {
                throw new UnknownTagException("failed to find tag for " + key);
            }
        } else {
            tag = protocolConfig.getTagForName(key);
        }
        return tag;
    }


    private Message encode(Cdr cdr, FieldMap message) throws EtiqetException {
        for (HashMap.Entry<String, CdrItem> entry : cdr.getItems().entrySet()) {
            String key = entry.getKey();

            // Tags prefixed with - should be ignored
            if (key.charAt(0) == '-') {
                logger.info("Ignoring field [" + getIntegerTag(key.substring(1)) + "]");
                continue;
            }

            Integer tag = getIntegerTag(key);

            // Warning ... we are skiping this Checksum tag ... is calculated by QF4J.
            if (tag == 10) continue;

            if (message instanceof Message) {
                // Determine field type and assign correctly
                encodeKnownMsg(tag, entry, message);
            } else {
                // Determine field type and assign correctly
                encodeUnknownMsg(entry, tag, message);
            }
        }
        return (Message) message;
    }

    @Override
    public Message encode(Cdr cdr) throws EtiqetException {
        try {
            com.neueda.etiqet.core.config.dtos.Message messageConfig =
                protocolConfig.getMessage(cdr.getType());
            ParserUtils.fillDefault(messageConfig, cdr);
            return encode(cdr, (Message) Class.forName(messageConfig.getImplementation()).getConstructor()
                                              .newInstance());
        } catch (Exception e) {
            logger.error(e);
            throw new SerializeException(e);
        }
    }

    // TODO: Handle repeating groups on decode. Currently have only needed to send
    @Override
    public Cdr decode(Message msg) throws EtiqetException {
        try {
            Cdr d = new Cdr(protocolConfig.getMsgName(msg.getHeader().getString(MsgType.FIELD)));
            Iterator<Field<?>> itr = msg.getHeader().iterator();
            while (itr.hasNext()) {
                StringField f = (StringField) itr.next();
                d.set(protocolConfig.getNameForTag(f.getTag()), f.getValue());
            }

            itr = msg.iterator();
            while (itr.hasNext()) {
                StringField f = (StringField) itr.next();
                d.set(protocolConfig.getNameForTag(f.getTag()), f.getValue());
            }

            itr = msg.getTrailer().iterator();
            while (itr.hasNext()) {
                StringField f = (StringField) itr.next();
                d.set(protocolConfig.getNameForTag(f.getTag()), f.getValue());
            }
            return d;
        } catch (FieldNotFound fieldNotFound) {
            throw new EtiqetException(fieldNotFound);
        }
    }

    @Override
    public void setProtocolConfig(ProtocolConfig protocolConfig) {
        this.protocolConfig = protocolConfig;
    }
}

