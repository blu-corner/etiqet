package com.neueda.etiqet.core.util;

import org.junit.Test;

import static com.neueda.etiqet.core.util.StringUtils.isNullOrEmpty;
import static com.neueda.etiqet.core.util.StringUtils.removeTrailing;
import static org.junit.Assert.*;

public class StringUtilsTest {

    @Test
    public void testIsNullOrEmpty() {
        assertTrue(isNullOrEmpty(""));
        assertTrue(isNullOrEmpty(null));
        assertFalse(isNullOrEmpty("testString"));
    }

    @Test
    public void testRemoveLastInstanceOf() {
        String testString = "1,2,3,4,5,6,7,";
        String expected = "1,2,3,4,5,6,7";
        assertEquals(expected, removeTrailing(testString, ","));
        assertEquals(expected, removeTrailing(expected, ","));
        assertEquals("21", removeTrailing("21,", ","));
        assertEquals("21", removeTrailing("21" + Separators.LEVEL_SEPARATOR, Separators.LEVEL_SEPARATOR));
    }
}