package org.chorusbdd.chorus.tools.webagent.httpconnector;

import org.chorusbdd.chorus.ChorusSuite;
import org.junit.runner.RunWith;

/**
 * User: nick
 * Date: 26/12/12
 * Time: 12:19
 */
@RunWith(ChorusSuite.class)
public class TestHttpConnector {

    public static String getChorusArgs() {
        return
            "-f src/test/java/org/chorusbdd/chorus/tools/webagent/httpconnector/HttpConnector.feature " +
            "-h org.chorusbdd.chorus.tools.webagent" +
            " -l warn"// +
            //" -t @ThisOne"
        ;
    }
}
