package com.neueda.etiqet.core.transport;

import com.google.protobuf.Descriptors;
import com.google.protobuf.Message;
import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import com.neueda.etiqet.core.common.exceptions.EtiqetRuntimeException;
import com.neueda.etiqet.core.message.cdr.Cdr;
import com.neueda.etiqet.core.message.cdr.CdrItem;
import com.neueda.etiqet.core.message.config.AbstractDictionary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;

import static com.google.protobuf.Descriptors.FieldDescriptor.JavaType.MESSAGE;

public class ProtobufCodec implements Codec<Cdr, Message> {

    private AbstractDictionary dictionary;

    final static Logger logger = LoggerFactory.getLogger(ProtobufCodec.class);

    @Override
    public Message encode(Cdr cdr) throws EtiqetException {
        try {
            String type = dictionary.getMsgType(cdr.getType());
            if (type == null) {
                throw new EtiqetException("Could not find message class for type " + cdr.getType());
            }
            Message.Builder builder = (Message.Builder) Class.forName(type).getMethod("newBuilder").invoke(this);
            return encode(cdr, builder);
        } catch (Exception e) {
            throw new EtiqetException(e);
        }
    }

    private Message encode(Cdr cdr, Message.Builder builder) {
        Descriptors.Descriptor descriptor = builder.getDescriptorForType();
        cdr.getItems().forEach(
            (fieldName, fieldValue) -> {
                Descriptors.FieldDescriptor fieldDescriptor = descriptor.findFieldByName(fieldName);
                if (fieldDescriptor == null) {
                    throw new EtiqetRuntimeException("Unable to find field " + fieldName);
                }
                if (fieldDescriptor.isRepeated()) {
                    if(fieldValue.getType().equals(CdrItem.CdrItemType.CDR_ARRAY)) {
                        fieldValue.getCdrs().stream()
                                  .map(item -> encode(item, builder.newBuilderForField(fieldDescriptor)))
                                  .forEach(
                                      valueItem -> builder.addRepeatedField(fieldDescriptor, valueItem)
                                          );
                    } else {
                        builder.addRepeatedField(fieldDescriptor, getValue(fieldValue, fieldDescriptor));
                    }
                } else {
                    final Object value;
                    if (fieldDescriptor.getJavaType() == MESSAGE) {
                        value = encode(fieldValue.getCdrs().get(0), builder.newBuilderForField(fieldDescriptor));
                    } else {
                        value = getValue(fieldValue, fieldDescriptor);
                    }
                    if (value == null && fieldDescriptor.isRequired()) {
                        throw new EtiqetRuntimeException("Unable to encode Cdr with type /*" + cdr + "*/. No value found for required field " + fieldDescriptor.getName());
                    }
                    try {
                        builder.setField(fieldDescriptor, value);
                    } catch (Exception e) {
                        throw new EtiqetRuntimeException("Unable to set field " + fieldName + " with value " + value, e);
                    }
                }
            }
        );
        return builder.build();
    }



    private Object getValue(CdrItem cdrItem, Descriptors.FieldDescriptor fieldDescriptor){
        try {
            switch (fieldDescriptor.getJavaType()) {
                case INT:
                    return cdrItem.getIntval() != null ? cdrItem.getIntval().intValue() : Integer.parseInt(cdrItem.getStrval());
                case LONG:
                    return cdrItem.getIntval() != null ? cdrItem.getIntval().longValue() : Long.parseLong(cdrItem.getStrval());
                case DOUBLE:
                    return cdrItem.getDoubleval() != null ? cdrItem.getDoubleval().doubleValue() : Double.parseDouble(cdrItem.getStrval());
                case FLOAT:
                    return cdrItem.getDoubleval() != null ? cdrItem.getDoubleval().floatValue() : Float.parseFloat(cdrItem.getStrval());
                case ENUM:
                    Descriptors.EnumDescriptor enumDescriptor = fieldDescriptor.getEnumType();
                    return enumDescriptor.findValueByName(cdrItem.getStrval());
                case STRING:
                    return cdrItem.getStrval();
                case BOOLEAN:
                    return cdrItem.getBoolVal() != null ? cdrItem.getBoolVal() : Boolean.parseBoolean(cdrItem.getStrval());
                default:
                    throw new EtiqetRuntimeException("Unable to encode value of type " + fieldDescriptor.getJavaType() +
                                                         " for field " + fieldDescriptor.getName());
            }
        } catch (NumberFormatException e) {
            throw new EtiqetRuntimeException("Unable to encode value of type " + fieldDescriptor.getJavaType() +
                                                 " for field " + fieldDescriptor.getName(), e);
        }
    }

