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
package org.chorusbdd.chorus.handlerconfig.configproperty;

import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Default validation patterns for primitive properties
 */
public class PrimitiveOrEnumValidationPattern {

    public static final Pattern INTEGER_OR_LONG = Pattern.compile("^[-+]?\\d+$");

    public static final Pattern FLOAT_OR_DOUBLE = Pattern.compile("^[-+]?[0-9]*\\.?[0-9]+(?:[eE][-+]?[0-9]+)?$");

    public static final Pattern BOOLEAN = Pattern.compile("(?i)^true|false$");

    public static final Pattern CHARACTER = Pattern.compile("^\\S$");


    static Optional<Pattern> getDefaultPatternIfPrimitive(Class javaType) {
        Optional<Pattern> result = Optional.empty();
        if (Float.class.equals(javaType) || Double.class.equals(javaType)) {
            result = Optional.of(FLOAT_OR_DOUBLE);
        } else if (Integer.class.equals(javaType) || Long.class.equals(javaType) || Short.class.equals(javaType)) {
            result = Optional.of(INTEGER_OR_LONG);
        } else if (Character.class.equals(javaType)) {
            result = Optional.of(CHARACTER);
        } else if (Boolean.class.equals(javaType)) {
            result = Optional.of(BOOLEAN);
        }
        return result;
    }
}
