package org.chorusbdd.chorus.handlers.util.config.source;

import org.chorusbdd.chorus.core.interpreter.results.FeatureToken;
import org.chorusbdd.chorus.util.logging.ChorusLog;
import org.chorusbdd.chorus.util.logging.ChorusLogFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

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

    public Map<String, Properties> getPropertiesGroups() {
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
            //figure out where the properties file is
            String propertiesFilePath = featureDir.getAbsolutePath() + File.separatorChar + "conf" + File.separatorChar + featureFile.getName();
            propertiesFilePath = propertiesFilePath.replace(".feature", propertiesSuffix + ".properties");

            //load the properties
            File propertiesFile = new File(propertiesFilePath);
            if (propertiesFile.exists()) {
                FileInputStream fis = new FileInputStream(propertiesFilePath);
                p.load(fis);
                fis.close();
                log.debug(String.format("Loaded " + handlerDescription + " configuration properties from: %s", propertiesFilePath));
            }

            //override properties for a specific run configuration (if specified)
            if (featureToken.getConfigurationName() != null) {
                String suffix = String.format(propertiesSuffix + "-%s.properties", featureToken.getConfigurationName());
                String overridePropertiesFilePath = propertiesFilePath.replace(propertiesSuffix + ".properties", suffix);
                File overridePropertiesFile = new File(overridePropertiesFilePath);
                if (overridePropertiesFile.exists()) {
                    FileInputStream fis = new FileInputStream(overridePropertiesFile);
                    p.load(fis);
                    fis.close();
                    log.debug(String.format("Loaded overriding " + handlerDescription + " configuration properties from: %s", overridePropertiesFilePath));
                }
            }

        } catch (IOException e) {
            log.error("Failed to load " + handlerDescription + " configuration properties", e);
        }
        return p;
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
