package com.neueda.etiqet.core.transport;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.protobuf.ProtobufMapper;
import com.fasterxml.jackson.dataformat.protobuf.schema.ProtobufField;
import com.fasterxml.jackson.dataformat.protobuf.schema.ProtobufSchema;
import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import com.neueda.etiqet.core.config.dtos.Message;
import com.neueda.etiqet.core.json.JsonUtils;
import com.neueda.etiqet.core.message.cdr.Cdr;
import com.neueda.etiqet.core.message.cdr.CdrItem;
import com.neueda.etiqet.core.message.config.ProtocolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;

public class ProtobufCodec implements Codec<Cdr, byte[]> {

    private static final Logger LOG = LoggerFactory.getLogger(ProtobufCodec.class);
    private ProtocolConfig protocolConfig;

    @Override
    public byte[] encode(Cdr cdr) throws EtiqetException {
        try {
            Message message = protocolConfig.getMessage(cdr.getType());
            if (message == null) {
                throw new EtiqetException("Could not find message class for type " + cdr.getType());
            }
            URL schemaURL = getClass().getClassLoader().getResource(message.getImplementation());
            ProtobufMapper protobufMapper = new ProtobufMapper();
            ProtobufSchema schema = protobufMapper.schemaLoader().load(schemaURL);
            ProtobufSchema typeSchema = schema.withRootType(cdr.getType());
            correctCdrTypes(cdr, typeSchema);
            String cdrString = JsonUtils.cdrToJson(cdr);
            return protobufMapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING)
                                 .writer(typeSchema)
                                 .writeValueAsBytes(new ObjectMapper().readTree(cdrString));
        } catch (Exception e) {
            throw new EtiqetException(e);
        }
    }

    private void correctCdrTypes(Cdr cdr, ProtobufSchema schema) {
        for (ProtobufField field : schema.getRootType().fields()) {
            if (cdr.containsKey(field.name)) {
                CdrItem item = cdr.getItem(field.name);
                if (!field.repeated) {
                    String strval = item.getStrval();
                    switch (field.type) {
                        case FIXINT32:
                        case FIXINT64:
                        case VINT32_STD:
                        case VINT32_Z:
                        case VINT64_STD:
                        case VINT64_Z:
                            if (item.getIntval() == null) {
                                item.setStrval(null);
                                item.setIntval(Long.parseLong(strval));
                            }
                            break;
                        case DOUBLE:
                        case FLOAT:
                            if (item.getDoubleval() == null) {
                                item.setStrval(null);
                                item.setDoubleval(Double.parseDouble(strval));
                            }
                            break;
                        case BOOLEAN:
                            if (item.getBoolVal() == null) {
                                item.setStrval(null);
                                item.setBoolVal(Boolean.parseBoolean(strval));
                            }
                            break;
                        default:
                            LOG.debug("{} appears to require no further conversion", field.name);
                    }
                } else {
                    item.setType(CdrItem.CdrItemType.CDR_ARRAY);
                }
                cdr.setItem(field.name, item);
            }
        }
    }

    @Override
    public Cdr decode(byte[] message) throws EtiqetException {
        ProtobufMapper protobufMapper = new ProtobufMapper();
        ProtobufSchema schema = null;
        String msgType = null;
        for (Message configMessage : protocolConfig.getMessages()) {
            try {
                URL schemaURL = getClass().getClassLoader().getResource(configMessage.getImplementation());
                schema = protobufMapper.schemaLoader().load(schemaURL);
                msgType = configMessage.getName();
                break;
            } catch (Exception e) {
                LOG.debug("Message received is not of type {}", configMessage.getName());
            }
        }
        if (schema == null) {
            throw new EtiqetException("Could not find suitable schema to parse message with");
        }

        Cdr cdr = new Cdr(msgType);
        try {
            String jsonString = protobufMapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING)
                                              .enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING)
                                              .reader()
                                              .with(schema.withRootType(msgType))
                                              .readTree(new ByteArrayInputStream(message))
                                              .toString();
            Cdr toCdr = JsonUtils.jsonToCdr(jsonString);
            cdr.update(toCdr);
        } catch (IOException e) {
            throw new EtiqetException("Unable to parse Protobuf message with schema " + schema, e);
        }
        return cdr;
    }

    @Override
    public void setProtocolConfig(ProtocolConfig protocolConfig) {
        this.protocolConfig = protocolConfig;
    }

}
