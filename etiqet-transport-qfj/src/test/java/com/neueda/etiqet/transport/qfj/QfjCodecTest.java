package com.neueda.etiqet.transport.qfj;

import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import com.neueda.etiqet.core.config.dtos.Message;
import com.neueda.etiqet.core.message.cdr.Cdr;
import com.neueda.etiqet.core.message.cdr.CdrItem;
import com.neueda.etiqet.core.message.config.ProtocolConfig;
import com.neueda.etiqet.fix.message.dictionary.Fix;
import com.neueda.etiqet.fix.message.dictionary.FixDictionary;
import org.junit.Before;
import org.junit.Test;
import quickfix.FieldNotFound;
import quickfix.fix44.NewOrderSingle;

import javax.xml.bind.JAXBContext;
import java.util.Arrays;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class QfjCodecTest {

    private ProtocolConfig protocolConfig;

    @Before
    public void setUp() throws Exception {
        protocolConfig = mock(ProtocolConfig.class);
    }

    @Test
    public void testBasicMessageTypes() throws EtiqetException, FieldNotFound {
        Cdr newOrderSingle = new Cdr("NewOrderSingle");
        newOrderSingle.set("Symbol", "ZINC.MI");
        newOrderSingle.set("LastPx", 123.45d);
        newOrderSingle.set("LastQty", 1);
        newOrderSingle.set("AggregatedBook", true);
        Message message = mock(Message.class);
        when(message.getImplementation()).thenReturn(NewOrderSingle.class.getName());
        when(protocolConfig.getMessage(eq("NewOrderSingle"))).thenReturn(message);
        when(protocolConfig.getTagForName(eq("Symbol"))).thenReturn(55);
        when(protocolConfig.getTagForName(eq("LastPx"))).thenReturn(31);
        when(protocolConfig.getTagForName(eq("LastQty"))).thenReturn(32);
        when(protocolConfig.getTagForName(eq("AggregatedBook"))).thenReturn(266);

        QfjCodec codec = new QfjCodec(protocolConfig);
        quickfix.FieldMap fixMessage = codec.encode(newOrderSingle);
        assertTrue(fixMessage instanceof NewOrderSingle);
        assertEquals("ZINC.MI", fixMessage.getString(55));
        assertEquals(123.45d, fixMessage.getDouble(31), 0);
        assertEquals(1, fixMessage.getInt(32));
        assertTrue(fixMessage.getBoolean(266));
    }

    @Test
    public void testGroups() throws Exception {
        Cdr newOrderSingle = new Cdr("NewOrderSingle");
        newOrderSingle.set("Symbol", "ZINC.MI");
        newOrderSingle.set("LastPx", 123.45d);
        newOrderSingle.set("LastQty", 1);
        newOrderSingle.set("AggregatedBook", true);

        Cdr originatorParty = new Cdr("NoPartyIDs");
        originatorParty.set("PartyID", "TEST_LEI");
        originatorParty.set("PartyRole", 13);

        Cdr counterParty = new Cdr("NoPartyIDs");
        counterParty.set("PartyID", "ANOTHER_TEST_LEI");
        counterParty.set("PartyRole", 17);

        CdrItem partiesItem = new CdrItem(CdrItem.CdrItemType.CDR_ARRAY);
        partiesItem.setCdrs(Arrays.asList(originatorParty, counterParty));

        newOrderSingle.setItem("NoPartyIDs", partiesItem);

        Message orderSingle = mock(Message.class);
        when(orderSingle.getImplementation()).thenReturn(NewOrderSingle.class.getName());
        when(protocolConfig.getMessage(eq("NewOrderSingle"))).thenReturn(orderSingle);

        Message noParties = mock(Message.class);
        when(noParties.getImplementation()).thenReturn(NewOrderSingle.NoPartyIDs.class.getName());
        when(protocolConfig.getMessage(eq("NoPartyIDs"))).thenReturn(noParties);
        when(protocolConfig.getTagForName(eq("Symbol"))).thenReturn(55);
        when(protocolConfig.getTagForName(eq("LastPx"))).thenReturn(31);
        when(protocolConfig.getTagForName(eq("LastQty"))).thenReturn(32);
        when(protocolConfig.getTagForName(eq("AggregatedBook"))).thenReturn(266);
        when(protocolConfig.getTagForName(eq("PartyID"))).thenReturn(448);
        when(protocolConfig.getTagForName(eq("PartyRole"))).thenReturn(452);
        when(protocolConfig.getTagForName(eq("NoPartyIDs"))).thenReturn(453);


        Fix fixDictionary = (Fix) JAXBContext.newInstance(Fix.class)
                                             .createUnmarshaller()
                                             .unmarshal(getClass().getClassLoader()
                                                                  .getResourceAsStream("config/FIX50SP2.xml"));

        FixDictionary dictionary = mock(FixDictionary.class);
        when(dictionary.getFixDictionary()).thenReturn(fixDictionary);
        when(protocolConfig.getDictionary()).thenReturn(dictionary);

        QfjCodec codec = new QfjCodec(protocolConfig);
        quickfix.FieldMap fixMessage = codec.encode(newOrderSingle);
        assertTrue(fixMessage instanceof NewOrderSingle);
        assertEquals("ZINC.MI", fixMessage.getString(55));
        assertEquals(123.45d, fixMessage.getDouble(31), 0);
        assertEquals(1, fixMessage.getInt(32));
        assertTrue(fixMessage.getBoolean(266));
        assertTrue(fixMessage.hasGroup(453));
    }

}
