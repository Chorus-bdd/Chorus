package org.chorusbdd.chorus.selftest;

import junit.framework.Assert;
import org.chorusbdd.chorus.handlers.ProcessesHandler;

import java.io.*;
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
        expectedResults.preProcessForTests();

        System.out.println("\n\nSummary:\n");

        //show differences where appropriate
        diffAssertEquals("exit code", expectedResults.getInterpreterExitCode(), testResults.getInterpreterExitCode());
        diffAssertEquals("std out", expectedResults.getStandardOutput(), testResults.getStandardOutput());
        diffAssertEquals("std err", expectedResults.getStandardError(), testResults.getStandardError());

        //now actually fail the test if appropriate
        assertEquals(expectedResults.getInterpreterExitCode(), testResults.getInterpreterExitCode());
        assertEquals(expectedResults.getStandardOutput(), testResults.getStandardOutput());
        assertEquals(expectedResults.getStandardError(), testResults.getStandardError());

        try {
            Thread.sleep(25);  //prevent output being confused by junit plugin with next test
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //assert equal with more difference information
    private void diffAssertEquals(String s, Object expected, Object actual) {
        if ( ! expected.equals(actual)) {
            System.out.println("Unexpected difference in " + s);
            System.out.println("Expected: [" + expected.toString() + "]\n");
            System.out.println("Actual: [" + actual.toString() + "]\n");
        }
    }

    protected ChorusSelfTestResults runFeature(String featurePath) throws IOException, InterruptedException {
        DefaultTestProperties sysProps = new DefaultTestProperties();
        sysProps.put("chorusFeaturePaths", featurePath);
        doUpdateTestProperties(sysProps);
        return runChorusInterpreter(sysProps);
    }

    /**
     * A test can override this method to modify the sys properties being used from the default set
     */
    protected void doUpdateTestProperties(DefaultTestProperties sysProps) {
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

    protected static String readToString(Class clazz, String relativePath) {
        InputStream is = null;
        StringBuilder sb = new StringBuilder();
        try {
            is = clazz.getResourceAsStream(relativePath);
            BufferedReader r = new BufferedReader(new InputStreamReader(is));
            String s = r.readLine();
            while( s != null) {
                sb.append(s + "\n");
                s = r.readLine();
            }
        } catch (IOException e) {
            fail("Failed to read contents of file at relative path " + relativePath);
            e.printStackTrace();
        }  finally {
            try {
                if ( is != null) {
                    is.close();
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        return sb.toString();
    }

}
