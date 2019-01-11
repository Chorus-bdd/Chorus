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
package org.chorusbdd.chorus.websockets.client;

import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;
import org.chorusbdd.chorus.websockets.message.AbstractTypedMessage;
import org.chorusbdd.chorus.websockets.message.ExecuteStepMessage;
import org.chorusbdd.chorus.websockets.message.MessageType;
import org.chorusbdd.chorus.websockets.util.JsonUtils;
import org.chorusbdd.chorus.util.ChorusException;
import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.Map;

/**
 * Created by Nick E on 13/12/2016.
 */
class ChorusWebSocketClient extends WebSocketClient implements StepClientMessageRouter {

    private ChorusLog log = ChorusLogFactory.getLog(WebSocketStepPublisher.class);
    private StepClientMessageProcessor stepClientMessageProcessor;

    public ChorusWebSocketClient(URI webSocketServerURI, StepClientMessageProcessor stepClientMessageProcessor) {
        super(webSocketServerURI);
        this.stepClientMessageProcessor = stepClientMessageProcessor;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        log.debug("WebSocket onOpen " + handshakedata);
    }

    @Override
    public void onMessage(String message) {
        log.debug("WebSocket onMessage " + message);

        Map<String, Object> m = null;
        try {
            m = JsonUtils.convertToMap(message);
        } catch (Exception e) {
            log.error("Failed while converting message from JSON ", e);
        }

        processIncomingMessage(getConnection(), message, m);
    }

    private void processIncomingMessage(WebSocket socket, String message, Map<String, Object> m) {
        String type = m.getOrDefault("type", "UNKNOWN").toString();
        MessageType t = MessageType.fromString(type);

        try {
            switch(t) {
                case EXECUTE_STEP :
                    ExecuteStepMessage executeStepMessage = JsonUtils.convertToObject(message, ExecuteStepMessage.class);
                    stepClientMessageProcessor.executeStep(executeStepMessage);
                    break;
                default:
                    log.warn("Received message with unsupported type " + type);
            }
        } catch (Exception e) {
            log.error("Failed to process message from client", e);
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        log.debug("WebSocket onClose, code " + code + ", reason " + reason + ", remote " + remote);
    }

    @Override
    public void onError(Exception ex) {
        log.debug("WebSocket onError", ex);
    }


    @Override
    public void sendMessage(AbstractTypedMessage message) {
        WebSocket webSocket = getConnection();
        if ( webSocket == null) {
            throw new ChorusException("Cannot send a message to WebSocketsManagerImpl no websocket connection");
        }

        String messageAsString = JsonUtils.prettyFormat(message);
        if ( log.isDebugEnabled()) {
            log.debug(String.format("Sending message to WebSocketsManagerImpl [%s]", messageAsString));
        }

        webSocket.send(messageAsString);
    }
}
