package com.neueda.etiqet.core.transport;

import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import com.neueda.etiqet.core.config.dtos.Message;
import com.neueda.etiqet.core.message.cdr.Cdr;
import com.neueda.etiqet.core.message.cdr.CdrItem;
import com.neueda.etiqet.core.message.config.ProtocolConfig;
import com.neueda.etiqet.core.util.ParserUtils;
import org.junit.Before;
import org.junit.Test;
import com.example.tutorial.AddressBookProtos;

import java.util.*;

import static com.neueda.etiqet.core.message.CdrBuilder.aCdr;
import static com.neueda.etiqet.core.message.CdrItemBuilder.aCdrItem;
import static com.neueda.etiqet.core.message.cdr.CdrItem.CdrItemType.CDR_ARRAY;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ProtobufCodecTest {
    private static final String PERSON = "Person";
    private static final String ADDRESS_BOOK = "AddressBook";
    private static final String PERSON_WITH_REQUIRED_ADDRESS = "PersonWithRequiredAddress";
    private static final String PERSON_WITH_MULTIPLE_FIELD_TYPES = "PersonWithMultipleFieldTypes";
    private Codec<Cdr, byte[]> codec;

    @Before
    public void setup() {
        ProtocolConfig protocolConfig = mock(ProtocolConfig.class);

        Message message = mock(Message.class);
        when(message.getImplementation()).thenReturn("config/protobuf/addressbook.proto");
        when(protocolConfig.getMessage(anyString())).thenAnswer(invocation -> {
            String msgName = invocation.getArgument(0, String.class);
            when(message.getName()).thenReturn(msgName);
            return message;
        });
        when(protocolConfig.getMessages()).thenReturn(new Message[]{message});

        codec = new ProtobufCodec();
        codec.setProtocolConfig(protocolConfig);
    }

    @Test
    public void testPerson() throws EtiqetException {
        Cdr cdr = aCdr(PERSON)
            .withField("name", "person name")
            .withField("email", "aaa@aaa.aaa")
            .withField("id", "23")
            .build();

        byte[] message = codec.encode(cdr);
        Map<String, CdrItem> result = codec.decode(message).getItems();

        assertEquals("person name", result.get("name").getStrval());
        assertEquals("aaa@aaa.aaa", result.get("email").getStrval());
        assertEquals(23, Math.round(result.get("id").getIntval()));
    }

    @Test
    public void testPersonWithNestedPhoneNumber() throws EtiqetException {
        Cdr cdr = aCdr(PERSON)
            .withField("name", "person name")
            .withField("email", "aaa@aaa.aaa")
            .withField("id", "23")
            .withCdrItem("phones",
                         aCdrItem(CDR_ARRAY)
                             .addCdr(
                                 aCdr("NONE")
                                     .withField("number", "789012345")
                                     .build()
                             ).build()
            ).build();

        byte[] message = codec.encode(cdr);
        Map<String, CdrItem> result = codec.decode(message).getItems();

        assertEquals("person name", result.get("name").getStrval());
        assertEquals("aaa@aaa.aaa", result.get("email").getStrval());
        assertEquals(23, Math.round(result.get("id").getIntval()));
        List<Cdr> phones = result.get("phones").getCdrs();
        assertEquals(1, phones.size());
        assertEquals("789012345", ParserUtils.getTagValueFromCdr("number", phones.get(0)));
    }

    @Test
    public void testPersonWithTwoPhoneNumbers() throws EtiqetException {
        Cdr cdr = aCdr(PERSON)
            .withField("name", "person name")
            .withField("email", "aaa@aaa.aaa")
            .withField("id", "23")
            .withCdrItem("phones",
                         aCdrItem(CDR_ARRAY)
                             .addCdr(
                                 aCdr("0")
                                     .withCdrItem("0", aCdrItem(CDR_ARRAY)
                                         .addCdr(aCdr("phone")
                                                     .withField("number", "789012345")
                                                     .build())
                                         .build())
                                     .build()
                             ).addCdr(
                             aCdr("1")
                                 .withCdrItem("1", aCdrItem(CDR_ARRAY)
                                     .addCdr(aCdr("phone")
                                                 .withField("number", "123")
                                                 .build())
                                     .build())
                                 .build()
                         ).build()
            ).build();

        byte[] message = codec.encode(cdr);
        Map<String, CdrItem> result = codec.decode(message).getItems();

        assertEquals("person name", result.get("name").getStrval());
        assertEquals("aaa@aaa.aaa", result.get("email").getStrval());
        assertEquals(23, Math.round(result.get("id").getIntval()));
        List<Cdr> phones = result.get("phones").getCdrs();
        assertEquals(2, phones.size());
    }

    @Test
    public void testPersonWithNestedPhoneNumberAndPhoneEnumType() throws EtiqetException {
        Cdr cdr = aCdr(PERSON)
            .withField("name", "person name")
            .withField("email", "aaa@aaa.aaa")
            .withField("id", "23")
            .withCdrItem("phones",
                         aCdrItem(CDR_ARRAY)
                             .addCdr(
                                 aCdr("NONE")
                                     .withField("number", "789012345")
                                     .withField("type", AddressBookProtos.Person.PhoneType.HOME.name())
                                     .build()
                             ).build()
            ).build();

        byte[] message = codec.encode(cdr);
        Map<String, CdrItem> result = codec.decode(message).getItems();

        assertEquals("person name", result.get("name").getStrval());
        assertEquals("aaa@aaa.aaa", result.get("email").getStrval());
        assertEquals(23, Math.round(result.get("id").getIntval()));
        List<Cdr> phones = result.get("phones").getCdrs();
        assertEquals(1, phones.size());
        assertEquals("789012345", ParserUtils.getTagValueFromCdr("number", phones.get(0)));
        assertEquals(String.valueOf(AddressBookProtos.Person.PhoneType.HOME.ordinal()),
                     ParserUtils.getTagValueFromCdr("type", phones.get(0)));
    }

    @Test
    public void testAddressBook() throws EtiqetException {
        Cdr cdr = aCdr(ADDRESS_BOOK)
            .withCdrItem("people",
                         aCdrItem(CDR_ARRAY)
                             .addCdr(
                                 aCdr("0")
                                     .withCdrItem("0",
                                                  aCdrItem(CDR_ARRAY)
                                                      .addCdr(aCdr("person").withField("name", "person name")
                                                                            .withField("email", "aaa@aaa.aaa")
                                                                            .withField("id", 23)
                                                                            .build())
                                                      .build())
                                     .build()
                             )
                             .addCdr(
                                 aCdr("1")
                                     .withCdrItem("0",
                                                  aCdrItem(CDR_ARRAY)
                                                      .addCdr(aCdr("person").withField("name", "person 2")
                                                                            .withField("email", "bbb@bbb.bbb")
                                                                            .withField("id", 4)
                                                                            .build())
                                                      .build())
                                     .build()
                             ).build()
            ).build();

        byte[] message = codec.encode(cdr);
        Map<String, CdrItem> result = codec.decode(message).getItems();

        List<Cdr> people = result.get("people").getCdrs();
        assertEquals(2, people.size());
        assertTrue(people.get(0)
                         .getItem("0")
                         .getCdrs()
                         .contains(aCdr("CDR_STRING").withField("email", "aaa@aaa.aaa").build()));
        assertTrue(people.get(1)
                         .getItem("1")
                         .getCdrs()
                         .contains(aCdr("CDR_STRING").withField("email", "bbb@bbb.bbb").build()));
    }

    @Test
    public void testPersonWithRequiredAddress() throws EtiqetException {
        Cdr cdr = aCdr(PERSON_WITH_REQUIRED_ADDRESS)
            .withField("id", "23")
            .withCdrItem("address",
                         aCdrItem(CDR_ARRAY)
                             .addCdr(
                                 aCdr("NONE")
                                     .withField("city", "city")
                                     .withField("street", "street")
                                     .build()
                             ).build()
            ).build();

        byte[] message = codec.encode(cdr);
        Cdr result = codec.decode(message);

        assertEquals(23, Math.round(result.getItem("id").getIntval()));
        assertEquals("city", ParserUtils.getTagValueFromCdr("city", result));
        assertEquals("street", ParserUtils.getTagValueFromCdr("street", result));
    }

    @Test
    public void testMultipleFieldTypes_long() throws EtiqetException {
        long longId = 10000L;

        Cdr cdr = aCdr(PERSON_WITH_MULTIPLE_FIELD_TYPES)
            .withField("longId", longId)
            .build();

        byte[] message = codec.encode(cdr);
        Map<String, CdrItem> result = codec.decode(message).getItems();
        assertEquals(longId, (long) result.get("longId").getIntval());
    }

    @Test
    public void testMultipleFieldTypes_boolean() throws EtiqetException {
        Cdr cdr = aCdr(PERSON_WITH_MULTIPLE_FIELD_TYPES)
            .withField("eligible", true)
            .build();

        byte[] message = codec.encode(cdr);
        Map<String, CdrItem> result = codec.decode(message).getItems();
        assertTrue(result.get("eligible").getBoolVal());
    }

    @Test
    public void testMultipleFieldTypes_double() throws EtiqetException {
        Cdr cdr = aCdr(PERSON_WITH_MULTIPLE_FIELD_TYPES)
            .withField("weight", 89.95)
            .build();

        byte[] message = codec.encode(cdr);
        Map<String, CdrItem> result = codec.decode(message).getItems();
        assertEquals(89.95, result.get("weight").getDoubleval(), 0);
    }

    @Test
    public void testMultipleFieldTypes_float() throws EtiqetException {
        Cdr cdr = aCdr(PERSON_WITH_MULTIPLE_FIELD_TYPES)
            .withField("height", 1.89)
            .build();

        byte[] message = codec.encode(cdr);
        Map<String, CdrItem> result = codec.decode(message).getItems();
        assertEquals(1.89f, result.get("height").getDoubleval().floatValue(), 0);
    }

    @Test
    public void testMultipleFieldTypes_fixedInt() throws EtiqetException {
        Cdr cdr = aCdr(PERSON_WITH_MULTIPLE_FIELD_TYPES)
            .withField("fixed32", 32)
            .build();

        byte[] message = codec.encode(cdr);
        Map<String, CdrItem> result = codec.decode(message).getItems();
        assertEquals(32, Math.round(result.get("fixed32").getIntval()));
    }

    @Test
    public void testInvalidClass() {
        Cdr cdr = aCdr("InvalidClass").build();
        try {
            codec.encode(cdr);
            fail("Should have thrown exception when marshalling to invalid class");
        } catch (EtiqetException e) {
            assertTrue(e.getMessage()
                        .contains(
                            "has no message type with name 'InvalidClass': known types: [Person, AddressBook, PersonWithRequiredAddress, PersonWithMultipleFieldTypes]"));
        }
    }

}
