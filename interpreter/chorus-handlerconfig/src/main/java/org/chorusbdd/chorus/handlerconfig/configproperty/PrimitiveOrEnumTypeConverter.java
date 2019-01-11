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

/**
 * Convert a String property value to an instance of a target primitive type wrapper class
 */
public class PrimitiveOrEnumTypeConverter implements ConfigBuilderTypeConverter {

    @Override
    public Object convertToTargetType(String propertyValue, Class targetClass) throws ConfigBuilderException {
        return  targetClass == String.class ? 
            propertyValue : 
            mapToEnumOrPrimitiveWrapper(propertyValue, targetClass);
    }

    private Object mapToEnumOrPrimitiveWrapper(String propertyValue, Class targetClass) throws ConfigBuilderException {
        Object result;
        try {
            if ( targetClass.isEnum()) {
                result = mapToEnum(propertyValue, targetClass);
            } else {
                result = mapToPrimitiveWrapper(propertyValue, targetClass);
            }
        } catch (NumberFormatException nfe) {
            throw new ConfigBuilderException(getClass().getSimpleName() +  " could not convert the property value '" +
                propertyValue + "' to a " + targetClass.getName());
        }
        return result;
    }

    private Object mapToPrimitiveWrapper(String propertyValue, Class targetClass) throws ConfigBuilderException {
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
                    throw new ConfigBuilderException("Could not convert a String with more than one character to a char");
                }
            default:
                throw new ConfigBuilderException(getClass().getSimpleName() +
                    " cannot convert property value to a " + targetClass.getName() + " consider configuring a custom value converter");
        }
    }

    private Object mapToEnum(String propertyValue, Class targetClass) throws ConfigBuilderException {
        Object[] enumConstants = targetClass.getEnumConstants();
        for (Object e: enumConstants) {
            if ( ((Enum)e).name().equalsIgnoreCase(propertyValue)) {
                return e;
            }
        }
        throw new ConfigBuilderException("Could not convert property value " + propertyValue + " to an instance of Enum class " + targetClass.getName());
    }

}
