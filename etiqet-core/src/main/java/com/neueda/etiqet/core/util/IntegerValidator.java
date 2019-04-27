package com.neueda.etiqet.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class to store different integer validations.
 *
 * @author Neueda
 */
public class IntegerValidator {

    private static final Logger LOG = LoggerFactory.getLogger(IntegerValidator.class);

    private IntegerValidator() {
    }

    /**
     * Method to check that a string can be parse into integer
     *
     * @param input integer like a string
     * @return result of check.
     */
    public static boolean isParseable(String input) {
        boolean isParseable = true;
        try {
            Integer.parseInt(input);
        } catch (NumberFormatException lne) {
            LOG.debug("Input: {} can not be parsed into an Integer", input);
            isParseable = false;
        }
        return isParseable;
    }

    /**
     * Method to check that a string can be parse into integer
     *
     * @param input integer like a string
     * @return Integer parsed, or null if unable to parse
     */
    public static Integer tryParse(String input) {
        Integer out = null;
        try {
            out = Integer.parseInt(input);
        } catch (NumberFormatException lne) {
            LOG.error("Input: {} can not be parsed into an Integer", input);
        }
        return out;
    }

}
