package org.chorusbdd.chorus.selftest.remoting.jmx.remotingwithlocalprocess;

import org.chorusbdd.chorus.annotations.Handler;
import org.chorusbdd.chorus.stepinvoker.StepInvoker;
import org.chorusbdd.chorus.stepinvoker.StepInvokerProvider;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by nick on 04/02/15.
 */
@Handler("BillAndBen")
public class BillAndBenHandler implements StepInvokerProvider {

    private final String jmxProperty = System.getProperty("com.sun.management.jmxremote.port");

    @Override
    public List<StepInvoker> getStepInvokers() {
        List<StepInvoker> l = new LinkedList<StepInvoker>();
        StepInvoker one = new StepInvoker() {

            String regex = "I can call a step method exported by the " + jmxProperty + " handler";
            @Override
            public Pattern getStepPattern() {
                return Pattern.compile(regex);
            }

            @Override
            public boolean isPending() {
                return false;
            }

            @Override
            public String getPendingMessage() {
                return "";
            }

            @Override
            public Object invoke(List<String> args) throws ReflectiveOperationException {
                return StepInvoker.VOID_RESULT;
            }

            @Override
            public String getId() {
                return String.valueOf(System.identityHashCode(this));
            }

            @Override
            public String getTechnicalDescription() {
                return regex;
            }
        };

        StepInvoker two = new StepInvoker() {

            String regex = "I can call a step and get the jmx port from the " + jmxProperty + " handler";
            @Override
            public Pattern getStepPattern() {
                return Pattern.compile(regex);
            }

            @Override
            public boolean isPending() {
                return false;
            }

            @Override
            public String getPendingMessage() {
                return "";
            }

            @Override
            public Object invoke(List<String> args) throws ReflectiveOperationException {
                Integer port = Integer.valueOf(jmxProperty);
                return port;
            }

            @Override
            public String getId() {
                return String.valueOf(System.identityHashCode(this));
            }

            @Override
            public String getTechnicalDescription() {
                return regex;
            }
        };

        l.add(one);
        l.add(two);
        return l;


    }
}
