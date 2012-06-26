package failedstep;

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


public class TestFailedStep extends AbstractInterpreterTest {

    final String featurePath = "src/test/features/failedstep";

    final int expectedExitCode = 1;  //fail

    final String standardOutput =
        "Feature: Failed Step                                                                                 \n" +
        "  Scenario: Simple Scenario\n" +
        "    Given Chorus is working properly                                                         PASSED  \n" +
        "    Then I can run a feature with a single scenario                                          PASSED  \n" +
        "    And if a step fails                                                                      FAILED  This step threw an exception to fail it\n" +
        "    Then the subsequent step is skipped                                                      SKIPPED \n" +
        "\n" +
        "\n" +
        "Scenarios (total:1) (passed:0) (failed:1)\n" +
        "Steps (total:4) (passed:2) (failed:1) (undefined:0) (pending:0) (skipped:1)\n";

    final String standardError =
        "ChorusInterpreter         --> INFO    - Loaded feature file: " + getPlatformPath("src/test/features/failedstep/failedstep.feature") + "\n" +
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
