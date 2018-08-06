package com.neueda.etiqet.fix.dictionary;

import com.neueda.etiqet.core.common.exceptions.UnknownTagException;
import com.neueda.etiqet.core.message.config.AbstractDictionary;
import com.neueda.etiqet.fix.message.dictionary.FixDictionary;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class FixDictionaryTest {

	/** Logger. */
	private static Logger LOG = LogManager.getLogger(FixDictionaryTest.class);

	private AbstractDictionary dictionary;

	@Before
	public void setUp() {
        dictionary = new FixDictionary("${etiqet.directory}/config/fix/FIX50SP2.xml");
    }
	
	@Test
	public void testGetMsgType() {
        assertEquals("D", dictionary.getMsgType("NewOrderSingle"));
        assertEquals("0", dictionary.getMsgType("Heartbeat"));
	}

	@Test
    public void testGetMsgName() {
        assertEquals("NewOrderSingle", dictionary.getMsgName("D"));
        assertEquals("Heartbeat", dictionary.getMsgName("0"));
        assertNull(dictionary.getMsgName("D12354"));
    }
	
	@Test
	public void testGetNameForTag() {
		assertEquals("AdvTransType", dictionary.getNameForTag(5));
		assertEquals("MsgType", dictionary.getNameForTag(35));
	}

	@Test
	public void testGetTagForName() throws UnknownTagException {
	    assertEquals((Integer) 5, dictionary.getTagForName("AdvTransType"));
	    assertEquals((Integer) 35, dictionary.getTagForName("MsgType"));
	}
	
	@Test
	public void testIsAdmin() {
	    assertFalse(dictionary.isAdmin("NewOrderSingle"));
	    assertTrue(dictionary.isAdmin("Heartbeat")); }
	
}
