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

import org.chorusbdd.chorus.handlers.util.config.AbstractHandlerConfig;
import org.chorusbdd.chorus.util.ChorusException;
import org.chorusbdd.chorus.annotations.ChorusResource;
import org.chorusbdd.chorus.annotations.Destroy;
import org.chorusbdd.chorus.annotations.Handler;
import org.chorusbdd.chorus.annotations.Step;
import org.chorusbdd.chorus.core.interpreter.results.FeatureToken;
import org.chorusbdd.chorus.handlers.util.config.loader.PropertiesConfigLoader;
import org.chorusbdd.chorus.handlers.util.config.source.PropertiesFilePropertySource;
import org.chorusbdd.chorus.remoting.jmx.ChorusHandlerJmxExporter;
import org.chorusbdd.chorus.util.NamedExecutors;
import org.chorusbdd.chorus.util.assertion.ChorusAssert;
import org.chorusbdd.chorus.util.logging.ChorusLog;
import org.chorusbdd.chorus.util.logging.ChorusLogFactory;

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by: Steve Neal
 * Date: 07/11/11
 */
@Handler("Processes")
@SuppressWarnings("UnusedDeclaration")
public class ProcessesHandler {

    private static ScheduledExecutorService processexHandlerExecutor = NamedExecutors.newSingleThreadScheduledExecutor("ProcessesHandlerScheduler");

    private static ChorusLog log = ChorusLogFactory.getLog(ProcessesHandler.class);

    @ChorusResource("feature.dir")
    private File featureDir;

    @ChorusResource("feature.file")
    private File featureFile;

    @ChorusResource("feature.token")
    private FeatureToken featureToken;

    private final Map<String, ProcessHandlerProcess> processes = new HashMap<String, ProcessHandlerProcess>();

    private final Map<String, Integer> processCounters = new HashMap<String, Integer>();

    private static boolean haveCreatedLogDir = false;

    public static final String STARTING_JAVA_LOG_PREFIX = "About to run Java: ";

    private PropertiesFilePropertySource propertiesLoader;

    private Map<String, ProcessesConfig> configMap;

    private Map<String, String> aliasToConfigName = new HashMap<String,String>();

    /**
     * Starts a new Java process using properties defined in a properties file alongside the feature file
     *
     * @throws Exception
     */
    @Step(".*start a (.*) process")
    public void startJava(String process) throws Exception {
        startJavaNamed(process, nextProcessName(process));
    }

    /**
     * Starts a new Java process using properties defined in a properties file alongside the feature file
     *
     * @throws Exception
     */
    @Step(".*start an? (.*) process named ([a-zA-Z0-9-_]*).*?")
    public void startJavaNamed(String configName, String alias) throws Exception {
        aliasToConfigName.put(alias, configName);
        ProcessesConfig processesConfig = getProcessProperties(configName);

        String stdoutLogPath = null;
        String stderrLogPath = null;
        String log4jProperties = "";

        //build a process name to use when naming log files
        String processNameForLogFiles = featureFile.getName();
        if (processNameForLogFiles.endsWith(".feature")) {
            processNameForLogFiles = processNameForLogFiles.substring(0, processNameForLogFiles.length() - 8);
        }
        if (! featureToken.isConfiguration()) {
            processNameForLogFiles = String.format("%s-%s", processNameForLogFiles, alias);
        } else {
            processNameForLogFiles = String.format("%s-%s-%s", processNameForLogFiles, featureToken.getConfigurationName(), alias);
        }

        //setting true for logging will turn on logging of the process std out and err to standard out and standard err log files
        //otherwise this output will appear inline with the chorus interpreter process std out
        if (processesConfig.isLogging()) {
            //if a logs directory was provided in the config use that, or default to featureDir/logs
            String defaultPath = featureDir.getAbsolutePath() + File.separatorChar + "logs";
            String directoryPath = processesConfig.getLogDirectory() != null ? processesConfig.getLogDirectory() : defaultPath;
            stdoutLogPath = String.format("%s%s%s-out.log",
                    directoryPath,
                    File.separator,
                    processNameForLogFiles);

            stderrLogPath = String.format("%s%s%s-err.log",
                    directoryPath,
                    File.separator,
                    processNameForLogFiles);
        }

        //if there is a log4j configuration file within the local conf directory, set the log4j configuration sys property
        //to point to this
        String log4jConfigPath = featureDir.getAbsolutePath() + File.separatorChar + "conf" + File.separatorChar + "log4j.xml";
        log.trace("looking for log4j config file at " + log4jConfigPath);
        if ( new File(log4jConfigPath).exists()) {
            log.debug("Found log4j config at " + log4jConfigPath + " will set -Dlog4j.configuration when starting process");
            //makes the ${feature.dir} and ${feature.process.name} values available to the log4j config file
            log4jProperties = String.format("-Dlog4j.configuration=file:%s -Dfeature.dir=%s -Dfeature.process.name=%s",
                log4jConfigPath,
                featureDir.getAbsolutePath(),
                processNameForLogFiles
            );
        }

        String jmxSystemProperties = "";
        if (processesConfig.getJmxPort() > -1) {
            jmxSystemProperties = String.format("-Dcom.sun.management.jmxremote.ssl=false " +
                    "-Dcom.sun.management.jmxremote.authenticate=false " +
                    "-Dcom.sun.management.jmxremote.port=%s " +
                    "-D" + ChorusHandlerJmxExporter.JMX_EXPORTER_ENABLED_PROPERTY + "=true", processesConfig.getJmxPort());
        }

        String debugSystemProperties = "";
        if (processesConfig.getDebugPort() > -1) {
            debugSystemProperties = String.format("-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=%s",
                    processesConfig.getDebugPort());
        }


        //surrounding the classpath in quotes is currently breaking the classpath parsing for linux when launched via
        //Runtime.getRuntime().exec() (but it is ok from the shell)
        //I think we want to keep this in on windows, since we will more likely encounter directory names with spaces -
        //I'm worried those will break for linux although this will fix the classpath issue.
        //-so this workaround at least gets things working, but may break for folders with spaces in the name on 'nix
        boolean isWindows = System.getProperty("os.name").toLowerCase().indexOf("win") >= 0;
        String commandTxt = isWindows ?
            "%s%sbin%sjava %s %s %s %s -classpath \"%s\" %s %s" :
            "%s%sbin%sjava %s %s %s %s -classpath %s %s %s";

        //construct a command
        String command = String.format(
                commandTxt,
                processesConfig.getJre(),
                File.separatorChar,
                File.separatorChar,
                processesConfig.getJvmargs(),
                log4jProperties,
                debugSystemProperties,
                jmxSystemProperties,
                processesConfig.getClasspath(),
                processesConfig.getMainclass(),
                processesConfig.getArgs()).trim();

        log.info(STARTING_JAVA_LOG_PREFIX + command);
        startProcess(alias, command, stdoutLogPath, stderrLogPath);
    }

