package simplefeature;

import org.junit.Test;

import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: nick
 * Date: 25/06/12
 * Time: 22:14
 */
public class TestSimpleFeature extends AbstractInterpreterTest {

    @Test
    public void testSimpleFeature() throws Exception {
        Properties sysProps = new Properties();
        sysProps.put("chorusFeaturePaths", "src/test/features/simplefeature");
        sysProps.put("chorusHandlerPackages", "simplefeature");

        //always test output at log level info using chorus built in log provider
        sysProps.put("chorusLogProvider", "org.chorusbdd.chorus.util.logging.StandardErrLogProvider");
        sysProps.put("chorusLogLevel", "info");

        ChorusSelfTestResults testResults = runChorusInterpreter(sysProps);

        ChorusSelfTestResults expectedResults = new ChorusSelfTestResults(
            //stdout
            "Feature: Simple Feature                                                                              \n" +
            "  Scenario: Simple Scenario\n" +
            "    Given Chorus is working properly                                                         PASSED  \n" +
            "    Then I can run a feature with a single scenario                                          PASSED  \n" +
            "\n" +
            "\n" +
            "Scenarios (total:1) (passed:1) (failed:0)\n" +
            "Steps (total:2) (passed:2) (failed:0) (undefined:0) (pending:0) (skipped:0)\n",

            //stderr
            "ChorusInterpreter         --> INFO    - Loaded feature file: " + getPlatformPath("src\\test\\features\\simplefeature\\simplefeature.feature") + "\n" +
            "ChorusInterpreter         --> INFO    - Processing scenario: Simple Scenario\n",

            //exitCode
            0
        );
        checkTestResults(testResults, expectedResults);
    }

}