    @Override
    public Cdr decode(Message message) {
        Cdr cdr = new Cdr(dictionary.getMsgName(message.getClass().getName()));
        message.getAllFields().forEach(
            (fieldDescriptor, value) -> {
                String fieldName = fieldDescriptor.getName();
                switch (fieldDescriptor.getJavaType()) {
                    case STRING:
                    case ENUM:
                        if(fieldDescriptor.isRepeated() && value instanceof List) {
                            if(!cdr.containsKey(fieldName)) {
                                cdr.setItem(fieldName, new CdrItem(CdrItem.CdrItemType.CDR_ARRAY));
                            }
                            for (Object listVal : (List) value) {
                                Cdr stringChild = new Cdr(fieldName);
                                stringChild.set(fieldName, listVal.toString());
                                cdr.getItem(fieldName).addCdrToList(stringChild);
                            }
                        } else {
                            cdr.set(fieldName, (String) value);
                        }
                        break;
                    case INT:
                        if(fieldDescriptor.isRepeated() && value instanceof List) {
                            if(!cdr.containsKey(fieldName)) {
                                cdr.setItem(fieldName, new CdrItem(CdrItem.CdrItemType.CDR_ARRAY));
                            }
                            for (Object listVal : (List) value) {
                                Cdr intChild = new Cdr(fieldName);
                                intChild.set(fieldName, (int) listVal);
                                cdr.getItem(fieldName).addCdrToList(intChild);
                            }
                        } else {
                            cdr.set(fieldName, (int) value);
                        }
                        break;
                    case LONG:
                        if(fieldDescriptor.isRepeated() && value instanceof List) {
                            if(!cdr.containsKey(fieldName)) {
                                cdr.setItem(fieldName, new CdrItem(CdrItem.CdrItemType.CDR_ARRAY));
                            }
                            for (Object listVal : (List) value) {
                                Cdr intChild = new Cdr(fieldName);
                                intChild.set(fieldName, (Long) listVal);
                                cdr.getItem(fieldName).addCdrToList(intChild);
                            }
                        } else {
                            cdr.set(fieldName, (Long) value);
                        }
                        break;
                    case DOUBLE:
                        if(fieldDescriptor.isRepeated() && value instanceof List) {
                            if(!cdr.containsKey(fieldName)) {
                                cdr.setItem(fieldName, new CdrItem(CdrItem.CdrItemType.CDR_ARRAY));
                            }
                            for (Object listVal : (List) value) {
                                Cdr intChild = new Cdr(fieldName);
                                intChild.set(fieldName, (Double) listVal);
                                cdr.getItem(fieldName).addCdrToList(intChild);
                            }
                        } else {
                            cdr.set(fieldName, (Double) value);
                        }
                        break;
                    case FLOAT:
                        if(fieldDescriptor.isRepeated() && value instanceof List) {
                            if(!cdr.containsKey(fieldName)) {
                                cdr.setItem(fieldName, new CdrItem(CdrItem.CdrItemType.CDR_ARRAY));
                            }
                            for (Object listVal : (List) value) {
                                Cdr intChild = new Cdr(fieldName);
                                intChild.set(fieldName, new Double((float) listVal));
                                cdr.getItem(fieldName).addCdrToList(intChild);
                            }
                        } else {
                            cdr.set(fieldName, new Double((float) value));
                        }
                        break;
                    case BOOLEAN:
                        cdr.set(fieldName, (Boolean) value);
                        break;
                    case MESSAGE:
                        CdrItem item = new CdrItem();
                        if (value instanceof Collection) {
                            for (Object itemValue : (Collection) value) {
                                item.addCdrToList(decode((Message) itemValue));
                            }
                        } else if (value instanceof Message){
                            Message messageValue = (Message) value;
                            item.addCdrToList(decode(messageValue));
                        }
                        cdr.setItem(fieldName, item);
                        break;
                    default:
                        throw new EtiqetRuntimeException("Unable to decode value of type " + fieldDescriptor.getJavaType() +
                            " for field " + fieldName);
                }
            }
        );
        return cdr;
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
