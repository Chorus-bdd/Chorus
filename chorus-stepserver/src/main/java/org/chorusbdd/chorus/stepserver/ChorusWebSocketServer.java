package org.chorusbdd.chorus.stepserver;

import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;
import org.chorusbdd.chorus.stepserver.message.*;
import org.chorusbdd.chorus.stepserver.util.JsonUtils;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by nick on 08/12/2016.
 */
public class ChorusWebSocketServer extends WebSocketServer {

    private ChorusLog log = ChorusLogFactory.getLog(ChorusWebSocketServer.class);

    private StepServerMessageProcessor stepServerMessageProcessor;

    private ConcurrentMap<WebSocket, ClientDetails> connectedClientDetails = new ConcurrentHashMap<>();

    public ChorusWebSocketServer(int port, StepServerMessageProcessor stepServerMessageProcessor ) {
        super( new InetSocketAddress( port ) );
        this.stepServerMessageProcessor = stepServerMessageProcessor;
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake ) {
        log.info("Opened a connection from " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onClose( WebSocket conn, int code, String reason, boolean remote ) {
        log.info("Closed a connection from " + conn.getRemoteSocketAddress() + ", code " + code + ", reason " + reason);
    }

    @Override
    public void onMessage( WebSocket conn, String message ) {

        log.info("Received a message " + message + " from " + conn.getRemoteSocketAddress());

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

    private void processIncomingMessage(WebSocket socket, String message, Map<String, Object> m) {
        String type = m.getOrDefault("type", "UNKNOWN").toString();
        MessageType t = MessageType.fromString(type);

        ClientDetails clientDetails;
        try {
            switch(t) {
                case CONNECT :
                    ConnectMessage connectMessage = JsonUtils.convertToObject(message, ConnectMessage.class);
                    clientDetails = addConnectedClient(socket, connectMessage.getChorusClientId());
                    stepServerMessageProcessor.receiveClientConnected(clientDetails, connectMessage);
                    break;
                case PUBLISH_STEP :
                    PublishStepMessage publishStep = JsonUtils.convertToObject(message, PublishStepMessage.class);
                    clientDetails = getConnectedClient(socket, publishStep.getChorusClientId(), "send " + MessageType.PUBLISH_STEP);
                    stepServerMessageProcessor.receivePublishStep(clientDetails, publishStep);
                    break;
                case STEPS_ALIGNED :
                    StepsAlignedMessage stepsAlignedMessage = JsonUtils.convertToObject(message, StepsAlignedMessage.class);
                    clientDetails = getConnectedClient(socket, stepsAlignedMessage.getChorusClientId(), "send " + MessageType.STEPS_ALIGNED);
                    stepServerMessageProcessor.receiveStepsAligned(clientDetails, stepsAlignedMessage);
                    break;
                case STEP_SUCCEEDED :
                    StepSucceededMessage stepSucceededMessage = JsonUtils.convertToObject(message, StepSucceededMessage.class);
                    clientDetails = getConnectedClient(socket, stepSucceededMessage.getChorusClientId(), "send " + MessageType.STEP_SUCCEEDED);
                    stepServerMessageProcessor.receiveStepSucceeded(clientDetails, stepSucceededMessage);
                    break;
                case STEP_FAILED :
                    StepFailedMessage stepFailedMessage = JsonUtils.convertToObject(message, StepFailedMessage.class);
                    clientDetails = getConnectedClient(socket, stepFailedMessage.getChorusClientId(), "send " + MessageType.STEP_FAILED);
                    stepServerMessageProcessor.receiveStepFailed(clientDetails, stepFailedMessage);
                    break;
                default:
                    log.warn("Received message with unsupported type " + type);
            }
        } catch (Exception e) {
            log.error("Failed to process message from client", e);
        }
    }

    private ClientDetails getConnectedClient(WebSocket socket, String clientId, String actionDescription) throws ClientConnectionException {
        ClientDetails clientDetails = connectedClientDetails.get(socket);
        if ( clientDetails == null) {
            throw new ClientConnectionException("The client " + clientId + " is not connected and cannot " + actionDescription);
        }
        return clientDetails;
    }

    private ClientDetails addConnectedClient(WebSocket socket, String chorusClientId) throws ClientConnectionException {
        if ( connectedClientDetails.containsKey(socket)) {
            throw new ClientConnectionException("Client " + connectedClientDetails.get(socket) + " is already connected");
        } else {
            return new ClientDetails(socket.getRemoteSocketAddress(), chorusClientId);
        }
    }

    @Override
    public void onError( WebSocket conn, Exception ex ) {
        ex.printStackTrace();
        if( conn != null ) {
            log.error("Error on connection " + conn.getRemoteSocketAddress() + " ", ex);
        }
    }
}