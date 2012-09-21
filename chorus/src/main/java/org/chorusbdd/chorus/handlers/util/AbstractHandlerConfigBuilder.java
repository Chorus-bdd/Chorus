package org.chorusbdd.chorus.handlers.util;

import org.chorusbdd.chorus.ChorusException;

/**
 * Created with IntelliJ IDEA.
 * User: GA2EBBU
 * Date: 21/09/12
 * Time: 11:09
 * To change this template use File | Settings | File Templates.
 */
public class AbstractHandlerConfigBuilder {

    protected int parseIntProperty(String value, String propertyName) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new ChorusException("Could not parse property '" + propertyName + "' with value '" + value + "' as an integer");
        }
    }

    protected boolean parseBooleanProperty(String value, String propertyKey) {
        try {
            return Boolean.parseBoolean(value);
        } catch (Exception e) {
            throw new ChorusException("Failed to parse property " + propertyKey + " with value '" + value + "' as boolean");
        }
    }
}
