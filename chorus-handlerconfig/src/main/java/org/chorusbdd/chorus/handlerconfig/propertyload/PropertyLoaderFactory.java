package org.chorusbdd.chorus.handlerconfig.propertyload;

import org.chorusbdd.chorus.results.FeatureToken;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import static org.chorusbdd.chorus.handlerconfig.propertyload.FilteringPropertyLoader.filterByKeyPrefix;
import static org.chorusbdd.chorus.handlerconfig.propertyload.KeyMappingPropertyLoader.prefixKeys;
import static org.chorusbdd.chorus.handlerconfig.propertyload.VariableExpandingPropertyLoader.expandVariables;


/**
 * Create a PropertyLoader to load the properties required by a handler
 *
 * The locations of the property files required are dependent on the handler name and the current feature
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
public class PropertyLoaderFactory {


    /**
     * @return a PropertyLoader to load the properties for a feature in a given configuration
     */
    public PropertyLoader createPropertyLoader(FeatureToken featureToken, String handlerName) {
        CompositePropertyLoader l = new CompositePropertyLoader();
        String handlerPrefix = handlerName + ".";

        File featureDir = featureToken.getFeatureDir();
        File featureConfDir = new File(featureDir, "conf");

        l.add(new ClassPathPropertyLoader(PropertyLoaderFactory.class, "/chorus.properties"));
        l.add(prefixKeys(handlerPrefix, new ClassPathPropertyLoader(PropertyLoaderFactory.class, "/chorus-" + handlerName + ".properties")));

        l.addAll(getLoadersForDirectory(featureDir, featureToken, featureToken.getConfigurationName(), handlerName, handlerPrefix));
        l.addAll(getLoadersForDirectory(featureConfDir, featureToken, featureToken.getConfigurationName(), handlerName, handlerPrefix));

        PropertyLoader result;
        result = expandVariables(l, featureToken);
        result = filterByKeyPrefix(handlerPrefix, result);
        return result;
    }

    private List<PropertyLoader> getLoadersForDirectory(File dir, FeatureToken featureToken, String configurationName, String handlerName, String handlerPrefix) {
        List<PropertyLoader> allLoaders = new LinkedList<>();
        allLoaders.add(getPropertyLoader(dir, "chorus.properties"));
        allLoaders.add(getPropertyPrefixingLoader(handlerPrefix, dir, "chorus-" + handlerName + ".properties"));

        String featureName = featureToken.getName();

        allLoaders.add(getPropertyLoader(dir, featureName + ".properties"));
        allLoaders.add(getPropertyPrefixingLoader(handlerPrefix, dir, featureName + "." + handlerName + ".properties"));

        allLoaders.add(getPropertyLoader(dir, featureName + "-" + configurationName + ".properties"));
        allLoaders.add(getPropertyPrefixingLoader(handlerPrefix, dir, featureName + "-" + configurationName + "." + handlerName + ".properties"));
        return allLoaders;
    }

    /**
     * Properties in 'handler-specific' property files will be missing the first token / handler prefix
     * add the handler prefix back in to make sure all properties have a consistent form
     * e.g. for "myfeature-remoting.properties", we need to add "remoting." as a prefix for each property key
     */
    private PropertyLoader getPropertyPrefixingLoader(String handlerPrefix, File dir, String s) {
        PropertyLoader p = getPropertyLoader(dir, s);
        return p == PropertyLoader.NULL_LOADER ? p : prefixKeys(handlerPrefix, p);
    }

    private PropertyLoader getPropertyLoader(File dir, String s) {
        File p = new File(dir, s);
        return p.exists() && p.canRead() ? new FilePropertyLoader(p) : PropertyLoader.NULL_LOADER;
    }

}
