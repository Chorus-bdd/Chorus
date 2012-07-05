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
import org.junit.Test;

import java.io.*;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 26/06/12
 * Time: 08:40
 */
public abstract class AbstractInterpreterTest extends Assert {

    /**
     * Set this sys property to have the actual test output overwrite expected output in the stdout.txt stderr.txt files
     * useful if you make a minor change to the output which breaks all the tests
     * review changes before you commit, and remove the sys prop
     */
    private static final boolean overwriteStdOutAndErr = Boolean.getBoolean("chorusSelfTestsOverwriteStdOutAndErr");

    /**
     * Set this sys prop if yuo want to run the tests in process, as well as forked
     * This is very useful when running locally, if you want to use the debugger
     */
    private static final boolean runTestsInProcess = Boolean.getBoolean("chorusSelfTestsRunInProcess");


    @Test
    public void runTest() throws Exception {

        DefaultTestProperties sysProps = getTestSysProps(getFeaturePath());

        String standardOut = readToString(getStreamFromExpectedStdOutFile());
        String standardErr = readToString(getStreamFromExpectedStdErrFile());

        ChorusSelfTestResults expectedResults = new ChorusSelfTestResults(
            standardOut,
            standardErr,
            getExpectedExitCode()
        );

        if (runTestsInProcess) {
            ChorusSelfTestResults r = new InProcessRunner().runChorusInterpreter(sysProps);
            checkTestResults(r, expectedResults);
        }
        ChorusSelfTestResults r = new ForkedRunner().runChorusInterpreter(sysProps);
        checkTestResults(r, expectedResults);
    }

    protected boolean checkTestResults(ChorusSelfTestResults actualResults, ChorusSelfTestResults expectedResults) {
        //System.out.println("*" + actualResults.getStandardError() + "*");
        //System.out.println("*" + expectedResults.getStandardError() + "*");
        actualResults.preProcessForTests();
        expectedResults.preProcessForTests();

        System.out.println("\n\nSummary:\n");

        //show differences where appropriate
        boolean exitIsDiff = diffAssertEquals("exit code", expectedResults.getInterpreterExitCode(), actualResults.getInterpreterExitCode());

        boolean outIsDiff = diffAssertEquals("std out", expectedResults.getStandardOutput(), actualResults.getStandardOutput());
        overwriteOutputIfSysPropertySet(outIsDiff, overwriteStdOutAndErr, actualResults.getStandardOutput(), getStdOutFilePath());

        boolean errIsDiff =  diffAssertEquals("std err", expectedResults.getStandardError(), actualResults.getStandardError());
        overwriteOutputIfSysPropertySet(errIsDiff, overwriteStdOutAndErr, actualResults.getStandardError(), getStdErrFilePath());

        boolean success = ! ( exitIsDiff || outIsDiff || errIsDiff );

        //now actually fail the test if appropriate
        assertEquals("wrong exit code", expectedResults.getInterpreterExitCode(), actualResults.getInterpreterExitCode());
        assertEquals("wrong stdout", expectedResults.getStandardOutput(), actualResults.getStandardOutput());
        assertEquals("wrong stderr", expectedResults.getStandardError(), actualResults.getStandardError());

        try {
            Thread.sleep(25);  //prevent output being confused by junit plugin with next test
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return success;
    }

    private DefaultTestProperties getTestSysProps(String featurePath) {
        DefaultTestProperties sysProps = new DefaultTestProperties();
        sysProps.put("chorusFeaturePaths", featurePath);
        doUpdateTestProperties(sysProps);
        return sysProps;
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

    /**
     * A test can override this method to modify the sys properties being used from the default set
     */
    protected void doUpdateTestProperties(DefaultTestProperties sysProps) {
    }

    public static String readToString(InputStream is) {
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

    protected abstract int getExpectedExitCode();

    protected abstract String getFeaturePath();

    protected String getStdOutFileName() {
        return "stdout.txt";
    }

    protected String getStdErrFileName() {
        return "stderr.txt";
    }

    protected String getStdOutFilePath() {
        return getPathToFile(getClass(), getStdOutFileName());
    }

    protected String getStdErrFilePath() {
        return getPathToFile(getClass(), getStdErrFileName());
    }

    public static String getPathToFile(Class clazz, String filePath) {
        String packageName = clazz.getPackage().getName();
        return"./src/test/features/" + packageName.replaceAll("\\.", "/") + "/" + filePath;
    }

    public InputStream getStreamFromExpectedStdOutFile() {
        return getClass().getResourceAsStream(getStdOutFileName());
    }

    public InputStream getStreamFromExpectedStdErrFile() {
       return getClass().getResourceAsStream(getStdErrFileName());
    }
}
