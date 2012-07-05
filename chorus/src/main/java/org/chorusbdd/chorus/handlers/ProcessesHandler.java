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
package org.chorusbdd.chorus.handlers;

import org.chorusbdd.chorus.annotations.ChorusResource;
import org.chorusbdd.chorus.annotations.Destroy;
import org.chorusbdd.chorus.annotations.Handler;
import org.chorusbdd.chorus.annotations.Step;
import org.chorusbdd.chorus.core.interpreter.results.FeatureToken;
import org.chorusbdd.chorus.remoting.jmx.ChorusHandlerJmxExporter;
import org.chorusbdd.chorus.util.logging.ChorusLog;
import org.chorusbdd.chorus.util.logging.ChorusLogFactory;

import java.io.*;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * Created by: Steve Neal
 * Date: 07/11/11
 */
@Handler("Processes")
@SuppressWarnings("UnusedDeclaration")
public class ProcessesHandler {

    private static ChorusLog log = ChorusLogFactory.getLog(ProcessesHandler.class);

    @ChorusResource("feature.dir")
    private File featureDir;

    @ChorusResource("feature.file")
    private File featureFile;

    @ChorusResource("feature.results")
    private FeatureToken featureToken;

    private final Map<String, ChildProcess> processes = new HashMap<String, ChildProcess>();

    private final Map<String, Integer> processCounters = new HashMap<String, Integer>();

    private final Properties javaProcessProperties = new Properties();

    private static boolean haveCreatedLogDir = false;

    public static final String STARTING_JAVA_LOG_PREFIX = "About to run Java: ";

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
        String jre = getJavaProcessProperty(process, "jre", System.getProperty("java.home"));
        String jvmArgs = getJavaProcessProperty(process, "jvmargs", "");
        String logging = getJavaProcessProperty(process, "logging", "");
        String jmxPort = getJavaProcessProperty(process, "jmxport", null);
        String debugPort = getJavaProcessProperty(process, "debugport", null);
        String classPath = getJavaProcessProperty(process, "classpath", System.getProperty("java.class.path"));
        String mainClass = getJavaProcessProperty(process, "mainclass", null);
        String args = getJavaProcessProperty(process, "args", "");

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
        String logging = getJavaProcessProperty(script, "logging", "");
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


    @Step(".*stop (?:the )?process named ([a-zA-Z0-9-_]*) ?.*")
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
            throw new RuntimeException("There is no process named '" + name + "' to stop");
        }
    }

    @Destroy
    public void destroy() {
        Set<String> processNames = new HashSet<String>(processes.keySet());
        for (String name : processNames) {
            stopProcess(name);
        }
    }

    /**
     * Will return the named property's value, or a default value if the name is not found.
     *
     * @param process      to which the property belongs
     * @param property     to load
     * @param defaultValue to use if the property does not exist
     * @return
     */
    private String getJavaProcessProperty(String process, String property, String defaultValue) {
        //make sure the properties are loaded
        if (javaProcessProperties.size() == 0) {
            try {
                //figure out where the properties file is
                String propertiesFilePath = featureDir.getAbsolutePath() + File.separatorChar + "conf" + File.separatorChar + featureFile.getName();
                propertiesFilePath = propertiesFilePath.replace(".feature", "-processes.properties");

                //load the properties
                File propertiesFile = new File(propertiesFilePath);
                if (propertiesFile.exists()) {
                    FileInputStream fis = new FileInputStream(propertiesFilePath);
                    javaProcessProperties.load(fis);
                    fis.close();
                    log.debug(String.format("Loaded process configuration properties from: %s", propertiesFilePath));
                }

                //override properties for a specific run configuration (if specified)
                if (featureToken.getConfigurationName() != null) {
                    String suffix = String.format("-processes-%s.properties", featureToken.getConfigurationName());
                    String overridePropertiesFilePath = propertiesFilePath.replace("-processes.properties", suffix);
                    File overridePropertiesFile = new File(overridePropertiesFilePath);
                    if (overridePropertiesFile.exists()) {
                        FileInputStream fis = new FileInputStream(overridePropertiesFile);
                        javaProcessProperties.load(fis);
                        fis.close();
                        log.debug(String.format("Loaded overriding process configuration properties from: %s", overridePropertiesFilePath));
                    }
                }

            } catch (IOException e) {
                log.error("Failed to load process configuration properties", e);
            }
        }

        //return the appropriate value
        String value = javaProcessProperties.getProperty(process + "." + property);
        return value != null ? value : defaultValue;
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
            if (null == stdoutLogPath) {
                this.outRedirector = new ProcessRedirector(process.getInputStream(), false, System.out);
            } else {
                PrintStream out = new PrintStream(new FileOutputStream(stdoutLogPath));
                this.outRedirector = new ProcessRedirector(process.getInputStream(), true, out);
            }
            if (null == stderrLogPath) {
                this.errRedirector = new ProcessRedirector(process.getErrorStream(), false, System.err);
            } else {
                PrintStream err = new PrintStream(new FileOutputStream(stderrLogPath));
                this.errRedirector = new ProcessRedirector(process.getErrorStream(), true, err);
            }
            Thread outThread = new Thread(outRedirector, name + "-stdout");
            outThread.setDaemon(true);
            outThread.start();
            Thread errThread = new Thread(errRedirector, name + "-stderr");
            errThread.setDaemon(true);
            errThread.start();

        }

        public void destroy() {
            // destroying the process will close its stdout/stderr and so cause our ProcessRedirector daemon threads to exit
            process.destroy();
            try {
                //this ensures that all of the processes resources are cleaned up before proceeding
                process.waitFor();
            } catch (InterruptedException e) {
                log.error("Interrupted while waiting for process to terminate",e);
            }
        }
    }

    public static class ProcessRedirector implements Runnable {
        private BufferedInputStream in;
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
}