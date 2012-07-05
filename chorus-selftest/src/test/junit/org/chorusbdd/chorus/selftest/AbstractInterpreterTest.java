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
package org.chorusbdd.chorus.selftest;

import junit.framework.Assert;
import org.chorusbdd.chorus.Main;
import org.chorusbdd.chorus.handlers.ProcessesHandler;
import org.chorusbdd.chorus.util.ChorusOut;
import org.chorusbdd.chorus.util.config.ChorusConfig;
import org.chorusbdd.chorus.util.config.InterpreterPropertyException;
import org.chorusbdd.chorus.util.logging.ChorusLogFactory;
import org.chorusbdd.chorus.util.logging.ChorusLogProvider;
import org.chorusbdd.chorus.util.logging.StandardOutLogProvider;
import org.junit.Test;

import java.io.*;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 26/06/12
 * Time: 08:40
 */
public abstract class AbstractInterpreterTest extends Assert {

    //set this sys property to have the actual test output overwrite expected output in the stdout.txt stderr.txt files
    //useful if you make a minor change to the output which breaks all the tests
    //review changes before you commit, and remove the sys prop
    private static final boolean overwriteStdOutAndErr = Boolean.valueOf(System.getProperty("overwriteStdOutAndErr", "false"));

    protected void checkTestResults(ChorusSelfTestResults actualResults, ChorusSelfTestResults expectedResults) {
        //System.out.println("*" + actualResults.getStandardError() + "*");
        //System.out.println("*" + expectedResults.getStandardError() + "*");
        actualResults.preProcessForTests();
        expectedResults.preProcessForTests();

        System.out.println("\n\nSummary:\n");

        //show differences where appropriate
        diffAssertEquals("exit code", expectedResults.getInterpreterExitCode(), actualResults.getInterpreterExitCode());

        boolean diff = diffAssertEquals("std out", expectedResults.getStandardOutput(), actualResults.getStandardOutput());
        overwriteOutputIfSysPropertySet(diff, overwriteStdOutAndErr, actualResults.getStandardOutput(), getStdOutFilePath());

        diff =  diffAssertEquals("std err", expectedResults.getStandardError(), actualResults.getStandardError());
        overwriteOutputIfSysPropertySet(diff, overwriteStdOutAndErr, actualResults.getStandardError(), getStdErrFilePath());

        //now actually fail the test if appropriate
        assertEquals("wrong exit code", expectedResults.getInterpreterExitCode(), actualResults.getInterpreterExitCode());
        assertEquals("wrong stdout", expectedResults.getStandardOutput(), actualResults.getStandardOutput());
        assertEquals("wrong stderr", expectedResults.getStandardError(), actualResults.getStandardError());

        try {
            Thread.sleep(25);  //prevent output being confused by junit plugin with next test
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void overwriteOutputIfSysPropertySet(boolean isDifferent, boolean overwriteStdOutAndErr, String actualText, String stdOutFileName) {
        if ( isDifferent && overwriteStdOutAndErr ) {
            try {
                File f = new File(stdOutFileName);
                if ( f.canWrite()) {
                    PrintWriter w = new PrintWriter(stdOutFileName);
                    w.write(actualText);
                    w.flush();
                    w.close();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    //assert equal with more difference information
    private boolean diffAssertEquals(String s, Object expected, Object actual) {
        boolean result = false;
        if ( ! expected.equals(actual)) {
            result = true;
            System.out.println("Unexpected difference in " + s);
            System.out.println("Expected: [" + expected.toString() + "]\n");
            System.out.println("Actual: [" + actual.toString() + "]\n");
        }
        return result;
    }

    protected ChorusSelfTestResults runFeature(String featurePath) throws IOException, InterruptedException {
        DefaultTestProperties sysProps = new DefaultTestProperties();
        sysProps.put("chorusFeaturePaths", featurePath);
        doUpdateTestProperties(sysProps);
        return runChorusInterpreterInProcess(sysProps);
    }

    /**
     * A test can override this method to modify the sys properties being used from the default set
     */
    protected void doUpdateTestProperties(DefaultTestProperties sysProps) {
    }

    protected ChorusSelfTestResults runChorusInterpreterInProcess(Properties systemProperties) {

        //some may only be applied / detected statically once per JVM session, nothing we can do about that
        clearAndResetChorusSysProperties(systemProperties);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream outStream = new PrintStream(out);

        ByteArrayOutputStream err = new ByteArrayOutputStream();
        PrintStream errStream = new PrintStream(err);

        boolean success = false;
        try {
            ChorusOut.setStdOutStream(outStream);
            ChorusOut.setStdErrStream(errStream);

            try {
               Main main = new Main(new String[0]);
               success = main.run();
            } catch (Exception e) {
               System.err.println(e.getMessage());
            }

        } finally {
            ChorusOut.setStdOutStream(System.out);
            ChorusOut.setStdErrStream(System.err);
        }

        return new ChorusSelfTestResults(out.toString(), err.toString(), success ? 0 : 1);
    }

    private void clearAndResetChorusSysProperties(Properties systemProperties) {
        //clear any existing chorus sys props
        Iterator<Map.Entry<Object,Object>> i = System.getProperties().entrySet().iterator();
        while(i.hasNext()) {
            Map.Entry<Object,Object> e = i.next();
            if ( e.getKey().toString().startsWith("chorus")) {
                i.remove();
            }
        }

        //set new chorus sys props
        i = systemProperties.entrySet().iterator();
        while(i.hasNext()) {
            Map.Entry<Object,Object> e = i.next();
            System.setProperty(e.getKey().toString(), e.getValue().toString());
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

    protected static String readToString(InputStream is) {
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader r = new BufferedReader(new InputStreamReader(is));
            String s = r.readLine();
            while( s != null) {
                sb.append(s + "\n");
                s = r.readLine();
            }
        } catch (IOException e) {
            fail("Failed to read contents of file stream " + is);
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

    @Test
    public void runTest() throws Exception {

        ChorusSelfTestResults testResults = runFeature(getFeaturePath());

        String standardOut = readToString(getStreamFromExpectedStdOutFile());
        String standardErr = readToString(getStreamFromExpectedStdErrFile());

        ChorusSelfTestResults expectedResults = new ChorusSelfTestResults(
            standardOut,
            standardErr,
            getExpectedExitCode()
        );

        checkTestResults(testResults, expectedResults);
    }

    protected abstract int getExpectedExitCode();

    protected abstract String getFeaturePath();

    protected String getStdOutFileName() {
        return "stdout.txt";
    }

    protected String getStdErrFileName() {
        return "stderr.txt";
    }

    protected String getStdOutFilePath() {
        String packageName = getClass().getPackage().getName();
        return"./src/test/features/" + packageName.replaceAll("\\.", "/") + "/" + getStdOutFileName();
    }

    protected String getStdErrFilePath() {
        String packageName = getClass().getPackage().getName();
        return "./src/test/features/" + packageName.replaceAll("\\.", "/") + "/" + getStdErrFileName();
    }

    public InputStream getStreamFromExpectedStdOutFile() {
        return getClass().getResourceAsStream(getStdOutFileName());
    }

    public InputStream getStreamFromExpectedStdErrFile() {
       return getClass().getResourceAsStream(getStdErrFileName());
    }
}
