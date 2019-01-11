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


import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;
import org.chorusbdd.chorus.websockets.message.*;
import org.chorusbdd.chorus.websockets.util.JsonUtils;
import org.chorusbdd.chorus.util.ChorusException;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by nick on 08/12/2016.
 */
public class ChorusWebSocketServer extends WebSocketServer implements WebSocketMessageRouter {

    private ChorusLog log = ChorusLogFactory.getLog(ChorusWebSocketServer.class);

    private volatile WebSocketMessageProcessor webSocketMessageProcessor;

    private Map<String, WebSocket> clientIdToSocket = new ConcurrentHashMap<>();

    public ChorusWebSocketServer(int port) {
        super( new InetSocketAddress( port ), 1);
        setReuseAddr(true);  //otherwise we can't restart the server-socket quickly enough (it's stuck in TIME_WAIT state)
    }

    @Override
    public void onStart() {
        log.debug("Started WebSocketServer on address " + getAddress());
    }

    public void setWebSocketMessageProcessor(WebSocketMessageProcessor webSocketMessageProcessor) {
        this.webSocketMessageProcessor = webSocketMessageProcessor;
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake ) {
        log.debug("Opened a connection from " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onClose( WebSocket conn, int code, String reason, boolean remote ) {
        log.debug("Closed a connection from " + conn.getRemoteSocketAddress() + ", code " + code + ", reason " + reason);
        Optional<String> clientId = findClientIdForWebSocket(conn);

        clientId.ifPresent(id -> {
            log.debug("Removing web socket for client id " + id);
            clientIdToSocket.remove(id);
            webSocketMessageProcessor.clientDisconnected(id);
        });
    }

    /**
     * Unless a client has sent a CONNECT message we will not have a client Id associated with the socket
     */
    private Optional<String> findClientIdForWebSocket(WebSocket conn) {
        return clientIdToSocket.entrySet().stream()
                .filter(e -> e.getValue() == conn)
                .map(Map.Entry::getKey)
                .findFirst();
    }

    @Override
    public void onMessage( WebSocket conn, String message ) {

        log.debug("Received a message " + message + " from " + conn.getRemoteSocketAddress());

        Map<String, Object> m = null;
        try {
            m = JsonUtils.convertToMap(message);
        } catch (Exception e) {
            log.error("Failed while converting message from JSON ", e);
        }

        if ( m != null) {
            processIncomingMessage(conn, message, m);
        } else {
            log.debug("Failed to decode message \n" + message);
        }
    }

    @Override
    public void sendMessage(String clientId, AbstractTypedMessage message) {

        WebSocket webSocket = clientIdToSocket.get(clientId);
        if ( webSocket == null) {
            throw new ChorusException("Cannot send a message to client " + clientId + " no websocket connection");
        }

        String messageAsString = JsonUtils.prettyFormat(message);
        if ( log.isDebugEnabled()) {
            log.debug(String.format("Sending message to web socket client %s [%s]", clientId, messageAsString));
        }

        webSocket.send(messageAsString);
    }

    private void processIncomingMessage(WebSocket socket, String message, Map<String, Object> m) {
        String type = m.getOrDefault("type", "UNKNOWN").toString();
        MessageType t = MessageType.fromString(type);

        try {
            switch(t) {
                case CONNECT :
                    ConnectMessage connectMessage = JsonUtils.convertToObject(message, ConnectMessage.class);
                    clientIdToSocket.put(connectMessage.getChorusClientId(), socket);
                    webSocketMessageProcessor.receiveClientConnected(connectMessage);
                    break;
                case PUBLISH_STEP :
                    PublishStepMessage publishStep = JsonUtils.convertToObject(message, PublishStepMessage.class);
                    webSocketMessageProcessor.receivePublishStep(publishStep);
                    break;
                case STEPS_ALIGNED :
                    StepsAlignedMessage stepsAlignedMessage = JsonUtils.convertToObject(message, StepsAlignedMessage.class);
                    webSocketMessageProcessor.receiveStepsAligned(stepsAlignedMessage);
                    break;
                case STEP_SUCCEEDED :
                    StepSucceededMessage stepSucceededMessage = JsonUtils.convertToObject(message, StepSucceededMessage.class);
                    webSocketMessageProcessor.receiveStepSucceeded(stepSucceededMessage);
                    break;
                case STEP_FAILED :
                    StepFailedMessage stepFailedMessage = JsonUtils.convertToObject(message, StepFailedMessage.class);
                    webSocketMessageProcessor.receiveStepFailed(stepFailedMessage);
                    break;
                default:
                    log.warn("Received message with unsupported type " + type);
            }
        } catch (Exception e) {
            log.error("Failed to process message from client", e);
        }
    }


    public void stop() throws IOException, InterruptedException {
        super.stop();

        //workaround for server socket left open on Windows
        //This issue is hopefully fixed in latest version of java websockets, or by setting 'reuse address' on the server socket
        //        closeSelector();
    }

    /**
     * A bug with ServerSocketChannel server.close() causes the server socket to be left open on Windows platform
     * Here we use reflection to access the selector and close this, which is a workaround for the issue
     * https://stackoverflow.com/questions/39656477/serversocketchannel-in-non-blocking-mode-is-not-closing-properly
     */
//    private void closeSelector() {
//        try {
//            Field field = WebSocketServer.class.getDeclaredField("selector");
//            field.setAccessible(true);
//            Selector selector = (Selector)field.get(this);
//            if ( selector.isOpen()) {
//                selector.close();
//            }
//        } catch (Exception e) {
//            log.error("Failed to close selector", e);
//        }
//    }


    @Override
    public void onError( WebSocket conn, Exception ex ) {
        if( conn != null ) {
            log.debug("Error on connection " + conn.getRemoteSocketAddress() + " ", ex);
        } else {
            log.debug("Error on WebSocketServer ", ex);
        }
    }
}