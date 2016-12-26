package org.chorusbdd.chorus.selftest.stepserver.simplepublisher;

import org.chorusbdd.chorus.annotations.Handler;
import org.chorusbdd.chorus.annotations.Step;
import org.chorusbdd.chorus.stepserver.client.StepPublisher;

import java.net.URI;

import static java.lang.Thread.sleep;
import static org.junit.Assert.assertFalse;

/**
 * Created by nick on 28/09/15.
 */
public class SimpleStepPublisher {


    public static void main(String[] args) throws InterruptedException {

        StepPublisher stepPublisher = new StepPublisher("SimpleStepPublisher", URI.create("ws://localhost:9080"), new SimpleStepServerClientHandler());
        stepPublisher.publish();

        sleep(30000);
    }

    @Handler("SimpleStepServerClientHandler")
    public static class SimpleStepServerClientHandler {

        @Step(".* call a step with a result")
        public String callAStepWithAResult() {
            return "Hello!";
        }

        @Step(".* call a step without a result")
        public void callAStepWithoutAResult() {}

        @Step(".* call a step which fails")
        public void callAStepWhichFails() {
            assertFalse("Whooa steady on there sailor", true);
        }

        @Step(".* call a step which blocks")
        public void blockForAWhile() throws InterruptedException {
            sleep(100000);
        }

    }
}
