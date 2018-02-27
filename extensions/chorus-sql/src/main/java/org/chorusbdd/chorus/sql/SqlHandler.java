package org.chorusbdd.chorus.sql;

import org.chorusbdd.chorus.annotations.ChorusResource;
import org.chorusbdd.chorus.annotations.Handler;
import org.chorusbdd.chorus.annotations.Scope;
import org.chorusbdd.chorus.annotations.Step;
import org.chorusbdd.chorus.handlerconfig.ConfigurationManager;
import org.chorusbdd.chorus.handlerconfig.HandlerConfigLoader;
import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;
import org.chorusbdd.chorus.results.ScenarioToken;
import org.chorusbdd.chorus.sql.manager.SqlManager;
import org.chorusbdd.chorus.util.ScopeUtils;
import org.chorusbdd.chorus.util.handler.HandlerPatterns;

import java.util.Properties;

/**
 * Created by nickebbutt on 27/02/2018.
 */
@Handler(value = "SQL", scope = Scope.FEATURE)
public class SqlHandler {

    private ChorusLog log = ChorusLogFactory.getLog(SqlHandler.class);

    @ChorusResource("subsystem.configurationManager")
    private ConfigurationManager configurationManager;

    @ChorusResource("scenario.token")
    private ScenarioToken scenarioToken;

    @ChorusResource("subsystem.sqlManager")
    private SqlManager sqlManager;
    
    @Step(".*I connect to the " + HandlerPatterns.namePattern + " database") 
    public void connectToDatabase(String configName) {
        Properties properties = getConfig(configName);
        sqlManager.connectToDatabase(configName, properties);
    }
    
    @Step(".*I execute the statement '(.*)' on the " + HandlerPatterns.namePattern + " database")
    public void executeAStatement(String statement, String configName ) {
        sqlManager.executeAStatement(configName, statement);
    }
    
    @Step(".*I execute the script (.*) on the " + HandlerPatterns.namePattern + " database") 
    public void executeAScript(String script, String configName ) {
        sqlManager.executeAScript(configName, script);
    }

    private Properties getConfig(String configName) {
        Properties p = new HandlerConfigLoader().loadPropertiesForSubGroup(configurationManager, "sql", configName);
        new ScopeUtils().setScopeForContextIfNotConfigured(scenarioToken, p);
        return p;
    }
    
    
}
