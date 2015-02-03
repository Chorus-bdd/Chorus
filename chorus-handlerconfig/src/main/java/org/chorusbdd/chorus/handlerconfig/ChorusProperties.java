package org.chorusbdd.chorus.handlerconfig;

import org.chorusbdd.chorus.executionlistener.ExecutionListener;
import org.chorusbdd.chorus.executionlistener.ExecutionListenerAdapter;
import org.chorusbdd.chorus.handlerconfig.properties.ClassPathPropertyLoader;
import org.chorusbdd.chorus.handlerconfig.properties.FilePropertyLoader;
import org.chorusbdd.chorus.results.ExecutionToken;
import org.chorusbdd.chorus.results.FeatureToken;
import org.chorusbdd.chorus.util.properties.PropertyLoader;
import org.chorusbdd.chorus.util.properties.PropertyOperations;

import java.io.File;
import java.util.Properties;

import static org.chorusbdd.chorus.handlerconfig.properties.VariableExpandingPropertyLoader.expandVariables;
import static org.chorusbdd.chorus.util.properties.PropertyOperations.properties;

/**
 * Created by GA2EBBU on 03/02/2015.
 *
 * A singleton which manages all chorus properties
 */
public class ChorusProperties implements PropertiesSubsystem {

    private static final ChorusProperties chorusProperties = new ChorusProperties();

    private Properties sessionProperties = new Properties();
    private Properties featureProperties = new Properties();

    private FeatureToken currentFeature;

    private ChorusProperties() {
    }

    public static ChorusProperties get() {
        return chorusProperties;
    }

    @Override
    public PropertyOperations getAllProperties() {
        return expandVariables(
            properties(sessionProperties).merge(properties(featureProperties)), currentFeature
        );
    }

    @Override
    public PropertyOperations getFeatureProperties() {
        return properties(featureProperties);
    }

    @Override
    public PropertyOperations getSessionProperties() {
        return properties(sessionProperties);
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
        this.featureProperties = featureProps.loadProperties();
    }

    private void loadSessionProperties() {
        PropertyOperations sessionProps = PropertyOperations.emptyProperties();
        sessionProps.merge(new ClassPathPropertyLoader("/chorus.properties"));
        this.sessionProperties = sessionProps.loadProperties();
    }

    @Override
    public ExecutionListener getExecutionListener() {
        return new PropertySubsystemExecutionListener();
    }

    /**
     * Load and remove properties at the appropriate points in the chorus lifecyle
     */
    private class PropertySubsystemExecutionListener extends ExecutionListenerAdapter {
        public void testsStarted(ExecutionToken testExecutionToken) {
            loadSessionProperties();
        }

        public void featureStarted(ExecutionToken testExecutionToken, FeatureToken feature) {
            currentFeature = feature;
            loadFeatureProperties(feature);
        }

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
