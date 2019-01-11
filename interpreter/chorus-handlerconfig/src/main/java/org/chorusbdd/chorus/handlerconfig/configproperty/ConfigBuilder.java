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

import org.chorusbdd.chorus.handlerconfig.configproperty.ConfigPropertyParser.HandlerConfigPropertyImpl;
import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


/**
 * Build an instance of a config class from a Properties object
 * 
 * The properties object will have its contents validated against the annotated properties of the config class
 */
public class ConfigBuilder {

    private ChorusLog log = ChorusLogFactory.getLog(ConfigBuilder.class);
    
    private ConfigPropertyParser configPropertyParser = new ConfigPropertyParser();
    
    public <C> C buildConfig(Class<C> configClass, Properties properties) throws ConfigBuilderException {

        Map<String, ConfigurationProperty> configPropertiesByName = configPropertyParser.getConfigPropertiesByName(configClass);

        C configInstance;
        try {
            configInstance = configClass.newInstance();
        } catch (Exception e) {
            throw new ConfigBuilderException("Failed to instantiate config class " + configClass.getSimpleName() + " - " + e.getClass().getSimpleName(), e);
        }
        
        //iterate sorted by field name for consistent/deterministic behaviour
        List<String> props = configPropertiesByName.keySet().stream().sorted().collect(Collectors.toList());
            
        for ( String configPropertyName : props) {
            log.debug("Processing config property " + configPropertyName);
            
            HandlerConfigPropertyImpl configProperty = (HandlerConfigPropertyImpl)configPropertiesByName.get(configPropertyName);
            String propertyValue = properties.getProperty(configPropertyName);
            setValueForConfigProperty(configInstance, configPropertyName, configProperty, propertyValue);
        }
        
        warnOnUnusedProperties(properties, configPropertiesByName);

        runClassLevelValidation(configClass, configInstance);
        
        return configInstance;
    }

    private <C> void runClassLevelValidation(Class<C> configClass, C configInstance) throws ConfigBuilderException {
        List<Method> validationMethods = configPropertyParser.getValidationMethods(configClass);
        for (Method m : validationMethods) {
            try {
                m.invoke(configInstance);
            } catch (InvocationTargetException i) {
                log.debug("Config validation failed", i);
                throw new ConfigBuilderException("Validation method failed: [" + i.getCause().getMessage() + "]");
            } catch (Exception e) {
                throw new ConfigBuilderException("Failed to execute validation method " + m.getName() + " on config class " + configClass.getSimpleName(), e);
            }
        }
    }

    private <C> void setValueForConfigProperty(C configInstance, String configPropertyName, HandlerConfigPropertyImpl configProperty, String propertyValue) throws ConfigBuilderException {
        Object convertedValue = null;

        if ( propertyValue != null) {
            log.trace("Validating config property " + configPropertyName + " with value " + propertyValue);
            validate(configPropertyName, propertyValue, configProperty.getValidationPattern());

            log.trace("Converting config property " + configPropertyName + " with value " + propertyValue);
            convertedValue = applyConverterFunction(configPropertyName, configProperty, propertyValue);
        }

        if ( convertedValue == null) {
            log.trace("Looking for default value for config property " + configPropertyName);
            convertedValue = configProperty.getDefaultValue().orElse(null);
        }

        log.debug("Value for config property named " + configPropertyName + " is " + convertedValue);

        if ( convertedValue == null ) {
            if ( configProperty.isMandatory()) {
                throw new ConfigBuilderException("Property " + configPropertyName + " is mandatory but no value was provided");
            } 
        } else {
            if (convertedValue.getClass() != configProperty.getJavaType()) {
                throw new ConfigBuilderException("The expected value type for the property " + configPropertyName + 
                    " is a " + configProperty.getJavaType().getName() + " but the converted value was a " 
                    + convertedValue.getClass().getName());
            }
            
            log.debug("Setting config property value for " + configPropertyName + " to " + convertedValue);
            try {
                configProperty.getSetterMethod().invoke(configInstance, convertedValue);
            } catch (Exception e) {
               throw new ConfigBuilderException("Failed to set property + " + configPropertyName + " to value " + convertedValue + 
                   " on config instance with class type " + configInstance.getClass().getName(), e);
            }
        }
    }

    private void warnOnUnusedProperties(Properties properties, Map<String, ConfigurationProperty> configPropertiesByName) {
        Set<String> p = new HashSet<>(properties.stringPropertyNames());
        p.removeAll(configPropertiesByName.keySet());
        
        //warn in deterministic sorted order (facilitate testing)
        p.stream().sorted().forEachOrdered(k -> {
            log.warn("A property '" + k + "' was provided but no such property is supported");
        });
    }

    private Object applyConverterFunction(String propertyName, HandlerConfigPropertyImpl configProperty, String propertyValue) throws ConfigBuilderException {
        Object result = configProperty.getValueConverter().convertToTargetType(propertyValue, configProperty.getJavaType());
        
        if ( result == null) {
            throw new ConfigBuilderException("Property " + propertyName + " converter function returned null when converting value " + propertyValue);
        }
        return result;
    }

    private void validate(String propertyName, String propertyValue, Optional<Pattern> p) throws ConfigBuilderException {
        if ( p.isPresent() ) {
            Pattern pattern = p.get();
            if ( ! pattern.matcher(propertyValue).matches()) {
                throw new ConfigBuilderException("Property " + propertyName + " value '" + propertyValue + "' does not match pattern '" + pattern + "'");
            }
            
        } 
    }


}
