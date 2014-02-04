package org.chorusbdd.chorus.selftest.stepmacro.featurelocal;

import org.chorusbdd.chorus.ChorusSuite;
import org.junit.runner.RunWith;

/**
 * User: nick
 * Date: 04/02/14
 * Time: 08:56
 */
@RunWith(ChorusSuite.class)
public class DoTest {
    
    public static String getChorusArgs() {
        return "-f src/test/features/org/chorusbdd/chorus/selftest/stepmacro/featurelocal -h org.chorusbdd.chorus.selftest";
    }    
}
