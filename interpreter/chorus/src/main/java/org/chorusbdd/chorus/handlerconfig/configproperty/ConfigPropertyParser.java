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

import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static org.chorusbdd.chorus.handlerconfig.configproperty.ConfigPropertyParser.PrimitiveToWrapperClassConverter.getWrapperClass;
import static org.chorusbdd.chorus.handlerconfig.configproperty.ConfigPropertyUtils.createValidationPatternFromEnumType;
import static org.chorusbdd.chorus.handlerconfig.configproperty.PrimitiveOrEnumValidationPattern.getDefaultPatternIfPrimitive;


public class ConfigPropertyParser {


    Map<String, ConfigurationProperty> getConfigPropertiesByName(Class configClass) throws ConfigBuilderException {
        List<ConfigurationProperty> properties = getConfigProperties(configClass);
        return properties.stream().collect(toMap(ConfigurationProperty::getName, identity()));
    }


    public List<ConfigurationProperty> getConfigProperties(Class configClass) throws ConfigBuilderException {
        Method[] methods = getMethodsFromConfigClass(configClass);

        List<ConfigurationProperty> result = new LinkedList<>();
        List<Method> l = Arrays.stream(methods)
                .filter(m -> m.isAnnotationPresent(ConfigProperty.class))
                .collect(Collectors.toList());

        for (Method m : l) {
            addConfigProperty(result, m);
        }

        return result;
    }

    List<Method> getValidationMethods(Class configClass) throws ConfigBuilderException {
        Method[] methods = getMethodsFromConfigClass(configClass);
        List<Method> result = new LinkedList<>();
        for (Method m : methods) {
            if (m.isAnnotationPresent(ConfigValidator.class) && checkValidationMethod(m, configClass)) {
                result.add(m);
            }
        }
        return result;
    }


    private Method[] getMethodsFromConfigClass(Class configClass) {
        return configClass.getDeclaredMethods();
    }

    private boolean checkValidationMethod(Method method, Class configClass) throws ConfigBuilderException {
        if (method.getParameterCount() > 0) {
            throw new ConfigBuilderException("Validation method " + method.getName() + " on class " + configClass.getSimpleName() + " requires an argument and this is not supported");
        } else if (method.getReturnType() != Void.TYPE) {
            throw new ConfigBuilderException("Validation method " + method.getName() + " on class " + configClass.getSimpleName() + " does not have a void return type");
        }
        return true;
    }

    private void addConfigProperty(List<ConfigurationProperty> result, Method method) throws ConfigBuilderException {
        ConfigProperty p = method.getAnnotation(ConfigProperty.class);

        if (!method.getName().startsWith("set")) {
            throw new ConfigBuilderException(
                    "A config bean can only annotate a setter method (the method " + method.getName() +
                            " does not start with 'set'), for " + getAnnotationDescription(p));
        }

        if (method.getParameterCount() != 1) {
            throw new ConfigBuilderException(
                    "The annotated method must take a single argument, for " + getAnnotationDescription(p)
            );
        }

        Class javaType = method.getParameterTypes()[0];

        //always use the wrapper class to describe the required java type, although this will be unboxed as required into the primitive
        //equivalent when the setter is invoked reflectively
        if (javaType.isPrimitive()) {
            javaType = getWrapperClass(javaType);
        }

        Object defaultValue = null;
        Optional<Pattern> validationPattern = getValidationPattern(p, javaType);
        ConfigBuilderTypeConverter converterFunction = getConverterFunction(p, javaType);

        if (!"".equals(p.defaultValue())) {

            if (validationPattern.isPresent()) {
                validateDefaultValue(validationPattern.get(), p);
            }

            defaultValue = convertDefaultValue(p, converterFunction, javaType);
            if (javaType != defaultValue.getClass()) {
                throw new ConfigBuilderException("Default value \"" + p.defaultValue() + "\" was converted to a type " +
                        defaultValue.getClass().getName() + " which did not match the expected class type " + javaType.getName()
                        + " for " + getAnnotationDescription(p));
            }
        }

        ConfigurationProperty h = new HandlerConfigPropertyImpl(
                p.name(),
                javaType,
                validationPattern.orElse(null),
                p.description(),
                defaultValue,
                p.mandatory(),
                converterFunction,
                method,
                p.order()
        );

        result.add(h);
    }

    private Optional<Pattern> getValidationPattern(ConfigProperty p, Class javaType) throws ConfigBuilderException {
        Optional<Pattern> pattern = p.validationPattern().equals("") ?
                getDefaultValidationPattern(javaType) :
                Optional.of(compilePattern(p));

        return pattern;
    }

