package simplefeature;

import junit.framework.Assert;
import org.chorusbdd.chorus.handlers.ProcessesHandler;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Map;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 26/06/12
 * Time: 08:40
 */
public class AbstractInterpreterTest extends Assert {

    protected void checkTestResults(ChorusSelfTestResults testResults, ChorusSelfTestResults expectedResults) {
        //System.out.println("*" + testResults.getStandardError() + "*");
        //System.out.println("*" + expectedResults.getStandardError() + "*");
        testResults.preProcessForTests();
        diffAssertEquals("exit code", expectedResults.getInterpreterExitCode(), testResults.getInterpreterExitCode());
        diffAssertEquals("std out", expectedResults.getStandardOutput(), testResults.getStandardOutput());
        diffAssertEquals("std err", expectedResults.getStandardError(), testResults.getStandardError());
    }

    //assert equal with more difference information
    private void diffAssertEquals(String s, Object expected, Object actual) {
        if ( ! expected.equals(actual)) {
            System.err.println("Unexpected difference in " + s);
            System.err.println("Expected: [" + expected.toString() + "]\n\n");
            System.err.println("Actual: [" + actual.toString() + "]\n\n");
            assertEquals(s, expected, actual);
        }
    }

    protected ChorusSelfTestResults runChorusInterpreter(Properties systemProperties) throws IOException, InterruptedException {
        String jre = System.getProperty("java.home");

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

        String args = "";

        StringBuilder jvmArgs = new StringBuilder();
        for ( Map.Entry<Object,Object> property : systemProperties.entrySet()) {
            jvmArgs.append("-D");
            jvmArgs.append(property.getKey());
            jvmArgs.append("=");
            jvmArgs.append(property.getValue());
            jvmArgs.append(" ");
        }

        //construct a command
        String command = String.format(
                commandTxt,
                jre,
                File.separatorChar,
                File.separatorChar,
                jvmArgs,
                classPath,
                "org.chorusbdd.chorus.Main",
                args).trim();

        System.out.println("About to run Java: " + command);

        ByteArrayOutputStream interpreterOut = new ByteArrayOutputStream();
        ByteArrayOutputStream interpreterErr = new ByteArrayOutputStream();

        Process process = Runtime.getRuntime().exec(command);
        ProcessesHandler.ProcessRedirector outRedirector = new ProcessesHandler.ProcessRedirector(process.getInputStream(), new PrintStream(interpreterOut), false);
        ProcessesHandler.ProcessRedirector errRedirector = new ProcessesHandler.ProcessRedirector(process.getErrorStream(), new PrintStream(interpreterErr), false);

        Thread outThread = new Thread(outRedirector, "interpreter-stdout");
        outThread.setDaemon(true);
        outThread.start();
        Thread errThread = new Thread(errRedirector, "interpreter-stderr");
        errThread.setDaemon(true);
        errThread.start();

        int result = process.waitFor();

        //wait for end of logging to be written to output buffers
        Thread.sleep(250);

        return new ChorusSelfTestResults(
            interpreterOut.toString("UTF-8"),
            interpreterErr.toString("UTF-8"),
            result
        );
    }

    /**
     * @return a String path replacing \ with the platform specific separator (will be identical, for Win)
     */
    protected String getPlatformPath(String s) {
        return s.replace("\\", System.getProperty("file.separator"));
    }
}
