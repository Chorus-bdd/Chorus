/**
 * MIT License
 *
 * Copyright (c) 2019 Chorus BDD Organisation.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.chorusbdd.chorus.stepinvoker;

import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;
import org.chorusbdd.chorus.util.RegexpUtils;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 28/06/12
 * Time: 08:58
 */
public class TypeCoercion {

    private ChorusLog log = ChorusLogFactory.getLog(RegexpUtils.class);

    private static Pattern floatPattern = Pattern.compile("-?[0-9]+\\.[0-9]+");
    private static Pattern intPattern = Pattern.compile("-?[0-9]+");

    /**
     * Will attempt to convert the String to the required type
     *
     * @return the coerced value, or null if the value cannot be converted to the required type
     */
    public static <T> T coerceType(ChorusLog log, String value, Class<T> requiredType) {

        T result = null;
        try {
            if ( "null".equals(value)) {
                result = null;
            } else if (isStringType(requiredType)) {
                result = (T) value;
            } else if (isStringBufferType(requiredType)) {
                result = (T) new StringBuffer(value);
            } else if (isIntType(requiredType)) {
                result = (T) new Integer(value);
            } else if (isLongType(requiredType)) {
                result = (T) new Long(value);
            } else if (isFloatType(requiredType)) {
                result = (T) new Float(value);
            } else if (isDoubleType(requiredType)) {
                result = (T) new Double(value);
            } else if (isBigDecimalType(requiredType)) {
                result = (T) new BigDecimal(value);
            } else if (isBigIntegerType(requiredType)) {
                result = (T) new BigInteger(value);
            } else if (isBooleanType(requiredType)
                 && "true".equalsIgnoreCase(value)      //be stricter than Boolean.parseValue
                 || "false".equalsIgnoreCase(value)) {  //do not accept 'wibble' as a boolean false value
                //dont create new Booleans (there are only 2 possible values)
                result = (T) (Boolean) Boolean.parseBoolean(value);
            } else if (isShortType(requiredType)) {
                result = (T) new Short(value);
            } else if (isByteType(requiredType)) {
                result = (T) new Byte(value);
            } else if (isCharType(requiredType) && value.length() == 1) {
                result = (T) (Character) value.toCharArray()[0];
            } else if (isEnumeratedType(requiredType)) {
                result = (T)coerceEnum(value, requiredType);
            } else if (isObjectType(requiredType)) {//attempt to convert the String to the most appropriate value
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

    /**
     * Rules for object coercion are probably most important for the ChorusContext
     * Here when we set the value of a variable, these rules are used to determine how the
     * String value supplied is represented - since float pattern comes first
     * I set the variable x with value 1.2 will become a float within the ChorusContext
     * - this will give some extra utility if we add more powerful comparison methods to ChorusContext
     */
    private static <T> T coerceObject(String value) {
        T result;
        //try boolean first
        if ("true".equals(value) || "false".equals(value)) {
            result = (T) (Boolean) Boolean.parseBoolean(value);
        }
        //then float numbers
        else if (floatPattern.matcher(value).matches()) {
            //handle overflow by converting to BigDecimal
            BigDecimal bd = new BigDecimal(value);
            Double d = bd.doubleValue();
            result = (T) ((d == Double.NEGATIVE_INFINITY || d == Double.POSITIVE_INFINITY) ? bd : d);
        }
        //then int numbers
        else if (intPattern.matcher(value).matches()) {
            //handle overflow by converting to BigInteger
            BigInteger bd = new BigInteger(value);
            result = (T) (bd.compareTo(BigInteger.valueOf(Long.MAX_VALUE)) == 1 ? bd : bd.longValue());
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


    private static <T> boolean isBigIntegerType(Class<T> requiredType) {
        return requiredType == BigInteger.class;
    }

    private static <T> boolean isBigDecimalType(Class<T> requiredType) {
        return requiredType == BigDecimal.class;
    }

    private static <T> boolean isObjectType(Class<T> requiredType) {
        return requiredType == Object.class;
    }

    private static <T> boolean isEnumeratedType(Class<T> requiredType) {
        return requiredType.isEnum();
    }

    //why support StringBuffer not StringBuilder?
    private static <T> boolean isStringBufferType(Class<T> requiredType) {
        return requiredType == StringBuffer.class;
    }

    private static <T> boolean isStringType(Class<T> requiredType) {
        return requiredType == String.class;
    }

    private static <T> boolean isCharType(Class<T> requiredType) {
        return requiredType == Character.class || requiredType == char.class;
    }

    private static <T> boolean isByteType(Class<T> requiredType) {
        return requiredType == Byte.class || requiredType == byte.class;
    }

    private static <T> boolean isShortType(Class<T> requiredType) {
        return requiredType == Short.class || requiredType == short.class;
    }

    private static <T> boolean isBooleanType(Class<T> requiredType) {
        return requiredType == Boolean.class || requiredType == boolean.class;
    }

    private static <T> boolean isDoubleType(Class<T> requiredType) {
        return requiredType == Double.class || requiredType == double.class;
    }

    private static <T> boolean isFloatType(Class<T> requiredType) {
        return requiredType == Float.class || requiredType == float.class;
    }

    private static <T> boolean isLongType(Class<T> requiredType) {
        return requiredType == Long.class || requiredType == long.class;
    }

    private static <T> boolean isIntType(Class<T> requiredType) {
        return requiredType == Integer.class || requiredType == int.class;
    }
}
