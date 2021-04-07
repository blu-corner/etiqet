package com.neueda.etiqet.sql.fixture;

import com.neueda.etiqet.core.util.Separators;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.impl.DSL;

import java.util.*;

import static org.jooq.impl.DSL.field;

public class SqlUtils {

    protected static HashMap<String, String> resolveParamsToFieldValMap(String colKeyVals) {
        String[] split = colKeyVals.split(Separators.PARAM_SEPARATOR);
        HashMap<String, String> keyValMap = new HashMap<>();
        for (String keyVal : split) {
            String[] kv = keyVal.split(Separators.KEY_VALUE_SEPARATOR);
            keyValMap.put(kv[0], kv[1]);
        }
        return keyValMap;
    }

    protected static HashMap<Field<Object>, Object> resolveToFieldValMap(DSLContext dslContext, String tableName,
                                                                         HashMap<String, String> colKeyVals) {
        HashMap<Field<Object>, Object> fieldKeyVals = new HashMap<>();
        for (Map.Entry<String, String> keyVal : colKeyVals.entrySet()) {
            Object val = resolveFieldType(dslContext, tableName, keyVal.getKey(), keyVal.getValue());
            Field<Object> field = field(DSL.name(keyVal.getKey()));
            fieldKeyVals.put(field, val);
        }
        return fieldKeyVals;
    }

    protected static Object resolveFieldType(DSLContext dslContext, String table, String column, String value) {
        Class<?> type = dslContext.select().from(DSL.name(table)).fetch().field(column).getType();
        if (type == Boolean.class) return Boolean.valueOf(value);
        if (type == Integer.class) return Integer.valueOf(value);
        if (type == Float.class)   return Float.valueOf(value);
        if (type == Double.class)  return Double.valueOf(value);
        return value;
    }

    protected static ArrayList<Field<Object>> resolveToFieldList(String fieldNames) {
        return resolveToFieldList(Arrays.asList(fieldNames.split(Separators.PARAM_SEPARATOR)));
    }

    protected static ArrayList<Field<Object>> resolveToFieldList(List<String> fieldNames) {
        ArrayList<Field<Object>> fields = new ArrayList<>();
        for (String field : fieldNames) {
            fields.add(field(DSL.name(field)));
        }
        return fields;
    }
}
