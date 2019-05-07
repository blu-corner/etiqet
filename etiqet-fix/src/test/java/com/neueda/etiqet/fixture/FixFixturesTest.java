package com.neueda.etiqet.fixture;

import com.neueda.etiqet.core.message.cdr.Cdr;
import com.neueda.etiqet.core.message.cdr.CdrItem;
import edu.emory.mathcs.backport.java.util.Collections;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
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

    @Test
    public void testGetGroupWithFilter_NoFilterSpecified() {
        Cdr message = new Cdr("Test");
        message.set("field", "value");

        CdrItem field = fixtures.getGroupWithFilter(message, "field");
        CdrItem expected = new CdrItem(CdrItem.CdrItemType.CDR_STRING);
        expected.setStrval("value");
        assertEquals(expected, field);
    }

    @Test
    public void testGetGroupWithFilter_FilterApplied() {
        CdrItem field = new CdrItem(CdrItem.CdrItemType.CDR_ARRAY);
        Cdr child1 = new Cdr("Test");
        child1.set("child", "value1");
        Cdr child2 = new Cdr("Test");
        child2.set("child", "value2");
        field.setCdrs(Arrays.asList(child1, child2));

        Cdr parent = new Cdr("Parent");
        parent.setItem("Test", field);

        CdrItem filteredGroup = fixtures.getGroupWithFilter(parent, "Test[child=value1]");
        assertEquals(field, filteredGroup);

        filteredGroup = fixtures.getGroupWithFilter(parent, "Test[child=value2]");
        assertEquals(field, filteredGroup);
    }

    @Test
    public void testGetGroupWithFilter_InvalidFilter_MissingEnd() {
        try {
            fixtures.getGroupWithFilter(new Cdr("Test"), "Test[");
            fail("Should have failed because there is no closing filter bracket");
        } catch (Throwable t) {
            assertTrue(t instanceof AssertionError);
            assertEquals("Could not find end of filter ']'. Actual: -1", t.getMessage());
        }
    }

    @Test
    public void testGetGroupWithFilter_InvalidFilter_MissingStart() {
        try {
            fixtures.getGroupWithFilter(new Cdr("Test"), "Test]");
            fail("Should have failed because there is no opening filter bracket");
        } catch (Throwable t) {
            assertTrue(t instanceof AssertionError);
            assertEquals("Could not find start of filter '['. Actual: -1", t.getMessage());
        }
    }

    @Test
    public void testGetGroupWithFilter_InvalidFilter_MissingField() {
        try {
            fixtures.getGroupWithFilter(new Cdr("Test"), "Test[=value]");
            fail("Should have failed because there is no field");
        } catch (Throwable t) {
            assertTrue(t instanceof AssertionError);
            assertEquals("No field specified in group filter Test[=value]", t.getMessage());
        }
    }

    @Test
    public void testGetGroupWithFilter_InvalidFilter_MissingValue() {
        try {
            fixtures.getGroupWithFilter(new Cdr("Test"), "Test[field=]");
            fail("Should have failed because there is no value");
        } catch (Throwable t) {
            assertTrue(t instanceof AssertionError);
            assertEquals("No value specified in group filter Test[field=] expected:<2> but was:<1>", t.getMessage());
        }
    }

    @Test
    public void testGetGroupWithFilter_InvalidFilter_FieldNotFound() {
        Cdr message = new Cdr("Test");
        message.set("field", "value");
        try {
            fixtures.getGroupWithFilter(message, "otherField[field=value]");
            fail("Should have failed because otherField wasn't defined");
        } catch (Throwable t) {
            assertTrue(t instanceof AssertionError);
            assertEquals("Could not find field otherField in message " + message, t.getMessage());
        }
    }

    @Test
    public void testGetGroupWithFilter_InvalidFilter_FieldNotArrayType() {
        Cdr message = new Cdr("Test");
        message.set("field", "value");
        try {
            fixtures.getGroupWithFilter(message, "field[field=value]");
            fail("Should have failed because field is a String type");
        } catch (Throwable t) {
            assertTrue(t instanceof AssertionError);
            assertEquals("Field field was not a group expected:<CDR_ARRAY> but was:<CDR_STRING>", t.getMessage());
        }
    }

    @Test
    public void testGetGroup_Nested() {
        CdrItem subfield2 = new CdrItem(CdrItem.CdrItemType.CDR_ARRAY);
        Cdr subChild1 = new Cdr("Test");
        subChild1.set("subChild", "value2");
        subChild1.set("subChild2", "value3");
        subfield2.setCdrs(Collections.singletonList(subChild1));

        CdrItem subfield = new CdrItem(CdrItem.CdrItemType.CDR_ARRAY);
        Cdr subChild2 = new Cdr("Test");
        subChild2.set("subChild", "value3");
        subfield.setCdrs(Collections.singletonList(subChild2));

        CdrItem field = new CdrItem(CdrItem.CdrItemType.CDR_ARRAY);
        Cdr child1 = new Cdr("Test");
        child1.set("child", "value1");
        child1.setItem("child2", subfield);
        Cdr child2 = new Cdr("Test");
        child2.set("child", "value2");
        child2.setItem("child2", subfield2);
        field.setCdrs(Arrays.asList(child1, child2));

        Cdr parent = new Cdr("Parent");
        parent.setItem("Test", field);

        CdrItem group = fixtures.getGroup(parent, "TestMessage", "Test/child2[subChild=value3]");
        assertNotNull(group);
    }

    @Test
    public void testGetGroup_NonNested() {
        CdrItem field = new CdrItem(CdrItem.CdrItemType.CDR_ARRAY);
        Cdr child1 = new Cdr("Test");
        child1.set("child", "value1");
        Cdr child2 = new Cdr("Test");
        child2.set("child", "value2");
        field.setCdrs(Arrays.asList(child1, child2));

        Cdr parent = new Cdr("Parent");
        parent.setItem("Test", field);

        CdrItem group = fixtures.getGroup(parent, "TestMessage", "Test");
        assertEquals(field, group);
    }

    @Test
    public void testGetGroup_NonNested_NotFound() {
        CdrItem field = new CdrItem(CdrItem.CdrItemType.CDR_ARRAY);
        Cdr child1 = new Cdr("Test");
        child1.set("child", "value1");
        Cdr child2 = new Cdr("Test");
        child2.set("child", "value2");
        field.setCdrs(Arrays.asList(child1, child2));

        Cdr parent = new Cdr("Parent");
        parent.setItem("Test", field);

        try {
            fixtures.getGroup(parent, "TestMessage", "Test2");
        } catch (Throwable t) {
            assertTrue(t instanceof AssertionError);
            assertEquals("Could not find group Test2 in message TestMessage", t.getMessage());
        }
    }

    @Test
    public void testGetGroup_NonNested_NotArrayType() {
        Cdr parent = new Cdr("Parent");
        parent.set("Test", "Value");
        try {
            fixtures.getGroup(parent, "TestMessage", "Test");
        } catch (Throwable t) {
            assertTrue(t instanceof AssertionError);
            assertEquals("Requested Test doesn't appear to be a group, was CDR_STRING expected:<CDR_ARRAY> but was:<CDR_STRING>",
                         t.getMessage());
        }
    }

}
