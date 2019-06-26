package com.neueda.etiqet.core.transport;

import com.google.protobuf.Descriptors;
import com.google.protobuf.Message;
import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import com.neueda.etiqet.core.common.exceptions.EtiqetRuntimeException;
import com.neueda.etiqet.core.message.cdr.Cdr;
import com.neueda.etiqet.core.message.cdr.CdrItem;

import java.util.Collection;

import static com.google.protobuf.Descriptors.FieldDescriptor.JavaType.MESSAGE;

public class ProtobufCodec implements Codec<Cdr, Message> {

    @Override
    public Message encode(Cdr cdr) throws EtiqetException {
        try {
            String type = cdr.getType();
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
                    fieldValue.getCdrs().stream()
                        .map(item -> encode(item, builder.newBuilderForField(fieldDescriptor)))
                        .forEach(
                            valueItem -> builder.addRepeatedField(fieldDescriptor, valueItem)
                        );
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
                    builder.setField(fieldDescriptor, value);
                }
            }
        );
        return builder.build();
    }



    private Object getValue(CdrItem cdrItem, Descriptors.FieldDescriptor fieldDescriptor){
        switch (fieldDescriptor.getJavaType()) {
            case INT:
                return Integer.parseInt(cdrItem.getStrval());
            case LONG:
                return cdrItem.getIntval();
            case DOUBLE:
                return cdrItem.getDoubleval();
            case FLOAT:
                return cdrItem.getDoubleval().floatValue();
            case ENUM:
                Descriptors.EnumDescriptor enumDescriptor = fieldDescriptor.getEnumType();
                return enumDescriptor.findValueByName(cdrItem.getStrval());
            case STRING:
                return cdrItem.getStrval();
            case BOOLEAN:
                return cdrItem.getBoolVal();
            default:
                throw new EtiqetRuntimeException("Unable to encode value of type " + fieldDescriptor.getJavaType() +
                    " for field " + fieldDescriptor.getName());
        }
    }

    @Override
    public Cdr decode(Message message) {
        Cdr cdr = new Cdr(message.getClass().getName());
        message.getAllFields().forEach(
            (fieldDescriptor, value) -> {
                String fieldName = fieldDescriptor.getName();
                switch (fieldDescriptor.getJavaType()) {
                    case STRING:
                        cdr.set(fieldName, (String) value);
                        break;
                    case INT:
                        cdr.set(fieldName, (int) value);
                        break;
                    case LONG:
                        cdr.set(fieldName, (Long) value);
                        break;
                    case DOUBLE:
                        cdr.set(fieldName, (Double) value);
                        break;
                    case FLOAT:
                        cdr.set(fieldName, new Double((float) value));
                        break;
                    case ENUM:
                        cdr.set(fieldName, value.toString());
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


}
