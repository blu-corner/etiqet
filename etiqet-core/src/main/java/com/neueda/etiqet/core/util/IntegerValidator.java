package com.neueda.etiqet.core.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class to store different integer validations.
 * @author Neueda
 */
public class IntegerValidator {

	private static final Logger LOG = LogManager.getLogger(IntegerValidator.class);

	private IntegerValidator() {}

	/**
	 * Method to check that a string can be parse into integer
	 * @param input integer like a string
	 * @return result of check.
	 */
	public static boolean isParseable(String input) {
		boolean isParseable = true;
		try {
			Integer.parseInt(input);			
		} catch (NumberFormatException lne) {
			LOG.debug("Input: " + input + " can not be parsed into an Integer");
			isParseable = false;
		}		
		return isParseable;
	}
	
	/**
	 * Method to check that a string can be parse into integer
	 * @param input integer like a string
	 * @return Integer parsed, or null if unable to parse
	 */
	public static Integer tryParse(String input) {
		Integer out = null;
		try {
			out = Integer.parseInt(input);			
		} catch (NumberFormatException lne) {
			LOG.error("Input: " + input + " can not be parsed into an Integer");
		}		
		return out;
	}
	
}
