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
package org.chorusbdd.chorus.sql.manager;

import org.chorusbdd.chorus.annotations.ExecutionPriority;
import org.chorusbdd.chorus.annotations.Scope;
import org.chorusbdd.chorus.executionlistener.ExecutionListener;
import org.chorusbdd.chorus.executionlistener.ExecutionListenerAdapter;
import org.chorusbdd.chorus.handlerconfig.ConfigurableManager;
import org.chorusbdd.chorus.handlerconfig.configproperty.ConfigBuilder;
import org.chorusbdd.chorus.handlerconfig.configproperty.ConfigBuilderException;
import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;
import org.chorusbdd.chorus.results.ExecutionToken;
import org.chorusbdd.chorus.results.FeatureToken;
import org.chorusbdd.chorus.results.ScenarioToken;
import org.chorusbdd.chorus.sql.config.SqlConfig;
import org.chorusbdd.chorus.sql.config.SqlConfigBean;
import org.chorusbdd.chorus.util.ChorusException;
import org.chorusbdd.chorus.util.FileUtils;
import org.chorusbdd.chorus.util.function.Tuple2;

import java.io.File;
import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.chorusbdd.chorus.util.function.Tuple2.tuple2;

/**
 * Created by nickebbutt on 27/02/2018.
 */
public class DefaultSqlManager extends ConfigurableManager<SqlConfigBean> implements SqlManager {

    private ChorusLog log = ChorusLogFactory.getLog(DefaultSqlManager.class);

    private Map<String, Tuple2<Connection, SqlConfig>> configNameToConnectionDetails = new ConcurrentHashMap<>();

    private FeatureToken feature;

    private CleanupShutdownHook cleanupShutdownHook = new CleanupShutdownHook();
    
    public DefaultSqlManager() {
        super(SqlConfigBean.class);
        addShutdownHook();
    }

    private void addShutdownHook() {
        log.trace("Adding shutdown hook for SqlManager " + this);
        Runtime.getRuntime().addShutdownHook(cleanupShutdownHook);
    }

    @Override
    public void connectToDatabase(String configName, Properties properties) {
        
        if ( configNameToConnectionDetails.containsKey(configName)) {
            throw new ChorusException("The database " + configName + " is already connected");
        }
        
        SqlConfig sqlConfig = getConfig(configName, properties, "sql");

        try {
            log.debug("Loading database driver " + sqlConfig.getDriverClassName());
            Class.forName(sqlConfig.getDriverClassName());
        } catch (ClassNotFoundException e) {
            throw new ChorusException("Failed to load JDBC driver class " + sqlConfig.getDriverClassName() + ", is the driver .jar on the classpath?", e);
        }

        Connection connection;
        try {
            connection = DriverManager.getConnection(
                sqlConfig.getUrl(),
                sqlConfig.getUsername(),
                sqlConfig.getPassword()
            );
        } catch (SQLException e) {
            throw new ChorusException("Failed to create connection to database " + sqlConfig.getConfigName(), e);
        }
        
        log.debug("Adding database connection for " + sqlConfig.getConfigName());
        configNameToConnectionDetails.put(sqlConfig.getConfigName(), tuple2(connection, sqlConfig));
    }
    
    @Override
    public void executeAStatement(String configName, String statement) {
        Tuple2<Connection, SqlConfig> connectionDetails = getConnectionDetails(configName);
        executeJdbcStatements(connectionDetails.getOne(), configName, statement, statement);
    }
    
    @Override
    public void executeAScript(String configName, String scriptPath) {
        //Resolve the script path relative to the feature file
        File script = feature.getFeatureDir().toPath().resolve(scriptPath).toFile();
        String scriptContents = FileUtils.readScriptFile(log, configName, scriptPath, script);
     
        Tuple2<Connection, SqlConfig> connectionDetails = getConnectionDetails(configName);
        executeJdbcStatements(connectionDetails.getOne(), configName, scriptContents, "script at " + scriptPath);
    }
    
