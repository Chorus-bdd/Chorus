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
package org.chorusbdd.chorus.selftest.jmxexecutionlistener;

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

    final String featurePath = "src/test/java/org/chorusbdd/chorus/selftest/jmxexecutionlistener/jmxexecutionlistener.feature";

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
        File expectedOutFile = new File("src/test/java/org/chorusbdd/chorus/selftest/jmxexecutionlistener/expected_listenerout.txt");
        File actualOutFile = new File("src/test/java/org/chorusbdd/chorus/selftest/jmxexecutionlistener/listenerout.txt");

        PrintStream outStream = null;
        try {
            outStream = new PrintStream(new FileOutputStream(actualOutFile));
            startJmxExecutionListenerProcess(f, outStream);
            
            //now actually run the test, with the chorusJmxListener property set
            super.runTest(true, false);
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
        
        if ( actualOut.contains(expectedOut))
        
        //help with debugging failure in Travis
        if ( ! actualOut.endsWith(expectedOut)) {
            System.out.println("Expected -->>>>");
            System.out.println(expectedOut);
            System.out.println("Actual -->>>>");
            System.out.println(actualOut);
        }
        
        //On Travis runs / Open JDK we are getting an extra 'Picked up _JAVA_OPTIONS:' at the start of actual
        assertTrue("The remote JMX listener produced the expected output", actualOut.endsWith(expectedOut));
    }

    private void startJmxExecutionListenerProcess(ForkedRunner f, PrintStream outStream) throws Exception {
        Properties sysPropsForTest = new Properties();
        sysPropsForTest.put("com.sun.management.jmxremote.authenticate", "false");
        sysPropsForTest.put("com.sun.management.jmxremote.port", "9999");
        sysPropsForTest.put("com.sun.management.jmxremote", "");
        sysPropsForTest.put("com.sun.management.jmxremote.ssl", "false");
        f.runForked(sysPropsForTest, "org.chorusbdd.chorus.selftest.jmxexecutionlistener.ExecutionListenerMain", outStream, 0);

        Thread.sleep(3000);  //let the forked listener start up and create its MBeans, no easy way to poll for this
    }

    protected void doUpdateTestProperties(DefaultTestProperties sysProps) {
        sysProps.put("chorusJmxListener", "localhost:9999");
    }


}
