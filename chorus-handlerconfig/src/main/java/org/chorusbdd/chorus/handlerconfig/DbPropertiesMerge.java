package org.chorusbdd.chorus.handlerconfig;

import org.chorusbdd.chorus.handlerconfig.configbean.HandlerConfigBean;
import org.chorusbdd.chorus.handlerconfig.properties.JdbcPropertyLoader;
import org.chorusbdd.chorus.util.function.Predicate;
import org.chorusbdd.chorus.util.properties.PropertyOperations;

import java.io.IOException;
import java.util.Properties;

import static org.chorusbdd.chorus.util.properties.PropertyOperations.properties;

/**
 * Created by GA2EBBU on 13/01/2015.
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
class DbPropertiesMerge {

    private String handlerName;

    public DbPropertiesMerge(String handlerName) {
        this.handlerName = handlerName;
    }

    PropertyOperations mergeWithDatabaseProperties(PropertyOperations properties) throws IOException {

        //if there is a special database configs properties group, use these to load even more properties!
        Properties dbProperties = properties.filterByKeyPrefix(
                HandlerConfigBean.DATABASE_CONFIGS_PROPERTY_GROUP).removeKeyPrefix(
                HandlerConfigBean.DATABASE_CONFIGS_PROPERTY_GROUP + ".").loadProperties();
        PropertyOperations dbProps = loadDbProps(dbProperties);

        //local properties take precedence over db loaded properties so merge them over the top of any db ones
        return dbProps.merge(properties).filterKeys(new RemoveDbProps());
    }

    private PropertyOperations loadDbProps(Properties connectionProperties) throws IOException {
        PropertyOperations result = PropertyOperations.emptyProperties();

        if (connectionProperties.size() > 0) {
            //remove the special database properties if they exist, these define a connection to the database to load more configs
            //they don't contain the standard configuration properties for this handler
            PropertyOperations propertiesFromDb = properties(new JdbcPropertyLoader(connectionProperties));
            result = result.merge(propertiesFromDb.filterByKeyPrefix(handlerName).removeKeyPrefix(handlerName + "."));
        }
        return result;
    }

    private static class RemoveDbProps implements Predicate<String> {
        public boolean test(String key) {
            return ! key.startsWith(HandlerConfigBean.DATABASE_CONFIGS_PROPERTY_GROUP);
        }
    }
}
