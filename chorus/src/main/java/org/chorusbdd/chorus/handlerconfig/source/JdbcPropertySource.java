/**
 *  Copyright (C) 2000-2014 The Software Conservancy and Original Authors.
 *  All rights reserved.
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to
 *  deal in the Software without restriction, including without limitation the
 *  rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 *  sell copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 *  FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 *  IN THE SOFTWARE.
 *
 *  Nothing in this notice shall be deemed to grant any rights to trademarks,
 *  copyrights, patents, trade secrets or any other intellectual property of the
 *  licensor or any contributor except as expressly stated herein. No patent
 *  license is granted separate from the Software, for code that you delete from
 *  the Software, or for combinations of the Software with other software or
 *  hardware.
 */
package org.chorusbdd.chorus.handlerconfig.source;

import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;
import org.chorusbdd.chorus.util.ChorusException;

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

    private final String JDBC_DRIVER = "jdbc_driver";
    private final String JDBC_URL = "jdbc_url";
    private final String JDBC_USER = "jdbc_user";
    private final String JDBC_PASSWORD = "jdbc_password";
    private final String JDBC_SQL = "jdbc_sql";

    private Properties jdbcProperties;

    public JdbcPropertySource(Properties jdbcProperties) {
        this.jdbcProperties = jdbcProperties;
    }

    public Map<String, Properties> loadProperties() {
        Connection conn = null;
        Map<String, Properties> propertiesByGroupName = new HashMap<String, Properties>();
        try {
            //load MBean config from DB
            Class.forName(jdbcProperties.getProperty(JDBC_DRIVER));

            conn = DriverManager.getConnection(
                jdbcProperties.getProperty(JDBC_URL),
                jdbcProperties.getProperty(JDBC_USER),
                jdbcProperties.getProperty(JDBC_PASSWORD)
            );
            Statement stmt = conn.createStatement();
            String query = jdbcProperties.getProperty(JDBC_SQL);
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                Properties p = new Properties();
                String configName = null;
                try {
                    configName = rs.getString("configName");
                } catch (SQLException e) {
                    //handle change between 1.6.x and 2.x
                    configName = rs.getString("name");
                }
                p.put("configName", configName);

                for ( int loop=1; loop <= rs.getMetaData().getColumnCount(); loop ++) {
                    String columnName = rs.getMetaData().getColumnLabel(loop);
                    Object o = rs.getObject(loop);
                    if ( o != null ) {
                        p.put(columnName, o.toString());
                    }
                }
                propertiesByGroupName.put(configName, p);
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
