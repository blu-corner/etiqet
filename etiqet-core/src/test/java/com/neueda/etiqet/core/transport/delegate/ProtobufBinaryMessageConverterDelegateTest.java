package com.neueda.etiqet.core.transport.delegate;

import com.example.tutorial.AddressBookProtos;
import com.example.tutorial.AddressBookProtos.Person;
import com.example.tutorial.AddressBookProtos.PersonWithRequiredAddress;
import com.google.protobuf.Any;
import com.google.protobuf.Message;
import com.google.protobuf.UnknownFieldSet.Field;
import com.neueda.etiqet.core.message.config.AbstractDictionary;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Map;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.when;

public class ProtobufBinaryMessageConverterDelegateTest {

    private ProtobufBinaryMessageConverterDelegate delegate;
    @Mock private AbstractDictionary dictionary;

    @Before
    public void setup() {
        this.delegate = new ProtobufBinaryMessageConverterDelegate();
        MockitoAnnotations.initMocks(this);
        when(dictionary.getMessageNames()).thenReturn(Arrays.asList("Person", "PersonWithRequiredAddress"));
        when(dictionary.getMsgType("Person")).thenReturn(Person.class.getName());
        when(dictionary.getMsgType("PersonWithRequiredAddress")).thenReturn(PersonWithRequiredAddress.class.getName());
        this.delegate.setDictionary(dictionary);
    }

    @Test
    public void testPersonBinaryConversion() {
        Message message = Person.newBuilder()
            .setId(23)
            .setName("Person name")
            .build();

        byte[] binaryMessage = delegate.toByteArray(message);
        Message resultMessage = delegate.fromByteArray(binaryMessage);

        assertEquals(Person.class, resultMessage.getClass());
        Person resultPerson = (Person) resultMessage;
        assertEquals(23, resultPerson.getId());
        assertEquals("Person name", resultPerson.getName());
    }

    @Test
    public void testPersonWithAddressBinaryConversion() {
        Message message = PersonWithRequiredAddress.newBuilder()
            .setId(23)
            .setAddress(
                PersonWithRequiredAddress.Address.newBuilder()
                .setCity("city")
                .setStreet("street")
                .build()
            ).build();

        byte[] binaryMessage = delegate.toByteArray(message);
        Message resultMessage = delegate.fromByteArray(binaryMessage);

        assertEquals(PersonWithRequiredAddress.class, resultMessage.getClass());
        PersonWithRequiredAddress resultPerson = (PersonWithRequiredAddress) resultMessage;
        assertEquals(23, resultPerson.getId());
        assertEquals("city", resultPerson.getAddress().getCity());
        assertEquals("street", resultPerson.getAddress().getStreet());
    }

    @Test
    public void testTryDeserializeAsAnyWhenClassNotInDictionary() {
        Message message = AddressBookProtos.PersonWithMultipleFieldTypes.newBuilder()
            .setEligible(true)
            .setHeight(199.9f)
            .setWeight(99.9)
            .setLongId(1234)
            .build();

        byte[] binaryMessage = delegate.toByteArray(message);
        Message resultMessage = delegate.fromByteArray(binaryMessage);

        assertEquals(Any.class, resultMessage.getClass());
        Map<Integer, Field> values = resultMessage.getUnknownFields().asMap();
        assertEquals(1, (long) values.get(4).getVarintList().get(0));
        assertEquals(1234, (long) values.get(1).getVarintList().get(0));
        // Cannot compare float and long values since Protobuf is unable to decoding them properly without their description
    }

}