    @Step(".*start a process using script '(.*)'$")
    public void startScript(String script) throws Exception {
        startScript(script, nextProcessName(), false);
    }

    @Step(".*start a process using script '(.*)' named (.*)$")
    public void startScript(String script, String name) throws Exception {
        startScript(script, name, false);
    }

    @Step(".*start a process using script '(.*)' with logging$")
    public void startScriptWithLogging(String script) throws Exception {
        startScript(script, nextProcessName(), true);
    }

    @Step(".*start a process using script '(.*)' named (.*) with logging$")
    public void startScriptWithLogging(String script, String name) throws Exception {
        startScript(script, name, true);
    }

    public void startScript(String script, String name, boolean logging) throws Exception {
        String nameWithoutExtension = getScriptNameWithoutExtension(script);

        String command = String.format("%s%s%s",
                featureDir.getAbsolutePath(),
                File.separatorChar,
                script);

        String stdoutLogPath = null;
        String stderrLogPath = null;
        if (logging) {
            stdoutLogPath = String.format("%s%slogs%s%s-out.log",
                    featureDir.getAbsolutePath(),
                    File.separatorChar,
                    File.separatorChar,
                    script);
            stderrLogPath = String.format("%s%slogs%s%s-err.log",
                    featureDir.getAbsolutePath(),
                    File.separatorChar,
                    File.separatorChar,
                    script);
        }

        log.debug("About to run script: " + command);
        startProcess(name, command, stdoutLogPath, stderrLogPath);
    }

    private String getScriptNameWithoutExtension(String script) {
        return null;  //To change body of created methods use File | Settings | File Templates.
    }


    @Step(".*stop (?:the )?process (?:named )?([a-zA-Z0-9-_]+).*?")
    public void stopProcess(String alias) {
        ProcessHandlerProcess p = processes.get(alias);
        if (p != null) {
            try {
                p.destroy();
                log.debug("Stopped process: " + alias);
            } catch (Exception e) {
                log.warn("Failed to destroy process", e);
            } finally {
                processes.remove(alias);
            }
        } else {
            throw new ChorusException("There is no process named '" + alias + "' to stop");
        }
    }

    @Step(".*?(?:the process )?(?:named )?([a-zA-Z0-9-_]+) (?:is |has )(?:stopped|terminated).*?")
    public void checkProcessHasStopped(String alias) {
        ProcessHandlerProcess p = processes.get(alias);
        if ( p == null ) {
            throw new ChorusException("There is no process named '" + alias + "' to check is stopped");
        }

        ChorusAssert.assertTrue("The process " + alias + " was not stopped", p.isStopped());
    }

    @Step(".*wait for (?:up to )?(\\d+) seconds for (?:the process )?(?:named )?([a-zA-Z0-9-_]+) to (?:stop|terminate).*?")
    public void waitXSecondsForProcessToTerminate(int waitSeconds, String processAlias) {
        waitForProcessToTerminate(processAlias, waitSeconds);
    }

