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
package org.chorusbdd.chorus.selftest;

import junit.framework.Assert;
import org.chorusbdd.chorus.output.AbstractChorusOutputWriter;
import org.chorusbdd.chorus.util.ChorusConstants;
import org.junit.Test;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 26/06/12
 * Time: 08:40
 */
public abstract class AbstractInterpreterTest extends Assert {
      
    static {
        //since the tests were written the default char length for a step line has increased, set this back
        //rather than update all the expected stdout.txt, since it is easier to read when comparing output ath this length
        setOutputWriterStepLength();

        //for spring test spring tries to load log4j so we need a configuration to avoid a warning
        System.setProperty("log4j.configuration", "org/chorusbdd/chorus/selftest/chorus-selftest-log4j.xml");
        System.setProperty(ChorusConstants.SUPPRESS_FAILURE_SUMMARY_PROPERTY, "true");
    }

    public static void setOutputWriterStepLength() {
        System.setProperty(AbstractChorusOutputWriter.OUTPUT_FORMATTER_STEP_LENGTH_CHARS, "100");
    }

    /**
     * Set this sys property to have the actual test output overwrite expected output in the stdout.txt stderr.txt files
     * useful if you make a minor change to the output which breaks all the tests
     * review changes before you commit, and remove the sys prop
     */
    private static final boolean overwriteStdOutAndErr = Boolean.getBoolean("chorusSelfTestsOverwriteStdOutAndErr");

    /**
     * Set this sys prop if you want to run the tests in process (as well as forked)
     * This is very useful when running locally, if you want to use the debugger, and also for generating coverage information
     */
    private static final boolean runTestsInProcess = Boolean.valueOf(System.getProperty("chorusSelfTestsRunInProcess", "true"));

    /**
     * Set this sys prop if you want to run the tests forked
     */
    private static final boolean runTestsForked = Boolean.valueOf(System.getProperty("chorusSelfTestsForked", "true"));
    private boolean inProcess;

    @Test
    public void runTest() throws Exception {
        runTest(
            AbstractInterpreterTest.runTestsInProcess,
            AbstractInterpreterTest.runTestsForked
        );
    }

    public void runTest(boolean runTestsInProcess, boolean runTestsForked) throws Exception {
        DefaultTestProperties sysPropsForTest = getTestSysProps(getFeaturePath());

        String standardOut = readToString(getStreamFromExpectedStdOutFile());
        String standardErr = readToString(getStreamFromExpectedStdErrFile());

        ChorusSelfTestResults expectedResults = new ChorusSelfTestResults(
            standardOut,
            standardErr,
            getExpectedExitCode()
        );

        if (runTestsInProcess) {
            inProcess = true;
            ChorusSelfTestResults r = new InProcessRunner().runChorusInterpreter(sysPropsForTest);
            checkTestResults(r, expectedResults);
        }

        if ( runTestsForked ) {
            inProcess = false;
            ChorusSelfTestResults r = new ForkedRunner().runChorusInterpreter(sysPropsForTest);
            checkTestResults(r, expectedResults);
        }

        assertTrue("Must execute tests using at least one runner", runTestsForked || runTestsInProcess);
    }

    protected boolean checkTestResults(ChorusSelfTestResults actualResults, ChorusSelfTestResults expectedResults) {
        //System.out.println("*" + actualResults.getStandardError() + "*");
        //System.out.println("*" + expectedResults.getStandardError() + "*");
        actualResults.preProcessForTests();
        expectedResults.preProcessForTests();
        
        processActual(actualResults);
        processExpected(expectedResults);

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
        assertEquals("wrong stdout", expectedResults.getStandardOutput().trim(), actualResults.getStandardOutput().trim());
        assertEquals("wrong stderr", expectedResults.getStandardError().trim(), actualResults.getStandardError().trim());

        try {
            Thread.sleep(25);  //prevent output being confused by junit plugin with next test
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return success;
    }

    protected void processExpected(ChorusSelfTestResults expectedResults) {
        processExpectedResults(expectedResults);
    }

    //hook for subclass processing
    protected void processActual(ChorusSelfTestResults actualResults) {
        processActualResults(actualResults);
    }
    
    //hook for subclass processing
    protected void processExpectedResults(ChorusSelfTestResults expectedResults) {
    }

    //hook for subclass processing
    protected void processActualResults(ChorusSelfTestResults actualResults) {
    }

    /**
     * If logging is false asynchronously logged lines may appear in std. out or error at variying points leading 
     * to indeterminate results. This lets us strip those lines
     */
    protected void removeLineFromStdOut(ChorusSelfTestResults r, String line, boolean failIfMissing) {
        String pattern = "(?m)^" + line + "\\s*$\\n";
        removeFromStdOut(r, pattern, failIfMissing);
    }

    protected void removeFromStdOut(ChorusSelfTestResults r, String pattern, boolean failIfMissing) {
        String o = r.getStandardOutput();
        String removed = removeAsynchronouslyLoggedOutput(o, pattern, failIfMissing, "standard out");
        r.setStandardOutput(removed);
    }


    /**
     * If logging is false asynchronously logged lines may appear in std. out or error at variying points leading 
     * to indeterminate results. This lets us strip those lines
     */
    protected void removeLineFromStdErr(ChorusSelfTestResults r, String line, boolean failIfMissing) {
        String pattern = "(?m)^" + line + "\\s*$\\n";
        String o = r.getStandardError();
        String removed = removeAsynchronouslyLoggedOutput(o, pattern, failIfMissing, "standard out");
        r.setStandardError(removed);
    }
    
    protected String removeAsynchronouslyLoggedOutput(String content, String pattern, boolean failIfMissing, String description) {
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(content);
        if ( m.find() ) {
            content = m.replaceAll("");
        } else {
            if (failIfMissing) {
                fail("Could not find the pattern [" + pattern + "] in " + description);
            }
        }
        return content;  
    }

    private DefaultTestProperties getTestSysProps(String featurePath) {
        DefaultTestProperties sysProps = new DefaultTestProperties();
        sysProps.put("chorusFeaturePaths", featurePath);
        sysProps.put("chorusSuiteName", getClass().getSimpleName());
        doUpdateTestProperties(sysProps);
        return sysProps;
    }

    /**
     * A test can override this method to modify the sys properties being used from the default set
     */
    protected void doUpdateTestProperties(DefaultTestProperties sysProps) {
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
            fail("Failed to read contents of stream " + is);
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
        return"./src/test/java/" + packageName.replaceAll("\\.", "/") + "/" + filePath;
    }

    public InputStream getStreamFromExpectedStdOutFile() {
        return getClass().getResourceAsStream(getStdOutFileName());
    }

    public InputStream getStreamFromExpectedStdErrFile() {
       return getClass().getResourceAsStream(getStdErrFileName());
    }
    
    //for tests which start processes which write to the interpreters standard out
    //we do not capture that output into the test results when running inline
    //since the ChorusOut streams are not used.
    
    public boolean isInProcess() {
        return inProcess;
    }
    
}
