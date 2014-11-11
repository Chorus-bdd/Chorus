/**
 *  Copyright (C) 2000-2014 The Software Conservancy and Original Authors.
 *  All rights reserved.
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to
 *  deal in the Software without restriction, including without limitation the
 *  rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 *  sell copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 *  FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 *  IN THE SOFTWARE.
 *
 *  Nothing in this notice shall be deemed to grant any rights to trademarks,
 *  copyrights, patents, trade secrets or any other intellectual property of the
 *  licensor or any contributor except as expressly stated herein. No patent
 *  license is granted separate from the Software, for code that you delete from
 *  the Software, or for combinations of the Software with other software or
 *  hardware.
 */
package org.chorusbdd.chorus.handlerconfig.source;

import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;
import org.chorusbdd.chorus.results.FeatureToken;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 18/09/12
 * Time: 18:34
 *
 * Properties for the handlers used by a given feature are loaded from .properties files which are either in the
 * same directory as the .feature or in a subdirectory /conf
 *
 * Properties can also be provided in properties files on the classpath at root level, and this is often used to set defaults
 *
 * We support properties files which are handler-specific (i.e. have the handler name in the suffix, e.g. -remoting.properties)
 * as well as general properties files which may contain properties for any handler
 *
 * In handler-specific files, the form of each property key should be:
 * configName.property
 * e.g. myfirstprocess.remotingPort=1234 - the configName is myfirstprocess and the property is remotingPort
 * When the properties are parsed, we return a separate Properties instance for each configName we encounter
 *
 * In non-handler-specific property files, e.g. chorus.properties, or featurename.properties we expect to find an
 * additional prefix token which identifies the handler type:
 * handlerType.configName.property
 * e.g. processes.myfirstprocess.remotingPort=1234
 *
 * In general the user can choose whether to use the general form of the properties files (all
 * properties in one place), or to opt for the handler-specific format (shorter property keys but more files!)
 *
 * We also support setting defaults for each handler type using the special configName 'default'
 * Any default values specified will be used to initialize handler configurations, before values for specific group names are applied.
 * e.g. remoting.default.port could be used to set the default port to use for all remoting configs, unless overridden
 * for a specific group name
 *
 * The order in which properties files are loaded is important, since properties may be specified in more than one file, and values
 * loaded later override properties loaded up front. This is intentional, to enable defaults to be set in more 'general' properties
 * files and then overridden by files specific to a particular feature or feature configuration.
 *
 * The order of loading properties files is therefore important, and is as follows:
 *
 * nb. In the below, handlerPropertySuffix will be for example '-remoting' for the Remoting handler
 * or -processes for the Processes handler
 *
 * From classpath:
 * 1. /chorus.properties
 * 2. /chorus-${handlerPropertySuffix}.properties
 *
 * Then from feature dir:
 * 3. chorus.properties
 * 4. chorus-${handlerPropertySuffix}.properties
 * 5. featureName.properties
 * 6. featureName-${handlerPropertySuffix}.properties
 * 7. featureName-featureConfig.properties
 * 8. featureName-featureConfig-${handlerPropertySuffix}.properties
 *
 * Then from conf subdirectory of feature directory:
 * 9. chorus.properties
 * 10. chorus-${handlerPropertySuffix}.properties
 * 11. featureName.properties
 * 12. featureName-${handlerPropertySuffix}.properties
 * 13. featureName-featureConfig.properties
 * 14. featureName-featureConfig-${handlerPropertySuffix}.properties
 *
 */
public class PropertiesFilePropertySource implements PropertyGroupsSource {

    private static ChorusLog log = ChorusLogFactory.getLog(PropertiesFilePropertySource.class);

    private String handlerName;
    private String propertiesFileSuffix;
    private final FeatureToken featureToken;

    public PropertiesFilePropertySource(String handlerName, String propertiesFileSuffix, FeatureToken featureToken) {
        this.handlerName = handlerName;
        this.propertiesFileSuffix = propertiesFileSuffix;
        this.featureToken = featureToken;
    }

    public Map<String, Properties> mergeProperties(Map<String, Properties> propertiesByConfigName) {
        Properties p = loadFromPropertyFiles();

        for ( Map.Entry e : p.entrySet()) {
            String key = e.getKey().toString();
            String value = e.getValue().toString();

            //on loading we should have stripped any handlerToken prefix for property keys in the long
            //form handlerToken.configName.property, and should also have removed any properties where the handlerToken
            //did not match this PropertySource's handlerToken
            // .
            //We should be left with keys in the form groupName.property which are all relevant for this handler type
            int tokenCount = getTokenCount(key);
            if ( tokenCount >= 2 ) {
                //some properties files mix properties for different handlers, in this case the property names we want start with the handler prefix token
                findConfigNameAndSetProperty(propertiesByConfigName, key, value);
            } else {
                log.warn("Unrecognised property key format " + key + " will ignore this property");
            }
        }
        return propertiesByConfigName;
    }

