package com.neueda.etiqet.core.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Random;
import org.junit.Test;

public class IntegerValidatorTest {

    @Test
    public void testIntegerValidator() {
        new Random().ints(25).forEach(i -> {
            assertTrue(IntegerValidator.isParseable(String.valueOf(i)));
            assertEquals(Integer.valueOf(i), IntegerValidator.tryParse(String.valueOf(i)));
        });

        for (String testValue : new String[]{"test", "true", System.getProperty("user.dir")}) {
            assertFalse(IntegerValidator.isParseable(testValue));
            assertNull(IntegerValidator.tryParse(testValue));
        }
    }
}
