package org.chorusbdd.chorus.remoting;

import org.chorusbdd.chorus.remoting.jmx.AbstractJmxProxy;

/**
 * Created by: Steve Neal
 * Date: 12/10/11
 */
public class DynamicMBeanProxyMain {
    public static void main(String[] args) throws ChorusRemotingException {
        String host = "localhost";
        int jmxPort = 18080;
        String mBeanName = "org.chorusbdd.chorus:name=testbean";
        AbstractJmxProxy proxy = new AbstractJmxProxy(host, jmxPort, mBeanName);
        Object result = proxy.getAttribute("StepDefinitions");
        System.out.println(result);
    }
}
