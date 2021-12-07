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

import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConfigPropertyUtils {

    /**
     * Create a regular expression which will match any of the values from the supplied enum type
     */
    public static <T extends Enum<T>> Pattern createValidationPatternFromEnumType(Class<T> enumType) {

        String regEx = Stream.of(enumType.getEnumConstants())
                .map(Enum::name)
                .collect(Collectors.joining("|", "(?i)", ""));

        //Enum constants may contain $ which needs to be escaped
        regEx = regEx.replace("$", "\\$");
        return Pattern.compile(regEx);
    }

    public static void checkNotNullAndNotEmpty(String value, String propertyName) {
        if (value == null || "".equals(value.trim())) {
            throw new ConfigValidatorException(propertyName + " cannot be null or empty String");
        }
    }
}
