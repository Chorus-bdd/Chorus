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

/**
 * Created by nick on 08/12/2016.
 */
public class ChorusWebSocketServer extends WebSocketServer {

    private ChorusLog log = ChorusLogFactory.getLog(ChorusWebSocketServer.class);

    private StepServerMessageProcessor stepServerMessageProcessor;

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
            processIncomingMessage(message, m);
        } else {
            log.debug("Failed to decode message \n" + message);
        }
    }

    private void processIncomingMessage(String message, Map<String, Object> m) {
        String type = m.getOrDefault("type", "UNKNOWN").toString();
        MessageType t = MessageType.fromString(type);

        switch(t) {
            case CONNECT :
                ConnectMessage connectMessage = JsonUtils.convertToObject(message, ConnectMessage.class);
                stepServerMessageProcessor.receiveClientConnected(connectMessage);
                break;
            case PUBLISH_STEP :
                PublishStep publishStep = JsonUtils.convertToObject(message, PublishStep.class);
                stepServerMessageProcessor.receivePublishStep(publishStep);
                break;
            case STEPS_ALIGNED :
                StepsAlignedMessage stepsAlignedMessage = JsonUtils.convertToObject(message, StepsAlignedMessage.class);
                stepServerMessageProcessor.receiveStepsAligned(stepsAlignedMessage);
                break;
            case STEP_SUCCEEDED :
                StepSucceededMessage stepSucceededMessage = JsonUtils.convertToObject(message, StepSucceededMessage.class);
                stepServerMessageProcessor.receiveStepSucceeded(stepSucceededMessage);
                break;
            case STEP_FAILED :
                StepFailedMessage stepFailedMessage = JsonUtils.convertToObject(message, StepFailedMessage.class);
                stepServerMessageProcessor.receiveStepFailed(stepFailedMessage);
                break;
            default:
                log.warn("Received message with unsupported type " + type);
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