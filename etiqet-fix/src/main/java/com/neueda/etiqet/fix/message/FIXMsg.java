package com.neueda.etiqet.fix.message;

import com.neueda.etiqet.core.common.cdr.Cdr;
import com.neueda.etiqet.core.common.cdr.CdrItem;
import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import com.neueda.etiqet.core.common.exceptions.SerializeException;
import com.neueda.etiqet.core.common.exceptions.UnknownTagException;
import com.neueda.etiqet.core.config.GlobalConfig;
import com.neueda.etiqet.core.message.config.ProtocolConfig;
import com.neueda.etiqet.core.util.IntegerValidator;
import com.neueda.etiqet.core.util.ParserUtils;
import com.neueda.etiqet.fix.config.FixConfigConstants;
import com.neueda.etiqet.fix.message.dictionary.Component;
import com.neueda.etiqet.fix.message.dictionary.Fix;
import com.neueda.etiqet.fix.message.dictionary.FixDictionary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.*;

import java.util.*;

public class FIXMsg {
    private static final Logger LOG = LoggerFactory.getLogger(FIXMsg.class);

    private FieldMap instance;
    private List<Integer> fieldsIgnored = new ArrayList<>();
    private ProtocolConfig protocolConfig;

    public FIXMsg() {
    }

    public FIXMsg(FieldMap instance) {
        this.instance = instance;
    }

    public FIXMsg(Cdr d, FieldMap instance) {
        this(d, instance, null);
    }

    public FIXMsg(Cdr d, FieldMap instance, ProtocolConfig protocolConfig) {
        this.protocolConfig = protocolConfig;
        this.instance = instance;
        try {
            create(d);
        } catch (Exception lex) {
            LOG.error("Error creating a FIXMsg");
        }
    }

    public void create(Cdr data) throws EtiqetException {
        populateMessage(data);
    }

    private void populateMessage(Cdr data) throws EtiqetException {
        ProtocolConfig protocolConfig = getFixProtocol();
        data.clear();
        ParserUtils.fillDefault(protocolConfig.getMessage(data.getType()), data);
    }

    ProtocolConfig getFixProtocol() throws EtiqetException {
        if(protocolConfig == null) {
            protocolConfig = GlobalConfig.getInstance().getProtocol(FixConfigConstants.PROTOCOL_NAME);
        }
        return protocolConfig;
    }

    public Message updateWithCdr(Cdr data) throws EtiqetException {
        encode(data);
        return (Message) instance;
    }

    public FieldMap serialize(Cdr cdr) throws EtiqetException {
        try {
            ProtocolConfig protocolConfig = getFixProtocol();
            instance = (FieldMap) Class.forName(protocolConfig.getMessage(cdr.getType()).getImplementation())
                                      .getConstructor()
                                      .newInstance();
            this.encode(cdr);
        } catch (EtiqetException e) {
            LOG.error("EtiqetException occurred while serializing FIXMsg", e);
            throw e;
        } catch (Exception e) {
            LOG.error("Unable to serialize FIXMsg", e);
            throw new SerializeException(e);
        }

        return instance;
    }

