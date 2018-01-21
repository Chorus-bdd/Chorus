package org.chorusbdd.chorus.selftest.websockets.simplewebsocketpublisher;

import org.chorusbdd.chorus.annotations.Handler;
import org.chorusbdd.chorus.annotations.Step;
import org.chorusbdd.chorus.context.ChorusContext;
import org.chorusbdd.chorus.logging.LogLevel;
import org.chorusbdd.chorus.logging.StdOutLogProvider;
import org.chorusbdd.chorus.websockets.client.StepPublisher;
import org.chorusbdd.chorus.util.ChorusException;

import java.net.URI;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Created by nick on 28/09/15.
 */
public class SimpleWebSocketStepPublisher {


    public static void main(String[] args) throws InterruptedException {

        StdOutLogProvider.setLogLevel(LogLevel.DEBUG);
        
        StepPublisher stepPublisher = new StepPublisher(
            "SimpleWebSocketStepPublisher",
            URI.create("ws://localhost:9080")
        );
        
        SimpleWebSocketStepPublisherHandler simpleWebSocketStepPublisherHandler = new SimpleWebSocketStepPublisherHandler(stepPublisher);
        stepPublisher.addHandlers(simpleWebSocketStepPublisherHandler);

        stepPublisher.publish();

        sleep(60000);
    }

    @Handler("SimpleWebSocketStepPublisherHandler")
    public static class SimpleWebSocketStepPublisherHandler {
        
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

        private int tryCount = 0;
        private StepPublisher stepPublisher;

        public SimpleWebSocketStepPublisherHandler(StepPublisher stepPublisher) {
            this.stepPublisher = stepPublisher;
        }

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

        @Step(value = ".* call a step with a step retry and the step is polled until it passes", retryDuration = 1, retryIntervalMillis = 100)
        public int stepWhichFailsAtFirst() throws InterruptedException {
            tryCount ++;
            if ( tryCount < 10) {
                throw new ChorusException("Simulate Failure");
            }
            return tryCount;
        }

        @Step(".*in the step publisher (.*) has the value (.*)")
        public void checkValue(String variable, String value) {
            String currentValue = (String)ChorusContext.getContext().get(variable);
            assertEquals(value, currentValue);
        }

        @Step(".*set the (.*) variable to (.*) in the step publisher")
        public void setContextValue(String variable, String value) {
            ChorusContext.getContext().put(variable, value);
        }

        
        @Step(".*disconnect the web socket publisher")
        public void disconnectAndReconnect() {
            scheduledExecutorService.schedule(() -> {
                try {
                    stepPublisher.disconnect();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }, 200, TimeUnit.MILLISECONDS);
        }
    }
}
