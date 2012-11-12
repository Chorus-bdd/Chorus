package org.chorusbdd.chorus.handlers.processes;

import org.chorusbdd.chorus.core.interpreter.results.FeatureToken;
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
        if ( ! logDirExists) {
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
