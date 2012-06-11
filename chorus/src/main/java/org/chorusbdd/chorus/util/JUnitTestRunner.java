package org.chorusbdd.chorus.util;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import org.junit.runner.RunWith;
import org.junit.runners.AllTests;

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
public class JUnitTestRunner {

    public static TestSuite suite() {
        TestSuite suite = new TestSuite();
        for (Test test : findAllTestCasesRuntime()) {
          suite.addTest(test);
        }
        suite.setName("My Suite");
        return suite;
      }

    private static Test[] findAllTestCasesRuntime() {
        return new Test[] {
            new TestCase() {

                public int countTestCases() {
                    return 1;
                }

                public void run(TestResult testResult) {
                    testResult.startTest(this);
                    testResult.endTest(this);
                }

                public String getName() {
                    return "My Test";
                }
            }
        };
    }


}
