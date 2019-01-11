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

import org.chorusbdd.chorus.annotations.Step;
import org.chorusbdd.chorus.logging.LogLevel;
import org.chorusbdd.chorus.logging.StdOutLogProvider;
import org.chorusbdd.chorus.websockets.message.*;
import org.chorusbdd.chorus.websockets.util.JsonUtils;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * Created by nick on 09/12/2016.
 */
public class TestClientMessages {


    private static WebSocketClient webSocketClient;
    private static WebSocketMessageProcessor mockProcessor;
    private static final ChorusWebSocketServer chorusWebSocketServer = new ChorusWebSocketServer(9080);

    @BeforeClass
    public static void startTestServer() {

        StdOutLogProvider.setLogLevel(LogLevel.INFO);

        mockProcessor = mock(WebSocketMessageProcessor.class);

        chorusWebSocketServer.setWebSocketMessageProcessor(mockProcessor);
        chorusWebSocketServer.start();
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        URI uri = URI.create("ws://localhost:9080");
        webSocketClient = new JUnitWebSocketClient(uri);

        boolean success = false;
        try {
            success = webSocketClient.connectBlocking();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertTrue(success);
    }

    @AfterClass
    public static void stopTestServer() throws IOException, InterruptedException {
        webSocketClient.close();
        webSocketClient = null;
        chorusWebSocketServer.stop();
    }


    @Test
    public void iCanSendAConnectMessage() {
        ConnectMessage connectMessage = new ConnectMessage("mockClient", "JUnit Mock Client");
        String json = JsonUtils.prettyFormat(connectMessage);
        webSocketClient.send(json);

        verify(mockProcessor, timeout(1000)).receiveClientConnected(connectMessage);
    }

    @Test
    public void iCanSendAPublishStepMessage() {
        PublishStepMessage publishStep = new PublishStepMessage(
            "stepId",
            "chorusClientId",
            "click the (.*) button",
            true,
            "pending message",
            "tech description",
            0,
            0
        );

        String json = JsonUtils.prettyFormat(publishStep);
        webSocketClient.send(json);

        verify(mockProcessor, timeout(1000)).receivePublishStep(publishStep);
    }

    @Test
    public void iCanSendAPublishStepMessageWithNoOptionalPendingOrPendingMessageField() {
        String json = "{\n" +
            "  \"chorusClientId\" : \"chorusClientId\",\n" +
            "  \"pattern\" : \"click the (.*) button\",\n" +
            "  \"stepId\" : \"stepId\",\n" +
            "  \"technicalDescription\" : \"tech description\",\n" +
            "  \"type\" : \"PUBLISH_STEP\"\n" +
            "}";

        webSocketClient.send(json);

        PublishStepMessage expectedPublishStep = new PublishStepMessage(
            "stepId",
            "chorusClientId",
            "click the (.*) button",
            false,
            Step.NO_PENDING_MESSAGE,
            "tech description",
            0,
            100
        );

        verify(mockProcessor, timeout(1000)).receivePublishStep(expectedPublishStep);
    }

    @Test
    public void iCanSendAStepsAlignedMessage() {
        StepsAlignedMessage stepsAlignedMessage = new StepsAlignedMessage("mockClient");
        String json = JsonUtils.prettyFormat(stepsAlignedMessage);
        webSocketClient.send(json);

        verify(mockProcessor, timeout(1000)).receiveStepsAligned(stepsAlignedMessage);
    }

    @Test
    public void iCanSendAStepSuccessMessage() {
        StepSucceededMessage stepSucceededMessage = new StepSucceededMessage(
            "stepId",
            "executionId",
            "chorusClientId",
            "StringResult",
            new HashMap<String, Object>(){{
                put("key1", "value1");
                put("key2", 1234.5678D);
                put("key3", true);
            }}
        );
        String json = JsonUtils.prettyFormat(stepSucceededMessage);
        webSocketClient.send(json);

        verify(mockProcessor, timeout(1000)).receiveStepSucceeded(stepSucceededMessage);
    }

    @Test
    public void iCanSendAStepFailureMessage() {

        StepFailedMessage stepFailedMessage = new StepFailedMessage(
            "stepId",
            "executionId",
            "mockClient",
            "Error while executing step",
            "Exception at Class X line 7"
        );
        String json = JsonUtils.prettyFormat(stepFailedMessage);
        webSocketClient.send(json);

        verify(mockProcessor, timeout(1000)).receiveStepFailed(stepFailedMessage);
    }

    @Test
    public void iCanSendAStepFailureMessageWithoutTheOptionalErrorText() {
        String json = "{\n" +
            "  \"stepId\" : \"stepId\",\n" +
            "  \"chorusClientId\" : \"mockClient\",\n" +
            "  \"description\" : \"Error while executing step\",\n" +
            "  \"executionId\" : \"executionId\",\n" +
            "  \"type\" : \"STEP_FAILED\"\n" +
            "}";
        webSocketClient.send(json);

        StepFailedMessage stepFailedMessage = new StepFailedMessage(
            "stepId",
            "executionId",
            "mockClient",
            "Error while executing step",
            ""
        );
        verify(mockProcessor, timeout(1000)).receiveStepFailed(stepFailedMessage);
    }

    private static class JUnitWebSocketClient extends WebSocketClient {
        public JUnitWebSocketClient(URI uri) {
            super(uri);
        }

        @Override
        public void onOpen(ServerHandshake handshakedata) {
            System.out.println("Client onOpen");
        }

        @Override
        public void onMessage(String message) {
            System.out.println("Client onMessage");
        }

        @Override
        public void onClose(int code, String reason, boolean remote) {
            System.out.println("Client onClose");
        }

        @Override
        public void onError(Exception ex) {
            System.out.println("Client onError");
        }
    }
}
