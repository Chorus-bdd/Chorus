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
package org.chorusbdd.chorus.handlerconfig.configbean;

import org.chorusbdd.chorus.util.properties.PropertyOperations;
import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;
import org.chorusbdd.chorus.util.ChorusException;

import java.util.*;

import static org.chorusbdd.chorus.util.properties.PropertyOperations.properties;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 18/09/12
 * Time: 08:39
 * 
 * Convert properties from Property instances to handler-specific config beans
 * using a HandlerConfigFactory
 */
public class DefaultConfigBeanBuilder<E extends HandlerConfigBean> implements ConfigBeanBuilder<E> {

    private static ChorusLog log = ChorusLogFactory.getLog(DefaultConfigBeanBuilder.class);
    private static final Properties EMPTY_PROPERTIES = new Properties();
    private String handlerName;
    private ConfigBeanFactory<E> configBuilder;

    public DefaultConfigBeanBuilder(
            String handlerName,
            ConfigBeanFactory<E> configBuilder) {
        this.handlerName = handlerName;
        this.configBuilder = configBuilder;
    }

    public Map<String, E> buildConfigs(Map<String, Properties> groupedProperties) {
        Map<String, E> map = new HashMap<String, E>();
        try {
           //get any default properties for this handler type
           PropertyOperations defaultProps = properties(groupedProperties.get(HandlerConfigBean.DEFAULT_PROPERTIES_GROUP));

           for ( Map.Entry<String, Properties> configProps : groupedProperties.entrySet()) {

               //merge instance properties over the defaults
               Properties p = defaultProps.merge(properties(configProps.getValue())).loadProperties();

               //create and validate
               E newConfig = configBuilder.createConfig(p, configProps.getKey());
               ConfigBeanValidator<? super E> validator = configBuilder.createValidator(newConfig);

               if ( ! HandlerConfigBean.DEFAULT_PROPERTIES_GROUP.equals(configProps.getKey())) {
                   //apart from the 'default' configs all configs must pass validation rules
                   //we still include the default configs in results so that we can see what the defaults were
                   addIfValid(map, configProps, newConfig, validator);
               }
           }
        } catch (Exception e) {
           log.error("Failed to load handler configuration",e);
           throw new ChorusException("Failed to load handler configuration");
        }
        return map;
    }

    private void addIfValid(Map<String, E> configMap, Map.Entry<String, Properties> props, E newConfig, ConfigBeanValidator<? super E> validator) {
        if (validator.isValid(newConfig)) {
            configMap.put(props.getKey(), newConfig);
        } else {
            log.warn(validator.getErrorDescription());
            log.warn("Removing " + props.getKey() + " which is not a valid " + handlerName + " handler config");
            log.debug(newConfig);
        }
    }

}
