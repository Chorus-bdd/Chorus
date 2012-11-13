package org.chorusbdd.chorus.handlers.processes;

import org.chorusbdd.chorus.core.interpreter.results.FeatureToken;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: GA2EBBU
 * Date: 12/11/12
 * Time: 14:12
 *
 * Create log directories for features which run processes, and manage output streams to log files,
 * according to the process configuration
 *
 * This class handles creating log directory, and creating output streams for process out and err log files.
 * It also closes the streams and cleans up when each scenario is complete.
 */
public class FeatureLogFileManager {

    private Map<String, ProcessLogOutput> logOutputByProcessName = new HashMap<String, ProcessLogOutput>();

    public ProcessLogOutput getLogOutput(File featureDir, File featureFile, FeatureToken featureToken, String processAlias, ProcessesConfig processesConfig) {

       //find the feature name to use for the log files
       String featureFileBaseName = getFeatureBaseNameForLogFiles(featureFile);

       //log file base name including both feature name and process alias ( + feature config )
       String processFileNameBase;
       if (! featureToken.isConfiguration()) {
           processFileNameBase = String.format("%s-%s", featureFileBaseName, processAlias);
       } else {
           processFileNameBase = String.format("%s-%s-%s", featureFileBaseName, featureToken.getConfigurationName(), processAlias);
       }

       ProcessLogOutput l = logOutputByProcessName.get(processFileNameBase);
       if ( l == null ) {
           l = new ProcessLogOutput(featureToken, featureDir, processFileNameBase, processesConfig);
           l.initializeOutputStreams();
           logOutputByProcessName.put(processFileNameBase, l);
       }
       return l;
    }

    public void destroy() {
        for ( ProcessLogOutput l : logOutputByProcessName.values()) {
            l.closeStreams();
        }
    }

    private String getFeatureBaseNameForLogFiles(File featureFile) {
        //build a process name to use when naming log files
        String processNameForLogFiles = featureFile.getName();
        if (processNameForLogFiles.endsWith(".feature")) {
            processNameForLogFiles = processNameForLogFiles.substring(0, processNameForLogFiles.length() - 8);
        }
        return processNameForLogFiles;
    }

}
