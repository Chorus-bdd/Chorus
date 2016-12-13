package org.chorusbdd.chorus.stepserver;

import org.chorusbdd.chorus.annotations.Handler;
import org.chorusbdd.chorus.annotations.Step;
import org.chorusbdd.chorus.logging.LogLevel;
import org.chorusbdd.chorus.logging.StdOutLogProvider;
import org.chorusbdd.chorus.stepserver.client.StepPublisher;
import org.chorusbdd.chorus.stepserver.message.*;
import org.chorusbdd.chorus.stepserver.util.JsonUtils;
import org.chorusbdd.chorus.util.PolledAssertion;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.junit.BeforeClass;
import org.junit.Test;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * Created by nick on 09/12/2016.
 */
public class TestStepPublisher {


    private static StepPublisher webSocketClient;
    private static StepServerMessageProcessor mockProcessor;
    private static final ChorusWebSocketServer chorusWebSocketServer = new ChorusWebSocketServer(9080);
    private static final AtomicBoolean stepCalled = new AtomicBoolean();

    @BeforeClass
    public static void startTestServer() {

        StdOutLogProvider.setLogLevel(LogLevel.INFO);

        mockProcessor = mock(StepServerMessageProcessor.class);

        chorusWebSocketServer.setStepServerMessageProcessor(mockProcessor);
        chorusWebSocketServer.start();
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Handler("Test Step Publisher Handler")
    public static class MockHandler {

        @Step("call a test step")
        public void callATestStep() {
            System.out.println("Hello!");
            stepCalled.set(true);
        }
    }

    @Test
    public void aClientCanPublishAStepAndAServerCanExecuteIt() {

        URI uri = URI.create("ws://localhost:9080");
        webSocketClient = new StepPublisher("testPublisher", uri, new MockHandler());
        webSocketClient.publish();

        PublishStepMessage publishStepMessage = new PublishStepMessage(
            "1:MockHandler:callATestStep",
            "testPublisher",
            "call a test step",
            false,
            "",
            "MockHandler:callATestStep"
        );
        verify(mockProcessor, timeout(1000)).receivePublishStep(publishStepMessage);

        chorusWebSocketServer.sendMessage("testPublisher", new ExecuteStepMessage(
            "testPublisher",
            "1:MockHandler:callATestStep",
            UUID.randomUUID().toString(),
            "call a test step",
            30,
            Collections.emptyList(),
            Collections.emptyMap()
        ));

        new PolledAssertion() {
            @Override
            protected void validate() throws Exception {
                assertTrue(stepCalled.get());
            }
        }.await(TimeUnit.SECONDS, 2);

    }


}
