package com.neueda.etiqet.rest.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.neueda.etiqet.core.common.cdr.Cdr;
import com.neueda.etiqet.core.common.cdr.CdrItem;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class CdrSerialiserTest {

//    @Test
    public void testSerialize() {
        fail("Stub test method to be removed");
    }

    @Test
    public void testCdrItemToJsonElementPrimitives() {
        CdrSerialiser cs = new CdrSerialiser();

        CdrItem booleanCdr = new CdrItem(CdrItem.CdrItemType.CDR_BOOLEAN);
        booleanCdr.setBoolVal(true);

        JsonElement boolElement = cs.cdrItemToJsonElement(booleanCdr);
        assertTrue(boolElement instanceof JsonPrimitive);
        assertTrue(boolElement.getAsBoolean());

        CdrItem stringCdr = new CdrItem(CdrItem.CdrItemType.CDR_STRING);
        stringCdr.setStrval("testString");

        JsonElement stringElement = cs.cdrItemToJsonElement(stringCdr);
        assertTrue(stringElement instanceof JsonPrimitive);
        assertEquals("testString", stringElement.getAsString());

        CdrItem intCdr = new CdrItem(CdrItem.CdrItemType.CDR_INTEGER);
        intCdr.setIntval(2);

        JsonElement intElement = cs.cdrItemToJsonElement(intCdr);
        assertTrue(intElement instanceof JsonPrimitive);
        assertEquals(2, intElement.getAsInt());

        CdrItem doubleCdr = new CdrItem(CdrItem.CdrItemType.CDR_DOUBLE);
        doubleCdr.setDoubleval(2.23);

        JsonElement doubleElement = cs.cdrItemToJsonElement(doubleCdr);
        assertTrue(doubleElement instanceof JsonPrimitive);
        assertEquals(2.23, doubleElement.getAsDouble(), 0);
    }

    @Test
    public void testCdrItemToJsonElementArray() {
        CdrSerialiser cs = new CdrSerialiser();

        CdrItem arrayCdrItem = new CdrItem(CdrItem.CdrItemType.CDR_ARRAY);
        List<Cdr> arrayChildren = new ArrayList<>();
        for(int i = 0; i < 5; i++) {
            Cdr cdr = new Cdr("");
            cdr.set("" + i, i);
            arrayChildren.add(cdr);
        }
        arrayCdrItem.setCdrs(arrayChildren);

        JsonElement arrayElement = cs.cdrItemToJsonElement(arrayCdrItem);
        assertTrue(arrayElement instanceof JsonArray);
        JsonArray castArray = (JsonArray) arrayElement;

        arrayChildren = new ArrayList<>();
        assertEquals(5, castArray.size());
        for(int i = 0; i < 5; i++) {
            // check the last array then prepare the next test
            assertEquals(new JsonPrimitive(i), castArray.get(i));

            Cdr cdr = new Cdr("");
            cdr.set("test" + i, i);
            arrayChildren.add(cdr);
        }
        arrayCdrItem.setCdrs(arrayChildren);

        JsonElement mapElement = cs.cdrItemToJsonElement(arrayCdrItem);
        assertTrue(mapElement instanceof JsonObject);
        JsonObject castObject = (JsonObject) mapElement;

        assertEquals(5, castObject.size());
        for(int i = 0; i < 5; i++) {
            assertTrue(castObject.has("test" + i));
            assertEquals("" + i, castObject.get("test" + i).getAsString());
        }
    }

//    @Test
    public void testDeserialize() {
        fail("Stub test method to be removed");
    }

    @Test
    public void testGetCdrItem() {

    }
}