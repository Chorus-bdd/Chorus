package org.chorusbdd.chorus.handlerconfig.configproperty;

import org.chorusbdd.chorus.util.ChorusException;

import java.util.function.BiFunction;

/**
 * Convert a String property value to an instance of a target primitive type wrapper class
 */
public class PrimitiveOrEnumTypeConverter implements BiFunction<String, Class, Object> {

    @Override
    public Object apply(String propertyValue, Class targetClass) {
        try {
            if ( targetClass.isEnum()) {
                return mapToEnum(propertyValue, targetClass);
            } else {
                return mapToPrimitiveWrapper(propertyValue, targetClass);
            }
        } catch (NumberFormatException nfe) {
            throw new ChorusException(getClass().getSimpleName() +  " could not convert the property value '" +
                propertyValue + "' to a " + targetClass.getName());
        }
    }

    private Object mapToPrimitiveWrapper(String propertyValue, Class targetClass) {
        switch (targetClass.getName()) {
            case "java.lang.String":
                return propertyValue;
            case "java.lang.Float":
            case "float":
                return Float.parseFloat(propertyValue);
            case "java.lang.Integer":
            case "int":
                return Integer.parseInt(propertyValue);
            case "java.lang.Double":
            case "double":
                return Double.parseDouble(propertyValue);
            case "java.lang.Long":
            case "long":
                return Long.parseLong(propertyValue);
            case "java.lang.Boolean":
            case "boolean":
                return Boolean.parseBoolean(propertyValue);
            case "java.lang.Short":
            case "short":
                return Short.parseShort(propertyValue);
            case "java.lang.Character":
            case "char":
                if (propertyValue.length() == 1) {
                    return propertyValue.charAt(0);
                } else {
                    throw new ChorusException("Could not convert a String with more than one character to a single char");
                }
            default:
                throw new ChorusException(getClass().getSimpleName() +
                    " cannot convert property value to a " + targetClass.getName() + " consider configuring a custom value converter");
        }
    }

    private Object mapToEnum(String propertyValue, Class targetClass) {
        Object[] enumConstants = targetClass.getEnumConstants();
        for (Object e: enumConstants) {
            if ( ((Enum)e).name().equalsIgnoreCase(propertyValue)) {
                return e;
            }
        }
        throw new ChorusException("Could not convert property value " + propertyValue + " to an instance of Enum class " + targetClass.getName());
    }

}
