package featureparsing.simplefeature;

import org.chorusbdd.chorus.selftest.AbstractInterpreterTest;

/**
 * Created with IntelliJ IDEA.
 * User: nick
 * Date: 25/06/12
 * Time: 22:14
 */
public class TestFeatureParsing extends AbstractInterpreterTest {

    final String featurePath = "src/test/features/featureparsing";

    final int expectedExitCode = 1;  //fail

    protected int getExpectedExitCode() {
        return expectedExitCode;
    }

    protected String getFeaturePath() {
        return featurePath;
    }
}
