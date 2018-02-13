package org.chorusbdd.chorus.selftest.selenium.e2e;

import org.chorusbdd.chorus.ChorusSuite;
import org.junit.runner.RunWith;

/**
 * Created by nickebbutt on 13/02/2018.
 */
@RunWith(ChorusSuite.class)
public class SeleniumManagerImplTest {

    public static String getChorusArgs() {
        return "-f /Users/nickebbutt/dev/chorus-js-react-calculator/features -h org.chorusbdd.chorus.selftest";
    }    
}
