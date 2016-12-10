package org.chorusbdd.chorus.stepserver;

import org.chorusbdd.chorus.stepserver.message.ConnectMessage;
import org.chorusbdd.chorus.stepserver.message.PublishStep;
import org.chorusbdd.chorus.stepserver.util.JsonUtils;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.junit.BeforeClass;
import org.junit.Test;

import java.net.URI;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Created by nick on 09/12/2016.
 */
public class TestPublishStep {


    private static WebSocketClient webSocketClient;
    private static StepServerMessageProcessor mockProcessor;

    @BeforeClass
    public static void startTestServer() {
        mockProcessor = mock(StepServerMessageProcessor.class);

        ChorusWebSocketServer chorusWebSocketServer = new ChorusWebSocketServer(9080, mockProcessor);
        chorusWebSocketServer.start();
        try {
            Thread.sleep(3000);
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


    @Test
    public void testConnectMessage() {
        ConnectMessage connectMessage = new ConnectMessage("mockClient", "JUnit Mock Client");
        String json = JsonUtils.prettyFormat(connectMessage);
        webSocketClient.send(json);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        verify(mockProcessor).processClientConnected(connectMessage);
    }

    @Test
    public void testPublishStepMessage() {
        PublishStep publishStep = new PublishStep(
            "stepId",
            "chorusClientId",
            "click the (.*) button",
            true,
            "pending message",
            "tech description"
        );

        String json = JsonUtils.prettyFormat(publishStep);
        webSocketClient.send(json);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        verify(mockProcessor).processPublishStep(publishStep);
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