    private int getIntegerTag(ProtocolConfig protocolConfig, String key) throws UnknownTagException {
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

    /**
     * Encode a msg based on the message type which it is an instance of
     *
     * @param protocolConfig
     * @param tag
     * @param entry
     * @throws EtiqetException
     */
    private void encodeKnownMsg(ProtocolConfig protocolConfig, Integer tag, Map.Entry<String, CdrItem> entry)
        throws EtiqetException {
        // Determine field type and assign correctly
        boolean isHeaderField = protocolConfig.isHeaderField(tag);
        switch (entry.getValue().getType()) {
            case CDR_INTEGER:
                IntField intField = new IntField(tag.intValue(), entry.getValue().getIntval().intValue());
                if (isHeaderField) {
                    ((Message) instance).getHeader().setField(intField);
                } else {
                    instance.setField(intField);
                }
                break;
            case CDR_DOUBLE:
                DoubleField doubleField = new DoubleField(tag.intValue(), entry.getValue().getDoubleval());
                if (isHeaderField) {
                    ((Message) instance).getHeader().setField(doubleField);
                } else {
                    instance.setField(doubleField);
                }
                break;
            case CDR_STRING:
                StringField stringField = new StringField(tag, entry.getValue().getStrval());
                if (isHeaderField) {
                    ((Message) instance).getHeader().setField(stringField);
                } else {
                    instance.setField(stringField);
                }
                break;
            case CDR_BOOLEAN:
                BooleanField booleanField = new BooleanField(tag.intValue(), entry.getValue().getBoolVal());
                if (isHeaderField) {
                    ((Message) instance).getHeader().setField(booleanField);
                } else {
                    instance.setField(booleanField);
                }
                break;
            case CDR_ARRAY:
                encodeArrayType(protocolConfig, entry.getKey(), entry.getValue(), instance);
                return;
            default:
                throw new UnknownTagException("Unhandled tag " + tag + " with type " + entry.getValue().getType());
        }
    }

    /**
     * Encode msg of type Array
     *
     * @param protocolConfig protocol configuration to populate the entry from
     * @param entry          entry to populate the values from
     * @param fieldMap
     * @throws EtiqetException When the MessageComponent couldn't be instantiated
     */
    private FieldMap encodeArrayType(ProtocolConfig protocolConfig, String fieldName, CdrItem entry, FieldMap fieldMap) throws EtiqetException {
        Fix dictionary = ((FixDictionary) protocolConfig.getDictionary()).getFixDictionary();
        Integer tag = protocolConfig.getTagForName(fieldName);
        Group msg = Arrays.stream(dictionary.getComponents().getComponent())
                          .map(Component::getGroup)
                          .filter(Objects::nonNull)
                          .filter(g -> g.getName().equalsIgnoreCase(fieldName))
                          .map(group -> getGroup(protocolConfig, tag, group))
                          .findFirst()
                          .orElse(null);
        if (msg == null) {
            LOG.error("Unable to get group for {}", fieldName);
            return fieldMap;
        }

        FIXMsg fixMsg = new FIXMsg(new Cdr(""), msg, getFixProtocol());
        for (Cdr child : entry.getCdrs()) {
            try {
                fixMsg.encode(child);
            } catch (EtiqetException e) {
                LOG.error("Error serializing component: {}", child);
            }
        }
        fieldMap.addGroup((Group) fixMsg.instance);
        return fieldMap;
    }

    private Group getGroup(ProtocolConfig protocolConfig, Integer tag, com.neueda.etiqet.fix.message.dictionary.Group group) {
        try {
            com.neueda.etiqet.fix.message.dictionary.Field[] fields = group.getField();
            if(fields == null) {
                return null;
            }
            // get the first field listed in the dictionary as the group delimeter field
            Integer delimeterTag = protocolConfig.getTagForName(fields[0].getName());
            return new Group(tag, delimeterTag);
        } catch (UnknownTagException e) {
            return null;
        }
    }

    /**
     * Encode msg based on field types
     *
     * @param entry
     * @param tag
     * @throws UnknownTagException
     */
    private FieldMap encodeUnknownMsg(String key, CdrItem value, Integer tag, FieldMap fieldMap) throws EtiqetException {
        switch (value.getType()) {
            case CDR_INTEGER:
                fieldMap.setField(new IntField(tag.intValue(), value.getIntval().intValue()));
                break;
            case CDR_DOUBLE:
                fieldMap.setField(new DoubleField(tag.intValue(), value.getDoubleval()));
                break;
            case CDR_STRING:
                fieldMap.setField(new StringField(tag, value.getStrval()));
                break;
            case CDR_BOOLEAN:
                fieldMap.setField(new BooleanField(tag.intValue(), value.getBoolVal()));
                break;
            case CDR_ARRAY:
                fieldMap.setGroups(encodeUnknownMsg(key, value, tag, fieldMap));
                return fieldMap;
            default:
                throw new UnknownTagException("Unhandled tag " + tag + " with type " + value.getType());
        }
        return fieldMap;
    }

    /**
     * Encodes abstract fields to fix class fields
     *
     * @param cdr cdr object to be encoded
     * @throws EtiqetException if something went wrong during the encoding.
     */
    private void encode(Cdr cdr) throws EtiqetException {
        ProtocolConfig protocolConfig = getFixProtocol();
        for (Map.Entry<String, CdrItem> entry : cdr.getItems().entrySet()) {
            String key = entry.getKey();

            // Tags prefixed with - should be ignored
            if (key.charAt(0) == '-') {
                fieldsIgnored.add(getIntegerTag(protocolConfig, key.substring(1)));
                continue;
            }

            // Get tag
            Integer tag = getIntegerTag(protocolConfig, key);

            if (instance instanceof Message) {
                // Determine field type and assign correctly
                encodeKnownMsg(protocolConfig, tag, entry);
            } else {
                // Determine field type and assign correctly
                encodeUnknownMsg(key, entry.getValue(), tag, instance);
            }
        }
    }

    public List<Integer> getFieldsIgnored() {
        return fieldsIgnored;
    }
}
