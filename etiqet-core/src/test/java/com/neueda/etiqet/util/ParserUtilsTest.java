package com.neueda.etiqet.util;

import com.neueda.etiqet.core.common.cdr.Cdr;
import com.neueda.etiqet.core.common.cdr.CdrItem;
import com.neueda.etiqet.core.common.exceptions.EtiqetException;
import com.neueda.etiqet.core.config.dtos.Field;
import com.neueda.etiqet.core.config.dtos.Fields;
import com.neueda.etiqet.core.config.dtos.Message;
import com.neueda.etiqet.core.message.config.ProtocolConfig;
import com.neueda.etiqet.core.testing.util.TestUtils;
import com.neueda.etiqet.core.util.ParserUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ParserUtilsTest {

	/** tool for loggin the exectution. */
	private static final Logger LOG = LogManager.getLogger(ParserUtilsTest.class);
	
	private static final String params = "campo1=1,campo2->campo21->campo211=2,"
                  + "campo2->campo21->campo212=3,campo2->campo21->campo213=4,"
                  + "campo2->campo22=22,campo3->campo31=5,campo3->campo32=6";
	
	/**
	 * Method to test stringToCdr function in ParserUtils
	 */
	@Test
	public void testStringToCdr() {
		Cdr cdr = ParserUtils.stringToCdr("None", params);
		assertNotNull(cdr);
        assertTrue(cdr.containsKey("campo1"));
        assertTrue(cdr.containsKey("campo2"));
        assertTrue(cdr.containsKey("campo3"));

        CdrItem child1 = cdr.getItem("campo1");
        assertNotNull(child1);
        assertEquals("1", child1.getStrval());

        CdrItem child2 = cdr.getItem("campo2");
        assertNotNull(child2);
        List<Cdr> child2Cdrs = child2.getCdrs();
        assertTrue("campo2 key should have children",
                        child2Cdrs != null && !child2Cdrs.isEmpty());
        assertEquals("campo2 key should have 2 children, found " + child2Cdrs.size(), 2, child2Cdrs.size());

        Cdr child21 = child2Cdrs.get(0);
        assertNotNull(child21);
        assertTrue(child21.containsKey("campo21"));
        CdrItem campo21 = child21.getItem("campo21");
        assertNotNull(campo21);
        List<Cdr> campo21Children = campo21.getCdrs();
        assertTrue("campo21 should have children",
                        campo21Children != null && !campo21Children.isEmpty());
        assertEquals("campo21 key should have 3 children, found " + campo21Children.size(), 3, campo21Children.size());
        assertTrue(campo21Children.get(0).containsKey("campo211"));
        assertEquals("2", campo21Children.get(0).getItem("campo211").getStrval());
        assertTrue(campo21Children.get(1).containsKey("campo212"));
        assertEquals("3", campo21Children.get(1).getItem("campo212").getStrval());
        assertTrue(campo21Children.get(2).containsKey("campo213"));
        assertEquals("4", campo21Children.get(2).getItem("campo213").getStrval());

        Cdr child22 = child2Cdrs.get(1);
        assertNotNull(child22);
        assertTrue(child22.containsKey("campo22"));
        CdrItem campo22 = child22.getItem("campo22");
        assertNotNull(campo22);
        assertEquals("22", campo22.getStrval());

        CdrItem child3 = cdr.getItem("campo3");
        assertNotNull(child3);
        List<Cdr> child3Cdrs = child3.getCdrs();
        assertTrue("campo3 key should have children",
                child3Cdrs != null && !child3Cdrs.isEmpty());
        assertEquals("campo3 key should have 2 children, found " + child3Cdrs.size(), 2, child3Cdrs.size());

        Cdr campo31 = child3Cdrs.get(0);
        assertNotNull(campo31);
        assertTrue(campo31.containsKey("campo31"));
        assertEquals("5", campo31.getItem("campo31").getStrval());

        Cdr campo32 = child3Cdrs.get(1);
        assertNotNull(campo32);
        assertTrue(campo32.containsKey("campo32"));
        assertEquals("6", campo32.getItem("campo32").getStrval());
    }

    /**
     * Method to test getFieldType function in ParserUtils
     */
    @Test
    public void testGetFieldType() throws EtiqetException {
        Field field = new Field();
        field.setType("double");
        field.setName("test");
        String type = ParserUtils.getFieldType(field);
        assertEquals("double", type);

        field.setType("string");
        String type1 = ParserUtils.getFieldType(field);
        assertEquals("string", type1);

        field.setName("testtime");
        String type2 = ParserUtils.getFieldType(field);
        assertEquals("datetime", type2);

        field.setName("testdate");
        String type3 = ParserUtils.getFieldType(field);
        assertEquals("date", type3);
    }

    /**
     * Method to test getRestTags function in ParserUtils
     */
    @Test
    public void testGetRestTags() throws EtiqetException {
        String[] tag = {"test1->this", "test->that", "test2->theother"};
        String rest = "test";
        String returnedTag = ParserUtils.getRestTags(tag,rest);
        assertEquals(String.format("%s", returnedTag), rest, returnedTag);

        String[] tag2 = {};
        String returnedTag2 = ParserUtils.getRestTags(tag2,rest);
        assertEquals("", returnedTag2);
    }

    /**
     * Method to test getBoolean function in ParserUtils
     */
    @Test
    public void testGetBoolean() throws EtiqetException {
        String []testStringError = {"F","T","X"};
        for(int i=0;i<testStringError.length;i++){
            Boolean error=Boolean.FALSE;
            try {
                ParserUtils.getBoolean(testStringError[i]);
            }catch (EtiqetException x){
                error=Boolean.TRUE;
            }
            assertTrue(String.format("Expected %s Got %s",error, testStringError[i]),error);
        }
        String []testStringFail = {"0","false","n"};
        for(int i=0;i<testStringFail.length;i++){
            Boolean expected=Boolean.FALSE;
            Boolean actual = ParserUtils.getBoolean(testStringFail[i]);
            assertEquals(String.format("Expected %s Got %s",expected, testStringError[i]),expected,actual);
        }
        String []testStringSuccess = {"1","true","y"};
        for(int i=0;i<testStringSuccess.length;i++){
            Boolean expected=Boolean.TRUE;
            Boolean actual = ParserUtils.getBoolean(testStringSuccess[i]);
            assertEquals(String.format("Expected %s Got %s",expected,testStringSuccess[i]),expected,actual);
        }
    }

    /**
	 * Method to test isTagInCdr function in ParserUtils
	 */
	@Test
	public void testIsTagInCdr() {
		Cdr cdr = ParserUtils.stringToCdr("None", params);
        assertTrue(ParserUtils.isTagInCdr("campo1", cdr));
        assertTrue(ParserUtils.isTagInCdr("campo21", cdr));
		assertFalse(ParserUtils.isTagInCdr("notInCdr", cdr));
	}
	
	/**
	 * Method to test getTagValueFromCdr function in ParserUtils
	 */
	@Test
	public void testGetTagValueFromCdr() {
		Cdr cdr = ParserUtils.stringToCdr("None", params);
		assertNotNull(cdr);
		String tag = "campo212";		
		
		Object value = ParserUtils.getTagValueFromCdr(tag, cdr);
		assertNotNull(value);
		assertEquals("3", value.toString());
	}
	
	@Test
	public void testCreateDefault() throws EtiqetException {
		String path = "${etiqet.directory}/etiqet-core/src/test/resources/protocols/testProtocol.xml";

		ProtocolConfig protocol = new ProtocolConfig(path);
        assertNotNull(protocol);

        Message messageNew = protocol.getMessage("TestMsg");
        assertNotNull(protocol);

        Cdr cdr = new Cdr("None");
        ParserUtils.fillDefault(messageNew, cdr);
        assertNotNull(cdr);
        assertEquals(7, cdr.getItems().size());
        assertEquals("testValue", cdr.getAsString("testField"));
        assertEquals(Integer.valueOf(1), cdr.getItem("testInt").getIntval());
        assertEquals(Boolean.TRUE, cdr.getItem("testBool").getBoolVal());

        String testDate = cdr.getItem("testDate").getStrval();
        assertTrue("Failed to Parse date", ParserUtils.matchDateTimeFormat( "Unable to parse %s as type %s (value: %s), defaulting to String type","testDate","date",testDate, "yyyyMMdd"));
        assertFalse("Parsed incorrect date", ParserUtils.matchDateTimeFormat( "Unable to parse %s as type %s (value: %s), defaulting to String type","testDate","date","NotAdate", "yyyyMMdd"));

        String testDatetime = cdr.getItem("testDatetime").getStrval();
        assertTrue("Failed to Parse date", ParserUtils.matchDateTimeFormat( "Unable to parse %s as type %s (value: %s), defaulting to String type","testDatetime","datetime",testDatetime, "yyyyMMdd-HH:mm:ss.SSSSSSSSS"));
        assertFalse("Parsed incorrect datetime??", ParserUtils.matchDateTimeFormat( "Unable to parse %s as type %s (value: %s), defaulting to String type","testDatetime","datetime","notAdateTime", "yyyyMMdd-HH:mm:ss.SSSSSSSSS"));

        assertEquals(Double.valueOf(1.1), cdr.getItem("testDouble").getDoubleval());
        assertEquals(TestUtils.defaultValue(), cdr.getItem("testUtilClass").getStrval());
	}

    @Test
    public void testFillDefaultInvalidTypes() throws EtiqetException {
        String protocolPath = "${etiqet.directory}/etiqet-core/src/test/resources/protocols/testProtocolInvalidTypes.xml";
        ProtocolConfig protocolConfig = new ProtocolConfig(protocolPath);

        Message message = protocolConfig.getMessage("TestMsg");
        Field[] fields = message.getFields().getField();
        assertEquals(11, fields.length);

        Cdr testMsg = new Cdr("TestMsg");
        ParserUtils.fillDefault(message, testMsg);
        assertEquals(11, testMsg.getItems().size());
        assertEquals("testValue", testMsg.getItem("testField").getStrval());

        // Should have been converted to string because "test" is not an integer
        assertEquals(CdrItem.CdrItemType.CDR_STRING, testMsg.getItem("testInt").getType());
        assertEquals("test", testMsg.getItem("testInt").getStrval());

        // Should have been converted to string because "12" is not a boolean
        assertEquals(CdrItem.CdrItemType.CDR_STRING, testMsg.getItem("testBool").getType());
        assertEquals("12", testMsg.getItem("testBool").getStrval());

        // Should have been converted to string because "false" is not a double
        assertEquals(CdrItem.CdrItemType.CDR_STRING, testMsg.getItem("testDouble").getType());
        assertEquals("false", testMsg.getItem("testDouble").getStrval());

        assertEquals(CdrItem.CdrItemType.CDR_BOOLEAN, testMsg.getItem("testBool1").getType());
        assertFalse(testMsg.getItem("testBool1").getBoolVal());
        assertEquals(CdrItem.CdrItemType.CDR_BOOLEAN, testMsg.getItem("testBool2").getType());
        assertFalse(testMsg.getItem("testBool2").getBoolVal());
        assertEquals(CdrItem.CdrItemType.CDR_BOOLEAN, testMsg.getItem("testBool3").getType());
        assertFalse(testMsg.getItem("testBool3").getBoolVal());
        assertEquals(CdrItem.CdrItemType.CDR_BOOLEAN, testMsg.getItem("testBool4").getType());
        assertTrue(testMsg.getItem("testBool4").getBoolVal());
        assertEquals(CdrItem.CdrItemType.CDR_BOOLEAN, testMsg.getItem("testBool5").getType());
        assertTrue(testMsg.getItem("testBool5").getBoolVal());
        assertEquals(CdrItem.CdrItemType.CDR_BOOLEAN, testMsg.getItem("testBool6").getType());
        assertTrue(testMsg.getItem("testBool6").getBoolVal());

        assertEquals(CdrItem.CdrItemType.CDR_STRING, testMsg.getItem("testUtilClass").getType());
        assertEquals("default", testMsg.getItem("testUtilClass").getStrval());
        assertEquals("default", testMsg.getItem("testUtilClass").getStrval());
    }

    @Test
    public void testFillDefaultUtilClassNotFound() {
        Field field = new Field();
        field.setName("testUtilField");
        field.setUtilclass("com.neueda.etiqet.util.TestUtils");
        field.setMethod("methodNotFound");

        Fields fields = new Fields();
        fields.setField(new Field[]{field});

        Message message = new Message();
        message.setFields(fields);

        try {
            ParserUtils.fillDefault(message, new Cdr("TestMsg"));
        } catch (Exception e) {
            assertTrue(e instanceof EtiqetException);
            assertEquals("Could not get Util Class for field " + field.toString(), e.getMessage());
        }
    }

    @Test
    public void testGetFullTagValueFromCdr() throws EtiqetException {
        String path = "${etiqet.directory}/etiqet-core/src/test/resources/protocols/testProtocol.xml";

        ProtocolConfig protocol = new ProtocolConfig(path);
        assertNotNull(protocol);

        Message messageNew = protocol.getMessage("TestMsg");
        assertNotNull(protocol);

        Cdr cdr = new Cdr("TestMsg");
        ParserUtils.fillDefault(messageNew, cdr);
        assertNotNull(cdr);

        Object bool = ParserUtils.getFullTagValueFromCdr("testBool", cdr);
        assertEquals("true", bool);

        CdrItem cdrItem = new CdrItem(CdrItem.CdrItemType.CDR_ARRAY);
        ArrayList<Cdr> cdrs = new ArrayList<>();
        for(int i = 1; i < 6; i++) {
            Cdr subItem = new Cdr("item" + i);
            subItem.set("item" + i, i);
            cdrs.add(subItem);
        }
        cdrItem.setCdrs(cdrs);
        cdr.setItem("list", cdrItem);

        assertEquals("1", ParserUtils.getFullTagValueFromCdr("list->item1", cdr));
        assertEquals("2", ParserUtils.getFullTagValueFromCdr("list->item2", cdr));
        assertEquals("3", ParserUtils.getFullTagValueFromCdr("list->item3", cdr));
        assertEquals("4", ParserUtils.getFullTagValueFromCdr("list->item4", cdr));
        assertEquals("5", ParserUtils.getFullTagValueFromCdr("list->item5", cdr));
    }

}
