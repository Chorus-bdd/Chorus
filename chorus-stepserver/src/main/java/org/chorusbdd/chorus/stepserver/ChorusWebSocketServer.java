package org.chorusbdd.chorus.stepserver;

import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;
import org.chorusbdd.chorus.stepserver.message.ConnectMessage;
import org.chorusbdd.chorus.stepserver.message.PublishStep;
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

        Map<String, Object> m = JsonUtils.convertToMap(message);
        if ( m != null) {

            String type = m.getOrDefault("type", "UNKNOWN").toString();

            switch(type) {
                case "CONNECT" :
                    ConnectMessage connectMessage = JsonUtils.convertToObject(message, ConnectMessage.class);
                    stepServerMessageProcessor.processClientConnected(connectMessage);
                    break;
                case "PUBLISH_STEP" :
                    PublishStep publishStep = JsonUtils.convertToObject(message, PublishStep.class);
                    stepServerMessageProcessor.processPublishStep(publishStep);
                    break;
                default:
                    log.warn("Received message with unsupported type " + type);
            }
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