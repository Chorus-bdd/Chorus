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
package org.chorusbdd.chorus.processes.manager;

import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;
import org.chorusbdd.chorus.processes.manager.config.LogFileAndMode;
import org.chorusbdd.chorus.processes.manager.config.OutputMode;
import org.chorusbdd.chorus.processes.manager.process.NamedProcess;
import org.chorusbdd.chorus.results.FeatureToken;
import org.chorusbdd.chorus.util.assertion.ChorusAssert;

import java.io.File;

/**
* Created with IntelliJ IDEA.
* User: Nick E
* Date: 12/11/12
* Time: 14:25
*
* Calculate the LogFileAndMode for process output based on feature file, scenario config and alias
*
* A process with the same config but a different alias will have different log file names, so this cannot
* be done in the ProcessesConfig
* 
* Also here we create the log directory if configured to do so, and fail the feature
* if it is not writable
*/
public class ProcessOutputConfiguration {

    private ChorusLog log = ChorusLogFactory.getLog(ProcessOutputConfiguration.class);

    private FeatureToken featureToken;
    private File featureDir;
    private String logFileBaseName;
    private NamedProcess processesConfig;

    private LogFileAndMode stdOutFileAndMode;
    private LogFileAndMode stdErrFileAndMode;

    private File logDirectory;  //calculated but may or may not exist
    private boolean isAppendToLogs;

    public ProcessOutputConfiguration(FeatureToken featureToken, NamedProcess processesConfig) {
        this.featureDir = featureToken.getFeatureDir();
        this.logFileBaseName = calculateLogFileBaseName(featureToken, featureToken.getFeatureFile(), processesConfig.getProcessName());
        this.processesConfig = processesConfig;
        this.isAppendToLogs = processesConfig.isAppendToLogs();
        this.featureToken = featureToken;

        logDirectory = calculateLogDirectory();

        stdOutFileAndMode = new LogFileAndMode(
            new File(logDirectory, String.format("%s-out.log", logFileBaseName)),
            processesConfig.getStdOutMode(),
            "stdOut",
            false
        );

        stdErrFileAndMode = new LogFileAndMode(
            new File(logDirectory, String.format("%s-err.log", logFileBaseName)),
            processesConfig.getStdErrMode(),
            "stdErr",
            true
        );
       
        //let's fail the feature if we cannot create the log directory
        //alternative would be to log inline but this might swamp interpreter output
        if (  OutputMode.isWriteToLogFile(stdOutFileAndMode.getMode()) || OutputMode.isWriteToLogFile(stdErrFileAndMode.getMode()) ) {
            getOrCreateLogDirectory(logDirectory);
            ChorusAssert.assertTrue("Cannot write to the logs directory at " + logDirectory, logDirectory.canWrite());
        }
    }

    private File calculateLogDirectory() {
        //if a logs directory was provided in the config use that, or default to featureDir/logs
        String defaultPath = featureDir.getAbsolutePath() + File.separatorChar + "logs";
        String directoryPath = processesConfig.getLogDirectory() != null ?
            processesConfig.getLogDirectory() :
            defaultPath;
        return new File(directoryPath);
    }

    private void getOrCreateLogDirectory(File logDirectory) {
        boolean logDirExists = logDirectory.exists();
        if ( ! logDirExists && processesConfig.isCreateLogDir()) {
            log.debug("Creating log directory at " + logDirectory.getPath() + " for feature " + featureToken.getName());
            logDirExists = logDirectory.mkdirs();
            if ( ! logDirExists ) {
                log.warn("Failed to create log directory at " + logDirectory.getPath() + " will not write logs");
            }
        }
    }

    private String calculateLogFileBaseName(FeatureToken featureToken, File featureFile, String processAlias) {
        String featureFileBaseName = getFeatureName(featureFile);

        //log file base name including both feature name and process alias ( + feature config )
        String processFileNameBase;
        if (! featureToken.isConfiguration()) {
            processFileNameBase = String.format("%s-%s", featureFileBaseName, processAlias);
        } else {
            processFileNameBase = String.format("%s-%s-%s", featureFileBaseName, featureToken.getConfigurationName(), processAlias);
        }
        return processFileNameBase;
    }

    private String getFeatureName(File featureFile) {
        //build a process name to use when naming log files
        String processNameForLogFiles = featureFile.getName();
        if (processNameForLogFiles.endsWith(".feature")) {
            processNameForLogFiles = processNameForLogFiles.substring(0, processNameForLogFiles.length() - 8);
        }
        return processNameForLogFiles;
    }

    public LogFileAndMode getStdOutFileAndMode() {
        return stdOutFileAndMode;
    }

    public LogFileAndMode getStdErrFileAndMode() {
        return stdErrFileAndMode;
    }

    public String getLogFileBaseName() {
        return logFileBaseName;
    }

    public boolean isAppendToLogs() {
        return isAppendToLogs;
    }

    @Override
    public String toString() {
        return "ProcessLogOutput{" +
                "logFileBaseName='" + logFileBaseName + '\'' +
                ", stdOutFileAndMode=" + stdOutFileAndMode +
                ", stdErrFileAndMode=" + stdErrFileAndMode +
                ", isAppendToLogs=" + isAppendToLogs +
                '}';
    }
}
