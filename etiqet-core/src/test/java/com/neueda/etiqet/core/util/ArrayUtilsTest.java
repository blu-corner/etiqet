package com.neueda.etiqet.core.util;

import org.junit.Test;

import java.util.*;

import static com.neueda.etiqet.core.util.ArrayUtils.isNullOrEmpty;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ArrayUtilsTest {

    @Test
    public void testIsNullOrEmptyCollection() {
        assertTrue(isNullOrEmpty((Collection) null));
        assertTrue(isNullOrEmpty(Collections.emptyList()));
        ArrayList<Object> list = new ArrayList<>();
        assertTrue(isNullOrEmpty(list));
        list.add("testObject");
        assertFalse(isNullOrEmpty(list));
    }

    @Test
    public void testIsNullOrEmptyMap() {
        assertTrue(isNullOrEmpty((Map) null));
        assertTrue(isNullOrEmpty(Collections.emptyMap()));

        HashMap<String, String> map = new HashMap<>();
        assertTrue(isNullOrEmpty(map));

        map.put("test", "object");
        assertFalse(isNullOrEmpty(map));
    }
}