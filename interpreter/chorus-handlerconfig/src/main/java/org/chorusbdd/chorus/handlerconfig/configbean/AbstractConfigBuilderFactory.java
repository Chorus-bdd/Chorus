/**
 * MIT License
 *
 * Copyright (c) 2018 Chorus BDD Organisation.
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
package org.chorusbdd.chorus.handlerconfig.configbean;

import org.chorusbdd.chorus.annotations.Scope;
import org.chorusbdd.chorus.util.ChorusException;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: Nick E
 * Date: 21/09/12
 * Time: 11:09
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractConfigBuilderFactory<Builder extends HandlerConfigBuilder> {

    public Builder createConfigBuilder(Properties p, String configName) {
        Builder c = createBuilder();
        setProperties(p, c);
        c.setConfigName(configName);
        return c;
    }

    protected abstract void setProperties(Properties p, Builder c);

    protected abstract Builder createBuilder();

    protected int parseIntProperty(String value, String propertyName) {
        try {
            return Integer.parseInt(value.trim());
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

    protected static Scope parseScope(String value) {
        Scope processScope = Scope.valueOf(value.toUpperCase().trim());
        if ( processScope == null) {
            throw new ChorusException(
                    "Failed to parse property processScope with value '" + value + "', should be one of: "
                            + Arrays.asList(Scope.values())
            );
        }
        return processScope;
    }
    
    protected static <T extends Enum<T>> T getEnumValue(Class<T> enumType, String propertyKey, String value) {
        T result;
        try {
            result = Enum.valueOf(enumType, value);
        } catch (Exception e) {
            EnumSet<T> enumVals = EnumSet.allOf(enumType);
            throw new ChorusException("Failed to parse property " + propertyKey +  " which should be one of " +
                    "[" + enumVals + "] but was [" + value + "]");
        }
        return result;
    }
}
