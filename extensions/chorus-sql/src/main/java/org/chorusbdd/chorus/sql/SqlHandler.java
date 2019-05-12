/**
 * MIT License
 *
 * Copyright (c) 2019 Chorus BDD Organisation.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.chorusbdd.chorus.sql;

import org.chorusbdd.chorus.annotations.*;
import org.chorusbdd.chorus.handlerconfig.ConfigPropertySource;
import org.chorusbdd.chorus.handlerconfig.ConfigurationManager;
import org.chorusbdd.chorus.handlerconfig.HandlerConfigLoader;
import org.chorusbdd.chorus.handlerconfig.configproperty.ConfigBuilderException;
import org.chorusbdd.chorus.handlerconfig.configproperty.ConfigPropertyParser;
import org.chorusbdd.chorus.handlerconfig.configproperty.ConfigurationProperty;
import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;
import org.chorusbdd.chorus.results.ScenarioToken;
import org.chorusbdd.chorus.sql.config.SqlConfigBean;
import org.chorusbdd.chorus.sql.manager.SqlManager;
import org.chorusbdd.chorus.util.ScopeUtils;
import org.chorusbdd.chorus.util.handler.HandlerPatterns;

import java.util.List;
import java.util.Properties;

/**
 * Created by nickebbutt on 27/02/2018.
 */
@Handler(value = "SQL", scope = Scope.FEATURE)
public class SqlHandler implements ConfigPropertySource {

    private ChorusLog log = ChorusLogFactory.getLog(SqlHandler.class);

    @ChorusResource("subsystem.configurationManager")
    private ConfigurationManager configurationManager;

    @ChorusResource("scenario.token")
    private ScenarioToken scenarioToken;

    @ChorusResource("subsystem.sqlManager")
    private SqlManager sqlManager;
    
    @Step(".*I connect to the " + HandlerPatterns.namePattern + " database")
    @Documentation(order = 10, description = "Connect to the named database using the connection parameters configured in the handler properties", example = "Given I connect to the mySql database")
    public void connectToDatabase(String configName) {
        Properties properties = getConfig(configName);
        sqlManager.connectToDatabase(configName, properties);
    }
    
    @Step(".*I execute the statement '(.*)' on the " + HandlerPatterns.namePattern + " database")
    @Documentation(order = 20, description = "Execute the provided text as a statement against the connected database with given name", example = "When I execute the statement 'insert into MyUsers values (\"Bob\")' on the mySql database")
    public void executeAStatement(String statement, String configName ) {
        sqlManager.executeAStatement(configName, statement);
    }
    
    @Step(".*I execute the script (.*) on the " + HandlerPatterns.namePattern + " database")
    @Documentation(order = 30, description = "Execute a SQL script from a file path relative to the feature directory against the connected database with given name. The script file may contain one or more semi-colon delimited SQL statements", example = "When I execute the script mySqlScript.sql on the mySql database")
    public void executeAScript(String script, String configName ) {
        sqlManager.executeAScript(configName, script);
    }

    private Properties getConfig(String configName) {
        Properties p = new HandlerConfigLoader().loadPropertiesForSubGroup(configurationManager, "sql", configName);
        new ScopeUtils().setScopeForContextIfNotConfigured(scenarioToken, p);
        return p;
    }

    @Override
    public List<ConfigurationProperty> getConfigProperties() throws ConfigBuilderException {
        return new ConfigPropertyParser().getConfigProperties(SqlConfigBean.class);
    }
}
