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
import org.chorusbdd.chorus.results.ExecutionToken;
import org.chorusbdd.chorus.results.FeatureToken;
import org.chorusbdd.chorus.core.interpreter.scanner.FeatureScanner;
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

    private static final String FEATURE_TOKEN_LIST = "FEATURE_TOKEN_LIST";
    private static final String EXECUTION_TOKEN = "EXECUTION_TOKEN";

    //all tokens executed during the suite

    public static TestSuite suite() {
        return suite(new String[0]);
    }

    public static TestSuite suite(String params) {
        return suite(params.split(" "));
    }

    public static TestSuite suite(String[] args) {
        TestSuite suite = null;
        try {
            //a map which holds state for this suite execution
            final Map<String, Object> executionEnvironment = new HashMap<String, Object>();

            final Chorus chorus = new Chorus(args);

            final List<FeatureToken> featureTokens = new ArrayList<FeatureToken>();
            executionEnvironment.put(FEATURE_TOKEN_LIST, featureTokens);

            suite = new TestSuite() {
                public void run(TestResult result) {
                    ExecutionToken executionToken = chorus.startTests();
                    executionEnvironment.put(EXECUTION_TOKEN, executionToken);
                    super.run(result);
                    chorus.endTests(executionToken, featureTokens);
                }
            };

            for (Test test : findAllTestCasesRuntime(chorus, executionEnvironment)) {
                suite.addTest(test);
            }

            suite.setName(chorus.getSuiteName());
            return suite;
        } catch (InterpreterPropertyException e) {
          e.printStackTrace();
        }
        return suite;
    }

    private static Test[] findAllTestCasesRuntime(Chorus chorus, Map<String, Object> executionEnvironment) {
        //scan for all feature files specified in base config
        FeatureScanner featureScanner = new FeatureScanner();
        List<File> featureFiles = featureScanner.getFeatureFiles(chorus.getFeatureFilePaths());

        //generate a junit test to execute the interpreter for each feature file
        Test[] tests = new Test[featureFiles.size()];
        int index=0;
        for ( File f : featureFiles) {
            tests[index++] = new ChorusTest(f, chorus, executionEnvironment);
        }
        return tests;
    }

    private static class ChorusTest extends TestCase {
        private File featureFile;
        private Chorus chorus;
        private Map<String, Object> executionEnvironment;

        public ChorusTest(File featureFile, Chorus chorus, Map<String, Object> executionEnvironment) {
            //To change body of created methods use File | Settings | File Templates.
            this.featureFile = featureFile;
            this.chorus = chorus;
            this.executionEnvironment = executionEnvironment;
        }

        public int countTestCases() {
            return 1;
        }

        public void run(TestResult testResult) {
            testResult.startTest(this);
            try {
                //run using the base config filtered through a mutator which replaces the
                //feature file paths property with the specific path for this feature file
                List<FeatureToken> tokens = chorus.run(
                    (ExecutionToken)executionEnvironment.get(EXECUTION_TOKEN),
                    new SingleFeatureConfigMutator()
                );

                boolean success = true;
                for ( FeatureToken f : tokens) {
                    success &= ( f.isPassed() || f.isPending() );
                }
                if ( ! success) {
                    testResult.addFailure(this, new AssertionFailedError("Chorus test failed"));
                }

                List<FeatureToken> l = (List<FeatureToken>)executionEnvironment.get(FEATURE_TOKEN_LIST);
                //add token for the feature to the list of all tokens for the suite
                l.addAll(tokens);

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

            public ConfigProperties getNewConfig(ConfigProperties baseConfig) {
                ConfigProperties p = baseConfig.deepCopy();
                p.setProperty(
                    ChorusConfigProperty.FEATURE_PATHS,
                    Collections.singletonList(featureFile.getPath())
                );
                return p;
            }
        }
    }
}