    private Statement createStatement(String configName, Connection connection) {
        Statement stmt;
        try {
            log.trace("Creating a JDBC statement");
            stmt = connection.createStatement();
        } catch (SQLException e) {
            throw new ChorusException("Failed to create a statement against database " + configName, e);
        }
        return stmt;
    }

    private Tuple2<Connection, SqlConfig> getConnectionDetails(String configName) {
        Tuple2<Connection, SqlConfig> connectionDetails = configNameToConnectionDetails.get(configName);
        if ( connectionDetails == null) {
            throw new ChorusException("Could not find a database connection for database " + configName);
        }
        return connectionDetails;
    }


    /**
     * Execute one or more SQL statements
     * 
     * @param statements, a String which may contain one or more semi-colon-delimited SQL statements
     */
    private void executeJdbcStatements(Connection connection, String configName, String statements, String description) {
        Statement stmt = createStatement(configName, connection);
        try {
            log.debug("Executing statement [" + description + "]");
            
            List<String> stmtsToExecute = Stream.of(statements.split(";"))
                    .map(String::trim)
                    .filter(s -> s.length() > 0)
                    .collect(Collectors.toList());
            
            if ( log.isTraceEnabled()) {
                log.trace("These statements will be executed:");
                stmtsToExecute.forEach(s -> log.trace("Statement: [" + s + "]"));
            }
            
            for ( String currentStatement : stmtsToExecute) {
                stmt.execute(currentStatement);
                log.trace("Executing statement: " + currentStatement + " OK!");
            }
            
        } catch (SQLException e) {
            throw new ChorusException(
                    String.format("Failed while executing statement [%s] on database + %s [%s]", description, configName, e.toString(), e)
            );
        }
    }

    @Override
    public ExecutionListener getExecutionListener() {
        @ExecutionPriority(ExecutionPriority.SQL_MANAGER_PRIORITY)
        class SqlManagerExecutionListener extends ExecutionListenerAdapter {

            @Override
            public void featureStarted(ExecutionToken testExecutionToken, FeatureToken feature) {
                DefaultSqlManager.this.feature = feature;
            }

            @Override
            public void scenarioCompleted(ExecutionToken testExecutionToken, ScenarioToken scenario) {
                closeConnectionsForScope(Scope.SCENARIO);
            }

            @Override
            public void featureCompleted(ExecutionToken testExecutionToken, FeatureToken feature) {
                closeConnectionsForScope(Scope.FEATURE);
            }

            private void closeConnectionsForScope(Scope scope) {
                List<Tuple2<Connection, SqlConfig>> configsForThisScope = configNameToConnectionDetails.values()
                        .stream()
                        .filter(c -> c.getTwo().getScope() == scope)
                        .collect(Collectors.toList());
                
                configsForThisScope.forEach(t -> {
                    String configName = t.getTwo().getConfigName();
                    Connection c = t.getOne();
                    closeAndRemoveConnection(configName, c);
                });
            }
        };
        return new SqlManagerExecutionListener();
    }

    private void closeAllConnections() {
        List<Tuple2<Connection, SqlConfig>> allConfigs = new ArrayList<>(configNameToConnectionDetails.values());
        allConfigs.forEach(c -> closeAndRemoveConnection(c.getTwo().getConfigName(), c.getOne()));
    }

    private void closeAndRemoveConnection(String configName, Connection c) {
        log.debug("Closing connection for database " + configName);
        try {
            c.close();
            
            //only do this if close succeeds - guard against creating another connection / connection leak
            configNameToConnectionDetails.remove(configName);
        } catch (Exception e) {
            throw new ChorusException("Failed to close database connection for " + configName, e);
        }
    }

    /**
     * If shut down before a scenario completes, try as hard as we can to cleanly close down any open web drivers
     * Not doing so can leave selenium hub in an inoperable state
     */
    private class CleanupShutdownHook extends Thread {
        public void run() {
            log.debug("Running Cleanup on shutdown for SqlManager");
            try {
                closeAllConnections();
            } catch (Throwable t) {
                log.debug("Failed during cleanup", t);
            }
        }
    }

}
