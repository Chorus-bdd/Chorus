package org.chorusbdd.chorus.sql.config;

import org.chorusbdd.chorus.handlerconfig.configbean.AbstractConfigBuilderFactory;
import org.chorusbdd.chorus.handlerconfig.configbean.ConfigBuilderFactory;
import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;

import java.util.Map;
import java.util.Properties;

/**
 * Created by nickebbutt on 27/02/2018.
 */
public class SqlConfigBuilderFactory extends AbstractConfigBuilderFactory<SqlConfigBuilder> implements ConfigBuilderFactory<SqlConfigBuilder> {

    private ChorusLog log = ChorusLogFactory.getLog(SqlConfigBuilderFactory.class);

    public static final String scope = "scope";
    public static final String driverClassName = "driverClassName";
    public static final String url = "url";
    public static final String username = "username";
    public static final String password = "password";

    @Override
    protected void setProperties(Properties p, SqlConfigBuilder c) {
        for (Map.Entry prop : p.entrySet()) {
            String key = prop.getKey().toString();
            String value = prop.getValue().toString();
            
            if(scope.equals(key)) {
                c.setScope(parseScope(value));
            } else if ( driverClassName.equals(key)) {
                c.setDriverClassName(value);
            } else if ( url.equals(key)) {
                c.setUrl(value);
            } else if ( username.equals(key)) {
                c.setUsername(value);
            } else if ( password.equals(key)) {
                c.setPassword(value);
            } else {
                log.warn("Ignoring property " + key + " which is not a supported WebSocketsManagerImpl handler property");
            }
        }
        
    }

    @Override
    protected SqlConfigBuilder createBuilder() {
        return new SqlConfigBuilder();
    }
}
