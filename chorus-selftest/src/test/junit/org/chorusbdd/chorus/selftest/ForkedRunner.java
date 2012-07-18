package org.chorusbdd.chorus.selftest;

import org.chorusbdd.chorus.handlers.ProcessesHandler;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.Map;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: GA2EBBU
 * Date: 05/07/12
 * Time: 11:44
 *
 * Fork an interpreter process and capture the std out, std err and exit code into ChorusSelfTestResults
 */
public class ForkedRunner implements ChorusSelfTestRunner {

    public ChorusSelfTestResults runChorusInterpreter(Properties systemProperties) throws Exception {
        String jre = System.getProperty("java.home");

        systemProperties.put("log4j.configuration", "org/chorusbdd/chorus/selftest/log4j-forked.xml");

        //See notes also in ProcessHandler
        //surrounding the classpath in quotes is currently breaking the classpath parsing for linux when launched via
        //Runtime.getRuntime().exec() (but it is ok from the shell)
        //I think we want to keep this in on windows, since we will more likely encounter directory names with spaces -
        //I'm worried those will break for linux although this will fix the classpath issue.
        //-so this workaround at least gets things working, but may break for folders with spaces in the name on 'nix
        boolean isWindows = System.getProperty("os.name").toLowerCase().indexOf("win") >= 0;
        String commandTxt = isWindows ?
              "%s%sbin%sjava %s -classpath \"%s\" %s %s" :
              "%s%sbin%sjava %s -classpath %s %s %s";

        String classPath = System.getProperty("java.class.path");

        String switches = "";

        StringBuilder jvmArgs = getJvmArgs(systemProperties);

        //construct a command
        String command = String.format(
              commandTxt,
              jre,
              File.separatorChar,
              File.separatorChar,
              jvmArgs,
              classPath,
              "org.chorusbdd.chorus.Main",
              switches).trim();

        System.out.println("About to run Java: " + command);

        ByteArrayOutputStream interpreterOut = new ByteArrayOutputStream();
        ByteArrayOutputStream interpreterErr = new ByteArrayOutputStream();

        Process process = Runtime.getRuntime().exec(command);
        ProcessesHandler.ProcessRedirector outRedirector = new ProcessesHandler.ProcessRedirector(process.getInputStream(), false, new PrintStream(interpreterOut), System.out);  //dumping both to out
        ProcessesHandler.ProcessRedirector errRedirector = new ProcessesHandler.ProcessRedirector(process.getErrorStream(), false, new PrintStream(interpreterErr), System.out);  //tend to get more consistent ordering

        Thread outThread = new Thread(outRedirector, "interpreter-stdout");
        outThread.setDaemon(true);
        outThread.start();
        Thread errThread = new Thread(errRedirector, "interpreter-stderr");
        errThread.setDaemon(true);
        errThread.start();

        int result = process.waitFor();

        //wait for logging to be flushed to output byte array asynchronously
        errThread.join(10000);
        outThread.join(10000);

        return new ChorusSelfTestResults(
          interpreterOut.toString("UTF-8"),
          interpreterErr.toString("UTF-8"),
          result
        );
    }


    private StringBuilder getJvmArgs(Properties systemProperties) {
        StringBuilder jvmArgs = new StringBuilder();
        for ( Map.Entry<Object,Object> property : systemProperties.entrySet()) {
            jvmArgs.append("-D");
            jvmArgs.append(property.getKey());
            jvmArgs.append("=");
            jvmArgs.append(property.getValue());
            jvmArgs.append(" ");
        }
        return jvmArgs;
    }
}
