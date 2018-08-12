package org.chorusbdd.chorus.handlerconfig.configproperty;

import org.chorusbdd.chorus.util.ChorusException;

import java.util.function.BiFunction;

/**
 * Convert a String property value to an instance of a target type
 */
public class SimplePropertyConverter implements BiFunction<String, Class, Object> {

    @Override
    public Object apply(String propertyValue, Class targetClass) {
        try {
            switch (targetClass.getName()) {
                case "java.lang.String":
                    return propertyValue;
                case "java.lang.Float":
                    return Float.parseFloat(propertyValue);
                case "java.lang.Integer":
                    return Integer.parseInt(propertyValue);
                case "java.lang.Double":
                    return Double.parseDouble(propertyValue);
                case "java.lang.Long":
                    return Long.parseLong(propertyValue);
                case "java.lang.Boolean":
                    return Boolean.parseBoolean(propertyValue);
                default:
                    throw new ChorusException(getClass().getSimpleName() + 
                        " cannot convert property value to a " + targetClass.getName() + " consider configuring a custom value converter");
            }
        } catch (NumberFormatException nfe) {
            throw new ChorusException(getClass().getSimpleName() +  " could not convert the property value '" +
                propertyValue + "' to a " + targetClass.getName());
        }
    }

}
