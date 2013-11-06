/**
 *  Copyright (C) 2000-2013 The Software Conservancy and Original Authors.
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
package org.chorusbdd.chorus.handlers.util.config.source;

import org.chorusbdd.chorus.util.ChorusException;
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

    public Map<String, Properties> getPropertyGroups() {
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
                for ( int loop=1; loop <= rs.getMetaData().getColumnCount(); loop ++) {
                    String columnName = rs.getMetaData().getColumnLabel(loop);
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
