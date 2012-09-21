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
package jmxexecutionlistener;

import org.chorusbdd.chorus.selftest.AbstractInterpreterTest;
import org.chorusbdd.chorus.selftest.DefaultTestProperties;
import org.chorusbdd.chorus.selftest.ForkedRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: nick
 * Date: 25/06/12
 * Time: 22:14
 */
public class TestJmxExecutionListener extends AbstractInterpreterTest {

    final String featurePath = "src/test/features/jmxexecutionlistener/jmxexecutionlistener.feature";

    final int expectedExitCode = 0;  //success

    protected int getExpectedExitCode() {
        return expectedExitCode;
    }

    protected String getFeaturePath() {
        return featurePath;
    }

    public void runTest() throws Exception {
        ForkedRunner f = new ForkedRunner();

        //capture the output of the remote jmx listener process we are going to start
        File expectedOutFile = new File("src/test/features/jmxexecutionlistener/expected_listenerout.txt");
        File actualOutFile = new File("src/test/features/jmxexecutionlistener/listenerout.txt");

        PrintStream outStream = null;
        try {
            outStream = new PrintStream(new FileOutputStream(actualOutFile));
            startJmxExecutionListenerProcess(f, outStream);
            //now actually run the test, with the chorusJmxListener property set
            super.runTest();
        } finally {
            try {
                outStream.flush();
                outStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        String expectedOut = readToString(new FileInputStream(expectedOutFile));
        String actualOut = readToString(new FileInputStream(actualOutFile));
        assertEquals("The remote JMX listener produced the expected output", expectedOut, actualOut);
    }

    private void startJmxExecutionListenerProcess(ForkedRunner f, PrintStream outStream) throws Exception {
        Properties sysPropsForTest = new Properties();
        sysPropsForTest.put("com.sun.management.jmxremote.authenticate", "false");
        sysPropsForTest.put("com.sun.management.jmxremote.port", "9999");
        sysPropsForTest.put("com.sun.management.jmxremote", "");
        sysPropsForTest.put("com.sun.management.jmxremote.ssl", "false");
        f.runForked(sysPropsForTest, "jmxexecutionlistener.ExecutionListenerMain", outStream, 0);
        Thread.sleep(2000);  //let the forked listener start up and create its MBeans
    }

    protected void doUpdateTestProperties(DefaultTestProperties sysProps) {
        sysProps.put("chorusJmxListener", "localhost:9999");
    }


}
