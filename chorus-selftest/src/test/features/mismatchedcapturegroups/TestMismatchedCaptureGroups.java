package mismatchedcapturegroups;

import org.chorusbdd.chorus.selftest.AbstractInterpreterTest;
import org.chorusbdd.chorus.selftest.ChorusSelfTestResults;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: nick
 * Date: 25/06/12
 * Time: 22:14
 */
public class TestMismatchedCaptureGroups extends AbstractInterpreterTest {

    final String featurePath = "src/test/features/multiplescenario";

    final int expectedExitCode = 1;  //fail

    final String standardOutput = readToString(TestMismatchedCaptureGroups.class, "stdout.txt");
    final String standardError = readToString(TestMismatchedCaptureGroups.class, "stderr.txt");

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
