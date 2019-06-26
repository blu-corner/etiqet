package com.neueda.etiqet.core.transport;

import com.google.protobuf.Message;
import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import com.neueda.etiqet.core.message.cdr.Cdr;
import com.neueda.etiqet.core.message.cdr.CdrItem;
import org.junit.Before;
import org.junit.Test;
import com.example.tutorial.AddressBookProtos;

import java.util.List;
import java.util.Map;

import static com.neueda.etiqet.core.message.CdrBuilder.aCdr;
import static com.neueda.etiqet.core.message.CdrItemBuilder.aCdrItem;
import static com.neueda.etiqet.core.message.cdr.CdrItem.CdrItemType.CDR_ARRAY;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class ProtobufCodecTest {
    private Codec<Cdr, Message> codec;

    @Before
    public void setup() {
        codec = new ProtobufCodec();
    }

    @Test
    public void testPerson() throws EtiqetException {
        Cdr cdr = aCdr(AddressBookProtos.Person.class.getName())
            .withField("name", "person name")
            .withField("email", "aaa@aaa.aaa")
            .withField("id", "23")
            .build();

        Message message = codec.encode(cdr);
        Map<String, CdrItem> result = codec.decode(message).getItems();

        assertEquals("person name", result.get("name").getStrval());
        assertEquals("aaa@aaa.aaa", result.get("email").getStrval());
        assertEquals(23, Math.round(result.get("id").getIntval()));
    }

    @Test
    public void testPersonWithNestedPhoneNumber() throws EtiqetException {
        Cdr cdr = aCdr(AddressBookProtos.Person.class.getName())
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

        Message message = codec.encode(cdr);
        Map<String, CdrItem> result = codec.decode(message).getItems();

        assertEquals("person name", result.get("name").getStrval());
        assertEquals("aaa@aaa.aaa", result.get("email").getStrval());
        assertEquals(23, Math.round(result.get("id").getIntval()));
        List<Cdr> phones = result.get("phones").getCdrs();
        assertEquals(1, phones.size());
        assertEquals("789012345", phones.get(0).getItem("number").getStrval());
    }

    @Test
    public void testPersonWithTwoPhoneNumbers() throws EtiqetException {
        Cdr cdr = aCdr(AddressBookProtos.Person.class.getName())
            .withField("name", "person name")
            .withField("email", "aaa@aaa.aaa")
            .withField("id", "23")
            .withCdrItem("phones",
                aCdrItem(CDR_ARRAY)
                    .addCdr(
                        aCdr("NONE")
                            .withField("number", "789012345")
                            .build()
                    ).addCdr(
                        aCdr("NONE")
                            .withField("number", "123")
                            .build()
                    ).build()
            ).build();

        Message message = codec.encode(cdr);
        Map<String, CdrItem> result = codec.decode(message).getItems();

        assertEquals("person name", result.get("name").getStrval());
        assertEquals("aaa@aaa.aaa", result.get("email").getStrval());
        assertEquals(23, Math.round(result.get("id").getIntval()));
        List<Cdr> phones = result.get("phones").getCdrs();
        assertEquals(2, phones.size());
        assertEquals("789012345", phones.get(0).getItem("number").getStrval());
        assertEquals("123", phones.get(1).getItem("number").getStrval());
    }

    @Test
    public void testPersonWithNestedPhoneNumberAndPhoneEnumType() throws EtiqetException {
        Cdr cdr = aCdr(AddressBookProtos.Person.class.getName())
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

        Message message = codec.encode(cdr);
        Map<String, CdrItem> result = codec.decode(message).getItems();

        assertEquals("person name", result.get("name").getStrval());
        assertEquals("aaa@aaa.aaa", result.get("email").getStrval());
        assertEquals(23, Math.round(result.get("id").getIntval()));
        List<Cdr> phones = result.get("phones").getCdrs();
        assertEquals(1, phones.size());
        assertEquals("789012345", phones.get(0).getItem("number").getStrval());
        assertEquals("HOME", phones.get(0).getItem("type").getStrval());
    }

    @Test
    public void testAddressBook() throws EtiqetException {
        Cdr cdr = aCdr(AddressBookProtos.AddressBook.class.getName())
            .withCdrItem("people",
                aCdrItem(CDR_ARRAY)
                    .addCdr(
                        aCdr("NONE")
                            .withField("name", "person name")
                            .withField("email", "aaa@aaa.aaa")
                            .withField("id", "23")
                            .build()
                    ).addCdr(
                        aCdr("NONE")
                            .withField("name", "person 2")
                            .withField("email", "bbb@bbb.bbb")
                            .withField("id", "4")
                            .build()
                    ).build()
            ).build();

        Message message = codec.encode(cdr);
        Map<String, CdrItem> result = codec.decode(message).getItems();

        List<Cdr> people = result.get("people").getCdrs();
        assertEquals(2, people.size());
        assertEquals("aaa@aaa.aaa", people.get(0).getAsString("email"));
        assertEquals("bbb@bbb.bbb", people.get(1).getAsString("email"));
    }

    @Test
    public void testPersonWithRequiredAddress() throws EtiqetException {
        Cdr cdr = aCdr(AddressBookProtos.PersonWithRequiredAddress.class.getName())
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

        Message message = codec.encode(cdr);
        Map<String, CdrItem> result = codec.decode(message).getItems();

        assertEquals(23, Math.round(result.get("id").getIntval()));
        List<Cdr> addressList = result.get("address").getCdrs();
        assertEquals(1, addressList.size());
        Cdr address = addressList.get(0);
        assertEquals("city", address.getAsString("city"));
        assertEquals("street", address.getAsString("street"));
    }

    @Test
    public void testMultipleFieldTypes_long() throws EtiqetException {
        long longId = Long.valueOf(Integer.MAX_VALUE + 100);

        Cdr cdr = aCdr(AddressBookProtos.PersonWithMultipleFieldTypes.class.getName())
            .withField("longId", longId)
            .build();

        Message message = codec.encode(cdr);
        Map<String, CdrItem> result = codec.decode(message).getItems();
        assertEquals(longId, (long) result.get("longId").getIntval());
    }

    @Test
    public void testMultipleFieldTypes_boolean() throws EtiqetException {
        Cdr cdr = aCdr(AddressBookProtos.PersonWithMultipleFieldTypes.class.getName())
            .withField("eligible", true)
            .build();

        Message message = codec.encode(cdr);
        Map<String, CdrItem> result = codec.decode(message).getItems();
        assertTrue(result.get("eligible").getBoolVal());
    }

    @Test
    public void testMultipleFieldTypes_double() throws EtiqetException {
        Cdr cdr = aCdr(AddressBookProtos.PersonWithMultipleFieldTypes.class.getName())
            .withField("weight", 89.95)
            .build();

        Message message = codec.encode(cdr);
        Map<String, CdrItem> result = codec.decode(message).getItems();
        assertEquals(89.95, result.get("weight").getDoubleval());
    }

    @Test
    public void testMultipleFieldTypes_float() throws EtiqetException {
        Cdr cdr = aCdr(AddressBookProtos.PersonWithMultipleFieldTypes.class.getName())
            .withField("height", 1.89)
            .build();

        Message message = codec.encode(cdr);
        Map<String, CdrItem> result = codec.decode(message).getItems();
        assertEquals(1.89f, result.get("height").getDoubleval().floatValue());
    }

    @Test
    public void testMultipleFieldTypes_fixedInt() throws EtiqetException {
        Cdr cdr = aCdr(AddressBookProtos.PersonWithMultipleFieldTypes.class.getName())
            .withField("fixed32", 32)
            .build();

        Message message = codec.encode(cdr);
        Map<String, CdrItem> result = codec.decode(message).getItems();
        assertEquals(32, Math.round(result.get("fixed32").getIntval()));
    }
}
