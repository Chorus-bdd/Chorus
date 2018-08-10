package org.chorusbdd.chorus.handlerconfig.configproperty;

import org.chorusbdd.chorus.util.ChorusException;
import org.chorusbdd.chorus.util.function.Tuple2;

import java.lang.reflect.Method;
import java.util.*;
import java.util.function.BiFunction;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;


public class ConfigClassPropertyParser {
    
    List<Tuple2<HandlerConfigProperty, Method>> readProperties(Object configBean) {
        
        Class clazz = configBean.getClass();
        Method[] methods = clazz.getDeclaredMethods();
        
        List<Tuple2<HandlerConfigProperty, Method>> result = new LinkedList<>();
        Arrays.asList(methods)
            .stream()
            .filter(m -> m.isAnnotationPresent(HandlerConfigClassProperty.class))
            .forEach(method -> {
                
                HandlerConfigClassProperty p = method.getAnnotation(HandlerConfigClassProperty.class);
                
                if ( ! method.getName().startsWith("set")) {
                    throw new ChorusException(
                        "A config bean can only annotate a setter method (the method " + method.getName() + 
                            " does not start with 'set'), for " + getAnnotationDescription(p));
                } 
                
                if ( method.getParameterCount() != 1) {
                    throw new ChorusException(
                        "The annotated method must take a single argument, for " + getAnnotationDescription(p)
                    );
                }

                if ( method.getParameterTypes()[0] != p.javaType()) {
                    throw new ChorusException(
                        "The annotated method must take an argument " +
                        "with a type which matches the javaType, for " + getAnnotationDescription(p)
                    );
                }

                Object value = null;
                if ( ! "".equals(p.defaultValue())) {
                    
                    if ( ! "".equals(p.validationPattern())) {
                        validateDefaultValue(p);
                    }
                    
                    value = instantiateConverterAndConvertDefaultValue(p);
                    if (p.javaType() != value.getClass()) {
                        throw new ChorusException("Default value \"" + p.defaultValue() + "\" was converted to a type " +
                            value.getClass().getName() + " which did not match the expected class type " +
                            p.javaType().getName() + " for " + getAnnotationDescription(p));
                    }
                }

                HandlerConfigProperty h = new HandlerConfigPropertyImpl(
                    p.name(),
                    p.javaType(),
                    nullIfEmptyString(p.validationPattern()), 
                    p.description(),
                    value,
                    p.mandatory()
                );
                
                result.add(Tuple2.tuple2(h, method));
            });
        
        return result;
    }

    private void validateDefaultValue(HandlerConfigClassProperty p) {

        Pattern pattern;
        
        try {
            pattern = Pattern.compile(p.validationPattern());
        } catch (PatternSyntaxException e) {
            throw new ChorusException("The validation pattern '" + p.validationPattern() + "' could not be compiled, for " + getAnnotationDescription(p));   
        }
        
        if ( ! pattern.matcher(p.defaultValue()).matches()) {
            throw new ChorusException("The default value did not match the validation pattern, for " + getAnnotationDescription(p));
        }

    }

    private String nullIfEmptyString(String validationPattner) {
        return "".equals(validationPattner) ? null : validationPattner;
    }

    private Object instantiateConverterAndConvertDefaultValue(HandlerConfigClassProperty p) {
        BiFunction<String, Class, Object> f;
        try {
            Class<? extends BiFunction<String, Class, Object>> c = p.valueConverter();
            f = c.newInstance();
        } catch (Exception e) {
            throw new ChorusException("Failed to instantiate converter class " + p.valueConverter().getClass().getName() + " for " + getAnnotationDescription(p), e);
        }

        try {
            return f.apply(p.defaultValue(), p.javaType());
        } catch (Exception e) {
            throw new ChorusException("Failed while converting default value provided for " + getAnnotationDescription(p), e);
        }

    }
    
    private String getAnnotationDescription(HandlerConfigClassProperty p) {
        return HandlerConfigClassProperty.class.getSimpleName() + " annotation with name " + p.name();
    }

    class HandlerConfigPropertyImpl<T> implements HandlerConfigProperty<T> {
        
        private final String name;
        private final Class<T> javaType;
        private final String validationPattern;
        private final String description;
        private final T defaultValue;
        private final boolean mandatory;

        public HandlerConfigPropertyImpl(String name, Class<T> javaType, String validationPattern, String description, T defaultValue, boolean mandatory) {
            Objects.requireNonNull(name, "name cannot be null");
            Objects.requireNonNull(javaType, "javaType cannot be null");
            Objects.requireNonNull(description, "description cannot be null");
            //defaultValue can be null as can validationPattern
            this.name = name;
            this.javaType = javaType;
            this.validationPattern = validationPattern;
            this.description = description;
            this.defaultValue = defaultValue;
            this.mandatory = mandatory;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public Class<T> getJavaType() {
            return javaType;
        }

        @Override
        public Optional<String> getValidationPattern() {
            return Optional.ofNullable(validationPattern);
        }

        @Override
        public String getDescription() {
            return description;
        }

        @Override
        public Optional<T> getDefaultValue() {
            return Optional.ofNullable(defaultValue);
        }

        @Override
        public boolean isMandatory() {
            return mandatory;
        }
    }
}
