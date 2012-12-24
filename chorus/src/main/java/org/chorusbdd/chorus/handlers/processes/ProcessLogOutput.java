/**
 *  Copyright (C) 2000-2012 The Software Conservancy and Original Authors.
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
package org.chorusbdd.chorus.handlers.processes;

import org.chorusbdd.chorus.results.FeatureToken;
import org.chorusbdd.chorus.util.logging.ChorusLog;
import org.chorusbdd.chorus.util.logging.ChorusLogFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
* Created with IntelliJ IDEA.
* User: GA2EBBU
* Date: 12/11/12
* Time: 14:25
*
* Create the output directory and output log streams for a specific process
*/
class ProcessLogOutput {

    private static ChorusLog log = ChorusLogFactory.getLog(ProcessLogOutput.class);

    private OutputStream stdoutStream;
    private OutputStream stderrStream;
    private FeatureToken featureToken;
    private File featureDir;
    private String processFileNameBase;
    private ProcessesConfig processesConfig;
    private boolean logging;

    public ProcessLogOutput(FeatureToken featureToken, File featureDir, String processFileNameBase, ProcessesConfig processesConfig) {
        this.featureToken = featureToken;
        this.featureDir = featureDir;
        this.processFileNameBase = processFileNameBase;
        this.processesConfig = processesConfig;
    }

    public void initializeOutputStreams() {
        if ( processesConfig.isLogging()) {
            createLogDirAndOpenStreams();
        }
    }

    private void createLogDirAndOpenStreams() {
        //if a logs directory was provided in the config use that, or default to featureDir/logs
        String defaultPath = featureDir.getAbsolutePath() + File.separatorChar + "logs";
        String directoryPath = processesConfig.getLogDirectory() != null ?
            processesConfig.getLogDirectory() :
            defaultPath;
        File logDirectory = new File(directoryPath);

        boolean logDirExists = getOrCreateLogDirectory(logDirectory);
        if ( logDirExists ) {
            createLogStreams(logDirectory);
        }
    }

    private void createLogStreams(File logDirectory) {
        File stdoutLog = new File(logDirectory, String.format("%s-out.log", processFileNameBase));
        File stderrLog = new File(logDirectory, String.format("%s-err.log", processFileNameBase));

        logging = true;
        boolean append = processesConfig.isAppendToLogs();
        try {
            log.debug("Creating process log at " + stdoutLog.getPath());
            stdoutStream = new FileOutputStream(stdoutLog, append);
        } catch (Exception e) {
            logging = false;
            log.warn("Failed to create log file to output log file " + stdoutLog.getPath() + " will not write a log file");
        }

        try {
            log.debug("Creating process log at " + stderrLog.getPath());
            stderrStream = new FileOutputStream(stderrLog, append);
        } catch (Exception e) {
            logging = false;
            log.warn("Failed to create log file to error log file " + stderrLog.getPath() + " will not write a log file");
        }
    }

    private boolean getOrCreateLogDirectory(File logDirectory) {
        boolean logDirExists = logDirectory.exists();
        if ( ! logDirExists && processesConfig.isCreateLogDir()) {
            log.debug("Creating log directory at " + logDirectory.getPath() + " for feature " + featureToken.getName());
            logDirExists = logDirectory.mkdirs();
            if ( ! logDirExists ) {
                log.warn("Failed to create log directory at " + logDirectory.getPath() + " will not write logs");
            }
        }
        return logDirExists;
    }

    public OutputStream getStdoutStream() {
        return stdoutStream;
    }

    public OutputStream getStderrStream() {
        return stderrStream;
    }

    public boolean isLogging() {
        return logging;
    }

    public String getProcessFileNameBase() {
        return processFileNameBase;
    }

    public void closeStreams() {
        if ( stdoutStream != null) {
            try {
                stdoutStream.flush();
                stdoutStream.close();
            } catch (IOException e) {
                log.trace("Failed to flush and close stdout log file stream", e);
            }
        }

        if ( stderrStream != null) {
            try {
                stderrStream.flush();
                stderrStream.close();
            } catch (IOException e) {
                log.trace("Failed to flush and close stderr log file stream", e);
            }
        }
    }
}
