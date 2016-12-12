package org.chorusbdd.chorus.stepserver;

import org.chorusbdd.chorus.executionlistener.ExecutionListener;
import org.chorusbdd.chorus.executionlistener.ExecutionListenerAdapter;
import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;
import org.chorusbdd.chorus.logging.LogLevel;
import org.chorusbdd.chorus.logging.StdOutLogProvider;
import org.chorusbdd.chorus.results.ExecutionToken;
import org.chorusbdd.chorus.results.FeatureToken;
import org.chorusbdd.chorus.stepinvoker.StepInvoker;
import org.chorusbdd.chorus.stepserver.message.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by nick on 09/12/2016.
 */
public class StepServer implements StepServerManager {

    private static ChorusLog log = ChorusLogFactory.getLog(StepServer.class);

    private static final int DEFAULT_PORT = 9080;
    private static final String DEFAULT_SERVER_NAME = "defaultStepServer";

    private ChorusWebSocketServer webSocketServer;
    private final AtomicBoolean isRunning = new AtomicBoolean();

    private final Map<ClientDetails, ConnectedClient> connectedClients = new ConcurrentHashMap<>();

    public StepServer() {}

    public void startServer(String serverName) {
        if ( ! isRunning.getAndSet(true)) {
            //TODO implement configuration by server name
            log.info("Starting StepServer on port port");
            MessageProcessor messageProcessor = new MessageProcessor();
            webSocketServer = new ChorusWebSocketServer(DEFAULT_PORT, messageProcessor);
            webSocketServer.start();
        } else {
            //when multiple configurations are supported this will change
            log.error("Only one step server can be started");
        }
    }

    @Override
    public void waitForClientConnection(String clientName, int timeoutSeconds) throws ClientConnectionException {
        //TODO
    }

    @Override
    public List<StepInvoker> getStepInvokers() {
        //TODO
        return Collections.emptyList();
    }

    @Override
    public void stopServer(String serverName) {
        if ( isRunning.getAndSet(false) ) {
            log.info("Stopping StepServer");
            //TODO implement configuration by server name
            try {
                webSocketServer.stop();
            } catch (Exception e) {
                log.error("Failed while stopping web socket sever", e);
            }
        }
    }

    @Override
    public ExecutionListener getExecutionListener() {
        return new ExecutionListenerAdapter() {

            //TODO here we are assuming scope by feature
            //allow step server to be scoped to feature or scenario (or session) in config

            public void featureCompleted(ExecutionToken testExecutionToken, FeatureToken feature) {
                stopServer(DEFAULT_SERVER_NAME);
            }
        };
    }

    private class MessageProcessor implements StepServerMessageProcessor {

        @Override
        public void receiveClientConnected(ClientDetails clientDetails, ConnectMessage connectMessage) {
            log.info("received a CONNECT message!");
            log.info(connectMessage.toString());
            connectedClients.put(clientDetails, new ConnectedClient(clientDetails));

        }

        @Override
        public void receivePublishStep(ClientDetails clientDetails, PublishStepMessage publishStep) {
            log.info("received a PUBLISH_STEP message!");
            log.info(publishStep.toString());

            ConnectedClient connectedClient = connectedClients.get(clientDetails);
            connectedClient.addStep(publishStep);
        }

        @Override
        public void receiveStepsAligned(ClientDetails clientDetails, StepsAlignedMessage stepsAlignedMessage) {
            log.info("received a STEPS_ALIGNED message!");
            log.info(stepsAlignedMessage.toString());
            ConnectedClient connectedClient = connectedClients.get(clientDetails);
            connectedClient.setAligned(true);
        }

        @Override
        public void receiveStepSucceeded(ClientDetails clientDetails, StepSucceededMessage stepSuccessMessage) {
            log.info("received a STEP_SUCCEEDED message!");
            log.info(stepSuccessMessage.toString());

            ConnectedClient connectedClient = connectedClients.get(clientDetails);
//            connectedClient.stepSucceeded(stepSuccessMessage);
        }

        @Override
        public void receiveStepFailed(ClientDetails clientDetails, StepFailedMessage stepFailedMessage) {
            log.info("received a STEP_FAILED message!");
            log.info(stepFailedMessage.toString());

            ConnectedClient connectedClient = connectedClients.get(clientDetails);
//            connectedClient.stepFailed(stepSuccessMessage);
        }
    }

    public static void main(String[] args) {
        StdOutLogProvider.setLogLevel(LogLevel.INFO);
        StepServer stepServer = new StepServer();
        stepServer.startServer(DEFAULT_SERVER_NAME);
        log.info("Socket Server Started!");
    }

}
