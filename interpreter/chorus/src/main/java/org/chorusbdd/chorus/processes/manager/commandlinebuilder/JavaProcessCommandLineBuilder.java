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
package org.chorusbdd.chorus.processes.manager.commandlinebuilder;

import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;
import org.chorusbdd.chorus.processes.manager.config.ProcessManagerConfig;
import org.chorusbdd.chorus.util.ChorusConstants;

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
public class JavaProcessCommandLineBuilder extends AbstractCommandLineBuilder {

    private ChorusLog log = ChorusLogFactory.getLog(JavaProcessCommandLineBuilder.class);

    private File featureDir;
    private ProcessManagerConfig processInfo;
    private String logFileBaseName;

    public JavaProcessCommandLineBuilder(File featureDir, ProcessManagerConfig processInfo, String logFileBaseName) {
        this.featureDir = featureDir;
        this.processInfo = processInfo;
        this.logFileBaseName = logFileBaseName;
    }
    
    public List<String> buildCommandLine() {
        String executableToken = getExecutableToken(processInfo);
        List<String> jvmArgs = getSpaceSeparatedTokens(processInfo.getJvmargs());
        List<String> log4jTokens = getLog4jTokens(logFileBaseName);
        List<String> debugTokens = getDebugTokens(processInfo);
        List<String> jmxTokens = getJmxTokens(processInfo);
        List<String> classPathTokens = getClasspathTokens(processInfo);
        String mainClassToken = processInfo.getMainclass();
        List<String> argsTokens = getSpaceSeparatedTokens(processInfo.getArgs());

        List<String> commandLineTokens = new ArrayList<>();
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

    private String getExecutableToken(ProcessManagerConfig processesConfig) {
        String executableTxt = "%s%sbin%sjava";
        return String.format(
                executableTxt,
                processesConfig.getJre(),
                File.separatorChar,
                File.separatorChar
        );
    }

    private List<String> getClasspathTokens(ProcessManagerConfig processesConfig) {
        List<String>  classPathTokens = new ArrayList<>();
        classPathTokens.add("-classpath");
        classPathTokens.add(processesConfig.getClasspath());
        return classPathTokens;
    }

    private List<String> getDebugTokens(ProcessManagerConfig processesConfig) {
        List<String> debugTokens = new ArrayList<>();
        if (processesConfig.getDebugPort() > -1) {
            debugTokens.add("-Xdebug");
            debugTokens.add(String.format("-Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=%s", processesConfig.getDebugPort()));
        }
        return debugTokens;
    }

    private List<String> getJmxTokens(ProcessManagerConfig processesConfig) {
        List<String> jmxTokens = new ArrayList<>();
        if (processesConfig.getRemotingPort() > -1) {
            jmxTokens.add("-Dcom.sun.management.jmxremote.ssl=false");
            jmxTokens.add("-Dcom.sun.management.jmxremote.authenticate=false");
            jmxTokens.add(String.format("-Dcom.sun.management.jmxremote.port=%s", processesConfig.getRemotingPort()));
            jmxTokens.add("-D" + ChorusConstants.JMX_EXPORTER_ENABLED_PROPERTY + "=true");
        }
        return jmxTokens;
    }

    private List<String> getLog4jTokens(String featureProcessName) {
        List<String> log4jTokens = new ArrayList<>();
        File log4jConfigFile = findLog4jConfigFile();
        if ( log4jConfigFile != null && log4jConfigFile.exists()) {
            log.debug("Found log4j config at " + log4jConfigFile.getPath() + " will set -Dlog4j.configuration when starting process");
            log4jTokens.add(String.format("-Dlog4j.configuration=file:%s", log4jConfigFile.getPath()));
            log4jTokens.add(String.format("-Dfeature.dir=%s", featureDir.getAbsolutePath()));
            log4jTokens.add(String.format("-Dfeature.process.name=%s", featureProcessName));
        }
        return log4jTokens;
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
