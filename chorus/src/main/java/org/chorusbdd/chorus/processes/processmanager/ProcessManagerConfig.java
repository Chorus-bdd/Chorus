package org.chorusbdd.chorus.processes.processmanager;

import org.chorusbdd.chorus.annotations.Scope;
import org.chorusbdd.chorus.handlerconfig.HandlerConfig;

/**
 * Created by nick on 24/09/2014.
 */
public interface ProcessManagerConfig extends HandlerConfig {

    String getGroupName();

    String getJre();

    String getClasspath();

    String getJvmargs();

    String getMainclass();

    String getPathToExecutable();

    String getArgs();

    OutputMode getStdErrMode();

    OutputMode getStdOutMode();

    int getJmxPort();

    boolean isRemotingConfigDefined();

    int getDebugPort();

    int getTerminateWaitTime();

    String getLogDirectory();

    boolean isAppendToLogs();

    boolean isCreateLogDir();

    int getProcessCheckDelay();

    int getReadAheadBufferSize();

    int getReadTimeoutSeconds();

    Scope getProcessScope();

    String getProcessConfigName();
}
