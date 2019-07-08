package com.neueda.etiqet.sql.config;

import com.sun.org.apache.xpath.internal.operations.Bool;
import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Settings {

    private static Logger logger = Logger.getLogger(Settings.class);

    private Boolean useJooqLogging;

    private Settings(){}

    public static Settings loadSettings() {
        Settings settings = new Settings();
        Properties properties;

        InputStream inputStream = Settings.class.getClassLoader().getResourceAsStream("config/settings.cfg");
        if (inputStream != null) {

            properties = new Properties();
            try {
                properties.load(inputStream);
                settings.resolveProperties(properties);
            }
            catch (IOException e) {
                logger.error("Failed to load settings from " + inputStream, e);
            }
        }
        return settings;
    }

    private void resolveProperties(Properties properties) {
        List<Field> fields = getAllFields(this);
        for (Field field : fields) {
            if (properties.stringPropertyNames().contains(field.getName())) {
                try {
                    field.set(this, resolveType(properties.getProperty(field.getName()), field));
                }
                catch (IllegalAccessException e) {
                    logger.error("Unable to initialize settings property " + field.getName(), e);
                }
            }
        }
    }

    private static List<Field> getAllFields(Object obj){
        List<Field> fields = new ArrayList<>();
        getAllFieldsRecursive(fields, obj.getClass());
        return fields;
    }

    private static List<Field> getAllFieldsRecursive(List<Field> fields, Class<?> type) {
        for (Field field: type.getDeclaredFields()) {
            fields.add(field);
        }
        if (type.getSuperclass() != null) {
            fields = getAllFieldsRecursive(fields, type.getSuperclass());
        }

        return fields;
    }

    private Object resolveType(String propertyValue, Field field) {
        if (field.getType() == Boolean.class) {
            return Boolean.valueOf(propertyValue);
        }
        else if (field.getType() == Integer.class) {
            return Integer.valueOf(propertyValue);
        }
        else if (field.getType() == Double.class) {
            return Double.valueOf(propertyValue);
        }
        else if (field.getType() == Float.class) {
            return Float.valueOf(propertyValue);
        }
        return propertyValue;
    }

    public Boolean getUseJooqLogging() {
        return useJooqLogging;
    }
}
