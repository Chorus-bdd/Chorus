package org.chorusbdd.chorus.sql.manager;

import org.chorusbdd.chorus.annotations.SubsystemConfig;
import org.chorusbdd.chorus.subsystem.Subsystem;

import java.util.Properties;

/**
 * Created by nickebbutt on 27/02/2018.
 */
@SubsystemConfig(
        id = "sqlManager",
        implementationClass = "org.chorusbdd.chorus.sql.manager.DefaultSqlManager",
        overrideImplementationClassSystemProperty = "chorusSqlManager")
public interface SqlManager extends Subsystem {
    
    void connectToDatabase(String configName, Properties properties);

    void executeAStatement(String configName, String statement);
}
