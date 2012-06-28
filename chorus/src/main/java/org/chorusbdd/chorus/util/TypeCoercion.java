package org.chorusbdd.chorus.util;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 28/06/12
 * Time: 08:58
 */
public class TypeCoercion {

    private static Pattern floatPattern = Pattern.compile("-?[0-9]+\\.[0-9]+");
    private static Pattern intPattern = Pattern.compile("-?[0-9]+");

    /**
     * Will attempt to convert the String to the required type
     *
     * @returns the coerced value, or null if the value cannot be converted to the required type
     */
    public static <T> T coerceType(String value, Class<T> requiredType) {
        if (requiredType == String.class) {
            return (T) value;
        }
        if (requiredType == StringBuffer.class) {
            return (T) new StringBuffer(value);
        }
        if (requiredType == Integer.class || requiredType == int.class) {
            return (T) new Integer(value);
        }
        if (requiredType == Long.class || requiredType == long.class) {
            return (T) new Long(value);
        }
        if (requiredType == Float.class || requiredType == float.class) {
            return (T) new Float(value);
        }
        if (requiredType == Double.class || requiredType == double.class) {
            return (T) new Double(value);
        }
        if (requiredType == BigDecimal.class) {
            return (T) new BigDecimal(value);
        }
        if (requiredType == Boolean.class || requiredType == boolean.class) {
            //dont create new Booleans (there are only 2 possible values)
            return (T) (Boolean) Boolean.parseBoolean(value);
        }
        if (requiredType == Short.class || requiredType == short.class) {
            return (T) new Short(value);
        }
        if (requiredType == Byte.class || requiredType == byte.class) {
            return (T) new Byte(value);
        }
        if (requiredType == Character.class || requiredType == char.class) {
            if (value.length() == 1) {
                return (T) (Character) value.toCharArray()[0];
            }
        }
        if (requiredType.isEnum()) {
            try {
                Method valuesMethod = requiredType.getMethod("values");
                Object[] values = (Object[]) valuesMethod.invoke(requiredType);
                for (Object e : values) {
                    if (e.toString().equalsIgnoreCase(value)) {
                        return (T) e;
                    }
                }
            } catch (Exception e) {
                //fail quietly if couldn't reflect on enum
            }
        }
        if (requiredType == Object.class) {//attempt to convert the String to the most appropriate value
            //try boolean first
            if ("true".equals(value) || "false".equals(value)) {
                return (T) (Boolean) Boolean.parseBoolean(value);
            }
            //then float numbers
            Matcher floatMatcher = floatPattern.matcher(value);
            if (floatMatcher.matches()) {
                return (T) new Double(value);
            }
            //then int numbers
            Matcher intMatcher = intPattern.matcher(value);
            if (intMatcher.matches()) {
                return (T) new Long(value);
            }
            //else just pass the String value to the Object parameter
            return (T) value;
        }
        return null;//not possible to coerce the value to the exptected type
    }
}
