package undefinedstep;

import org.chorusbdd.chorus.selftest.AbstractInterpreterTest;
import org.chorusbdd.chorus.selftest.ChorusSelfTestResults;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: nick
 * Date: 25/06/12
 * Time: 22:14
 */
public class TestUndefinedStep extends AbstractInterpreterTest {

    final String featurePath = "src/test/features/undefinedstep";

    final int expectedExitCode = 1;  //fail

    final String standardOutput = readToString(TestUndefinedStep.class, "stdout.txt");
    final String standardError = readToString(TestUndefinedStep.class, "stderr.txt");


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
