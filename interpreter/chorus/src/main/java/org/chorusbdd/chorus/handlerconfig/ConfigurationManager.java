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

import org.chorusbdd.chorus.annotations.SubsystemConfig;
import org.chorusbdd.chorus.subsystem.Subsystem;

import java.util.Properties;

/**
 * Created by Nick E on 03/02/2015.
 */
@SubsystemConfig(
    id = "configurationManager",
    implementationClass = "org.chorusbdd.chorus.handlerconfig.ChorusProperties",
    overrideImplementationClassSystemProperty = "chorusConfigurationManager")
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
