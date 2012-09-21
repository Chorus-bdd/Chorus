package org.chorusbdd.chorus.handlers.util.config.loader;

import org.chorusbdd.chorus.handlers.remoting.RemotingConfig;
import org.chorusbdd.chorus.handlers.util.config.HandlerConfigBuilder;
import org.chorusbdd.chorus.handlers.util.config.source.JdbcPropertySource;
import org.chorusbdd.chorus.util.logging.ChorusLog;
import org.chorusbdd.chorus.util.logging.ChorusLogFactory;

import java.util.Map;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 18/09/12
 * Time: 08:45
 */
public class JDBCConfigLoader<E extends RemotingConfig> extends AbstractConfigLoader<E> {

    private static ChorusLog log = ChorusLogFactory.getLog(JDBCConfigLoader.class);

    private Properties dbProperties;

    public JDBCConfigLoader(Properties dbProperties, HandlerConfigBuilder<E> configBuilder) {
        super(configBuilder);
        this.dbProperties = dbProperties;
    }

    public void doLoadConfigs() {
        JdbcPropertySource jdbcPropertiesLoader = new JdbcPropertySource(dbProperties);
        Map<String,Properties> propertiesGroups = jdbcPropertiesLoader.getPropertiesGroups();
        loadRemotingConfigs(propertiesGroups);
    }

}
