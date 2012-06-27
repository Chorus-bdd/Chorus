/**
 *  Copyright (C) 2000-2012 The Software Conservancy as Trustee.
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by: Steve Neal
 * Date: 18/10/11
 */
@SuppressWarnings("unchecked")
public class RegexpUtils {

    private static ChorusLog log = ChorusLogFactory.getLog(RegexpUtils.class);

    private static Pattern floatPattern = Pattern.compile("-?[0-9]+\\.[0-9]+");
    private static Pattern intPattern = Pattern.compile("-?[0-9]+");

    /**
     * Extracts the groups in a regular expression into typed data
     *
     * @param regex the regular expression to use
     * @param text  the text to extract the values from
     * @param types the data types for the corresponding group's text
     * @return an array of typed data or null if regex does not match the text, or the types are not compatible
     */
    public static Object[] extractGroups(String regex, String text, Class[] types) {
        Matcher matcher = Pattern.compile(regex).matcher(text);
        if (matcher.matches()) {
            int groupCount = matcher.groupCount();

            //check that there are the same number of expected values as there are regex groups
            if (groupCount != types.length) {
                //I think this is always an error in the handler's step definition
                //it's worth logging it to warn level, or people may spend hours looking and may not spot the problem
                log.warn("Number of method parameters does not match regex groups");
                return null;
            }

            //collect the regex group values
            String[] regexGroupValues = new String[groupCount];
            for (int i = 0; i < groupCount; i++) {
                regexGroupValues[i] = matcher.group(i + 1);
            }

            //convert the strings from the regex groups into an Object[]
            Object[] values = new Object[groupCount];
            for (int i = 0; i < groupCount; i++) {
                String valueStr = regexGroupValues[i];
                Class type = types[i];
                Object coercedValue = coerceType(valueStr, type);
                if (coercedValue != null) {
                    values[i] = coercedValue;
                } else {
                    return null;
                }
            }
            return values;

        } else {
            return null;
        }
    }

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