    /**
     * Try to create a sensible default for validation pattern, in the case where the java type
     * of the parameter is an enum type or a primitive or primitive wrapper type
     * @param javaType
     * @return
     */
    private Optional<Pattern> getDefaultValidationPattern(Class javaType) {
        return javaType.isEnum() ?
                Optional.of(createValidationPatternFromEnumType(javaType)) :
                getDefaultPatternIfPrimitive(javaType);
    }

    private Pattern compilePattern(ConfigProperty p) throws ConfigBuilderException {
        Pattern pattern;
        try {
            pattern = Pattern.compile(p.validationPattern());
        } catch (PatternSyntaxException e) {
            throw new ConfigBuilderException("The validation pattern '" + p.validationPattern() + "' could not be compiled, for " + getAnnotationDescription(p));
        }
        return pattern;
    }

    private void validateDefaultValue(Pattern validationPattern, ConfigProperty p) throws ConfigBuilderException {

        if (!validationPattern.matcher(p.defaultValue()).matches()) {
            throw new ConfigBuilderException(
                String.format("The default value [%s] did not match the validation pattern [%s], for %s",
                    p.defaultValue(),
                    validationPattern.pattern(),
                    getAnnotationDescription(p)
                )
            );
        }
    }

    private ConfigBuilderTypeConverter getConverterFunction(ConfigProperty p, Class javaType) throws ConfigBuilderException {
        ConfigBuilderTypeConverter f;
        try {
            Class<? extends ConfigBuilderTypeConverter> c = p.valueConverter();
            f = c.newInstance();
        } catch (Exception e) {
            throw new ConfigBuilderException("Failed to instantiate converter class " + p.valueConverter().getClass().getName() +
                    " for " + getAnnotationDescription(p), e);
        }
        return f;
    }

    private Object convertDefaultValue(ConfigProperty p, ConfigBuilderTypeConverter f, Class javaType) throws ConfigBuilderException {
        return f.convertToTargetType(p.defaultValue(), javaType);
    }

    private String getAnnotationDescription(ConfigProperty p) {
        return ConfigProperty.class.getSimpleName() + " annotation with name " + p.name();
    }

    static class HandlerConfigPropertyImpl implements ConfigurationProperty {

        private final String name;
        private final Class javaType;
        private final Pattern validationPattern;
        private final String description;
        private final Object defaultValue;
        private final boolean mandatory;
        private final ConfigBuilderTypeConverter valueConverter;
        private final Method setterMethod;
        private int order;

        public HandlerConfigPropertyImpl(String name, Class javaType, Pattern validationPattern, String description,
                                         Object defaultValue, boolean mandatory, ConfigBuilderTypeConverter valueConverter, 
                                         Method setterMethod, int order) {
            Objects.requireNonNull(name, "name cannot be null");
            Objects.requireNonNull(javaType, "javaType cannot be null");
            Objects.requireNonNull(description, "description cannot be null");
            Objects.requireNonNull(valueConverter, "valueConverter cannot be null");
            Objects.requireNonNull(setterMethod, "setterMethod cannot be null");
            Objects.requireNonNull(order, "order cannot be null");
            //defaultValue can be null as can validationPattern
            this.name = name;
            this.javaType = javaType;
            this.validationPattern = validationPattern;
            this.description = description;
            this.defaultValue = defaultValue;
            this.mandatory = mandatory;
            this.valueConverter = valueConverter;
            this.setterMethod = setterMethod;
            this.order = order;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public Class getJavaType() {
            return javaType;
        }

        @Override
        public Optional<Pattern> getValidationPattern() {
            return Optional.ofNullable(validationPattern);
        }

        @Override
        public String getDescription() {
            return description;
        }

        @Override
        public Optional<Object> getDefaultValue() {
            return Optional.ofNullable(defaultValue);
        }

        @Override
        public boolean isMandatory() {
            return mandatory;
        }

        @Override
        public ConfigBuilderTypeConverter getValueConverter() {
            return valueConverter;
        }

        @Override
        public int getOrder() {
            return order;
        }

        public Method getSetterMethod() {
            return setterMethod;
        }
    }

    public static class PrimitiveToWrapperClassConverter {
        public final static Map<Class<?>, Class<?>> map = new HashMap<>();

        static {
            map.put(boolean.class, Boolean.class);
            map.put(byte.class, Byte.class);
            map.put(short.class, Short.class);
            map.put(char.class, Character.class);
            map.put(int.class, Integer.class);
            map.put(long.class, Long.class);
            map.put(float.class, Float.class);
            map.put(double.class, Double.class);
            map.put(void.class, Void.class);
        }

        public static Class getWrapperClass(Class primitiveType) {
            if (!primitiveType.isPrimitive()) {
                throw new IllegalArgumentException("Parameter must be a primitive type");
            }
            return map.get(primitiveType);
        }
    }
}
