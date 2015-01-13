package org.chorusbdd.chorus.handlerconfig.loader;

import org.chorusbdd.chorus.handlerconfig.HandlerConfig;
import org.chorusbdd.chorus.handlerconfig.HandlerConfigFactory;
import org.chorusbdd.chorus.handlerconfig.propertyload.JdbcPropertyLoader;
import org.chorusbdd.chorus.handlerconfig.propertyload.PropertyLoaderFactory;
import org.chorusbdd.chorus.handlerconfig.propertyload.operations.PropertyOperations;
import org.chorusbdd.chorus.results.FeatureToken;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static org.chorusbdd.chorus.handlerconfig.propertyload.operations.PropertyOperations.properties;

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
public class PropertiesFileAndDbConfigLoader<E extends HandlerConfig> implements GroupedConfigLoader<E> {

    private HandlerConfigFactory<E> configFactory;
    private String handlerName;
    private FeatureToken featureToken;

    public PropertiesFileAndDbConfigLoader(
            HandlerConfigFactory<E> configFactory,
            String handlerName,
            FeatureToken featureToken) {
        this.configFactory = configFactory;
        this.handlerName = handlerName;
        this.featureToken = featureToken;
    }

    public Map<String, E> loadConfigs() throws IOException {

        //load these up front to avoid loading twice
        PropertyOperations propertyLoader = new PropertyLoaderFactory().createPropertyLoader(featureToken, handlerName);
        Map<String, Properties> groupedProperties = propertyLoader.stripAndGroupByFirstKeyToken("\\.").loadProperties();

        //if there were database properties specified, use these to load even more properties!
        Map<String, Properties> dbGroupedProperties = new HashMap<>();
        if ( groupedProperties.containsKey(HandlerConfig.DATABASE_CONFIGS_PROPERTY_GROUP)) {
            //remove the special database properties if they exist, these define a connection to the database to load more configs
            //they don't contain the standard configuration properties for this handler
            Properties dbConnectionProps = groupedProperties.remove(HandlerConfig.DATABASE_CONFIGS_PROPERTY_GROUP);
            dbGroupedProperties = properties(new JdbcPropertyLoader(dbConnectionProps)).stripAndGroupByFirstKeyToken("\\.").loadProperties();
        }

        //the ordering here means local properties take precedence
        dbGroupedProperties.putAll(groupedProperties);

        Map<String, E> configs = transformtoHandlerConfigs(dbGroupedProperties);
        return configs;
    }

    private Map<String, E> transformtoHandlerConfigs(Map<String, Properties> propertiesByGroup) {
        DefaultConfigLoader configLoader = new DefaultConfigLoader(handlerName, configFactory, propertiesByGroup);
        return configLoader.loadConfigs();
    }

}
