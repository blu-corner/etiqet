package com.neueda.etiqet.core.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Config {

	/** Constant for String default value. */
	public static final String DEFAULT_STRING = "";
	
	/** Constant for Integer default value. */
	public static final Integer DEFAULT_INTEGER = 0;
	
	/** Constant for Long default value. */
	public static final Long DEFAULT_LONG = 0L;
	
	/** Constant for Double default value. */
	public static final Double DEFAULT_DOUBLE = 0.0;

	
	/** private map with properties. */
	private Map<String, String> properties;
	
	/**
	 * Defaul construct.
	 */
	public Config() {
		properties = new HashMap<>();
	}
	
	/**
	 * Setter of attribute properties.
	 * @param properties map containing properties of the config
	 */
	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}

	/**
	 * Adds a property to config
	 * @param key property key
	 * @param value property value
	 */
	public void addProperty(String key, Object value) {
		properties.put(key, value.toString());
	}
	
	/**
	 * Method to create the key set.
	 * @return properties key set.
	 */
	public Collection<String> getKeySet() {
		return properties.keySet();
	}
	
	/**
	 * Method to create if there is no properties.
	 * @return no properties check.
	 */
	public boolean isEmpty() {
		return properties.isEmpty();
	}
	
	/**
	 * Method to create a property value like String.
	 * @param key key.
	 * @return String value of the property.
	 */
	public String getString(String key) {
		String value = DEFAULT_STRING;
		if (properties != null && properties.containsKey(key)) {
			value = properties.get(key);
		}
		return value;
	}
	
	/**
	 * Method to create a property value like Integer.
	 * @param key key.
	 * @return Integer value of the property.
	 */
	public Integer getInteger(String key) {
		Integer value = null;
		String stringValue = getString(key);
		if (!StringUtils.isNullOrEmpty(stringValue)) {
			try {
				value = Integer.parseInt(stringValue);
			} catch (NumberFormatException lex) {
				// log conversion to Integer 
				value = DEFAULT_INTEGER;
			} 
		} else {
			value = DEFAULT_INTEGER;
		}
		
		return value;
	}
	
	/**
	 * Method to create a property value like Long.
	 * @param key key.
	 * @return Long value of the property.
	 */
	public Long getLong(String key) {
		Long value = null;
		String stringValue = getString(key);
		if (!StringUtils.isNullOrEmpty(stringValue)) {
			try {
				value = Long.parseLong(stringValue);
			} catch (NumberFormatException lex) {
				// log conversion to Integer
				value = DEFAULT_LONG;
			}
		} else {
			value = DEFAULT_LONG;
		}
		
		return value;
	}
	
	/**
	 * Method to create a property value like Double.
	 * @param key key.
	 * @return Double value of the property.
	 */
	public Double getDouble(String key) {
		Double value = null;
		String stringValue = getString(key);
		if (!StringUtils.isNullOrEmpty(stringValue)) {
			try {
				value = Double.parseDouble(stringValue);
			} catch (NumberFormatException lex) {
				// log conversion to Integer
				value = DEFAULT_DOUBLE;
			}
		} else {
			value = DEFAULT_DOUBLE;
		}
		
		return value;
	}
	
	/**
	 * Method to create a property value like boolean.
	 * @param key key.
	 * @return boolean value of the property.
	 */
	public boolean getBoolean(String key) {
		boolean value = false;
		String stringValue = getString(key);
		if (!StringUtils.isNullOrEmpty(stringValue)) {
			value = "1".equals(stringValue) || "true".equalsIgnoreCase(stringValue);
		}
		
		return value;
	}
	
}
