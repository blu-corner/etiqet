package com.neueda.etiqet.rest.json;

import com.google.gson.*;
import com.neueda.etiqet.core.common.cdr.Cdr;
import com.neueda.etiqet.core.common.cdr.CdrItem;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Serialiser / Deserialiser to handle conversion of JSON to Cdr and vice-versa
 */
public class CdrSerialiser implements JsonSerializer<Cdr>, JsonDeserializer<Cdr> {

    /**
     * Serialises a Cdr object into a Json Element
     * @param src
     *          Cdr object to be serialised into JSON
     * @param typeOfSrc
     *          the actual type (fully genericized version) of the source object.
     * @param context
     *          Context for serialization, passed to by Gson internally
     * @return
     *          JSON String representing the Cdr object
     */
    @Override
    public JsonElement serialize(Cdr src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonCdr = new JsonObject();

        for(Map.Entry<String, CdrItem> cdrEntry : src.getItems().entrySet()) {
            String cdrKey = cdrEntry.getKey();
            CdrItem cdrItem = cdrEntry.getValue();
            jsonCdr.add(cdrKey, cdrItemToJsonElement(cdrItem));
        }

        return jsonCdr;
    }

    /**
     * Converts a CdrItem into a JSON Element depending on the CdrItemType. For lists / nested objects, recursion is
     * used to traverse the JSON tree.
     *
     * @param cdrItem
     *          CdrItem to be converted
     * @return JsonElement representing the CdrItem
     */
    JsonElement cdrItemToJsonElement(CdrItem cdrItem) {
        switch (cdrItem.getType()) {
            case CDR_STRING:
                return new JsonPrimitive(cdrItem.getStrval());
            case CDR_INTEGER:
                return new JsonPrimitive(cdrItem.getIntval());
            case CDR_DOUBLE:
                return new JsonPrimitive(cdrItem.getDoubleval());
            case CDR_BOOLEAN:
                return new JsonPrimitive(cdrItem.getBoolVal());
            case CDR_ARRAY:
                List<Cdr> childCdrs = cdrItem.getCdrs();

                // if it has numerical keys, assume that it's a list, rather an JSON object
                boolean isMap = !childCdrs.get(0).containsKey("0");
                if(isMap) {
                    return getMap(childCdrs);
                } else {
                    // if it has numerical keys, assume that it's a list, rather an JSON object
                    return getArray(childCdrs);
                }
            default:
                return JsonNull.INSTANCE;
        }
    }

    /**
     * Converts a list of CDRs into a JsonObject
     * @param cdrs list of CDRs to be converted
     * @return JSON object representing the map in the CDR list
     */
    private JsonObject getMap(List<Cdr> cdrs) {
        JsonObject map = new JsonObject();
        for(Cdr child : cdrs) {
            for(Map.Entry<String, CdrItem> childEntry : child.getItems().entrySet()) {
                JsonElement childElement = cdrItemToJsonElement(childEntry.getValue());
                map.add(childEntry.getKey(), childElement);
            }
        }
        return map;
    }

    /**
     * Converts a list of CDRs into a JsonArray
     * @param cdrs list of CDRs to be converted
     * @return JSON array representing the list of CDRs
     */
    private JsonArray getArray(List<Cdr> cdrs) {
        JsonArray list = new JsonArray();
        for(Cdr child : cdrs) {
            for(Map.Entry<String, CdrItem> childEntry : child.getItems().entrySet()) {
                JsonElement childElement = cdrItemToJsonElement(childEntry.getValue());
                list.add(childElement);
            }
        }
        return list;
    }

    /**
     * Deserialises the provided JSON object into a Cdr object. This uses {@link #getCdrItem(Object)} to
     * traverse the JSON tree and convert elements to the relevant CdrItem
     *
     * @param json
     *          the parse tree.
     * @param typeOfT
     *          type of the expected return value.
     * @param context
     *          Context for serialization, passed to by Gson internally
     * @return
     *          Cdr object parsed from the JSON element
     * @throws JsonParseException
     *          when the JSON is not valid and GSON can't handle it
     */
    @Override
    public Cdr deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            {
        Cdr data = new Cdr(typeOfT.getTypeName());
        Map<String, Object> map = context.deserialize(json, Map.class);
        for(Map.Entry<String, Object> entry : map.entrySet()) {
            CdrItem cdrItem = getCdrItem(entry.getValue());
            data.setItem(entry.getKey(), cdrItem);
        }

        return data;
    }

    /**
     * Creates a CdrItem from an object provided. In the case of a list or nested object, this will call this function
     * recursively to create a CDR_ARRAY type CdrItem.
     *
     * <p>Suppressed warnings of unchecked types to allow us to cast the entries in Lists/Maps to Object</p>
     *
     * @param value
     *          Value retrieved from the JSON parser
     * @return CdrItem
     *          A CdrItem to be added to the current Cdr being created.
     */
    @SuppressWarnings("unchecked")
    CdrItem getCdrItem(Object value) {
        CdrItem cdrValue;

        // work out what the value is
        if(value instanceof List) {
            List<Object> listValue = (List) value;
            List<Cdr> cdrList = new ArrayList<>(listValue.size());

            for(int i=0, len=listValue.size(); i < len; i++) {
                CdrItem cdrChildItem = getCdrItem(listValue.get(i));
                Cdr cdrChild = new Cdr(cdrChildItem.getType().name());
                cdrChild.setItem(String.valueOf(i), cdrChildItem);
                cdrList.add(cdrChild);
            }

            cdrValue = new CdrItem(CdrItem.CdrItemType.CDR_ARRAY);
            cdrValue.setCdrs(cdrList);
        } else if(value instanceof Map) {
            Map<String, Object> mapValue = (Map<String, Object>) value;
            List<Cdr> cdrList = new ArrayList<>(mapValue.size());

            for(Map.Entry<String, Object> entry : mapValue.entrySet()) {
                CdrItem cdrChildItem = getCdrItem(entry.getValue());
                Cdr cdrChild = new Cdr(cdrChildItem.getType().name());
                cdrChild.setItem(entry.getKey(), cdrChildItem);
                cdrList.add(cdrChild);
            }

            cdrValue = new CdrItem(CdrItem.CdrItemType.CDR_ARRAY);
            cdrValue.setCdrs(cdrList);
        } else if(value instanceof Integer) {
            cdrValue = new CdrItem(CdrItem.CdrItemType.CDR_INTEGER);
            cdrValue.setIntval((Integer) value);
        } else if(value instanceof Double) {
            cdrValue = new CdrItem(CdrItem.CdrItemType.CDR_DOUBLE);
            cdrValue.setDoubleval((Double) value);
        } else {
            // treat it as a string otherwise
            cdrValue = new CdrItem(CdrItem.CdrItemType.CDR_STRING);
            cdrValue.setStrval(String.valueOf(value));
        }

        return cdrValue;
    }
}
