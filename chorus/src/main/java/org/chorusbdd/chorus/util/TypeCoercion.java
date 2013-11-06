/**
 *  Copyright (C) 2000-2013 The Software Conservancy and Original Authors.
 *  All rights reserved.
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to
 *  deal in the Software without restriction, including without limitation the
 *  rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 *  sell copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 *  FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 *  IN THE SOFTWARE.
 *
 *  Nothing in this notice shall be deemed to grant any rights to trademarks,
 *  copyrights, patents, trade secrets or any other intellectual property of the
 *  licensor or any contributor except as expressly stated herein. No patent
 *  license is granted separate from the Software, for code that you delete from
 *  the Software, or for combinations of the Software with other software or
 *  hardware.
 */
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
     * @return the coerced value, or null if the value cannot be converted to the required type
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
