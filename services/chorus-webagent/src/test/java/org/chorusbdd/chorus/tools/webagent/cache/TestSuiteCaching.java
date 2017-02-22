package org.chorusbdd.chorus.tools.webagent.cache;

import org.chorusbdd.chorus.ChorusSuite;
import org.junit.runner.RunWith;

/**
 * User: nick
 * Date: 26/12/12
 * Time: 12:19
 */
@RunWith(ChorusSuite.class)
public class TestSuiteCaching {

    public static String getChorusArgs() {
        return
            "-f src/test/java/org/chorusbdd/chorus/tools/webagent/cache/SuiteCaching.feature " +
            "-h org.chorusbdd.chorus.tools.webagent" +
            " -l warn"
        ;
    }
}
