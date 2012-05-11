package org.chorusbdd.chorus.remoting;

import org.chorusbdd.chorus.remoting.jmx.DynamicJmxProxy;
import org.junit.Test;

/**
 * Created by: Steve Neal
 * Date: 12/10/11
 */
public class DynamicMBeanProxyTest {

    @Test(expected = ChorusRemotingException.class)
    public void exceptionForIllegalHost() throws Exception {
        System.out.println("This test is expected to fail to connect to MBean, a logged error is expected");
        new DynamicJmxProxy("NO-SUCH-HOST", -1, "");
    }

}
