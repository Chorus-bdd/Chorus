package uk.co.smartkey.chorus.remoting;

import uk.co.smartkey.chorus.remoting.jmx.DynamicJmxProxy;

/**
 * Created by: Steve Neal
 * Date: 12/10/11
 */
public class DynamicMBeanProxyMain {
    public static void main(String[] args) throws ChorusRemotingException {
        //try calling a method on the shadow pricer
        String host = "localhost";
        int jmxPort = 18080;
        String mBeanName = "uk.co.smartkey.chorus:name=testbean";
        DynamicJmxProxy proxy = new DynamicJmxProxy(host, jmxPort, mBeanName);
        Object result = proxy.getAttribute("StepDefinitions");
        System.out.println(result);
    }
}
