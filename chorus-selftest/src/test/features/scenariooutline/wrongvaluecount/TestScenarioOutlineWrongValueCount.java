package scenariooutline.wrongvaluecount;

import org.chorusbdd.chorus.selftest.AbstractInterpreterTest;
import org.chorusbdd.chorus.selftest.ChorusSelfTestResults;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: nick
 * Date: 25/06/12
 * Time: 22:14
 */
public class TestScenarioOutlineWrongValueCount extends AbstractInterpreterTest {

    final String featurePath = "src/test/features/scenariooutline/wrongvaluecount/wrongvaluecount.feature";

    final int expectedExitCode = 0;  //fail

    final String standardOutput = readToString(TestScenarioOutlineWrongValueCount.class, "stdout.txt");
    final String standardError = readToString(TestScenarioOutlineWrongValueCount.class, "stderr.txt");

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
