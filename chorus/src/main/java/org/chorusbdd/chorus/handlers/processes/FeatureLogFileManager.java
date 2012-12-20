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
