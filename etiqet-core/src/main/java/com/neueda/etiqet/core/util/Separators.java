package com.neueda.etiqet.core.util;

import java.util.regex.Pattern;

public final class Separators {

    private Separators() {}

	public static final String PARAM_SEPARATOR = ",";
    public static final String LEVEL_SEPARATOR = "->";
    public static final String KEY_VALUE_SEPARATOR = "=";
    public static final String FIELD_SEPARATOR = Pattern.quote("|");

}
