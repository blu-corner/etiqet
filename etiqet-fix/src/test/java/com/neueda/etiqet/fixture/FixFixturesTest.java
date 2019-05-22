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
    public void testCreateRepeatingGroupWithFields() {
        Cdr fixMsg = new Cdr("TEST_FIX");
        when(handlers.getSentMessage("testMessage")).thenReturn(fixMsg);

        when(handlers.preTreatParams(anyString())).thenCallRealMethod();

        fixtures.createRepeatingGroupWithFields("NoSides", "Side=1,Currency=GBP", "testMessage");

        assertTrue(fixMsg.containsKey("NoSides"));
        CdrItem parties = fixMsg.getItem("NoSides");
        assertNotNull(parties);
        List<Cdr> childCdrs = parties.getCdrs();
        assertNotNull(childCdrs);
        assertEquals(1, childCdrs.size());
        Cdr child = childCdrs.get(0);
        assertTrue(child.containsKey("Side"));
        assertEquals("1", child.getAsString("Side"));
        assertTrue(child.containsKey("Currency"));
        assertEquals("GBP", child.getAsString("Currency"));
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
            assertEquals("Requested Parties doesn't appear to be a group", t.getMessage());
        }
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
        Cdr nested = new Cdr("nested");
        CdrItem children = new CdrItem(CdrItem.CdrItemType.CDR_ARRAY);
        children.addCdrToList(nested);

        Cdr message = new Cdr("Test");
        message.setItem("field", children);

        Cdr field = fixtures.getGroupWithFilter(message, "field");
        assertEquals(nested, field);
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

        Cdr filteredGroup = fixtures.getGroupWithFilter(parent, "Test[child=value1]");
        assertEquals(child1, filteredGroup);

        filteredGroup = fixtures.getGroupWithFilter(parent, "Test[child=value2]");
        assertEquals(child2, filteredGroup);
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

        Cdr group = fixtures.getGroup(parent, "TestMessage", "Test/child2[subChild=value3]");
        assertNotNull(group);
    }

    @Test
    public void testGetGroup_NonNested_NotArrayType() {
        Cdr parent = new Cdr("Parent");
        parent.set("Test", "Value");
        try {
            fixtures.getGroup(parent, "TestMessage", "Test");
        } catch (Throwable t) {
            assertTrue(t instanceof AssertionError);
            assertEquals("Requested Test doesn't appear to be a group", t.getMessage());
        }
    }

    @Test
    public void testCreateRepeatingGroupInGroup() {
        Cdr parent = new Cdr("Parent");
        CdrItem groupItem = new CdrItem(CdrItem.CdrItemType.CDR_ARRAY);
        groupItem.setCdrs(new ArrayList<>());
        parent.setItem("Child", groupItem);

        when(handlers.getSentMessage("Parent")).thenReturn(parent);
        fixtures.createRepeatingGroupInGroup("Child2", "Child", "Parent");

        assertEquals(1, groupItem.getCdrs().size());
        Cdr child = groupItem.getCdrs().get(0);
        assertEquals("Child", child.getType());
        assertTrue(child.containsKey("Child2"));
        assertEquals(CdrItem.CdrItemType.CDR_ARRAY, child.getItem("Child2").getType());
    }

    @Test
    public void testCreateRepeatingGroupInGroup_NoCdrList() {
        Cdr parent = new Cdr("Parent");
        CdrItem groupItem = new CdrItem(CdrItem.CdrItemType.CDR_ARRAY);
        groupItem.setCdrs(new ArrayList<>());
        parent.setItem("Child", groupItem);

        when(handlers.getSentMessage("Parent")).thenReturn(parent);
        fixtures.createRepeatingGroupInGroup("Child2", "Child", "Parent");

        assertNotNull(groupItem.getCdrs());
        Cdr child = groupItem.getCdrs().get(0);
        assertEquals("Child", child.getType());
        assertTrue(child.containsKey("Child2"));
        assertEquals(CdrItem.CdrItemType.CDR_ARRAY, child.getItem("Child2").getType());
    }

    @Test
    public void testCreateRepeatingGroupWithFieldsInGroup() {
        Cdr parent = new Cdr("Parent");
        CdrItem groupItem = new CdrItem(CdrItem.CdrItemType.CDR_ARRAY);
        groupItem.setCdrs(new ArrayList<>());
        parent.setItem("Child", groupItem);

        when(handlers.preTreatParams(anyString())).thenCallRealMethod();
        when(handlers.getSentMessage("Parent")).thenReturn(parent);
        fixtures.createRepeatingGroupWithFieldsInGroup("Child2", "field1=value1,field2=value2", "Child", "Parent");

        assertNotNull(groupItem.getCdrs());
        assertEquals(1, groupItem.getCdrs().size());
        Cdr child = groupItem.getCdrs().get(0);
        assertEquals("Child", child.getType());
        assertTrue(child.containsKey("Child2"));
        CdrItem child2 = child.getItem("Child2");
        assertEquals(CdrItem.CdrItemType.CDR_ARRAY, child2.getType());

        assertEquals(1, child2.getCdrs().size());
        Cdr nestedChild = child2.getCdrs().get(0);
        assertTrue(nestedChild.containsKey("field1"));
        assertEquals("value1", nestedChild.getAsString("field1"));
        assertTrue(nestedChild.containsKey("field2"));
        assertEquals("value2", nestedChild.getAsString("field2"));
    }

}
