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
package org.chorusbdd.chorus.handlerconfig.loader;

import org.chorusbdd.chorus.handlerconfig.HandlerConfig;
import org.chorusbdd.chorus.handlerconfig.HandlerConfigFactory;
import org.chorusbdd.chorus.handlerconfig.source.JdbcPropertySource;
import org.chorusbdd.chorus.handlerconfig.source.PropertiesFilePropertySource;
import org.chorusbdd.chorus.handlerconfig.source.PropertyGroupsSource;
import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;
import org.chorusbdd.chorus.results.FeatureToken;
import org.chorusbdd.chorus.util.ChorusException;

import java.io.File;
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

    private String handlerName;
    private FeatureToken featureToken;

    public JDBCConfigLoader(
            String handlerName,
            HandlerConfigFactory<E> configFactory, 
            FeatureToken featureToken) {
        
        super(handlerName, configFactory, featureToken);
        this.handlerName = handlerName;
        this.featureToken = featureToken;
    }
    
    public PropertyGroupsSource getPropertySource() {
        PropertiesFilePropertySource propertiesFilePropertySource = new PropertiesFilePropertySource(
            "db",
            "db",
            featureToken
        );
        Map<String, Properties> p = propertiesFilePropertySource.getPropertiesByConfigName();
        Properties props = p.get(handlerName);
        if ( props == null) {
            throw new ChorusException("Could not find db properties for db. " + handlerName);
        }

        JdbcPropertySource jdbcPropertiesLoader = new JdbcPropertySource(props);
        return jdbcPropertiesLoader;
    }

    public String toString() {
        return "JDBCConfigLoader{}";
    }
}
