package com.neueda.etiqet.rest.json;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This is a very roundabout way to get Gson to parse integers as an Integer, rather than as a Double. The only
 * difference between this and the default Object TypeAdapter provided by Gson is the case for JsonToken.NUMBER allows
 * for parsing integers if no decimal point is found in the value parsed.
 *
 * @see <a href="https://stackoverflow.com/questions/36508323/how-can-i-prevent-gson-from-converting-integers-to-doubles">StackOverflow answer</a>
 */
public class ObjectTypeAdapter extends TypeAdapter<Object> {

    /**
     * Delegate type adapter for handling the writing back to JSON.
     */
    private final TypeAdapter<Object> delegate = new Gson().getAdapter(Object.class);

    /**
     * Writes an object out to the provided JsonWriter. This just calls
     * {@link com.google.gson.internal.bind.ObjectTypeAdapter#write(JsonWriter, Object)}
     * @param out JsonWriter to be used as an output buffer
     * @param value serialised object to be written to JSON
     * @throws IOException when the JSON cannot be parsed
     */
    @Override
    public void write(JsonWriter out, Object value) throws IOException {
        delegate.write(out, value);
    }

    /**
     * This reads in a JSON string parsing properties / values into the correct types. This is almost exactly the same
     * as {@link com.google.gson.internal.bind.ObjectTypeAdapter#read(JsonReader)}, except that we can parse integers.
     * The default implementation treats all numbers as Double.
     * @param in JsonReader that's parsing the JSON string
     * @return parsed type of the field
     * @throws IOException when JSON cannot be read correctly
     */
    @Override
    public Object read(JsonReader in) throws IOException {
        JsonToken token = in.peek();

        switch (token) {
            case BEGIN_ARRAY:
                List<Object> list = new ArrayList<>();
                in.beginArray();
                while (in.hasNext()) {
                    list.add(read(in));
                }
                in.endArray();
                return list;

            case BEGIN_OBJECT:
                Map<String, Object> map = new LinkedTreeMap<>();
                in.beginObject();
                while (in.hasNext()) {
                    map.put(in.nextName(), read(in));
                }
                in.endObject();
                return map;

            case STRING:
                return in.nextString();

            case NUMBER:
                String n = in.nextString();
                if(n.indexOf('.') == -1) {
                    return Integer.parseInt(n);
                }
                return Double.parseDouble(n);

            case BOOLEAN:
                return in.nextBoolean();

            case NULL:
                in.nextNull();
                return null;

            default:
                throw new IllegalStateException();
        }
    }

}
