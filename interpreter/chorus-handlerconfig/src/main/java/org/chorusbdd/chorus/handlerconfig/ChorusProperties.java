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

import org.chorusbdd.chorus.annotations.ExecutionPriority;
import org.chorusbdd.chorus.executionlistener.ExecutionListener;
import org.chorusbdd.chorus.executionlistener.ExecutionListenerAdapter;
import org.chorusbdd.chorus.handlerconfig.properties.ClassPathPropertyLoader;
import org.chorusbdd.chorus.handlerconfig.properties.FilePropertyLoader;
import org.chorusbdd.chorus.handlerconfig.properties.JdbcPropertyLoader;
import org.chorusbdd.chorus.handlerconfig.properties.VariableExpandingPropertyLoader;
import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;
import org.chorusbdd.chorus.results.ExecutionToken;
import org.chorusbdd.chorus.results.FeatureToken;
import org.chorusbdd.chorus.util.ChorusConstants;
import org.chorusbdd.chorus.util.properties.PropertyLoader;
import org.chorusbdd.chorus.util.properties.PropertyOperations;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.chorusbdd.chorus.handlerconfig.properties.VariableExpandingPropertyLoader.expandVariables;
import static org.chorusbdd.chorus.util.properties.PropertyOperations.properties;

/**
 * Created by Nick E on 03/02/2015.
 *
 * The default ConfigurationManager for chorus
 */
public class ChorusProperties implements ConfigurationManager {

    private ChorusLog log = ChorusLogFactory.getLog(ChorusProperties.class);

    private Properties sessionProperties = new Properties();
    private Properties featureProperties = new Properties();

    private FeatureToken currentFeature;
    private String currentProfile;

    @Override
    public Properties getAllProperties() {
        PropertyOperations sessionProps = getPropertiesForScope(sessionProperties);
        PropertyOperations featureProps = getPropertiesForScope(featureProperties);

        PropertyOperations sessionAndFeature = sessionProps.merge(featureProps);
        return expandVariables(sessionAndFeature, currentFeature).loadProperties();
    }

    @Override
    public Properties getFeatureProperties() {
        return expandVariables(getPropertiesForScope(featureProperties), currentFeature).loadProperties();
    }

    @Override
    public Properties getSessionProperties() {
        return expandVariables(getPropertiesForScope(sessionProperties), currentFeature).loadProperties();
    }


    private PropertyOperations getPropertiesForScope(Properties props) {
        PropertyOperations scopedProps = properties(props);
        scopedProps = mergeConfigurationAndProfileProperties(scopedProps);
        return scopedProps;
    }

    /**
     * Some property keys may be prefixed with the name of a configuration or the name of a profile
     *
     * If this matches the current configration/profile we strip this prefix and merge the new property over the top of any default ones
     * This enables us to declare properties which are only active in a certain profile or configuration
     *
     * We do configurations first, so if we take it to the extreme we could have the below for a processes.myProcess.remotingPort property
     *
     * configurations.configA.profiles.profile1.processes.myProcess.remotingPort = 12345
     *
     * The above property would be only set while running feature configuration A in profile 1
     */
    private PropertyOperations mergeConfigurationAndProfileProperties(PropertyOperations props) {
        PropertyOperations result;
        result = mergeConfigurationProperties(props);
        result = mergeProfileProperties(result);
        return result;
    }

    private PropertyOperations mergeConfigurationProperties(PropertyOperations sessionProps) {
        PropertyOperations sessionPropsForProfile = sessionProps.filterByAndRemoveKeyPrefix("configurations." + currentFeature.getConfigurationName() + ".");
        return sessionProps.merge(sessionPropsForProfile).filterKeysNotStartingWith("configurations.");
    }

    private PropertyOperations mergeProfileProperties(PropertyOperations sessionProps) {
        PropertyOperations sessionPropsForProfile = sessionProps.filterByAndRemoveKeyPrefix("profiles." + currentProfile + ".");
        return sessionProps.merge(sessionPropsForProfile).filterKeysNotStartingWith("profiles.");
    }

    @Override
    public void addSessionProperties(Properties properties) {
        sessionProperties = properties(sessionProperties).merge(properties(properties)).getProperties();
    }

