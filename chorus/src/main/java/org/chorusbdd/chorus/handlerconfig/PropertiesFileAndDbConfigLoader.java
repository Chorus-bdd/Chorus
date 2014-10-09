package org.chorusbdd.chorus.handlerconfig;

import org.chorusbdd.chorus.handlerconfig.loader.ConfigLoader;
import org.chorusbdd.chorus.handlerconfig.loader.DefaultConfigLoader;
import org.chorusbdd.chorus.handlerconfig.source.JdbcPropertySource;
import org.chorusbdd.chorus.handlerconfig.source.PropertiesFilePropertySource;
import org.chorusbdd.chorus.handlerconfig.source.VariableReplacingPropertySource;
import org.chorusbdd.chorus.results.FeatureToken;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by nick on 03/10/2014.
 *
 * Load properties, from the filesystem and from the database if special
 * dbconfigs properties are provided for this handler
 *
 * Properties specified on the filesystem override db properties
 */
public class PropertiesFileAndDbConfigLoader<E extends HandlerConfig> implements ConfigLoader<E> {

    private HandlerConfigFactory<E> configFactory;
    private String handlerName;
    private String propertiesFileSuffix;
    private FeatureToken featureToken;

    public PropertiesFileAndDbConfigLoader(
            HandlerConfigFactory<E> configFactory,
            String handlerName,
            String propertiesFileSuffix,
            FeatureToken featureToken) {
        this.configFactory = configFactory;
        this.handlerName = handlerName;
        this.propertiesFileSuffix = propertiesFileSuffix;
        this.featureToken = featureToken;
    }

    public Map<String, E> loadConfigs() {

        Map<String, Properties> filesystemProperties = getPropertiesFromFilesystem();

        //remove the special database properties if they exist, these define a connection to the database to load more configs
        //they don't contain the standard configuration properties for this handler
        Properties dbProperties = filesystemProperties.remove(HandlerConfig.DATABASE_CONFIGS_PROPERTY_GROUP);

        Map<String, E> filesystemConfigs = loadConfigsFromProperties(filesystemProperties);
        Map<String, E> dbConfigs = getConfigsFromDatabase(dbProperties);

        HashMap<String, E> combinedConfigs = combineConfigs(filesystemConfigs, dbConfigs);
        return combinedConfigs;
    }

    private HashMap<String, E> combineConfigs(Map<String, E> filesystemConfigs, Map<String, E> dbConfigs) {
        //the properties file properties may override those from the db
        HashMap<String, E> result = new HashMap<String, E>();
        result.putAll(dbConfigs);
        result.putAll(filesystemConfigs);
        return result;
    }

    private Map<String, E> getConfigsFromDatabase(Properties dbProperties) {
        Map<String,E> dbConfigs = new HashMap<String,E>();
        if ( dbProperties != null) {
            Map<String, Properties> dbLoadedProperties = getPropertiesFromDatabase(dbProperties);
            dbConfigs = loadConfigsFromProperties(dbLoadedProperties);
        }
        return dbConfigs;
    }

    private Map<String, Properties> getPropertiesFromDatabase(Properties dbProperties) {
        return new VariableReplacingPropertySource(
            new JdbcPropertySource(dbProperties),
            featureToken
        ).loadProperties();
    }

    private Map<String, Properties> getPropertiesFromFilesystem() {
        return new VariableReplacingPropertySource(
            new PropertiesFilePropertySource(handlerName, propertiesFileSuffix, featureToken),
            featureToken
        ).loadProperties();
    }

    private Map<String, E> loadConfigsFromProperties(Map<String, Properties> filesystemProperties) {
        DefaultConfigLoader configLoader = new DefaultConfigLoader(handlerName, configFactory, filesystemProperties);
        return configLoader.loadConfigs();
    }

}
