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
package org.chorusbdd.chorus.handlerconfig;

import org.chorusbdd.chorus.util.ChorusConstants;
import org.chorusbdd.chorus.util.properties.PropertyOperations;

import java.util.Properties;

import static org.chorusbdd.chorus.util.properties.PropertyOperations.properties;

/**
 * Created by Nick E on 03/02/2015.
 */
public class HandlerConfigLoader {

    /**
     * Get all properties for a simple handler which takes only properties prefixed with handler name:
     *
     * myHandler.property1=val
     * myHandler.property2=val
     */
    public Properties loadProperties(ConfigurationManager configurationManager, String handlerPrefix) {
        PropertyOperations allproperties = properties(configurationManager.getAllProperties());

        PropertyOperations handlerProps = allproperties.filterByAndRemoveKeyPrefix(handlerPrefix + ".");
        return handlerProps.loadProperties();
    }

    /**
     * Get properties for a specific config, for a handler which maintains properties grouped by configNames
     *
     * Defaults may also be provided in a special default configName, defaults provide base values which may be overridden
     * by those set at configName level.
     *
     * myHandler.config1.property1=val
     * myHandler.config1.property2=val
     *
     * myHandler.config2.property1=val
     * myHandler.config2.property2=val
     *
     * myHandler.default.property1=val
     */
    public Properties loadPropertiesForSubGroup(ConfigurationManager configurationManager, String handlerPrefix, String groupName) {
        PropertyOperations handlerProps = properties(loadProperties(configurationManager, handlerPrefix));

        PropertyOperations defaultProps = handlerProps.filterByAndRemoveKeyPrefix(ChorusConstants.DEFAULT_PROPERTIES_GROUP + ".");
        PropertyOperations configProps = handlerProps.filterByAndRemoveKeyPrefix(groupName + ".");

        PropertyOperations merged = defaultProps.merge(configProps);
        return merged.loadProperties();
    }
}
