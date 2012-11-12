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
 * For the lifetime of each feature, we need to maintain output streams to each process log file by log file name
 * - if a log file is reused by multiple secnario in the feature, it should not get overwritten when a new scenario starts
 *
 * This class handles creating log directory, and creating output streams for process out and err log files.
 * It also closes the streams when the feature is complete.
 *
 */
public class FeatureLogFileManager {

    private Map<String, ProcessLogOutput> logOutputByProcessName = new HashMap<String, ProcessLogOutput>();

    public ProcessLogOutput getLogOutput(String featureFileBaseName, String processAlias, File featureDir, FeatureToken featureToken, ProcessesConfig processesConfig) {
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

}
