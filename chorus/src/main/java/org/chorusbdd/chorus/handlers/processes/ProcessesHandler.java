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

import org.chorusbdd.chorus.ChorusException;
import org.chorusbdd.chorus.annotations.ChorusResource;
import org.chorusbdd.chorus.annotations.Destroy;
import org.chorusbdd.chorus.annotations.Handler;
import org.chorusbdd.chorus.annotations.Step;
import org.chorusbdd.chorus.core.interpreter.results.FeatureToken;
import org.chorusbdd.chorus.handlers.util.HandlerPropertiesLoader;
import org.chorusbdd.chorus.remoting.jmx.ChorusHandlerJmxExporter;
import org.chorusbdd.chorus.util.ChorusOut;
import org.chorusbdd.chorus.util.NamedExecutors;
import org.chorusbdd.chorus.util.assertion.ChorusAssert;
import org.chorusbdd.chorus.util.logging.ChorusLog;
import org.chorusbdd.chorus.util.logging.ChorusLogFactory;

import java.io.*;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.Executors;
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

    @ChorusResource("feature.results")
    private FeatureToken featureToken;

    private final Map<String, ChildProcess> processes = new HashMap<String, ChildProcess>();

    private final Map<String, Integer> processCounters = new HashMap<String, Integer>();

    private static boolean haveCreatedLogDir = false;

    public static final String STARTING_JAVA_LOG_PREFIX = "About to run Java: ";

    private HandlerPropertiesLoader propertiesLoader;

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
    @Step(".*start an? (.*) process named ([a-zA-Z0-9-_]*) ?.*")
    public void startJavaNamed(String process, String alias) throws Exception {
        String jre = getOrCreatePropertiesLoader().readProperty(process, "jre", System.getProperty("java.home"));
        String jvmArgs = getOrCreatePropertiesLoader().readProperty(process, "jvmargs", "");
        String logging = getOrCreatePropertiesLoader().readProperty(process, "logging", "");
        String jmxPort = getOrCreatePropertiesLoader().readProperty(process, "jmxport", null);
        String debugPort = getOrCreatePropertiesLoader().readProperty(process, "debugport", null);
        String classPath = getOrCreatePropertiesLoader().readProperty(process, "classpath", System.getProperty("java.class.path"));
        String mainClass = getOrCreatePropertiesLoader().readProperty(process, "mainclass", null);
        String args = getOrCreatePropertiesLoader().readProperty(process, "args", "");

        if (mainClass == null) {
            throw new RuntimeException("No configuration found for process: " + process);
        }

        String stdoutLogPath = null;
        String stderrLogPath = null;
        String loggingProperties = "";
        if ("true".equals(logging)) {

            //build a process name for the log4j file to use when naming log files
            String featureProcessName = featureFile.getName();
            if (featureProcessName.endsWith(".feature")) {
                featureProcessName = featureProcessName.substring(0, featureProcessName.length() - 8);
            }
            if (featureToken.getConfigurationName() == null) {
                featureProcessName = String.format("%s-%s", featureProcessName, alias);
            } else {
                featureProcessName = String.format("%s-%s-%s", featureProcessName, featureToken.getConfigurationName(), alias);
            }

            //makes the ${feature.dir} and ${feature.process.name} values available to the log4j config file
            loggingProperties = String.format("-Dlog4j.configuration=file:%s%sconf%slog4j.xml -Dfeature.dir=%s -Dfeature.process.name=%s",
                    featureDir.getAbsolutePath(),
                    File.separatorChar,
                    File.separatorChar,
                    featureDir.getAbsolutePath(),
                    featureProcessName);

            stdoutLogPath = String.format("%s%slogs%s%s-out.log",
                    featureDir.getAbsolutePath(),
                    File.separatorChar,
                    File.separatorChar,
                    featureProcessName);

            stderrLogPath = String.format("%s%slogs%s%s-err.log",
                    featureDir.getAbsolutePath(),
                    File.separatorChar,
                    File.separatorChar,
                    featureProcessName);
        }

        String jmxSystemProperties = "";
        if (jmxPort != null) {
            jmxSystemProperties = String.format("-Dcom.sun.management.jmxremote.ssl=false " +
                    "-Dcom.sun.management.jmxremote.authenticate=false " +
                    "-Dcom.sun.management.jmxremote.port=%s " +
                    "-D" + ChorusHandlerJmxExporter.JMX_EXPORTER_ENABLED_PROPERTY + "=true", jmxPort);
        }

        String debugSystemProperties = "";
        if (debugPort != null) {
            debugSystemProperties = String.format("-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=%s",
                    debugPort);
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
                jre,
                File.separatorChar,
                File.separatorChar,
                jvmArgs,
                loggingProperties,
                debugSystemProperties,
                jmxSystemProperties,
                classPath,
                mainClass,
                args).trim();

        log.info(STARTING_JAVA_LOG_PREFIX + command);
        startProcess(alias, command, stdoutLogPath, stderrLogPath);
    }

    @Step(".*start a process using script '(.*)'$")
    public void startScript(String script) throws Exception {
        startScript(script, nextProcessName());
    }

    @Step(".*start a process using script '(.*)' named (.*)$")
    public void startScript(String script, String name) throws Exception {
        String command = String.format("%s%s%s",
                featureDir.getAbsolutePath(),
                File.separatorChar,
                script);

        String stdoutLogPath = null;
        String stderrLogPath = null;
        String logging = getOrCreatePropertiesLoader().readProperty(script, "logging", "");
        if (logging != null && "true".equals(logging)) {
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


    @Step(".*stop (?:the )?process (?:named )?([a-zA-Z0-9-_]*) ?.*")
    public void stopProcess(String name) {
        ChildProcess p = processes.get(name);
        if (p != null) {
            try {
                p.destroy();
                log.debug("Stopped process: " + name);
            } catch (Exception e) {
                log.warn("Failed to destroy process", e);
            } finally {
                processes.remove(name);
            }
        } else {
            throw new ChorusException("There is no process named '" + name + "' to stop");
        }
    }

    @Step(".*the process named ([a-zA-Z0-9-_]*) (?:is|has) (?:stopped|terminated).*")
    public void checkProcessHasStopped(String processName) {
        ChildProcess p = processes.get(processName);
        if ( p == null ) {
            throw new ChorusException("There is no process named '" + processName + "' to check is stopped");
        }

        ChorusAssert.assertTrue("The process " + processName + " was not stopped", p.isStopped());
    }

    @Step(".*wait for the process named ([a-zA-Z0-9-_]*) to (?:stop|terminate).*")
    public void waitForProcessToTerminate(String processName) {
        ChildProcess p = processes.get(processName);
        if ( p == null ) {
            throw new ChorusException("There is no process named '" + processName + "' to wait for");
        }

        long waitTime = 30000;
        String wait = getOrCreatePropertiesLoader().readProperty(processName, "waitforterminate", "30000");
        try {
            waitTime = Long.parseLong(wait);
        } catch (NumberFormatException nfe) {
            log.warn("waitforterminate process property must be an integer, will default to 30000");
        }

        InterruptWaitTask t = new InterruptWaitTask(Thread.currentThread(), processName);
        processexHandlerExecutor.schedule(t, waitTime, TimeUnit.MILLISECONDS);

        try {
            p.waitFor();
        } catch (InterruptedException e) {
            log.warn("Interrupted while waiting for process " + processName + " to terminate");
            throw new ChorusException("Process " + processName + " failed to terminate after " + waitTime + " milliseconds");
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
        return String.format("%s-%d", prefix, counter);
    }

    private ChildProcess startProcess(String name, String command, String stdoutLogPath, String stderrLogPath) throws Exception {

        //check the default processes' logs directory exists
        if (stdoutLogPath != null || stderrLogPath != null && !haveCreatedLogDir) {
            String logDirPath = String.format("%s%slogs", featureDir.getAbsolutePath(), File.separatorChar);
            new File(logDirPath).mkdir();
            haveCreatedLogDir = true;
        }

        // have to redirect/consume stdout/stderr of child process to prevent it deadlocking
        ChildProcess child = new ChildProcess(name, command, stdoutLogPath, stderrLogPath);
        processes.put(name, child);
        return child;
    }

    private class ChildProcess {
        private Process process;
        private ProcessRedirector outRedirector;
        private ProcessRedirector errRedirector;

        public ChildProcess(String name, String command, String stdoutLogPath, String stderrLogPath) throws Exception {
            this.process = Runtime.getRuntime().exec(command);

            InputStream processOutStream = process.getInputStream();
            InputStream processErrorStream = process.getErrorStream();
            log.debug("Started process " + process + " with out stream " + processOutStream + " and err stream " + processErrorStream);

            if (null == stdoutLogPath) {
                this.outRedirector = new ProcessRedirector(processOutStream, false, ChorusOut.out);
            } else {
                PrintStream out = new PrintStream(new FileOutputStream(stdoutLogPath), true);
                this.outRedirector = new ProcessRedirector(processOutStream, true, out);
            }
            if (null == stderrLogPath) {
                this.errRedirector = new ProcessRedirector(processErrorStream, false, ChorusOut.err);
            } else {
                PrintStream err = new PrintStream(new FileOutputStream(stderrLogPath), true);
                this.errRedirector = new ProcessRedirector(processErrorStream, true, err);
            }
            
            Thread outThread = new Thread(outRedirector, name + "-stdout");
            outThread.setDaemon(true);
            outThread.start();
            Thread errThread = new Thread(errRedirector, name + "-stderr");
            errThread.setDaemon(true);
            errThread.start();
        }

        public boolean isStopped() {
            boolean stopped = true;
            try {
                process.exitValue();
            } catch (IllegalThreadStateException e) {
                stopped = false;
            }
            return stopped;
        }

        public void destroy() {
            // destroying the process will close its stdout/stderr and so cause our ProcessRedirector daemon threads to exit
            log.debug("Destroying process " + process);
            process.destroy();
            try {
                //this ensures that all of the processes resources are cleaned up before proceeding
                process.waitFor();
            } catch (InterruptedException e) {
                log.error("Interrupted while waiting for process to terminate",e);
            }
        }

        public void waitFor() throws InterruptedException {
            process.waitFor();
        }


    }

    public static class ProcessRedirector implements Runnable {
        private InputStream in;
        private PrintStream[] out;
        private boolean closeOnExit;

        public ProcessRedirector(InputStream in, boolean closeOnExit, PrintStream... out) {
            this.closeOnExit = closeOnExit;
            this.in = new BufferedInputStream(in);
            this.out = out;
        }

        public void run() {
            try {
                byte[] buf = new byte[1024];
                int x = 0;
                try {
                    while ((x = in.read(buf)) != -1) {
                        for ( PrintStream s : out) {
                            s.write(buf, 0, x);
                        }
                    }
                } catch (IOException e) {
                    //e.printStackTrace();
                    //tends to be verbose on Linux when process terminates
                }
            } finally {
                for ( PrintStream s : out) {
                    s.flush();
                    if ( closeOnExit ) {
                        s.close();
                    }
                }
            }
        }
    }
    
    //must be lazy created since featureToken dir and file will not be set on construction
    private HandlerPropertiesLoader getOrCreatePropertiesLoader() {
        if ( propertiesLoader == null ) {
            propertiesLoader = new HandlerPropertiesLoader("Processes", "-processes", featureToken, featureDir, featureFile);
        }
        return propertiesLoader;
    }
}