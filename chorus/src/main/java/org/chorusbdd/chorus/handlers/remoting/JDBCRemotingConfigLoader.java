package org.chorusbdd.chorus.handlers.remoting;

import org.chorusbdd.chorus.ChorusException;
import org.chorusbdd.chorus.util.logging.ChorusLog;
import org.chorusbdd.chorus.util.logging.ChorusLogFactory;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 18/09/12
 * Time: 08:45
 */
public class JDBCRemotingConfigLoader extends AbstractRemotingConfigLoader {

    private static ChorusLog log = ChorusLogFactory.getLog(JDBCRemotingConfigLoader.class);

    public Map<String, RemotingConfig> loadConfigsFromDb(Properties p) {
        Connection conn = null;
        try {
            //load MBean config from DB
            Class.forName(p.getProperty("jdbc.driver"));
            conn = DriverManager.getConnection(p.getProperty("jdbc.url"), p.getProperty("jdbc.user"), p.getProperty("jdbc.password"));
            Statement stmt = conn.createStatement();
            String query = p.getProperty("jdbc.sql");
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                RemotingConfig remotingConfig = new RemotingConfig();
                remotingConfig.setName(rs.getString("name"));
                remotingConfig.setProtocol(rs.getString("protocol"));
                remotingConfig.setHost(rs.getString("host"));
                remotingConfig.setPort(rs.getInt("port"));
                remotingConfig.setConnectionRetryAttempts(rs.getInt("retryAttempts"));
                remotingConfig.setConnectionRetryMillis(rs.getInt("retryMillis"));
                getRemotingConfigMap().put(remotingConfig.getName(), remotingConfig);
            }
            rs.close();
            stmt.close();
            log.debug("Loaded " + getRemotingConfigMap().size() + " remoting configurations from database");
        } catch (Exception e) {
            throw new ChorusException("Failed to load remoting configuration from database" + e.toString());
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    //noop
                }
            }
        }

        removeInvalidConfigs();
        return getRemotingConfigMap();
    }

}
