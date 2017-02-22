package org.chorusbdd.chorus.tools.webagent.suite;

import org.chorusbdd.chorus.ChorusSuite;
import org.junit.runner.RunWith;

/**
 * User: nick
 * Date: 26/12/12
 * Time: 12:19
 */
@RunWith(ChorusSuite.class)
public class TestMockSuite {

    public static String getChorusArgs() {
        return
            "-f src/test/java/org/chorusbdd/chorus/tools/webagent/suite/TestMockSuite.feature " +
            "-h org.chorusbdd.chorus.tools.webagent " +
            "-l info " //+
            //" -t @ThisOne"
        ;
    }
}
