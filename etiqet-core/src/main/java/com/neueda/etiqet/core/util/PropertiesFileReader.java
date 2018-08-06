package com.neueda.etiqet.core.util;

import com.neueda.etiqet.core.common.exceptions.EtiqetException;

import java.io.FileReader;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PropertiesFileReader {

	private PropertiesFileReader() {}

	/**
	 * Method to read a property file from a path
	 * @param fileName full path of property file.
	 * @return map with pairs key, value.
	 * @throws EtiqetException exception.
	 */
	public static Config loadPropertiesFile(String fileName) throws EtiqetException {
		Map<String, String> propertyMap = new HashMap<>();

		try (FileReader reader = new FileReader(fileName)) {
			Properties p = new Properties();
			p.load(reader);

			Enumeration<?> keys = p.propertyNames();
			while (keys.hasMoreElements()) {
				String key = (String) keys.nextElement();
				propertyMap.put(key, p.getProperty(key));
			}

			Config config = new Config();
			config.setProperties(propertyMap);
			return config;
		} catch (Exception e) {
			throw new EtiqetException(e);
		}

	}

	/**
	 * Method to read a property file from classpath
	 * @param fileName full path of property file.
	 * @return map with pairs key, value.
	 * @throws EtiqetException when properties cannot be loaded from file
	 */
	public static Map<String, String> loadFromClasspath(String fileName) throws EtiqetException {
		Map<String, String> propertyMap = new HashMap<>();

		InputStream stream = PropertiesFileReader.class.getResourceAsStream(fileName);

		Properties p = new Properties();
		try {
            p.load(stream);

            Enumeration<?> keys = p.propertyNames();

            while (keys.hasMoreElements()) {
                String key = (String) keys.nextElement();
                propertyMap.put(key, p.getProperty(key));
            }

            stream.close();
        } catch (Exception e) {
		    throw new EtiqetException("Could not read classpath file " + fileName, e);
        }

		return propertyMap;

	}

}
