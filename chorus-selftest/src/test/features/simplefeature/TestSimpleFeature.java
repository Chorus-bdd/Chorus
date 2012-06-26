package simplefeature;

import org.chorusbdd.chorus.selftest.AbstractInterpreterTest;
import org.chorusbdd.chorus.selftest.ChorusSelfTestResults;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: nick
 * Date: 25/06/12
 * Time: 22:14
 *
 * Test we can run a simple feature and scenario successfully
 */
public class TestSimpleFeature extends AbstractInterpreterTest {

    final String featurePath = "src/test/features/simplefeature";

    final int expectedExitCode = 0;  //success

    final String standardOutput =
        "Feature: Simple Feature                                                                              \n" +
        "  Scenario: Simple Scenario\n" +
        "    Given Chorus is working properly                                                         PASSED  \n" +
        "    Then I can run a feature with a single scenario                                          PASSED  \n" +
        "\n" +
        "\n" +
        "Scenarios (total:1) (passed:1) (failed:0)\n" +
        "Steps (total:2) (passed:2) (failed:0) (undefined:0) (pending:0) (skipped:0)\n";

    final String standardError =
        "ChorusInterpreter         --> INFO    - Loaded feature file: " + getPlatformPath("src/test/features/simplefeature/simplefeature.feature") + "\n" +
        "ChorusInterpreter         --> INFO    - Processing scenario: Simple Scenario\n";


    @Test
    public void runTest() throws Exception {

        ChorusSelfTestResults testResults = runFeature(featurePath);

        ChorusSelfTestResults expectedResults = new ChorusSelfTestResults(
            standardOutput,
            standardError,
            expectedExitCode
        );

        checkTestResults(testResults, expectedResults);
    }

}
