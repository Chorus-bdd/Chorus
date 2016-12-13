package org.chorusbdd.chorus.selftest.stepserver.simpleclient;

import org.chorusbdd.chorus.annotations.Handler;
import org.chorusbdd.chorus.annotations.Step;
import org.chorusbdd.chorus.stepserver.client.StepPublisher;

import java.net.URI;

/**
 * Created by nick on 28/09/15.
 */
public class SimpleStepServerClient {


    public static void main(String[] args) throws InterruptedException {

        StepPublisher stepPublisher = new StepPublisher("SimpleStepServerClient", URI.create("ws://localhost:9080"), new SimpleStepServerClientHandler());
        stepPublisher.publish();

        Thread.sleep(30000);
    }

    @Handler("SimpleStepServerClientHandler")
    public static class SimpleStepServerClientHandler {

        @Step(".* call a step with a result")
        public String callAStepWithAResult() {
            return "Hello!";
        }

    }
}
