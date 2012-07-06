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
package org.chorusbdd.chorus.util;

import junit.framework.*;
import org.chorusbdd.chorus.ConfigMutator;
import org.chorusbdd.chorus.Main;
import org.chorusbdd.chorus.core.interpreter.results.ExecutionToken;
import org.chorusbdd.chorus.core.interpreter.results.FeatureToken;
import org.chorusbdd.chorus.core.interpreter.scanner.FeatureScanner;
import org.chorusbdd.chorus.util.config.ChorusConfigProperty;
import org.chorusbdd.chorus.util.config.ConfigReader;
import org.chorusbdd.chorus.util.config.InterpreterPropertyException;
import org.junit.runner.RunWith;
import org.junit.runners.AllTests;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    private static Main chorusMain;

    static {
        try {
            chorusMain = new Main(new String[]{});
        } catch (InterpreterPropertyException e) {
            e.printStackTrace();
        }
    }

    //executionToken for the test suite
    private static ExecutionToken executionToken;

    //all tokens executed during the suite
    private static List<FeatureToken> featureTokens = new ArrayList<FeatureToken>();

    public static TestSuite suite() throws InterpreterPropertyException {
        TestSuite suite = new TestSuite() {
            public void run(TestResult result) {
                executionToken = chorusMain.startTests();
                super.run(result);
                chorusMain.endTests(executionToken, featureTokens);
            }
        };

        for (Test test : findAllTestCasesRuntime(chorusMain)) {
            suite.addTest(test);
        }

        suite.setName(chorusMain.getSuiteName());
        return suite;
    }

    private static Test[] findAllTestCasesRuntime(Main chorusMain) {
        //scan for all feature files specified in base config
        FeatureScanner featureScanner = new FeatureScanner();
        List<File> featureFiles = featureScanner.getFeatureFiles(chorusMain.getFeatureFilePaths());

        //generate a junit test to execute the interpreter for each feature file
        Test[] tests = new Test[featureFiles.size()];
        int index=0;
        for ( File f : featureFiles) {
            tests[index++] = new ChorusTest(f);
        }
        return tests;
    }

    private static class ChorusTest extends TestCase {
        private File featureFile;

        public ChorusTest(File featureFile) {
            //To change body of created methods use File | Settings | File Templates.
            this.featureFile = featureFile;
        }

        public int countTestCases() {
            return 1;
        }

        public void run(TestResult testResult) {
            testResult.startTest(this);
            try {
                //run using the base config filtered through a mutator which replaces the
                //feature file paths property with the specific path for this feature file
                List<FeatureToken> tokens = chorusMain.run(
                    executionToken,
                    new SingleFeatureConfigMutator()
                );

                boolean success = true;
                for ( FeatureToken f : tokens) {
                    success &= f.isPassed() && f.isFullyImplemented();
                }
                if ( ! success) {
                    testResult.addFailure(this, new AssertionFailedError("Chorus test failed"));
                }

                //add token for the feature to the list of all tokens for the suite
                featureTokens.addAll(tokens);

            } catch (Throwable t) {
                testResult.addError(this, t);
            }

            //this sleep is purely superficial - so that asynchronous log4j logging output
            //ends up under the right test in the junit viewer
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            testResult.endTest(this);
        }

        public String getName() {
            return featureFile.getName();
        }

        //mutate the base config to replace the feature paths with the path of just one selected feature file
        private class SingleFeatureConfigMutator implements ConfigMutator {

            public ConfigReader getNewConfig(ConfigReader baseConfigReader) {
                ConfigReader configReader = baseConfigReader.deepCopy();
                configReader.setProperty(
                    ChorusConfigProperty.FEATURE_PATHS,
                    Collections.singletonList(featureFile.getPath())
                );
                return configReader;
            }
        }
    }
}
