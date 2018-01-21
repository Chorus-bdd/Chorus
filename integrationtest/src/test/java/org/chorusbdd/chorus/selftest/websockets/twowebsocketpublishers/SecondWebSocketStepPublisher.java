package org.chorusbdd.chorus.selftest.websockets.twowebsocketpublishers;

import org.chorusbdd.chorus.annotations.Handler;
import org.chorusbdd.chorus.annotations.Step;
import org.chorusbdd.chorus.websockets.client.StepPublisher;

import java.net.URI;

import static java.lang.Thread.sleep;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Created by nick on 28/09/15.
 */
public class SecondWebSocketStepPublisher {


    public static void main(String[] args) throws InterruptedException {

        StepPublisher stepPublisher = new StepPublisher(
            "SecondWebSocketStepPublisher",
            URI.create("ws://localhost:9080"),
            new SecondWebSocketStepPublisherHandler()
        );

        stepPublisher.publish();

        sleep(60000);
    }

    @Handler("SecondWebSocketStepPublisherHandler")
    public static class SecondWebSocketStepPublisherHandler {
        
        @Step(".* call a step on the second publisher")
        public String sayHelloFromSecondPublisher() {
            return "Hello!";
        }
    }
}
