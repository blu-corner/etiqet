package com.neueda.etiqet.core.message.dictionary;

import com.neueda.etiqet.core.common.exceptions.EtiqetRuntimeException;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.TestCase.assertEquals;

public class ProtobufDictionaryTest {

    private ProtobufDictionaryStub protobufDictionary;

    @Before
    public void setup() {
        try {
            protobufDictionary = new ProtobufDictionaryStub("aFile.desc");
        } catch (EtiqetRuntimeException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testGetMsgType_noPackageOrWrapperClass() {
        Map<String, ProtobufClass> messageTypes = new HashMap<>();
        messageTypes.put("testClass", new ProtobufClass(null, null, "PersonClass"));
        protobufDictionary.setMessageTypes(messageTypes);

        final String messageType = protobufDictionary.getMsgType("testClass");

        assertEquals("PersonClass", messageType);
    }

    @Test
    public void testGetMsgType_emptyPackageOrWrapperClass() {
        Map<String, ProtobufClass> messageTypes = new HashMap<>();
        messageTypes.put("testClass", new ProtobufClass("", "", "PersonClass"));
        protobufDictionary.setMessageTypes(messageTypes);

        final String messageType = protobufDictionary.getMsgType("testClass");

        assertEquals("PersonClass", messageType);
    }

    @Test
    public void testGetMsgType() {
        Map<String, ProtobufClass> messageTypes = new HashMap<>();
        messageTypes.put("testClass", new ProtobufClass("com.package", "ClassWrapper", "PersonClass"));
        protobufDictionary.setMessageTypes(messageTypes);

        final String messageType = protobufDictionary.getMsgType("testClass");

        assertEquals("com.package.ClassWrapper$PersonClass", messageType);
    }

    @Test
    public void testGetMsgName_withOuterClassAndPackage() {
        assertEquals("PersonClass", protobufDictionary.getMsgName("com.package.ClassWrapper$PersonClass"));
    }

    @Test
    public void testGetMsgName() {
        assertEquals("PersonClass", protobufDictionary.getMsgName("PersonClass"));
    }

    @Test
    public void testGetMsgName_withOuterClass() {
        assertEquals("PersonClass", protobufDictionary.getMsgName("ClassWrapper$PersonClass"));
    }

    @Test
    public void testGetMsgName_withPackage() {
        assertEquals("PersonClass", protobufDictionary.getMsgName("com.package.PersonClass"));
    }

    class ProtobufDictionaryStub extends ProtobufDictionary {

        public ProtobufDictionaryStub(String configPath) {
            super(configPath);
        }

        void setMessageTypes(Map<String, ProtobufClass> messageTypes) {
            this.messageTypes = messageTypes;
        }
    }
}
