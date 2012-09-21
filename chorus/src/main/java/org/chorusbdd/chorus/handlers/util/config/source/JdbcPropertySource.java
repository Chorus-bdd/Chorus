package org.chorusbdd.chorus.handlers.util.config.source;

import org.chorusbdd.chorus.ChorusException;
import org.chorusbdd.chorus.util.logging.ChorusLog;
import org.chorusbdd.chorus.util.logging.ChorusLogFactory;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: GA2EBBU
 * Date: 21/09/12
 * Time: 10:21
 * To change this template use File | Settings | File Templates.
 */
public class JdbcPropertySource implements PropertyGroupsSource {

    private static ChorusLog log = ChorusLogFactory.getLog(JdbcPropertySource.class);

    private Properties jdbcProperties;

    public JdbcPropertySource(Properties jdbcProperties) {
        this.jdbcProperties = jdbcProperties;
    }

    public Map<String, Properties> getPropertiesGroups() {
        Connection conn = null;
        Map<String, Properties> propertiesByGroupName = new HashMap<String, Properties>();
        try {
            //load MBean config from DB
            Class.forName(jdbcProperties.getProperty("jdbc.driver"));
            conn = DriverManager.getConnection(jdbcProperties.getProperty("jdbc.url"), jdbcProperties.getProperty("jdbc.user"), jdbcProperties.getProperty("jdbc.password"));
            Statement stmt = conn.createStatement();
            String query = jdbcProperties.getProperty("jdbc.sql");
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                Properties p = new Properties();
                String groupName = rs.getString("name");
                for ( int loop=1; loop < rs.getMetaData().getColumnCount(); loop ++) {
                    String columnName = rs.getMetaData().getColumnName(loop);
                    Object o = rs.getObject(loop);
                    if ( o != null ) {
                        p.put(columnName, o.toString());
                    }
                }
                propertiesByGroupName.put(groupName, p);
            }
            rs.close();
            stmt.close();
            log.debug("Loaded " + propertiesByGroupName.size() + " property group configurations from database");
        } catch (Exception e) {
            log.error("Failed to load property group configurations from database", e);
            throw new ChorusException("Failed to load property group configurations from database");
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    //noop
                }
            }
        }
        return propertiesByGroupName;
    }
}
