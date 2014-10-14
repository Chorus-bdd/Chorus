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
package org.chorusbdd.chorus.processes.manager;

import org.chorusbdd.chorus.core.interpreter.subsystem.processes.OutputMode;
import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;
import org.chorusbdd.chorus.results.FeatureToken;
import org.chorusbdd.chorus.util.assertion.ChorusAssert;

import java.io.File;

/**
* Created with IntelliJ IDEA.
* User: GA2EBBU
* Date: 12/11/12
* Time: 14:25
*
* Calculate the standard out and standard error files for a process log output
* based on feature file, config and alias
* 
* A process with the same config but a different alias will have different log file names, so this cannot 
* be done in the ProcessesConfig
* 
* Also here we create the log directory if configured to do so, and fail the feature
* if it is not writable
*/
class ProcessLogOutput {

    private static ChorusLog log = ChorusLogFactory.getLog(ProcessLogOutput.class);

    private FeatureToken featureToken;
    private File featureDir;
    private String logFileBaseName;
    private ProcessInfo processesConfig;
    private OutputMode stdErrMode;
    private OutputMode stdOutMode;

    private File logDirectory;  //calculated but may or may not exist
    private File stdOutLogFile; //calculated but may or may not exist
    private File stdErrLogFile; //calculated but may or may not exist
    private boolean isAppendToLogs;
    private int readAheadBufferSize;
    private int readTimeoutSeconds;

    public ProcessLogOutput(FeatureToken featureToken, File featureDir, File featureFile, ProcessInfo processesConfig) {
        this.featureDir = featureDir;
        this.logFileBaseName = calculateLogFileBaseName(featureToken, featureFile, processesConfig.getProcessName());
        this.processesConfig = processesConfig;
        this.isAppendToLogs = processesConfig.isAppendToLogs();
        this.featureToken = featureToken;
        this.readAheadBufferSize = processesConfig.getReadAheadBufferSize();
        this.readTimeoutSeconds = processesConfig.getReadTimeoutSeconds();

        logDirectory = calculateLogDirectory();
        calculateLogFiles(logDirectory);

        stdOutMode = processesConfig.getStdOutMode();
        stdErrMode = processesConfig.getStdErrMode();
       
        //let's fail the feature if we cannot create the log directory
        //alternative would be to log inline but this might swamp interpreter output
        if (  OutputMode.isWriteToLogFile(stdOutMode) || OutputMode.isWriteToLogFile(stdErrMode) ) {
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

    private void calculateLogFiles(File logDirectory) {
        stdOutLogFile = new File(logDirectory, String.format("%s-out.log", logFileBaseName));
        stdErrLogFile = new File(logDirectory, String.format("%s-err.log", logFileBaseName));
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

    OutputMode getStdErrMode() {
        return stdErrMode;
    }

    OutputMode getStdOutMode() {
        return stdOutMode;
    }

    public String getLogFileBaseName() {
        return logFileBaseName;
    }

    public File getStdOutLogFile() {
        return stdOutLogFile;
    }

    public File getStdErrLogFile() {
        return stdErrLogFile;
    }

    public boolean isAppendToLogs() {
        return isAppendToLogs;
    }

    int getReadAheadBufferSize() {
        return readAheadBufferSize;
    }

    int getReadTimeoutSeconds() {
        return readTimeoutSeconds;
    }

    @Override
    public String toString() {
        return "ProcessLogOutput{" +
                "stdErrMode=" + stdErrMode +
                ", stdOutMode=" + stdOutMode +
                ", stdOutLogFile=" + stdOutLogFile +
                ", stdErrLogFile=" + stdErrLogFile +
                ", logFileBaseName='" + logFileBaseName + '\'' +
                ", isAppendToLogs=" + isAppendToLogs +
                '}';
    }
}
