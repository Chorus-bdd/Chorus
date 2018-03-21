package org.chorusbdd.chorus.sql.manager;

import org.chorusbdd.chorus.annotations.ExecutionPriority;
import org.chorusbdd.chorus.annotations.Scope;
import org.chorusbdd.chorus.executionlistener.ExecutionListener;
import org.chorusbdd.chorus.executionlistener.ExecutionListenerAdapter;
import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;
import org.chorusbdd.chorus.results.ExecutionToken;
import org.chorusbdd.chorus.results.FeatureToken;
import org.chorusbdd.chorus.results.ScenarioToken;
import org.chorusbdd.chorus.sql.config.SqlConfig;
import org.chorusbdd.chorus.sql.config.SqlConfigBeanValidator;
import org.chorusbdd.chorus.sql.config.SqlConfigBuilder;
import org.chorusbdd.chorus.sql.config.SqlConfigBuilderFactory;
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
public class DefaultSqlManager implements SqlManager {

    private ChorusLog log = ChorusLogFactory.getLog(DefaultSqlManager.class);

    private SqlConfigBuilderFactory sqlConfigBuilderFactory = new SqlConfigBuilderFactory();
    private SqlConfigBeanValidator sqlConfigBeanValidator = new SqlConfigBeanValidator();
    
    private Map<String, Tuple2<Connection, SqlConfig>> configNameToConnectionDetails = new ConcurrentHashMap<>();

    private FeatureToken feature;

    private CleanupShutdownHook cleanupShutdownHook = new CleanupShutdownHook();
    
    public DefaultSqlManager() {
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
        
        SqlConfig sqlConfig = getSqlConfig(configName, properties);
        validateConfig(configName, sqlConfig);

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

    private void validateConfig(String configName, SqlConfig sqlConfig) {
        boolean valid = sqlConfigBeanValidator.isValid(sqlConfig);
        if ( ! valid) {
            log.warn(sqlConfigBeanValidator.getErrorDescription());
            throw new ChorusException("The sql config for " + configName + " must be valid");
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

    private SqlConfig getSqlConfig(String configName, Properties processProperties) {
        SqlConfigBuilder builder = sqlConfigBuilderFactory.createConfigBuilder(processProperties, configName);
        return builder.build();
    }

    /**
     * If shut down before a scenario completes, try as hard as we can to cleanly close down any open web drivers
     * Not doing so can leave selenium hub in an inoperable state
     */
    private class CleanupShutdownHook extends Thread {
        public void run() {
            log.debug("Running Cleanup on shutdown for " + this.getClass().getSimpleName());
            try {
                closeAllConnections();
            } catch (Throwable t) {
                log.debug("Failed during cleanup", t);
            }
        }
    }

}
