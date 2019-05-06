package com.neueda.etiqet.fixture;

import com.neueda.etiqet.core.message.cdr.Cdr;
import com.neueda.etiqet.core.message.cdr.CdrItem;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class FixFixturesTest {

    private FixFixtures fixtures;
    private EtiqetHandlers handlers;

    @Before
    public void setUp() {
        handlers = mock(EtiqetHandlers.class);
        fixtures = new FixFixtures(handlers);
    }

    @Test
    public void testCreateRepeatingGroup_GroupAddedToMessage() {
        Cdr fixMsg = new Cdr("TEST_FIX");
        when(handlers.getSentMessage("testMessage")).thenReturn(fixMsg);

        fixtures.createRepeatingGroup("Parties", "testMessage");

        assertTrue(fixMsg.containsKey("Parties"));
        CdrItem parties = fixMsg.getItem("Parties");
        assertNotNull(parties);
        assertNotNull(parties.getCdrs());
    }

    @Test
    public void testCreateRepeatingGroup_MessageNotFound() {
        try {
            fixtures.createRepeatingGroup("Parties", "testMessage");
            fail("Should have thrown an assertion error because the message wasn't found");
        } catch (Throwable t) {
            assertTrue(t instanceof AssertionError);
            assertEquals("Could not find message testMessage", t.getMessage());
        }
    }

    @Test
    public void testAddFieldsToRepeatingGroup_GroupAddedToCdr() {
        Cdr party1 = new Cdr("Parties");
        party1.set("PartyID", "EXAMPLE_LEI");
        party1.set("PartyRole", "13");

        Cdr party2 = new Cdr("Parties");
        party2.set("PartyID", "EXAMPLE_LEI_2");
        party2.set("PartyRole", "17");
        List<Cdr> partiesList = new ArrayList<>(2);
        partiesList.add(party1);
        partiesList.add(party2);

        CdrItem fields = new CdrItem(CdrItem.CdrItemType.CDR_ARRAY);
        fields.setCdrs(partiesList);
        Cdr fixMsg = new Cdr("TEST_FIX");
        fixMsg.setItem("Parties", fields);

        when(handlers.getSentMessage(eq("testMessage"))).thenReturn(fixMsg);
        when(handlers.preTreatParams(anyString())).thenCallRealMethod();

        fixtures.addFieldsToRepeatingGroup("PartyID=PRIME_BROKER,PartyRole=1", "Parties", "testMessage");

        assertEquals(1, fixMsg.getItems().size());
        CdrItem partiesGroup = fixMsg.getItem("Parties");
        assertNotNull(partiesGroup);
        assertEquals(CdrItem.CdrItemType.CDR_ARRAY, partiesGroup.getType());
        List<Cdr> partiesCdrList = partiesGroup.getCdrs();
        assertNotNull(partiesCdrList);
        assertEquals(3, partiesCdrList.size());
        assertTrue(partiesCdrList.contains(party1));
        assertTrue(partiesCdrList.contains(party2));

        Cdr expectedParty3 = new Cdr("Parties");
        expectedParty3.set("PartyID", "PRIME_BROKER");
        expectedParty3.set("PartyRole", "1");
        assertTrue(partiesCdrList.contains(expectedParty3));
    }

    @Test
    public void testAddFieldsToRepeatingGroup_MessageNotFound() {
        try {
            fixtures.addFieldsToRepeatingGroup("PartyID=PRIME_BROKER,PartyRole=1", "Parties", "testMessage");
            fail("Should have thrown an AssertionError because no message returned");
        } catch (Throwable t) {
            assertTrue(t instanceof AssertionError);
            assertEquals("Could not find message testMessage", t.getMessage());
        }
    }

    @Test
    public void testAddFieldsToRepeatingGroup_GroupNotFound() {
        try {
            when(handlers.getSentMessage(eq("testMessage"))).thenReturn(new Cdr("Test"));
            fixtures.addFieldsToRepeatingGroup("PartyID=PRIME_BROKER,PartyRole=1", "Parties", "testMessage");
            fail("Should have thrown an AssertionError because no group found");
        } catch (Throwable t) {
            assertTrue(t instanceof AssertionError);
            assertEquals("Could not find group Parties in message testMessage", t.getMessage());
        }
    }

    @Test
    public void testAddFieldsToRepeatingGroup_GroupNotArrayType() {
        try {
            Cdr cdr = new Cdr("Test");
            CdrItem item = new CdrItem(CdrItem.CdrItemType.CDR_STRING);
            item.setStrval("Not an Array");
            cdr.setItem("Parties", item);
            when(handlers.getSentMessage(eq("testMessage"))).thenReturn(cdr);
            fixtures.addFieldsToRepeatingGroup("PartyID=PRIME_BROKER,PartyRole=1", "Parties", "testMessage");
            fail("Should have thrown an AssertionError because group is a String type, not Array");
        } catch (Throwable t) {
            assertTrue(t instanceof AssertionError);
            assertEquals("Requested Parties doesn't appear to be a group, was CDR_STRING expected:<CDR_ARRAY> but was:<CDR_STRING>", t.getMessage());
        }
    }

    @Test
    public void testAddFieldsToRepeatingGroup_GroupAddedToCdrNoChildren() {
        Cdr fixMsg = new Cdr("TEST_FIX");
        fixMsg.setItem("Parties", new CdrItem(CdrItem.CdrItemType.CDR_ARRAY));

        when(handlers.getSentMessage(eq("testMessage"))).thenReturn(fixMsg);
        when(handlers.preTreatParams(anyString())).thenCallRealMethod();

        fixtures.addFieldsToRepeatingGroup("PartyID=PRIME_BROKER,PartyRole=1", "Parties", "testMessage");

        assertEquals(1, fixMsg.getItems().size());
        CdrItem partiesGroup = fixMsg.getItem("Parties");
        assertNotNull(partiesGroup);
        assertEquals(CdrItem.CdrItemType.CDR_ARRAY, partiesGroup.getType());
        List<Cdr> partiesCdrList = partiesGroup.getCdrs();
        assertNotNull(partiesCdrList);
        assertEquals(1, partiesCdrList.size());

        Cdr expectedParty3 = new Cdr("Parties");
        expectedParty3.set("PartyID", "PRIME_BROKER");
        expectedParty3.set("PartyRole", "1");
        assertTrue(partiesCdrList.contains(expectedParty3));
    }

    @Test
    public void testAddGroupToCdr_NonNullMessage() {
        Cdr message = new Cdr("Test");
        assertFalse(message.containsKey("Parties"));
        fixtures.addGroupToCdr("Parties", message);
        assertTrue(message.containsKey("Parties"));
        CdrItem parties = message.getItem("Parties");
        assertNotNull(parties);
        assertEquals(CdrItem.CdrItemType.CDR_ARRAY, parties.getType());
        assertEquals(new ArrayList<Cdr>(), parties.getCdrs());
    }

    @Test
    public void testAddGroupToCdr_NullMessage() {
        try {
            fixtures.addGroupToCdr("Parties", null);
            fail("Should have failed for null message");
        } catch (Throwable t) {
            assertTrue(t instanceof AssertionError);
            assertEquals("No message found to add group Parties", t.getMessage());
        }
    }

}
