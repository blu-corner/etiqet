package com.neueda.etiqet.core.util;

import java.util.Collection;
import java.util.Map;

public class ArrayUtils {

    private ArrayUtils() {}

    public static boolean isNullOrEmpty(Collection collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean isNullOrEmpty(Map map) {
        return map == null || map.isEmpty();
    }

}
