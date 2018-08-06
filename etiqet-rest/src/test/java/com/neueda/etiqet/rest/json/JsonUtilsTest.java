package com.neueda.etiqet.rest.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.neueda.etiqet.core.common.cdr.Cdr;
import com.neueda.etiqet.core.common.cdr.CdrItem;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class JsonUtilsTest {

    @Test
    public void testJsonToCdr() {
        String json = "{ test: { test2: \"value1\", test3: [ \"abc\", \"def\", \"ghi\" ], test4: 4.0, test5: 5 } }";
        Cdr cdr = JsonUtils.jsonToCdr(json);

        assertEquals(1, cdr.getItems().size());

        CdrItem test = cdr.getItem("test");
        assertNotNull(test);
        assertEquals(CdrItem.CdrItemType.CDR_ARRAY, test.getType());

        List<Cdr> testChildren = test.getCdrs();
        assertEquals(4, testChildren.size());

        assertTrue(testChildren.get(0).containsKey("test2"));
        testCdrItem(testChildren.get(0).getItem("test2"), CdrItem.CdrItemType.CDR_STRING, "value1");

        assertTrue(testChildren.get(1).containsKey("test3"));
        assertEquals(CdrItem.CdrItemType.CDR_ARRAY, testChildren.get(1).getItem("test3").getType());

        List<Cdr> test3Children = testChildren.get(1).getItem("test3").getCdrs();
        assertEquals(3, test3Children.size());

        assertTrue(test3Children.get(0).containsKey("0"));
        testCdrItem(test3Children.get(0).getItem("0"), CdrItem.CdrItemType.CDR_STRING, "abc");

        assertTrue(test3Children.get(1).containsKey("1"));
        testCdrItem(test3Children.get(1).getItem("1"), CdrItem.CdrItemType.CDR_STRING, "def");

        assertTrue(test3Children.get(2).containsKey("2"));
        testCdrItem(test3Children.get(2).getItem("2"), CdrItem.CdrItemType.CDR_STRING, "ghi");

        assertTrue(testChildren.get(2).containsKey("test4"));
        testCdrItem(testChildren.get(2).getItem("test4"), CdrItem.CdrItemType.CDR_DOUBLE, 4.0);

        assertTrue(testChildren.get(3).containsKey("test5"));
        testCdrItem(testChildren.get(3).getItem("test5"), CdrItem.CdrItemType.CDR_INTEGER, 5);
    }

    private void testCdrItem(CdrItem cdrItem, CdrItem.CdrItemType expectedType, Object expectedValue) {
        assertNotNull(cdrItem);
        assertNotNull(expectedType);
        assertNotNull(expectedValue);

        assertEquals(expectedType, cdrItem.getType());
        switch (expectedType) {
            case CDR_STRING:
                assertEquals(expectedValue, cdrItem.getStrval());
                break;
            case CDR_INTEGER:
                assertEquals(expectedValue, cdrItem.getIntval());
                break;
            case CDR_DOUBLE:
                assertEquals(expectedValue, cdrItem.getDoubleval());
                break;
            case CDR_ARRAY:
            default:
                fail("CdrItemType " + expectedType + " not supported for testCdrItem()");
        }
    }

    @Test
    public void testCdrToJson() {
        String json = "{ test: { test2: \"value1\", test3: [ \"abc\", \"def\", \"ghi\" ], test4: 4.0, test5: 5 } }";
        Cdr cdr = JsonUtils.jsonToCdr(json);
        String newJson = JsonUtils.cdrToJson(cdr);

        JsonParser parser = new JsonParser();
        JsonElement originalJson = parser.parse(json);
        JsonElement reparsedJson = parser.parse(newJson);

        assertEquals(originalJson, reparsedJson);
    }
}