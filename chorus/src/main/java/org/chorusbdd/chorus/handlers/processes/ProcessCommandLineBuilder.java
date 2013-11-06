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

import org.chorusbdd.chorus.remoting.jmx.ChorusHandlerJmxExporter;
import org.chorusbdd.chorus.util.logging.ChorusLog;
import org.chorusbdd.chorus.util.logging.ChorusLogFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: nick
 * Date: 21/07/13
 * Time: 12:29
 * To change this template use File | Settings | File Templates.
 */
public class ProcessCommandLineBuilder {

    private static ChorusLog log = ChorusLogFactory.getLog(ProcessesHandler.class);

    private File featureDir;
    private ProcessesConfig processesConfig;
    private String logFileBaseName;

    public ProcessCommandLineBuilder(File featureDir, ProcessesConfig processesConfig, String logFileBaseName) {
        this.featureDir = featureDir;
        this.processesConfig = processesConfig;
        this.logFileBaseName = logFileBaseName;
    }
    
    public List<String> buildCommandLine() {
        String executableToken = getExecutableTokens(processesConfig);
        List<String> jvmArgs = getSpaceSeparatedTokens(processesConfig.getJvmargs());
        List<String> log4jTokens = getLog4jTokens(logFileBaseName);
        List<String> debugTokens = getDebugTokens(processesConfig);
        List<String> jmxTokens = getJmxTokens(processesConfig);
        List<String> classPathTokens = getClasspathTokens(processesConfig);
        String mainClassToken = processesConfig.getMainclass();
        List<String> argsTokens = getSpaceSeparatedTokens(processesConfig.getArgs());

        List<String> commandLineTokens = new ArrayList<String>();
        commandLineTokens.add(executableToken);
        commandLineTokens.addAll(jvmArgs);
        commandLineTokens.addAll(log4jTokens);
        commandLineTokens.addAll(debugTokens);
        commandLineTokens.addAll(jmxTokens);
        commandLineTokens.addAll(classPathTokens);
        commandLineTokens.add(mainClassToken);
        commandLineTokens.addAll(argsTokens);
        return commandLineTokens;
    }

    private String getExecutableTokens(ProcessesConfig processesConfig) {
        String executableTxt = "%s%sbin%sjava";
        return String.format(
                executableTxt,
                processesConfig.getJre(),
                File.separatorChar,
                File.separatorChar
        );
    }

    private List<String> getClasspathTokens(ProcessesConfig processesConfig) {
        //surrounding the classpath in quotes is currently breaking the classpath parsing for linux when launched via
        //Runtime.getRuntime().exec() (but it is ok from the shell)
        //I think we want to keep this in on windows, since we will more likely encounter directory names with spaces -
        //I'm worried those will break for linux although this will fix the classpath issue.
        //-so this workaround at least gets things working, but may break for folders with spaces in the name on 'nix
        boolean isWindows = System.getProperty("os.name").toLowerCase().indexOf("win") >= 0;
        List<String>  classPathTokens = new ArrayList<String>();
        classPathTokens.add("-classpath");
        classPathTokens.add(isWindows ? "\"" + processesConfig.getClasspath() + "\"" : processesConfig.getClasspath());
        return classPathTokens;
    }

    private List<String> getDebugTokens(ProcessesConfig processesConfig) {
        List<String> debugTokens = new ArrayList<String>();
        if (processesConfig.getDebugPort() > -1) {
            debugTokens.add("-Xdebug");
            debugTokens.add(String.format("-Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=%s", processesConfig.getDebugPort()));
        }
        return debugTokens;
    }

    private List<String> getJmxTokens(ProcessesConfig processesConfig) {
        List<String> jmxTokens = new ArrayList<String>();
        if (processesConfig.getJmxPort() > -1) {
            jmxTokens.add("-Dcom.sun.management.jmxremote.ssl=false");
            jmxTokens.add("-Dcom.sun.management.jmxremote.authenticate=false");
            jmxTokens.add(String.format("-Dcom.sun.management.jmxremote.port=%s", processesConfig.getJmxPort()));
            jmxTokens.add("-D" + ChorusHandlerJmxExporter.JMX_EXPORTER_ENABLED_PROPERTY + "=true");
        }
        return jmxTokens;
    }

    private List<String> getLog4jTokens(String featureProcessName) {
        List<String> log4jTokens = new ArrayList<String>();
        File log4jConfigFile = findLog4jConfigFile();
        if ( log4jConfigFile != null && log4jConfigFile.exists()) {
            log.debug("Found log4j config at " + log4jConfigFile.getPath() + " will set -Dlog4j.configuration when starting process");
            log4jTokens.add(String.format("-Dlog4j.configuration=file:%s", log4jConfigFile.getPath()));
            log4jTokens.add(String.format("-Dfeature.dir=%s", featureDir.getAbsolutePath()));
            log4jTokens.add(String.format("-Dfeature.process.name=%s", featureProcessName));
        }
        return log4jTokens;
    }

    private List<String> getSpaceSeparatedTokens(String spaceSeparated) {
        List<String> tokens = new ArrayList<String>();
        String[] j = spaceSeparated.split(" ");
        for ( String s : j ) {
            if ( s.trim().length() > 0) {
                tokens.add(s);
            }
        }
        return tokens;
    }

    private File findLog4jConfigFile() {
        String log4jConfigPath = featureDir.getAbsolutePath() + File.separatorChar + "conf" + File.separatorChar + "log4j.xml";
        log.trace("looking for log4j config file at " + log4jConfigPath);
        File f = new File(log4jConfigPath);
        if ( ! f.exists()) {
            log4jConfigPath = featureDir.getAbsolutePath() + File.separatorChar + "log4j.xml";
            log.trace("looking for log4j config file at " + log4jConfigPath);
            f = new File(log4jConfigPath);
        }

        if ( f.exists()) {
            log.trace("Found log4j config at " + f.getPath());
        }
        return f.exists() ? f : null;
    }
}
