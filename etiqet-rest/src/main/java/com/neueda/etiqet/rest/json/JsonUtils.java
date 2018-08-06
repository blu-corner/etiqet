package com.neueda.etiqet.rest.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.neueda.etiqet.core.common.cdr.Cdr;

import java.util.List;
import java.util.Map;

public class JsonUtils {

    private static Gson gson;

    private JsonUtils() {}

    /**
     * Singleton for getting a {@link Gson} object. This sets type adapters for {@link Map}s and {@link List}s to make
     * sure that {@link Integer}s aren't parsed as {@link Double}s. This also sets the {@link CdrSerialiser} to handle
     * conversion of JSON into {@link Cdr} objects
     * @return Gson implementation with the specified type adapters
     */
    public static Gson getGson() {
        if(gson == null) {
            // ObjectTypeAdapter used to ensure that integer values are parsed as Integer, not Double
            gson = new GsonBuilder().registerTypeAdapter(Map.class, new ObjectTypeAdapter())
                                    .registerTypeAdapter(List.class, new ObjectTypeAdapter())
                                    .registerTypeAdapter(Cdr.class, new CdrSerialiser())
                                    .create();
        }

        return gson;
    }

    /**
     * Converts a JSON string to a Cdr object for use within AbstractMessages.
     * @param json String of JSON to be converted to Cdr
     * @return Cdr representation of the JSON object
     */
    public static Cdr jsonToCdr(String json) {
        return getGson().fromJson(json, Cdr.class);
    }

    /**
     * Converts a Cdr object back to JSON.
     * @param cdr Cdr object to be converted back to JSON
     * @return JSON string representing the Cdr object
     */
    public static String cdrToJson(Cdr cdr) {
        return getGson().toJson(cdr);
    }

}
