package org.chorusbdd.chorus.handlerconfig;

import org.chorusbdd.chorus.subsystem.Subsystem;

import java.util.Properties;

/**
 * Created by GA2EBBU on 03/02/2015.
 */
public interface ConfigurationManager extends Subsystem {

   /**
    * Returns all properties, with system properties and other special Chorus properties expanded
    *
    * To derive all properties, the feature properties are merged with the session properties.
    * Feature level properties take precedence and may override session level properties
    *
    * @return All properties defined, the feature properties merged over the session properties.
    */
    Properties getAllProperties();

    /**
     * @return Feature-specific properties which are cleared down after each feature.
     * These override any similar session properties
     * System properties in values and other special Chorus properties are expanded
     */
    Properties getFeatureProperties();

    /**
     * @return Properties which are maintained for the entire interpreter session
     * System properties in values and other special Chorus properties expanded
     */
    Properties getSessionProperties();

    /**
     * Add properties which will be retained for the entire interpreter session
     */
    void addSessionProperties(Properties properties);

    /**
     * Add properties which will be cleared down at the end of the feature
     * These may override any session properties or existing properties set at feature level
     */
    void addFeatureProperties(Properties properties);

    /**
     * Clear all properties at feature level
     */
    void clearFeatureProperties();

    /**
     * Clear all properties at session level
     */
    void clearSessionProperties();
}
