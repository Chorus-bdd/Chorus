/**
 *  Copyright (C) 2000-2013 The Software Conservancy and Original Authors.
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

import org.chorusbdd.chorus.handlerconfig.HandlerConfig;
import org.chorusbdd.chorus.handlerconfig.HandlerConfigBuilder;
import org.chorusbdd.chorus.handlerconfig.HandlerConfigValidator;
import org.chorusbdd.chorus.util.ChorusException;
import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;

import java.util.Map;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 18/09/12
 * Time: 08:39
 */
public abstract class AbstractConfigLoader<E extends HandlerConfig> {

    private static ChorusLog log = ChorusLogFactory.getLog(AbstractConfigLoader.class);
    private static final Properties EMPTY_PROPERTIES = new Properties();
    private String handlerDescription;

    public AbstractConfigLoader(String handlerDescription) {
        this.handlerDescription = handlerDescription;
    }

    public Map<String, E> loadConfigs() {
        Map<String, E> configs = doLoadConfigs();
        return configs;
    }

    public abstract Map<String, E> doLoadConfigs();

    protected void addConfigsFromPropertyGroups(Map<String, Properties> propertiesGroups, Map<String, E> configMap, HandlerConfigBuilder<E> handlerConfigBuilder) {
        try {
           //get any default properties for this handler type
           Properties defaultProperties = propertiesGroups.get(HandlerConfig.DEFAULT_PROPERTIES_GROUP);
           if ( defaultProperties == null) {
               defaultProperties = EMPTY_PROPERTIES;
           }

           for ( Map.Entry<String, Properties> props : propertiesGroups.entrySet()) {
               E c = handlerConfigBuilder.createConfig(props.getValue(), defaultProperties);
               HandlerConfigValidator<? super E> validator = handlerConfigBuilder.createValidator(c);

               //add the new config to the configMap only if it is valid or represents default values
               addIfValid(configMap, props, c, validator);
           }
        } catch (Exception e) {
           log.error("Failed to load handler configuration",e);
           throw new ChorusException("Failed to load handler configuration");
        }
    }

    private void addIfValid(Map<String, E> configMap, Map.Entry<String, Properties> props, E c, HandlerConfigValidator<? super E> validator) {
        //apart from the 'default' configs all configs must pass validation rules
        //the defaults specified may be an incomplete subset
        if (props.getKey().equals(HandlerConfig.DEFAULT_PROPERTIES_GROUP) || validator.isValid(c)) {
            configMap.put(props.getKey(), c);
        } else {
            log.warn("Removing " + props.getKey() + " which is not a valid " + handlerDescription + " handler config " + validator.getValidationRuleDescription());
            log.debug(c);
        }
    }

}
