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
package org.chorusbdd.chorus.selftest.selenium.chromedriver;

import org.chorusbdd.chorus.selftest.AbstractInterpreterTest;
import org.chorusbdd.chorus.selftest.ChorusSelfTestResults;
import org.junit.Before;

import java.io.IOException;

import static org.junit.Assume.assumeTrue;

/**
 * Created with IntelliJ IDEA.
 * User: nick
 * Date: 25/06/12
 * Time: 22:14
 */
public class TestSeleniumChromeDriver extends AbstractInterpreterTest {

    final String featurePath = "src/test/java/org/chorusbdd/chorus/selftest/selenium/chromedriver";

    final int expectedExitCode = 0;  //success

    @Before
    public void doBefore() {
        //only run if the chromedriver is on the path and executable
        int exitStatus = -1;
        Process p = null;
        try {
            p = Runtime.getRuntime().exec("chromedriver --version");
            p.waitFor();
            exitStatus = p.exitValue();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Could not execute chromedriver, not on PATH? Will not run Selenium tests");
        }
        assumeTrue(exitStatus == 0);
    }

    protected int getExpectedExitCode() {
        return expectedExitCode;
    }

    protected String getFeaturePath() {
        return featurePath;
    }

    //hook for subclass processing
    //strip file path which is system specific
    protected void processActual(ChorusSelfTestResults actualResults) {
        String output = actualResults.getStandardOutput();
        output = output.replaceAll("file.*PASSED", "REPLACED PASSED");
        actualResults.setStandardOutput(output);
        
        String err = actualResults.getStandardError();
        //some chromedriver dump this to std err!
        err = err.replaceAll("ChromeDriver was started successfully.", "");
        actualResults.setStandardError(err);
    }
}
