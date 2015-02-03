package org.chorusbdd.chorus.handlerconfig;

import org.chorusbdd.chorus.handlerconfig.properties.HandlerPropertyLoaderFactory;
import org.chorusbdd.chorus.results.FeatureToken;
import org.chorusbdd.chorus.util.properties.PropertyLoader;
import org.chorusbdd.chorus.util.properties.PropertyOperations;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by nick on 13/01/15.
 *
 * Load properties required for a handler from all the possible property file paths and locations
 *
 * For more complex cases where you want to convert properties to config beans see HandlerConfigBeanLoader
 */
public class HandlerPropertyLoader implements PropertyLoader {

    private final String handlerName;
    private final FeatureToken featureToken;

    public HandlerPropertyLoader(
            String handlerName,
            FeatureToken featureToken) {
        this.handlerName = handlerName;
        this.featureToken = featureToken;
    }

    public Properties loadProperties() {

        //load Properties from file paths based on the current feature and handler name
        PropertyOperations propertyLoader = new HandlerPropertyLoaderFactory().createPropertyLoader(featureToken, handlerName);

        //If a db properties are included, load and merge extra properties from the db
        //the locally defined properties take precedence
        propertyLoader = new DbPropertiesMerge(handlerName).mergeWithDatabaseProperties(propertyLoader);


        return propertyLoader.loadProperties();
    }

}