    private void findConfigNameAndSetProperty(Map<String, Properties> propertiesByConfigName, String key, String value) {
        //find the configName which should now be the first token in key, and get or create the Properties group for it
        //the property to set in the configName propertyGroup is the second token in key - set that property to value
        int i = key.indexOf('.');
        String configName = key.substring(0, i);
        String property = key.substring(i + 1, key.length());

        Properties propertiesForThisConfigName = getOrCreateProperties(propertiesByConfigName, configName);
        propertiesForThisConfigName.setProperty(property, value);
    }

    private Properties loadFromPropertyFiles() {
        Properties p = new Properties();
        log.trace("Reading properties for handler " + handlerName +
                ", feature: " + featureToken +
                ", dir: " + featureToken.getFeatureDir() +
                ", file: " + featureToken.getFeatureFile());
        try {
            //we support setting properties in a chorus.properties at the top of the classpath
            //and a handler-specific chorus properties file for each built in handler at the top of the classpath
            //this is chiefly to provide an easy way for the user to set 'default' properties
            loadPropertiesFromClasspathResource(p, "/chorus.properties", false);
            loadPropertiesFromClasspathResource(p, "/chorus-" + propertiesFileSuffix + ".properties", true);

            List<String> propertiesFilePaths = getPropertiesFilePaths();
            loadPropertiesFromFiles(p, propertiesFilePaths);
        } catch (IOException e) {
            log.error("Failed to load " + handlerName + " configuration properties", e);
        }
        return p;
    }

    /**
     * @return a list of the properties files paths which will be loaded for this handler/feature in order of override priority
     */
    private List<String> getPropertiesFilePaths() {

        List<String> propertiesFilePaths = new ArrayList<String>();

        //look for properties files locally in feature dir
        String featureDirectory = this.featureToken.getFeatureDir().getAbsolutePath() + File.separatorChar;
        addPropertiesPathsForDirectory(propertiesFilePaths, featureDirectory);

        //look for properties files in /conf subdir of feature dir
        String confDir = featureToken.getFeatureDir().getAbsolutePath() + File.separatorChar + "conf" + File.separatorChar;
        addPropertiesPathsForDirectory(propertiesFilePaths, confDir);
        return propertiesFilePaths;
    }

    private void addPropertiesPathsForDirectory(List<String> propertiesFilePaths, String propertyDirPrefix) {
        //first look for chorus.properties
        String chorusProperties = propertyDirPrefix + "chorus.properties";
        propertiesFilePaths.add(chorusProperties);

        //then override with handler specific properties file, e.g. remoting.properties
        String handlerTypeProperties = propertyDirPrefix + "chorus-" + propertiesFileSuffix + ".properties";
        propertiesFilePaths.add(handlerTypeProperties);

        //then override with feature specific properties file, e.g myfeaturename.properties
        String featureProperties = propertyDirPrefix + featureToken.getFeatureFile().getName();
        featureProperties = featureProperties.replace(".feature", ".properties");
        propertiesFilePaths.add(featureProperties);

        //override properties for a specific feature and handler type, e.g. myfeaturename-remoting.properties
        String featureAndHandlerProperties = propertyDirPrefix + featureToken.getFeatureFile().getName();
        featureAndHandlerProperties = featureAndHandlerProperties.replace(".feature", "-" + propertiesFileSuffix + ".properties");
        propertiesFilePaths.add(featureAndHandlerProperties);

         //override properties for a specific feature run configuration (if specified), e.g. myfeaturename-myconfig.properties
        if (featureToken.isConfiguration()) {
            String suffix = String.format("-%s.properties", featureToken.getConfigurationName());
            String featureConfigOverrideProperties = featureProperties.replace(".properties", suffix);
            propertiesFilePaths.add(featureConfigOverrideProperties);
        }

        //override properties for a specific feature and handler type with run configuration (if specified), e.g. myfeaturename-remoting-myconfig.properties
        if (featureToken.isConfiguration()) {
            String suffix = String.format("-%s-" + propertiesFileSuffix + ".properties", featureToken.getConfigurationName());
            String featureConfigOverrideProperties = featureAndHandlerProperties.replace("-" + propertiesFileSuffix + ".properties", suffix);
            propertiesFilePaths.add(featureConfigOverrideProperties);
        }
    }

