package org.chorusbdd.chorus.util;

import org.chorusbdd.chorus.util.logging.ChorusLog;
import org.chorusbdd.chorus.util.logging.ChorusLogFactory;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 28/06/12
 * Time: 08:58
 */
public class TypeCoercion {

    private static ChorusLog log = ChorusLogFactory.getLog(RegexpUtils.class);

    private static Pattern floatPattern = Pattern.compile("-?[0-9]+\\.[0-9]+");
    private static Pattern intPattern = Pattern.compile("-?[0-9]+");

    /**
     * Will attempt to convert the String to the required type
     *
     * @returns the coerced value, or null if the value cannot be converted to the required type
     */
    public static <T> T coerceType(String value, Class<T> requiredType) {
        T result = null;
        try {
            if (requiredType == String.class) {
                result = (T) value;
            } else if (requiredType == StringBuffer.class) {
                result = (T) new StringBuffer(value);
            }else if (requiredType == Integer.class || requiredType == int.class) {
                result = (T) new Integer(value);
            } else if (requiredType == Long.class || requiredType == long.class) {
                result = (T) new Long(value);
            } else if (requiredType == Float.class || requiredType == float.class) {
                result = (T) new Float(value);
            } else if (requiredType == Double.class || requiredType == double.class) {
                result = (T) new Double(value);
            } else if (requiredType == BigDecimal.class) {
                result = (T) new BigDecimal(value);
            } else if ((requiredType == Boolean.class || requiredType == boolean.class)
                 && "true".equalsIgnoreCase(value)      //be stricter than Boolean.parseValue
                 || "false".equalsIgnoreCase(value)) {  //do not accept 'wibble' as a boolean false value
                //dont create new Booleans (there are only 2 possible values)
                result = (T) (Boolean) Boolean.parseBoolean(value);
            } else if (requiredType == Short.class || requiredType == short.class) {
                result = (T) new Short(value);
            } else if (requiredType == Byte.class || requiredType == byte.class) {
                result = (T) new Byte(value);
            } else if ( (requiredType == Character.class || requiredType == char.class) && value.length() == 1) {
                result = (T) (Character) value.toCharArray()[0];
            } else if (requiredType.isEnum()) {
                result = (T)coerceEnum(value, requiredType);
            } else if (requiredType == Object.class) {//attempt to convert the String to the most appropriate value
                result = (T)coerceObject(value);
            }
        } catch (Throwable t) {
            //Only log at debug since this failure may be an 'expected' NumberFormatException for example
            //There may be another handler method which provides a matching type and this is expected to fail
            //Even if something else has gone wrong with the conversion, we don't want to propagate errors since
            //this causes unpredictable output from the interpreter, we simply want the step not to be matched in this case
            log.debug("Exception when coercing value " + value + " to a " + requiredType, t);
        }
        return result;
    }

    private static <T> T coerceObject(String value) {
        T result;
        //try boolean first
        if ("true".equals(value) || "false".equals(value)) {
            result = (T) (Boolean) Boolean.parseBoolean(value);
        }
        //then float numbers
        else if (floatPattern.matcher(value).matches()) {
            result = (T) new Double(value);
        }
        //then int numbers
        else if (intPattern.matcher(value).matches()) {
            result = (T) new Long(value);
        }
        //else just pass the String value to the Object parameter
        else {
            result = (T) value;
        }
        return result;
    }

    private static <T> T coerceEnum(String value, Class<T> requiredType) {
        T result = null;
        try {
            Method valuesMethod = requiredType.getMethod("values");
            Object[] values = (Object[]) valuesMethod.invoke(requiredType);
            for (Object e : values) {
                if (e.toString().equalsIgnoreCase(value)) {
                    result = (T) e;
                }
            }
        } catch (Exception e) {
            //fail quietly if couldn't reflect on enum
        }
        return result;
    }
}
