package org.chorusbdd.chorus.handlers.remoting;

import org.chorusbdd.chorus.handlers.util.AbstractConfigLoader;
import org.chorusbdd.chorus.handlers.util.JdbcPropertySource;
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
public class JDBCRemotingConfigLoader extends AbstractConfigLoader<RemotingConfig> {

    private static ChorusLog log = ChorusLogFactory.getLog(JDBCRemotingConfigLoader.class);

    private Properties dbProperties;

    public JDBCRemotingConfigLoader(Properties dbProperties) {
        super(new RemotingConfigBuilder());
        this.dbProperties = dbProperties;
    }

    public void doLoadConfigs() {
        JdbcPropertySource jdbcPropertiesLoader = new JdbcPropertySource(dbProperties);
        Map<String,Properties> propertiesGroups = jdbcPropertiesLoader.getPropertiesGroups();
        loadRemotingConfigs(propertiesGroups);
    }

}
