package org.chorusbdd.chorus.handlerconfig;

import org.chorusbdd.chorus.subsystem.Subsystem;
import org.chorusbdd.chorus.util.properties.PropertyOperations;

import java.util.Properties;

/**
 * Created by GA2EBBU on 03/02/2015.
 */
public interface ConfigurationManager extends Subsystem {

   /**
    * Returns all properties defined, with system properties and other special Chorus properties expanded
    *
    * To derive this, the feature properties are merged with the session properties.
    * Feature level properties take precedence and may override session level properties
    *
    * @return All properties defined, the feature properties merged with the session properties.
    */
    PropertyOperations getAllProperties();

    /**
     * @return Feature-specific properties
     */
    PropertyOperations getFeatureProperties();

    /**
     * @return Properties which are maintained for the whole interpreter session
     */
    PropertyOperations getSessionProperties();

    void addSessionProperties(Properties properties);

    void addFeatureProperties(Properties properties);

    void clearFeatureProperties();

    void clearSessionProperties();
}
