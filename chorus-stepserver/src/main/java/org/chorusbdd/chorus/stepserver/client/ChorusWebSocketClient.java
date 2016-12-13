package org.chorusbdd.chorus.stepserver.client;

import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;
import org.chorusbdd.chorus.stepserver.message.*;
import org.chorusbdd.chorus.stepserver.util.JsonUtils;
import org.chorusbdd.chorus.util.ChorusException;
import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.Map;

/**
 * Created by GA2EBBU on 13/12/2016.
 */
public class ChorusWebSocketClient extends WebSocketClient implements StepClientMessageRouter {

    private ChorusLog log = ChorusLogFactory.getLog(StepPublisher.class);
    private StepClientMessageProcessor stepClientMessageProcessor;

    public ChorusWebSocketClient(URI stepServerUri, StepClientMessageProcessor stepClientMessageProcessor) {
        super(stepServerUri);
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
            throw new ChorusException("Cannot send a message to StepServer no websocket connection");
        }

        String messageAsString = JsonUtils.prettyFormat(message);
        if ( log.isDebugEnabled()) {
            log.debug(String.format("Sending message to StepServer [%s]", messageAsString));
        }

        webSocket.send(messageAsString);
    }
}
