package org.chorusbdd.chorus.handlerconfig.configproperty;

import org.chorusbdd.chorus.util.ChorusException;

import java.lang.reflect.Method;
import java.util.*;
import java.util.function.BiFunction;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;


public class ConfigClassUtils {
    
    
    public <C> C createInstance(Class<C> configBeanClass, Properties properties) {
        return null;
    }


    public Map<String, HandlerConfigProperty> getConfigPropertiesByName(Object configBean) {
        List<HandlerConfigProperty> properties = getConfigProperties(configBean);
        return properties.stream().collect(toMap(HandlerConfigProperty::getName, identity()));
    }
    
    public List<HandlerConfigProperty> getConfigProperties(Object configBean) {
        
        Class clazz = configBean.getClass();
        Method[] methods = clazz.getDeclaredMethods();
        
        List<HandlerConfigProperty> result = new LinkedList<>();
        Arrays.asList(methods)
            .stream()
            .filter(m -> m.isAnnotationPresent(ConfigClassProperty.class))
            .forEach(method -> addConfigProperty(result, method));
        
        return result;
    }

    private void addConfigProperty(List<HandlerConfigProperty> result, Method method) {
        ConfigClassProperty p = method.getAnnotation(ConfigClassProperty.class);

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
        
        Class javaType = method.getParameterTypes()[0];

        Object value = null;
        if ( ! "".equals(p.defaultValue())) {
            
            if ( ! "".equals(p.validationPattern())) {
                validateDefaultValue(p);
            }
            
            value = convertDefaultValue(p, javaType);
            if (javaType != value.getClass()) {
                throw new ChorusException("Default value \"" + p.defaultValue() + "\" was converted to a type " +
                    value.getClass().getName() + " which did not match the expected class type " +javaType.getName() 
                    + " for " + getAnnotationDescription(p));
            }
        }

        HandlerConfigProperty h = new HandlerConfigPropertyImpl(
            p.name(),
            javaType,
            nullIfEmptyString(p.validationPattern()), 
            p.description(),
            value,
            p.mandatory(),
            method
        );

        result.add(h);
    }

    private void validateDefaultValue(ConfigClassProperty p) {

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

    private String nullIfEmptyString(String validationPattern) {
        return "".equals(validationPattern) ? null : validationPattern;
    }

    private Object convertDefaultValue(ConfigClassProperty p, Class javaType) {
        BiFunction<String, Class, Object> f;
        try {
            Class<? extends BiFunction<String, Class, Object>> c = p.valueConverter();
            f = c.newInstance();
        } catch (Exception e) {
            throw new ChorusException("Failed to instantiate converter class " + p.valueConverter().getClass().getName() + 
                " for " + getAnnotationDescription(p), e);
        }

        try {
            return f.apply(p.defaultValue(), javaType);
        } catch (Exception e) {
            throw new ChorusException("Failed while converting default value provided for " + getAnnotationDescription(p), e);
        }

    }
    
    private String getAnnotationDescription(ConfigClassProperty p) {
        return ConfigClassProperty.class.getSimpleName() + " annotation with name " + p.name();
    }

    static class HandlerConfigPropertyImpl<T> implements HandlerConfigProperty<T> {
        
        private final String name;
        private final Class<T> javaType;
        private final String validationPattern;
        private final String description;
        private final T defaultValue;
        private final boolean mandatory;
        private final Method setterMethod;

        public HandlerConfigPropertyImpl(String name, Class<T> javaType, String validationPattern, String description, T defaultValue, boolean mandatory, Method setterMethod) {
            Objects.requireNonNull(name, "name cannot be null");
            Objects.requireNonNull(javaType, "javaType cannot be null");
            Objects.requireNonNull(description, "description cannot be null");
            Objects.requireNonNull(setterMethod, "setterMethod cannot be null");
            //defaultValue can be null as can validationPattern
            this.name = name;
            this.javaType = javaType;
            this.validationPattern = validationPattern;
            this.description = description;
            this.defaultValue = defaultValue;
            this.mandatory = mandatory;
            this.setterMethod = setterMethod;
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

        public Method getSetterMethod() {
            return setterMethod;
        }
    }
}
