package org.chorusbdd.chorus.handlerconfig.properties;

import org.chorusbdd.chorus.util.properties.PropertyLoader;
import org.chorusbdd.chorus.util.properties.PropertyOperations;
import org.chorusbdd.chorus.results.FeatureToken;

import java.io.File;
import java.util.Properties;

import static org.chorusbdd.chorus.handlerconfig.properties.VariableExpandingPropertyLoader.expandVariables;
import static org.chorusbdd.chorus.util.properties.PropertyOperations.properties;


/**
 * Create a PropertyLoader to load the properties required by a handler
 *
 * The supported locations of the property files required are dependent on the handler name and the current feature
 **
 * <p/>
 * The order in which properties files are loaded is important, since properties may be specified in more than one file, and values
 * loaded later override properties loaded up front. This is intentional, to enable defaults to be set in more 'general' properties
 * files and then overridden by files specific to a particular feature or feature configuration.
 * <p/>
 * The order of loading properties files is therefore important, and is as follows:
 * <p/>
 * From classpath:
 * 1. /chorus.properties
 * 2. /chorus-${handlerPropertySuffix}.properties
 * <p/>
 * Then from feature dir:
 * 3. chorus.properties
 * 4. chorus-${handlerPropertySuffix}.properties
 * 5. featureName.properties
 * 6. featureName-${handlerPropertySuffix}.properties
 * 7. featureName-featureConfig.properties
 * 8. featureName-featureConfig-${handlerPropertySuffix}.properties
 * <p/>
 * Then from conf subdirectory of feature directory:
 * 9. chorus.properties
 * 10. chorus-${handlerPropertySuffix}.properties
 * 11. featureName.properties
 * 12. featureName-${handlerPropertySuffix}.properties
 * 13. featureName-featureConfig.properties
 * 14. featureName-featureConfig-${handlerPropertySuffix}.properties
 */
public class HandlerPropertyLoaderFactory {

    /**
     * @return a PropertyLoader to load the properties for a feature in a given configuration
     */
    public PropertyOperations createPropertyLoader(FeatureToken featureToken, String handlerName) {
        PropertyOperations props = PropertyOperations.properties(new Properties());
        final String handlerPrefix = handlerName + ".";

        File featureDir = featureToken.getFeatureDir();
        File featureConfDir = new File(featureDir, "conf");

        props = props.merge(new ClassPathPropertyLoader("/chorus.properties"));
        props = props.merge(properties(new ClassPathPropertyLoader("/chorus-" + handlerName + ".properties")).prefixKeys(handlerPrefix));

        props = mergeLoadersForDirectory(props, featureDir, featureToken, featureToken.getConfigurationName(), handlerName, handlerPrefix);
        props = mergeLoadersForDirectory(props, featureConfDir, featureToken, featureToken.getConfigurationName(), handlerName, handlerPrefix);

        props = expandVariables(props, featureToken);
        props = props.filterByKeyPrefix(handlerPrefix);
        props = props.removeKeyPrefix(handlerPrefix);
        return props;
    }

    private PropertyOperations mergeLoadersForDirectory(PropertyOperations props, File dir, FeatureToken featureToken, String configurationName, String handlerName, String handlerPrefix) {
        props = props.merge(getPropertyLoader(dir, "chorus.properties"));
        props = props.merge(getPropertyPrefixingLoader(handlerPrefix, dir, "chorus-" + handlerName + ".properties"));

        String featureNameBase = featureToken.getFeatureFile().getName().replace(".feature", "");

        props = props.merge(getPropertyLoader(dir, featureNameBase + ".properties"));
        props = props.merge(getPropertyPrefixingLoader(handlerPrefix, dir, featureNameBase + "-" + handlerName + ".properties"));

        props = props.merge(getPropertyLoader(dir, featureNameBase + "-" + configurationName + ".properties"));
        props = props.merge(getPropertyPrefixingLoader(handlerPrefix, dir, featureNameBase + "-" + configurationName + "-" + handlerName + ".properties"));
        return props;
    }

    /**
     * Properties in 'handler-specific' property files will be missing the first token / handler prefix
     * add the handler prefix back in to make sure all properties have a consistent form
     * e.g. for "myfeature-remoting.properties", we need to add "remoting." as a prefix for each property key
     */
    private PropertyLoader getPropertyPrefixingLoader(String handlerPrefix, File dir, String s) {
        PropertyLoader p = getPropertyLoader(dir, s);
        return p == PropertyLoader.NULL_LOADER ? p : properties(p).prefixKeys(handlerPrefix);
    }

    private PropertyLoader getPropertyLoader(File dir, String s) {
        File propsFile = new File(dir, s);
        return propsFile.exists() && propsFile.canRead() ? new FilePropertyLoader(propsFile) : PropertyLoader.NULL_LOADER;
    }

}
