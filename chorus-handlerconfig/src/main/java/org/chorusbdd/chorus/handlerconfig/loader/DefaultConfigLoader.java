/**
 *  Copyright (C) 2000-2014 The Software Conservancy and Original Authors.
 *  All rights reserved.
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to
 *  deal in the Software without restriction, including without limitation the
 *  rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 *  sell copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 *  FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 *  IN THE SOFTWARE.
 *
 *  Nothing in this notice shall be deemed to grant any rights to trademarks,
 *  copyrights, patents, trade secrets or any other intellectual property of the
 *  licensor or any contributor except as expressly stated herein. No patent
 *  license is granted separate from the Software, for code that you delete from
 *  the Software, or for combinations of the Software with other software or
 *  hardware.
 */
package org.chorusbdd.chorus.handlerconfig.loader;

import org.chorusbdd.chorus.handlerconfig.ConfigValidator;
import org.chorusbdd.chorus.handlerconfig.HandlerConfig;
import org.chorusbdd.chorus.handlerconfig.HandlerConfigFactory;
import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;
import org.chorusbdd.chorus.util.ChorusException;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 18/09/12
 * Time: 08:39
 * 
 * Load properties from a PropertiesSource and convert them to
 * Handler specific configs using a HandlerConfigFactory
 */
public class DefaultConfigLoader<E extends HandlerConfig> implements ConfigLoader<E> {

    private static ChorusLog log = ChorusLogFactory.getLog(DefaultConfigLoader.class);
    private static final Properties EMPTY_PROPERTIES = new Properties();
    private String handlerName;
    private HandlerConfigFactory<E> configBuilder;
    private Map<String, Properties> propertiesByConfigName;

    public DefaultConfigLoader(
            String handlerName,
            HandlerConfigFactory<E> configBuilder,
            Map<String, Properties> propertiesByConfigName) {
        this.handlerName = handlerName;
        this.configBuilder = configBuilder;
        this.propertiesByConfigName = propertiesByConfigName;
    }

    public Map<String, E> loadConfigs() {
        Map<String, E> map = new HashMap<String, E>();
        try {
           //get any default properties for this handler type
           Properties defaultProperties = propertiesByConfigName.get(HandlerConfig.DEFAULT_PROPERTIES_GROUP);
           if ( defaultProperties == null) {
               defaultProperties = EMPTY_PROPERTIES;
           }

           for ( Map.Entry<String, Properties> props : propertiesByConfigName.entrySet()) {

               //build an ordered list supplying default property values first
               List<Properties> propertiesList = Arrays.asList(defaultProperties, props.getValue());

               //create and validate
               E newConfig = configBuilder.createConfig(propertiesList);
               ConfigValidator<? super E> validator = configBuilder.createValidator(newConfig);

               if ( ! HandlerConfig.DEFAULT_PROPERTIES_GROUP.equals(props.getKey())) {
                   //apart from the 'default' configs all configs must pass validation rules
                   //we still include the default configs in results so that we can see what the defaults were
                   addIfValid(map, props, newConfig, validator);
               }
           }
        } catch (Exception e) {
           log.error("Failed to load handler configuration",e);
           throw new ChorusException("Failed to load handler configuration");
        }
        return map;
    }

    private void addIfValid(Map<String, E> configMap, Map.Entry<String, Properties> props, E newConfig, ConfigValidator<? super E> validator) {
        if (validator.isValid(newConfig)) {
            configMap.put(props.getKey(), newConfig);
        } else {
            log.warn(validator.getErrorDescription());
            log.warn("Removing " + props.getKey() + " which is not a valid " + handlerName + " handler config");
            log.debug(newConfig);
        }
    }

}
