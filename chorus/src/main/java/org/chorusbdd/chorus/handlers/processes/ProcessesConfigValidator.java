package org.chorusbdd.chorus.handlers.processes;

import org.chorusbdd.chorus.handlerconfig.AbstractConfigValidator;
import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;

import java.io.File;

/**
 * Created by nick on 23/09/2014.
 */
public class ProcessesConfigValidator extends AbstractConfigValidator<ProcessesConfig> {

    private static ChorusLog log = ChorusLogFactory.getLog(ProcessesConfigValidator.class);

    public boolean checkValid(ProcessesConfig processesConfig) {
        boolean valid = true;

        if ( isJavaProcess(processesConfig) ) {
            //some properties are mandatory for java processes
            String jre = processesConfig.getJre();
            if ( jre == null || ! new File(jre).isDirectory() ) {
                valid = logInvalidConfig(log, "jre property is null or jre path does not exist", processesConfig);
            } else if ( ! isSet(processesConfig.getClasspath()) ) {
                valid = logInvalidConfig(log, "classpath was null", processesConfig);
            } else if ( ! isSet(processesConfig.getMainclass()) ) {
                valid = logInvalidConfig(log, "main class was null or empty", processesConfig);
            }
        } else {
            //some properties should not be used for non-java processes
            valid = checkPropertiesForNativeProcess(processesConfig);
        }
        return valid;
    }

    public boolean isJavaProcess(ProcessesConfig processesConfig) {
        return ! isSet(processesConfig.getPathToExecutable());
    }

    private boolean checkPropertiesForNativeProcess(ProcessesConfig processesConfig) {
        boolean valid = true;
        if (isSet(processesConfig.getMainclass())) {
            valid = logInvalidConfig(log, "Cannot the mainclass property for non-java process configured with pathToExecutable", processesConfig);
        } else if (isSet(processesConfig.getJvmargs()) ) {
            valid = logInvalidConfig(log, "Cannot set jvmargs property for non-java process configured with pathToExecutable", processesConfig);
        }
        return valid;
    }

    public String getValidationRuleDescription() {
        return "groupName, jre, classpath and mainclass must be set for java processes";
    }
}
