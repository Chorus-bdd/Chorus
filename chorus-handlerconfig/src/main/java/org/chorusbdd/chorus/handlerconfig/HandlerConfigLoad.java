package org.chorusbdd.chorus.handlerconfig;

import org.chorusbdd.chorus.util.ChorusConstants;
import org.chorusbdd.chorus.util.properties.PropertyOperations;

import java.util.Properties;

import static org.chorusbdd.chorus.util.properties.PropertyOperations.properties;

/**
 * Created by GA2EBBU on 03/02/2015.
 */
public class HandlerConfigLoad {

    public Properties getConfig(ConfigurationManager configurationManager, String configName, String handlerPrefix) {
        PropertyOperations handlerProps = configurationManager.getAllProperties().filterByKeyPrefix(handlerPrefix + ".").removeKeyPrefix(handlerPrefix + ".");
        Properties defaultProps = handlerProps.filterByKeyPrefix(ChorusConstants.DEFAULT_PROPERTIES_GROUP + ".").removeKeyPrefix(ChorusConstants.DEFAULT_PROPERTIES_GROUP + ".").getProperties();
        Properties configProps = handlerProps.filterByKeyPrefix(configName + ".").removeKeyPrefix(configName + ".").getProperties();
        Properties merged = properties(defaultProps).merge(properties(configProps)).getProperties();
        return merged;
    }
}