    @Override
    public void addFeatureProperties(Properties properties) {
        featureProperties = properties(featureProperties).merge(properties(properties)).getProperties();
    }

    @Override
    public void clearFeatureProperties() {
        featureProperties = new Properties();
    }

    @Override
    public void clearSessionProperties() {
        sessionProperties = new Properties();
    }


    private void loadFeatureProperties(FeatureToken feature) {
        PropertyOperations featureProps = PropertyOperations.emptyProperties();
        featureProps = mergeLoadersForDirectory(featureProps, feature.getFeatureDir(), feature);
        featureProps = mergeLoadersForDirectory(featureProps, new File(feature.getFeatureDir(), "conf"), feature);
        featureProps = addPropertiesFromDatabase(properties(featureProps.loadProperties())); //load the feature props once then merge any db props
        this.featureProperties = featureProps.loadProperties();
    }

    private void loadSessionProperties() {
        PropertyOperations sessionProps = PropertyOperations.emptyProperties();
        sessionProps = sessionProps.merge(new ClassPathPropertyLoader("/chorus.properties"));
        PropertyOperations withDbProps = addPropertiesFromDatabase(sessionProps);  //load the session props once then merge any db props
        this.sessionProperties = withDbProps.loadProperties();
    }

    /**
     * If there are any database properties defined in sourceProperties then use them to merge extra properties from the databsase
     * @param sourceProperties
     * @return
     */
    private PropertyOperations addPropertiesFromDatabase(PropertyOperations sourceProperties) {
        PropertyOperations dbPropsOnly = sourceProperties.filterByKeyPrefix(ChorusConstants.DATABASE_CONFIGS_PROPERTY_GROUP + ".")
                                                         .removeKeyPrefix(ChorusConstants.DATABASE_CONFIGS_PROPERTY_GROUP + ".");
        dbPropsOnly = VariableExpandingPropertyLoader.expandVariables(dbPropsOnly, currentFeature);
        Map<String, Properties> dbPropsByDbName = dbPropsOnly.splitKeyAndGroup("\\.").loadPropertyGroups();
        PropertyOperations o = sourceProperties;
        for ( Map.Entry<String, Properties> m : dbPropsByDbName.entrySet()) {
            log.debug("Creating loader for database properties " + m.getKey());

            //current properties which may be from properties files or classpath take precedence over db properties so merge them on top
            o = properties(new JdbcPropertyLoader(m.getValue())).merge(o);
        }
        return o;
    }

    @Override
    public ExecutionListener getExecutionListener() {
        return new PropertySubsystemExecutionListener();
    }

    /**
     * Load and remove properties at the appropriate points in the chorus lifecyle
     */
    @ExecutionPriority(ExecutionPriority.PROPERTY_SUBSYSTEM_PRIORITY)
    private class PropertySubsystemExecutionListener extends ExecutionListenerAdapter {
        public void testsStarted(ExecutionToken testExecutionToken, List<FeatureToken> features) {
            currentProfile = testExecutionToken.getProfile();
            loadSessionProperties();
        }

        @Override
        public void featureStarted(ExecutionToken testExecutionToken, FeatureToken feature) {
            currentFeature = feature;
            loadFeatureProperties(feature);
        }

        @Override
        public void featureCompleted(ExecutionToken testExecutionToken, FeatureToken feature) {
            clearFeatureProperties();
        }
    }

    private PropertyOperations mergeLoadersForDirectory(PropertyOperations props, File dir, FeatureToken featureToken) {
        props = props.merge(getPropertyLoader(dir, "chorus.properties"));
        String featureNameBase = featureToken.getFeatureFile().getName().replace(".feature", "");
        props = props.merge(getPropertyLoader(dir, featureNameBase + ".properties"));
        props = props.merge(getPropertyLoader(dir, featureNameBase + "-" + featureToken.getConfigurationName() + ".properties"));
        return props;
    }

    private PropertyLoader getPropertyLoader(File dir, String s) {
        File propsFile = new File(dir, s);
        return propsFile.exists() && propsFile.canRead() ? new FilePropertyLoader(propsFile) : PropertyLoader.NULL_LOADER;
    }
}
