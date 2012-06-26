package simplefeature;

import org.chorusbdd.chorus.selftest.AbstractInterpreterTest;
import org.chorusbdd.chorus.selftest.ChorusSelfTestResults;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: nick
 * Date: 25/06/12
 * Time: 22:14
 */
public class TestSimpleFeature extends AbstractInterpreterTest {

    final String featurePath = "src/test/features/simplefeature";

    final int expectedExitCode = 0;  //success

    final String standardOutput = readToString(TestSimpleFeature.class, "stdout.txt");
    final String standardError = readToString(TestSimpleFeature.class, "stderr.txt");

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
