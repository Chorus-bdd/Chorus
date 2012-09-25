/**
 *  Copyright (C) 2000-2012 The Software Conservancy and Original Authors.
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
package org.chorusbdd.chorus.handlers.util.config.source;

import org.chorusbdd.chorus.core.interpreter.results.FeatureToken;
import org.chorusbdd.chorus.util.logging.ChorusLog;
import org.chorusbdd.chorus.util.logging.ChorusLogFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 18/09/12
 * Time: 18:34
 */
public class PropertiesFilePropertySource implements PropertyGroupsSource {

    private static ChorusLog log = ChorusLogFactory.getLog(PropertiesFilePropertySource.class);

    private String handlerDescription;
    private String propertiesSuffix;
    private final FeatureToken featureToken;
    private final File featureDir;
    private final File featureFile;

    public PropertiesFilePropertySource(String handlerDescription, String propertiesSuffix, FeatureToken featureToken, File featureDir, File featureFile) {
        this.handlerDescription = handlerDescription;
        this.propertiesSuffix = propertiesSuffix;
        this.featureToken = featureToken;
        this.featureDir = featureDir;
        this.featureFile = featureFile;
    }

    public Map<String, Properties> getPropertyGroups() {
        Properties p = loadProperties();

        Map<String, Properties> propertiesByGroup = new HashMap<String, Properties>();
        for ( Map.Entry e : p.entrySet()) {
            String key = e.getKey().toString();
            int i = key.indexOf('.');
            if ( i == -1 || i == key.length() - 1) {
                log.warn("Skipping property " + key + " since it does not have a groupName prefix (property keys should be in the form groupName.property)");
                continue;
            }

            String group = key.substring(0, i);
            String property = key.substring(i + 1);
            String value = e.getValue().toString();

            Properties g = getOrCreateProperties(propertiesByGroup, group);
            g.setProperty(property, value);
        }
        return propertiesByGroup;
    }

    private Properties loadProperties() {
        Properties p = new Properties();
        log.trace("Reading properties for handler " + handlerDescription + ", feature: " + featureToken + ", dir: " + featureDir + ", file: " + featureFile);
        try {
            List<String> propertiesFilePaths = getPropertiesFilePaths();

            loadPropertiesFromFiles(p, propertiesFilePaths);

        } catch (IOException e) {
            log.error("Failed to load " + handlerDescription + " configuration properties", e);
        }
        return p;
    }

    /**
     * @return a list of the properties files paths which will be loaded for this handler/feature in order of override priority
     */
    private List<String> getPropertiesFilePaths() {

        List<String> propertiesFilePaths = new ArrayList<String>();

        //look for properties files locally in feature dir
        addPropertiesPathsForDirectory(propertiesFilePaths, featureDir.getAbsolutePath());

        //look for properties files in /conf subdir of feature dir
        String propertyDirPrefix = featureDir.getAbsolutePath() + File.separatorChar + "conf" + File.separatorChar;
        addPropertiesPathsForDirectory(propertiesFilePaths, propertyDirPrefix);
        return propertiesFilePaths;
    }

    private void addPropertiesPathsForDirectory(List<String> propertiesFilePaths, String propertyDirPrefix) {
        //first look for chorus.properties
        String chorusProperties = propertyDirPrefix + "chorus.properties";
        propertiesFilePaths.add(chorusProperties);

        //then override with handler specific properties file, e.g. remoting.properties
        String handlerTypeProperties = propertyDirPrefix + propertiesSuffix + ".properties";
        propertiesFilePaths.add(handlerTypeProperties);

        //then override with feature specific properties file, e.g myfeaturename.properties
        String featureProperties = featureDir.getAbsolutePath() + File.separatorChar + "conf" + File.separatorChar + featureFile.getName();
        featureProperties = featureProperties.replace(".feature", ".properties");
        propertiesFilePaths.add(featureProperties);

         //override properties for a specific feature run configuration (if specified), e.g. myfeaturename-myconfig.properties
        if (featureToken.isConfiguration()) {
            String suffix = String.format("-%s.properties", featureToken.getConfigurationName());
            String featureConfigOverrideProperties = featureProperties.replace(".properties", suffix);
            propertiesFilePaths.add(featureConfigOverrideProperties);
        }

        //override properties for a specific feature and handler type, e.g. myfeaturename-remoting.properties
        String featureAndHandlerProperties = featureDir.getAbsolutePath() + File.separatorChar + "conf" + File.separatorChar + featureFile.getName();
        featureAndHandlerProperties = featureAndHandlerProperties.replace(".feature", "-" + propertiesSuffix + ".properties");
        propertiesFilePaths.add(featureAndHandlerProperties);

        //override properties for a specific feature and handler type with run configuration (if specified), e.g. myfeaturename-remoting-myconfig.properties
        if (featureToken.isConfiguration()) {
            String suffix = String.format("-" + propertiesSuffix + "-%s.properties", featureToken.getConfigurationName());
            String featureConfigOverrideProperties = featureAndHandlerProperties.replace("-" + propertiesSuffix + ".properties", suffix);
            propertiesFilePaths.add(featureConfigOverrideProperties);
        }
    }

    private void loadPropertiesFromFiles(Properties p, List<String> paths) throws IOException {
        for ( String path : paths) {
            loadPropertiesFromFile(p, new File(path));
        }
    }

    private void loadPropertiesFromFile(Properties p, File overridePropertiesFile) throws IOException {
        if (overridePropertiesFile.exists()) {
            FileInputStream fis = new FileInputStream(overridePropertiesFile);
            p.load(fis);
            fis.close();
            log.debug(String.format("Loaded " + handlerDescription + " configuration properties from: %s", overridePropertiesFile.getAbsolutePath()));
        }
    }

    private Properties getOrCreateProperties(Map<String, Properties> propertiesByGroup, String groupName) {
        Properties g = propertiesByGroup.get(groupName);
        if ( g == null ) {
            g = new Properties();
            g.setProperty("name", groupName);
            propertiesByGroup.put(groupName, g);
        }
        return g;
    }
}
