package uk.co.smartkey.chorus.remoting.jmx;

/**
 * Created by: Steve Neal
 * Date: 14/10/11
 */
public class TestBean implements TestBeanMBean {
    public void acceptMessage(String message) {
        System.out.println(message);
    }
}
