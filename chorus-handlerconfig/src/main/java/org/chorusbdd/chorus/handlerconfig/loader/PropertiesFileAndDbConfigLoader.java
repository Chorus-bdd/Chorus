package org.chorusbdd.chorus.handlerconfig.loader;

import org.chorusbdd.chorus.handlerconfig.HandlerConfig;
import org.chorusbdd.chorus.handlerconfig.HandlerConfigFactory;
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
 * Load properties, from properties files on the filesystem and/or from the database
 *
 * if special dbconfigs properties are provided for the handler, then a jdbc connection will be established to
 * load properties from the database
 *
 * For a given config name, properties specified in property files override any loaded from the database
 *
 * To specify database connection properties, provide these properties prefixed with the correct handler name:
 *
 * handlerName.dbconfigs.jdbc_driver=
 * handlerName.dbconfigs.jdbc_url=
 * handlerName.dbconfigs.jdbc_user=
 * handlerName.dbconfigs.jdbc_password=
 * handlerName.dbconfigs.jdbc_sql=
 *
 * The sql property must specify a query which returns a result set having the following columns:
 *
 * configName - the config name a property applies to
 * property   - the property key to set
 * value      - the property value to set
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

        Map<String, Properties> propertiesByConfigName = getPropertiesFromFilesystem(new HashMap<String, Properties>());

        if ( propertiesByConfigName.containsKey(HandlerConfig.DATABASE_CONFIGS_PROPERTY_GROUP)) {
            //remove the special database properties if they exist, these define a connection to the database to load more configs
            //they don't contain the standard configuration properties for this handler
            Properties dbConnectionProps = propertiesByConfigName.remove(HandlerConfig.DATABASE_CONFIGS_PROPERTY_GROUP);
            propertiesByConfigName = mergePropertiesFromDatabase(dbConnectionProps, propertiesByConfigName);
        }

        Map<String, E> configs = transformtoHandlerConfigs(propertiesByConfigName);
        return configs;
    }

    private Map<String, Properties> mergePropertiesFromDatabase(Properties dbConnectionProperties, Map<String, Properties> propertiesByConfigName) {
        return new VariableReplacingPropertySource(
            new JdbcPropertySource(dbConnectionProperties),
            featureToken
        ).mergeProperties(propertiesByConfigName);
    }

    private Map<String, Properties> getPropertiesFromFilesystem(Map<String, Properties> propertiesByConfigName) {
        return new VariableReplacingPropertySource(
            new PropertiesFilePropertySource(handlerName, propertiesFileSuffix, featureToken),
            featureToken
        ).mergeProperties(propertiesByConfigName);
    }

    private Map<String, E> transformtoHandlerConfigs(Map<String, Properties> filesystemProperties) {
        DefaultConfigLoader configLoader = new DefaultConfigLoader(handlerName, configFactory, filesystemProperties);
        return configLoader.loadConfigs();
    }

}
