package org.chorusbdd.chorus.handlerconfig.propertyload;

import org.chorusbdd.chorus.results.FeatureToken;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Create the standard property loaders required for a feature
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
public class FeaturePropertyLoaderFactory {

    /**
     * @return a List of precedence-sorted PropertyLoader with the highest precedence loaders last
     */
    public List<PropertyLoader> createPropertyLoaders(FeatureToken featureToken, String configurationName, String handlerSuffix) {
        List<PropertyLoader> results = new LinkedList<>();
        String handlerPrefix = handlerSuffix + ".";

        File featureDir = featureToken.getFeatureDir();
        File featureConfDir = new File(featureDir, "conf");

        results.add(new ClassPathPropertyLoader(FeaturePropertyLoaderFactory.class, "/chorus.properties"));
        results.add(new PrefixingPropertyLoader(new ClassPathPropertyLoader(FeaturePropertyLoaderFactory.class, "/chorus-" + handlerSuffix + ".properties"), handlerPrefix));

        results.addAll(addPropertiesForConfigDir(featureDir, featureToken, configurationName, handlerSuffix, handlerPrefix));
        results.addAll(addPropertiesForConfigDir(featureConfDir, featureToken, configurationName, handlerSuffix, handlerPrefix));

        results= stripNullLoader(results);
        results = replaceVariables(results, featureToken);
        return results;
    }

    //decorate with a loader which replaces system property and special chorus variables
    private List<PropertyLoader> replaceVariables(List<PropertyLoader> loaders, FeatureToken featureToken) {
        List<PropertyLoader> results = new LinkedList<>();
        Iterator<PropertyLoader> i = loaders.iterator();
        while (i.hasNext()) {
            results.add(new VariableReplacingPropertyLoader(i.next(), featureToken));
        }
        return results;
    }

    private List<PropertyLoader> stripNullLoader(List<PropertyLoader> results) {
        Iterator<PropertyLoader> i = results.iterator();
        while (i.hasNext()) {
            if (i.next() == PropertyLoader.NULL_LOADER) {
                i.remove();
            }
        }
        return results;
    }

    private List<PropertyLoader> addPropertiesForConfigDir(File dir, FeatureToken featureToken, String configurationName, String handlerSuffix, String handlerPrefix) {
        List<PropertyLoader> allLoaders = new LinkedList<>();
        allLoaders.add(getPropertyFileLoader(dir, "chorus.properties"));
        allLoaders.add(getHandlerPropertyFileLoader(dir, "chorus-" + handlerSuffix + ".properties", handlerPrefix));

        String featureName = featureToken.getName();

        allLoaders.add(getPropertyFileLoader(dir, featureName + ".properties"));
        allLoaders.add(getHandlerPropertyFileLoader(dir, featureName + "." + handlerSuffix + ".properties", handlerPrefix));

        allLoaders.add(getPropertyFileLoader(dir, featureName + "-" + configurationName + ".properties"));
        allLoaders.add(getHandlerPropertyFileLoader(dir, featureName + "-" + configurationName + "." + handlerSuffix + ".properties", handlerPrefix));
        return allLoaders;
    }

    private PropertyLoader getHandlerPropertyFileLoader(File dir, String s, String handlerPrefix) {
        PropertyLoader p = getPropertyFileLoader(dir, s);
        return p == PropertyLoader.NULL_LOADER ? p : new PrefixingPropertyLoader(p, handlerPrefix);
    }

    private PropertyLoader getPropertyFileLoader(File dir, String s) {
        File p = new File(dir, s);
        return p.exists() && p.canRead() ? new FilePropertyLoader(p) : PropertyLoader.NULL_LOADER;
    }

}
