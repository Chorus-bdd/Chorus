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
package org.chorusbdd.chorus.handlerconfig.configproperty;

import org.chorusbdd.chorus.handlerconfig.configproperty.ConfigPropertyParser.HandlerConfigPropertyImpl;
import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;
import org.chorusbdd.chorus.util.ChorusException;

import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;


/**
 * Build an instance of a config class from a Properties object
 * 
 * The properties object will have its contents validated against the annotated properties of the config class
 */
public class ConfigBuilder {

    private ChorusLog log = ChorusLogFactory.getLog(ConfigBuilder.class);
    
    private ConfigPropertyParser configPropertyParser = new ConfigPropertyParser();
    
    public <C> C buildConfig(Class<C> configClass, Properties properties) {

        Map<String, HandlerConfigProperty> configPropertiesByName = configPropertyParser.getConfigPropertiesByName(configClass);

        C configInstance;
        try {
            configInstance = configClass.newInstance();
        } catch (Exception e) {
            throw new ChorusException("Failed to instantiate config class " + configClass.getName(), e);
        }
        
        //iterate sorted by field name for consistent/deterministic behaviour
        configPropertiesByName.keySet().stream().sorted().forEachOrdered(configPropertyName -> {
            
            log.debug("Processing config property " + configPropertyName);
            
            HandlerConfigPropertyImpl configProperty = (HandlerConfigPropertyImpl)configPropertiesByName.get(configPropertyName);
            
            String propertyValue = properties.getProperty(configPropertyName);
            Object convertedValue = null;
            
            if ( propertyValue != null) {
                log.trace("Validating config property " + configPropertyName + " with value " + propertyValue);
                validate(propertyValue, configProperty.getValidationPattern());

                log.trace("Converting config property " + configPropertyName + " with value " + propertyValue);
                convertedValue = applyConverterFunction(configProperty, propertyValue);
            }
            
            if ( convertedValue == null) {
                log.trace("Looking for default value for config property " + configPropertyName);
                convertedValue = configProperty.getDefaultValue().orElse(null);
            }
            
            log.debug("Value for config property named " + configPropertyName + " is " + convertedValue);
            
            if ( convertedValue == null ) {
                if ( configProperty.isMandatory()) {
                    throw new ChorusException("Property " + configPropertyName + " is mandatory but no value was provided");
                } 
            } else {
                if (convertedValue.getClass() != configProperty.getJavaType()) {
                    throw new ChorusException("The expected value type for the property " + configPropertyName + 
                        " is a " + configProperty.getJavaType().getName() + " but the converted value was a " 
                        + convertedValue.getClass().getName());
                }
                
                log.debug("Setting config property value for " + configPropertyName + " to " + convertedValue);
                try {
                    configProperty.getSetterMethod().invoke(configInstance, convertedValue);
                } catch (Exception e) {
                   throw new ChorusException("Failed to set property + " + configPropertyName + " to value " + convertedValue + 
                       " on config instance with class type " + configInstance.getClass().getName(), e);
                }
            }
        });
        
        warnOnUnusedProperties(properties, configPropertiesByName);
        
        return configInstance;
    }

    private void warnOnUnusedProperties(Properties properties, Map<String, HandlerConfigProperty> configPropertiesByName) {
        Set<String> p = properties.stringPropertyNames();
        p.removeAll(configPropertiesByName.keySet());
        
        //warn in deterministic sorted order (facilitate testing)
        p.stream().sorted().forEachOrdered(k -> {
            log.warn("A property '" + k + "' was provided but no such property is supported");
        });
    }

    private Object applyConverterFunction(HandlerConfigPropertyImpl configProperty, String propertyValue) {
        Object result = configProperty.getValueConverter().apply(propertyValue);
        
        if ( result == null) {
            throw new ChorusException("Converter function returned null when converting property value " + propertyValue);
        }
        return result;
    }

    private void validate(String propertyValue, Optional<Pattern> p) {
        p.ifPresent(pattern -> {
            if ( ! pattern.matcher(propertyValue).matches()) {
                throw new ChorusException("Property value '" + propertyValue + "' does not match pattern '" + pattern + "'");
            }
        });
    }


}
