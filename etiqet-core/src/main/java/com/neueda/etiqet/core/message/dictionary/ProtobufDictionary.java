package com.neueda.etiqet.core.message.dictionary;

import com.google.protobuf.DescriptorProtos.DescriptorProto;
import com.google.protobuf.DescriptorProtos.FileDescriptorProto;
import com.google.protobuf.DescriptorProtos.FileDescriptorSet;
import com.neueda.etiqet.core.common.exceptions.EtiqetRuntimeException;
import com.neueda.etiqet.core.common.exceptions.UnknownTagException;
import com.neueda.etiqet.core.message.config.AbstractDictionary;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

public class ProtobufDictionary extends AbstractDictionary {
    protected Map<String, ProtobufClass> messageTypes;

    public ProtobufDictionary(String configPath) {
        super(configPath);
        messageTypes = new HashMap<>();
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream(configPath);
            FileDescriptorSet fileDescriptorSet = FileDescriptorSet.parseFrom(is);
            for (FileDescriptorProto fileDescriptorProto : fileDescriptorSet.getFileList()) {
                for (DescriptorProto descriptorProto : fileDescriptorProto.getMessageTypeList()) {
                    final String className = descriptorProto.getName();
                    ProtobufClass protobufClass = new ProtobufClass(fileDescriptorProto.getPackage(),
                        fileDescriptorProto.getOptions().getJavaOuterClassname(), className);
                    messageTypes.put(className, protobufClass);
                }
            }
        } catch (IOException e) {
            throw new EtiqetRuntimeException("Unable to initialize protobuf dictionary for config path " + configPath);
        }
    }

    @Override
    public String getMsgType(String messageName) {
        ProtobufClass protobufClass = messageTypes.get(messageName);
        if (protobufClass == null) {
            throw new EtiqetRuntimeException("Unable to determine protobuf class for message " + messageName +
                ". Available message types: " + messageTypes.keySet().stream().collect(joining(", ")));
        }
        return Stream.of(
                Stream.of(protobufClass.getPackagePath(), protobufClass.getJavaOuterClassName())
                    .filter(StringUtils::isNotEmpty)
                    .collect(joining(".")),
                protobufClass.getClassName())
            .filter(StringUtils::isNotEmpty)
            .collect(joining("$"));
    }

    public List<String> getMessageNames() {
        return new LinkedList<>(messageTypes.keySet());
    }


    @Override
    public String getMsgName(String messageType) {
        String[] splitMessage = messageType.split("(\\.|\\$)");
        return splitMessage[splitMessage.length - 1];
    }

    @Override
    public String getNameForTag(Integer tag) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Integer getTagForName(String n) throws UnknownTagException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean tagContains(Integer tag) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isHeaderField(String fieldName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isHeaderField(Integer tag) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isAdmin(String messageName) {
        throw new UnsupportedOperationException();
    }

}
