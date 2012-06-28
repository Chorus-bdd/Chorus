package handlerpriority;

import org.chorusbdd.chorus.selftest.AbstractInterpreterTest;
import org.chorusbdd.chorus.selftest.ChorusSelfTestResults;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: nick
 * Date: 25/06/12
 * Time: 22:14
 */
public class TestHandlerPriority extends AbstractInterpreterTest {

    final String featurePath = "src/test/features/handlerpriority/handlerpriority_defaulthandler.feature";

    final int expectedExitCode = 0;  //pass

    final String standardOutput = readToString(TestHandlerPriority.class, "stdout_defaulthandler.txt");
    final String standardError = readToString(TestHandlerPriority.class, "stderr_defaulthandler.txt");

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
