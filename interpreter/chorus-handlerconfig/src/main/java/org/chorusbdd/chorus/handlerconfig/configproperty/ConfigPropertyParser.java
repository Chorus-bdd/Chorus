package org.chorusbdd.chorus.handlerconfig.configproperty;

import org.chorusbdd.chorus.util.ChorusException;

import java.lang.reflect.Method;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;


public class ConfigPropertyParser {
    

    public Map<String, HandlerConfigProperty> getConfigPropertiesByName(Class configClass) {
        List<HandlerConfigProperty> properties = getConfigProperties(configClass);
        return properties.stream().collect(toMap(HandlerConfigProperty::getName, identity()));
    }
    
    public List<HandlerConfigProperty> getConfigProperties(Class configClass) {
        Method[] methods = configClass.getDeclaredMethods();
        
        List<HandlerConfigProperty> result = new LinkedList<>();
        Arrays.asList(methods)
            .stream()
            .filter(m -> m.isAnnotationPresent(ConfigProperty.class))
            .forEach(method -> addConfigProperty(result, method));
        
        return result;
    }

    private void addConfigProperty(List<HandlerConfigProperty> result, Method method) {
        ConfigProperty p = method.getAnnotation(ConfigProperty.class);

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

        Object defaultValue = null;
        Pattern validationPattern = null;
        Function<String, Object> converterFunction = getConverterFunction(p, javaType);
        
        if ( ! "".equals(p.defaultValue())) {
            
            if ( ! "".equals(p.validationPattern())) {
                validationPattern = validateDefaultValue(p);
            }
            
            defaultValue = convertDefaultValue(p, converterFunction);
            if (javaType != defaultValue.getClass()) {
                throw new ChorusException("Default value \"" + p.defaultValue() + "\" was converted to a type " +
                    defaultValue.getClass().getName() + " which did not match the expected class type " +javaType.getName() 
                    + " for " + getAnnotationDescription(p));
            }
        }

        HandlerConfigProperty h = new HandlerConfigPropertyImpl(
            p.name(),
            javaType,
            validationPattern, 
            p.description(),
            defaultValue,
            p.mandatory(),
            converterFunction,
            method
        );

        result.add(h);
    }

    private Pattern validateDefaultValue(ConfigProperty p) {

        Pattern pattern;
        
        try {
            pattern = Pattern.compile(p.validationPattern());
        } catch (PatternSyntaxException e) {
            throw new ChorusException("The validation pattern '" + p.validationPattern() + "' could not be compiled, for " + getAnnotationDescription(p));   
        }
        
        if ( ! pattern.matcher(p.defaultValue()).matches()) {
            throw new ChorusException("The default value did not match the validation pattern, for " + getAnnotationDescription(p));
        }

        return pattern;
    }

    private Function<String, Object> getConverterFunction(ConfigProperty p, Class javaType) {
        BiFunction<String, Class, Object> f;
        try {
            Class<? extends BiFunction<String, Class, Object>> c = p.valueConverter();
            f = c.newInstance();
            
            return str -> f.apply(str, javaType);
        } catch (Exception e) {
            throw new ChorusException("Failed to instantiate converter class " + p.valueConverter().getClass().getName() +
                " for " + getAnnotationDescription(p), e);
        }
    }
    
    private Object convertDefaultValue(ConfigProperty p, Function<String, Object> f) {
        try {
            return f.apply(p.defaultValue());
        } catch (Exception e) {
            throw new ChorusException("Failed while converting default value provided for " + getAnnotationDescription(p), e);
        }

    }
    
    private String getAnnotationDescription(ConfigProperty p) {
        return ConfigProperty.class.getSimpleName() + " annotation with name " + p.name();
    }

    static class HandlerConfigPropertyImpl implements HandlerConfigProperty {
        
        private final String name;
        private final Class javaType;
        private final Pattern validationPattern;
        private final String description;
        private final Object defaultValue;
        private final boolean mandatory;
        private final Function<String, Object> valueConverter;
        private final Method setterMethod;

        public HandlerConfigPropertyImpl(String name, Class javaType, Pattern validationPattern, String description, Object defaultValue, boolean mandatory, Function<String, Object> valueConverter, Method setterMethod) {
            Objects.requireNonNull(name, "name cannot be null");
            Objects.requireNonNull(javaType, "javaType cannot be null");
            Objects.requireNonNull(description, "description cannot be null");
            Objects.requireNonNull(valueConverter, "valueConverter cannot be null");
            Objects.requireNonNull(setterMethod, "setterMethod cannot be null");
            //defaultValue can be null as can validationPattern
            this.name = name;
            this.javaType = javaType;
            this.validationPattern = validationPattern;
            this.description = description;
            this.defaultValue = defaultValue;
            this.mandatory = mandatory;
            this.valueConverter = valueConverter;
            this.setterMethod = setterMethod;
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
        public Function<String, Object> getValueConverter() {
            return valueConverter;
        }

        public Method getSetterMethod() {
            return setterMethod;
        }
    }
}
