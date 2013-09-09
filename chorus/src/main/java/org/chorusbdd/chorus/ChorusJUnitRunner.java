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
package org.chorusbdd.chorus;

import junit.framework.*;
import org.chorusbdd.chorus.core.interpreter.scanner.FilePathScanner;
import org.chorusbdd.chorus.results.EndState;
import org.chorusbdd.chorus.results.ExecutionToken;
import org.chorusbdd.chorus.results.FeatureToken;
import org.chorusbdd.chorus.util.config.ChorusConfigProperty;
import org.chorusbdd.chorus.util.config.ConfigProperties;
import org.chorusbdd.chorus.util.config.InterpreterPropertyException;
import org.junit.runner.RunWith;
import org.junit.runners.AllTests;

import java.io.File;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: GA2EBBU
 * Date: 11/06/12
 * Time: 15:48
 *
 * Create a JUnit test suite from identified features, and
 * run each as a separate JUnit test
 *
 * This enables Chorus tests to leverage JUnit support built into
 * IDE / continuous integration frameworks
 */
@RunWith(AllTests.class)
public class ChorusJUnitRunner {

    public static TestSuite suite() {
        return suite(new String[0]);
    }

    public static TestSuite suite(String params) {
        return suite(params.split(" "));
    }

    public static TestSuite suite(String[] args) {
        TestSuite suite = null;
        try {
            final Chorus chorus = new Chorus(args);

            final ExecutionToken executionToken = chorus.startTests();
            
            final List<FeatureToken> featureTokens = chorus.getFeatureList(executionToken);            

            suite = new TestSuite() {
                public void run(TestResult result) {
                    super.run(result);
                    chorus.endTests(executionToken, featureTokens);
                }
            };

            
            for (FeatureToken f : featureTokens) {
                suite.addTest(new ChorusTest(chorus, executionToken, f));
            }

            suite.setName(chorus.getSuiteName());
            return suite;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return suite;
    }

    private static class ChorusTest extends TestCase {
        private Chorus chorus;
        private ExecutionToken executionToken;
        private FeatureToken featureToken;

        public ChorusTest(Chorus chorus, ExecutionToken executionToken, FeatureToken featureToken) {
            this.chorus = chorus;
            this.executionToken = executionToken;
            this.featureToken = featureToken;
        }

        public int countTestCases() {
            return 1;
        }

        public void run(TestResult testResult) {
            testResult.startTest(this);
            try {
                chorus.processFeatures(executionToken, Collections.singletonList(featureToken));

                boolean success = ChorusSuite.isJUnitPass(featureToken);
                if ( ! success) {
                    testResult.addFailure(this, new AssertionFailedError("Chorus test failed"));
                }
            } catch (Throwable t) {
                t.printStackTrace();
                testResult.addFailure(this, new AssertionFailedError("Chorus test failed with exception"));                
                testResult.addError(this, t);
            }

//            this sleep is purely superficial - so that asynchronous log4j logging output
//            ends up under the right test in the junit viewer
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            testResult.endTest(this);
        }

        public String getName() {
            return featureToken.getNameWithConfiguration();
        }
    }
}
