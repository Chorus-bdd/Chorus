package org.chorusbdd.chorus.handlerconfig;

import org.chorusbdd.chorus.util.ChorusConstants;
import org.chorusbdd.chorus.util.properties.PropertyOperations;

import java.util.Properties;

import static org.chorusbdd.chorus.util.properties.PropertyOperations.properties;

/**
 * Created by GA2EBBU on 03/02/2015.
 */
public class HandlerConfigLoad {

    /**
     * Get all properties for a simple handler which takes only properties prefixed with handler name:
     *
     * myHandler.property1=val
     * myHandler.property2=val
     */
    public Properties getHandlerProperties(ConfigurationManager configurationManager, String handlerPrefix) {
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
    public Properties getPropertiesForConfigName(ConfigurationManager configurationManager, String handlerPrefix, String configName) {
        PropertyOperations handlerProps = properties(getHandlerProperties(configurationManager, handlerPrefix));

        PropertyOperations defaultProps = handlerProps.filterByAndRemoveKeyPrefix(ChorusConstants.DEFAULT_PROPERTIES_GROUP + ".");
        PropertyOperations configProps = handlerProps.filterByAndRemoveKeyPrefix(configName + ".");

        PropertyOperations merged = defaultProps.merge(configProps);
        return merged.loadProperties();
    }
}