    @Step(".*wait for (?:the process )?(?:named )?([a-zA-Z0-9-_]*) to (?:stop|terminate).*?")
    public void waitForProcessToTerminate(String processAlias) {
        String configName = getConfigNameForAlias(processAlias);
        ProcessesConfig c = getProcessProperties(configName);
        int waitTime = c.getTerminateWaitTime();
        waitForProcessToTerminate(processAlias, waitTime);
    }

    private String getConfigNameForAlias(String processAlias) {
        String configName = aliasToConfigName.get(processAlias);
        if ( configName == null) {
            throw new ChorusException("Could not find a config name for process " + processAlias);
        }
        return configName;
    }

    private void waitForProcessToTerminate(String processName, int waitTimeSeconds) {
        ProcessHandlerProcess p = processes.get(processName);
        if ( p == null ) {
            throw new ChorusException("There is no process named '" + processName + "' to wait for");
        }

        InterruptWaitTask t = new InterruptWaitTask(Thread.currentThread(), processName);
        processexHandlerExecutor.schedule(t, waitTimeSeconds, TimeUnit.SECONDS);

        try {
            p.waitFor();
        } catch (InterruptedException e) {
            log.warn("Interrupted while waiting for process " + processName + " to terminate");
            throw new ChorusException("Process " + processName + " failed to terminate after " + waitTimeSeconds + " milliseconds");
        }
        t.setWaitFinished(); //prevent the interrupt, process finished naturally
    }

    class InterruptWaitTask implements Runnable {
       private Thread waitingThread;
       private String processName;
       private volatile boolean isWaitFinished;

       InterruptWaitTask(Thread waitingThread, String processName) {
           this.waitingThread = waitingThread;
           this.processName = processName;
       }

       public void setWaitFinished() {
           isWaitFinished = true;
       }

       public void run() {
           if ( ! isWaitFinished) {
               log.warn("The process named " + processName + " appears not to have terminated, will stop waiting");
               waitingThread.interrupt();
           }
       }
   }

    @Destroy
    //by default stop any processes which were started during a scenario
    public void destroy() {
        Set<String> processNames = new HashSet<String>(processes.keySet());
        for (String name : processNames) {
            stopProcess(name);
        }
    }


    private void validateMainClass(String mainClassName) {
        Class mainClass;
        try {
            mainClass = Class.forName(mainClassName);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Class not found: " + mainClassName);
        }
        boolean foundMainMethod = false;
        for (Method m : mainClass.getMethods()) {
            if ("main".equals(m.getName())
                    && m.getParameterTypes().length == 1
                    && String[].class.equals(m.getParameterTypes()[0])
                    && void.class.equals(m.getReturnType())
                    && Modifier.isStatic(m.getModifiers())
                    && Modifier.isPublic(m.getModifiers())) {
                foundMainMethod = true;
                break;
            }
        }
        if (!foundMainMethod) {
            throw new IllegalStateException("Cannot run this class, main method not found");
        }
    }

    private String nextProcessName() {
        return nextProcessName("process");
    }

    private synchronized String nextProcessName(String prefix) {
        Integer counter = processCounters.get(prefix);
        if (counter == null) {
            counter = 1;
        } else {
            counter++;
        }
        processCounters.put(prefix, counter);
        //for first process just use the config name with no suffix
        //this is so if we just start a single myconfig process, it will be called myconfig
        //the second will be called myconfig-2
        return counter == 1 ? prefix : String.format("%s-%d", prefix, counter);
    }

    private ProcessHandlerProcess startProcess(String name, String command, String stdoutLogPath, String stderrLogPath) throws Exception {

        //check the default processes' logs directory exists
        if (stdoutLogPath != null || stderrLogPath != null && !haveCreatedLogDir) {
            String logDirPath = String.format("%s%slogs", featureDir.getAbsolutePath(), File.separatorChar);
            new File(logDirPath).mkdir();
            haveCreatedLogDir = true;
        }

        // have to redirect/consume stdout/stderr of child process to prevent it deadlocking
        ProcessHandlerProcess child = new ProcessHandlerProcess(name, command, stdoutLogPath, stderrLogPath);
        processes.put(name, child);
        return child;
    }

    //must be lazy created since featureToken dir and file will not be set on construction
    private ProcessesConfig getProcessProperties(String processName) {
        if ( configMap == null ) {
            loadProperties();
        }
        ProcessesConfig c = configMap.get(processName);
        if (c == null) {
            throw new RuntimeException("No configuration found for process: " + processName);
        }
        return c;
    }

    private PropertiesFilePropertySource loadProperties() {
        if ( configMap == null ) {
            PropertiesConfigLoader<ProcessesConfig> l = new PropertiesConfigLoader<ProcessesConfig>(
                    new ProcessesConfigBuilder(),
                    "Processes",
                    "processes",
                    featureToken,
                    featureDir,
                    featureFile
            );
            configMap = l.loadRemotingConfigs();
        }
        return propertiesLoader;
    }
}