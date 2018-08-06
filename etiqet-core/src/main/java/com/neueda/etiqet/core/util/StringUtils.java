package com.neueda.etiqet.core.util;

public class StringUtils {

	/**
	 * Method to check if a string is null or empty.
	 * @param input input chain.
	 * @return check result.
	 */
	public static boolean isNullOrEmpty(String input) {
		return input == null || "".equals(input);
	}

    /**
     * Method to remove the last instance of a
     * @param input
     * @param toRemove
     * @return
     */
	public static String removeTrailing(String input, String toRemove) {
	    if(isNullOrEmpty(input) || (input.length() - toRemove.length() != input.lastIndexOf(toRemove)))
	        return input;

	    return input.substring(0, input.length() - toRemove.length());
    }

}
