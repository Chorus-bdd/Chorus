package org.chorusbdd.chorus.handlerconfig.configbean;

import org.chorusbdd.chorus.handlerconfig.properties.JdbcPropertyLoader;
import org.chorusbdd.chorus.handlerconfig.properties.operations.PropertyOperations;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static org.chorusbdd.chorus.handlerconfig.properties.operations.PropertyOperations.properties;

/**
 * Created by nick on 03/10/2014.
 *
 * Uses the supplied HandlerConfigFactory to convert Properties into config beans for a handler
 *
 * The properties are supplied in a Map<String, Properties> where each value represents a named config - a separate group of
 * properties which needs to be converted to a config bean.
 *
 * If a special group of properties 'dbconfigs' are provided then a jdbc connection will be established to
 * load extra properties from the database.
 *
 * Local properties in config files override any similar properties loaded from
 * the db.
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
 * property   - the property key to set, e.g. remoting.myprocess.jmxPort
 * value      - the property value to set
 */
public class PropertyFileAndDbConfigBeanBuilder<E extends HandlerConfigBean> implements ConfigBeanBuilder<E> {

    private ConfigBeanFactory<E> configFactory;
    private String handlerName;

    public PropertyFileAndDbConfigBeanBuilder(
            ConfigBeanFactory<E> configFactory,
            String handlerName) {
        this.configFactory = configFactory;
        this.handlerName = handlerName;
    }

    public Map<String, E> buildConfigs(Map<String, Properties> groupedConfigs) throws IOException {

        //if there is a special database configs properties group, use these to load even more properties!
        Map<String, Properties> dbGroupedProperties = new HashMap<>();
        if ( groupedConfigs.containsKey(HandlerConfigBean.DATABASE_CONFIGS_PROPERTY_GROUP)) {
            //remove the special database properties if they exist, these define a connection to the database to load more configs
            //they don't contain the standard configuration properties for this handler
            Properties dbConnectionProps = groupedConfigs.remove(HandlerConfigBean.DATABASE_CONFIGS_PROPERTY_GROUP);

            PropertyOperations dbProps = properties(new JdbcPropertyLoader(dbConnectionProps));
            dbGroupedProperties = dbProps.filterByKeyPrefix(handlerName).removeKeyPrefix(handlerName + ".").splitKeyAndGroup("\\.").loadProperties();
        }

        //the ordering here means local properties take precedence
        dbGroupedProperties.putAll(groupedConfigs);

        Map<String, E> configs = transformtoHandlerConfigs(dbGroupedProperties);
        return configs;
    }

    private Map<String, E> transformtoHandlerConfigs(Map<String, Properties> groupedConfigs) {
        DefaultConfigBeanBuilder builder = new DefaultConfigBeanBuilder(handlerName, configFactory);
        return builder.buildConfigs(groupedConfigs);
    }

}
