/**
 * MIT License
 *
 * Copyright (c) 2019 Chorus BDD Organisation.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.chorusbdd.chorus.websockets;

import org.chorusbdd.chorus.annotations.Handler;
import org.chorusbdd.chorus.annotations.Step;
import org.chorusbdd.chorus.logging.LogLevel;
import org.chorusbdd.chorus.logging.StdOutLogProvider;
import org.chorusbdd.chorus.websockets.client.WebSocketStepPublisher;
import org.chorusbdd.chorus.websockets.message.ExecuteStepMessage;
import org.chorusbdd.chorus.websockets.message.PublishStepMessage;
import org.chorusbdd.chorus.util.PolledAssertion;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * Created by nick on 09/12/2016.
 */
public class TestWebSocketStepPublisher {


    private static WebSocketStepPublisher stepPublisher;
    private static WebSocketMessageProcessor mockProcessor;
    private static final ChorusWebSocketServer chorusWebSocketServer = new ChorusWebSocketServer(9080);
    private static final AtomicBoolean stepCalled = new AtomicBoolean();

    @BeforeClass
    public static void startTestServer() {

        StdOutLogProvider.setLogLevel(LogLevel.DEBUG);

        mockProcessor = mock(WebSocketMessageProcessor.class);

        chorusWebSocketServer.setWebSocketMessageProcessor(mockProcessor);
        chorusWebSocketServer.start();
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @AfterClass
    public static void stopTestServer() throws IOException, InterruptedException {
        stepPublisher.disconnect();
        stepPublisher = null;
        chorusWebSocketServer.stop();
    }

    @Handler("Test Step Publisher Handler")
    public static class MockHandler {

        @Step(value = "call a test step", id = "step1")
        public void callATestStep() {
            System.out.println("Hello!");
            stepCalled.set(true);
        }
    }

    @Test
    public void aClientCanPublishAStepAndAServerCanExecuteIt() {

        URI uri = URI.create("ws://localhost:9080");
        stepPublisher = new WebSocketStepPublisher("testPublisher", uri, new MockHandler());
        stepPublisher.publish();

        PublishStepMessage publishStepMessage = new PublishStepMessage(
            "step1",
            "testPublisher",
            "call a test step",
            false,
            "org.chorusbdd.chorus.annotations.Step.NO_PENDING_MESSAGE",
            "MockHandler:callATestStep",
            0,
            0
        );
        verify(mockProcessor, timeout(1000)).receivePublishStep(publishStepMessage);

        chorusWebSocketServer.sendMessage("testPublisher", new ExecuteStepMessage(
            "testPublisher",
            "step1",
            UUID.randomUUID().toString(),
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
