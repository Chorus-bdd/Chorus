package org.chorusbdd.chorus.handlers.util.config.loader;

import org.chorusbdd.chorus.handlers.util.config.HandlerConfig;
import org.chorusbdd.chorus.handlers.util.config.HandlerConfigBuilder;
import org.chorusbdd.chorus.handlers.util.config.source.JdbcPropertySource;
import org.chorusbdd.chorus.util.logging.ChorusLog;
import org.chorusbdd.chorus.util.logging.ChorusLogFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 18/09/12
 * Time: 08:45
 */
public class JDBCConfigLoader<E extends HandlerConfig> extends AbstractConfigLoader<E> {

    private static ChorusLog log = ChorusLogFactory.getLog(JDBCConfigLoader.class);

    private Properties dbProperties;
    private HandlerConfigBuilder<E> configBuilder;

    public JDBCConfigLoader(Properties dbProperties, HandlerConfigBuilder<E> configBuilder) {
        this.dbProperties = dbProperties;
        this.configBuilder = configBuilder;
    }

    public Map<String, E> doLoadConfigs() {
        JdbcPropertySource jdbcPropertiesLoader = new JdbcPropertySource(dbProperties);
        Map<String,Properties> propertiesGroups = jdbcPropertiesLoader.getPropertiesGroups();

        Map<String, E> map = new HashMap<String, E>();
        addConfigsFromPropertyGroups(propertiesGroups, map, configBuilder);
        return map;
    }

}