    private void loadPropertiesFromFiles(Properties p, List<String> paths) throws IOException {
        for ( String path : paths) {
            boolean isHandlerSpecificPropertyFile = path.endsWith("-" + propertiesFileSuffix + ".properties");
            loadPropertiesFromFile(p, new File(path), isHandlerSpecificPropertyFile);
        }
    }

    private void loadPropertiesFromFile(Properties p, File propertiesFile, boolean handlerSpecificProperties) throws IOException {
        if (propertiesFile.exists()) {
            Properties props = new Properties();
            FileInputStream fis = new FileInputStream(propertiesFile);
            props.load(fis);
            fis.close();
            props = filterProperties(handlerSpecificProperties, props);
            p.putAll(props); //override any existing propeties with the loaded ones
            log.debug(String.format("Loaded " + handlerName + " configuration properties from: %s", propertiesFile.getAbsolutePath()));
        }
    }

    private void loadPropertiesFromClasspathResource(Properties p, String path, boolean handlerSpecificProperties) throws IOException {
        Class c = getClass();
        URL u = c.getResource(path);
        if ( u != null ) {
            Properties props = new Properties();
            InputStream is = c.getResourceAsStream(path);
            props.load(is);
            is.close();
            props = filterProperties(handlerSpecificProperties, props);
            p.putAll(props);  //override any existing propeties with the loaded ones
            log.debug(String.format("Loaded " + handlerName + " configuration properties from classpath at path: %s", path));
        }
    }

    private Properties filterProperties(boolean handlerSpecificProperties, Properties props) {
        //handler-specifc property files may have properties in the long form (prefixed with handlerToken) or short
        //general property files must have the long form with the handler prefix
        filterByTokenCount(props, handlerSpecificProperties ? 2 : 3, 3);

        //now strip the first token for any property keys in long form, skipping any which don't have the right handlerToken
        props = filterByHandlerType(props);
        return props;
    }

    private Properties filterByHandlerType(Properties props) {
        Properties p = new Properties();
        Iterator<Map.Entry<Object,Object>> i = props.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry<Object,Object> e = i.next();
            String property = e.getKey().toString();
            String value = e.getValue().toString();
            int tokenCount = getTokenCount(property);
            if ( tokenCount == 3) {
                //this is a key in the form handlerToken.configName.property, we need to check the handlerToken and
                //if it does not match our handler, skip the property, or else strip the first token from the key and add it
                checkHandlerTypeAndStripHandlerPrefix(p, property, value);
            } else {
                p.put(property, value);
            }
        }
        return p;
    }

    private void checkHandlerTypeAndStripHandlerPrefix(Properties p, String property, String value) {
        if ( ! property.startsWith(propertiesFileSuffix)) {
            log.debug("Ingoring property " + property + " which does not have this handler's prefix " + propertiesFileSuffix);
        } else {
            int firstPeriod = property.indexOf('.');
            property = property.substring(firstPeriod + 1);
            p.put(property, value);
        }
    }

    //remove all properties for which the number of tokens in the key does not match the expected number of tokens
    //we expect two tokens configName.property in a handler-specific config file (e.g. in a -remoting.properties file)
    //or three tokens, in which the handler type is the first token, e.g. 'remoting.' in a non-handler-specific file,
    //eg. remoting.configName.properties in a chorus.properties file
    private void filterByTokenCount(Properties props, int minTokenCount, int maxTokenCount) {
        Iterator<Map.Entry<Object,Object>> i = props.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry<Object,Object> e = i.next();
            String property = e.getKey().toString();
            int tokenCount = getTokenCount(property);
            if ( tokenCount < minTokenCount || tokenCount > maxTokenCount) {
                log.warn("Ignoring property " + property + " which is not in the expected form");
                i.remove();
            }
        }
    }

    private Properties getOrCreateProperties(Map<String, Properties> propertiesByConfigName, String configName) {
        Properties g = propertiesByConfigName.get(configName);
        if ( g == null ) {
            g = new Properties();
            //always set the config name as a property
            g.setProperty("configName", configName);
            propertiesByConfigName.put(configName, g);
        }
        return g;
    }

    private int getTokenCount(String key) {
        int count = 1;
        for (char c : key.toCharArray()) {
            if ( c == '.') {
                count++;
            }
        }
        return count;
    }

}
