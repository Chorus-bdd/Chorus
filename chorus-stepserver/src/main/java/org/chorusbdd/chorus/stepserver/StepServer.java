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
import org.chorusbdd.chorus.util.PolledAssertion;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertTrue;

/**
 * Created by nick on 09/12/2016.
 */
public class StepServer implements StepServerManager {

    private static ChorusLog log = ChorusLogFactory.getLog(StepServer.class);

    private static final int DEFAULT_PORT = 9080;
    private static final int DEFAULT_TIMEOUT_SECONDS = 5;

    private ChorusWebSocketServer webSocketServer;
    private final AtomicBoolean isRunning = new AtomicBoolean();

    private final Map<String, WebSocketClientStepInvoker> stepIdToInvoker = new ConcurrentHashMap<>();

    private final Set<String> connectedClients = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private final Set<String> alignedClients = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public StepServer() {}

    public void startServer(String serverName) {
        if ( ! isRunning.getAndSet(true)) {
            //TODO implement configuration by server name
            log.info("Starting StepServer on port " + DEFAULT_PORT);
            webSocketServer = new ChorusWebSocketServer(DEFAULT_PORT);
            MessageProcessor messageProcessor = new MessageProcessor(webSocketServer);
            webSocketServer.setStepServerMessageProcessor(messageProcessor);
            webSocketServer.start();
        } else {
            //when multiple configurations are supported this will change
            log.error("Only one step server can be started");
        }
    }

    @Override
    public boolean waitForClientConnection(String clientName, int timeoutSeconds) throws ClientConnectionException {
        PolledAssertion polledAssertion = new PolledAssertion() {
            @Override
            protected void validate() throws Exception {
                assertTrue(alignedClients.contains(clientName));
            }
        };

        boolean result = true;
        try {
            polledAssertion.await(TimeUnit.SECONDS, timeoutSeconds);
        } catch (AssertionError assertionError) {
            result = false;
        }
        return result;
    }

    @Override
    public List<StepInvoker> getStepInvokers() {
        return new ArrayList<>(stepIdToInvoker.values());
    }

    @Override
    public void stopServer(String serverName) {
        if ( isRunning.getAndSet(false) ) {
            log.debug("Stopping StepServer");
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


        private StepServerMessageRouter messageRouter;

        public MessageProcessor(StepServerMessageRouter messageRouter) {
            this.messageRouter = messageRouter;
        }

        @Override
        public void receiveClientConnected(ConnectMessage connectMessage) {
            log.debug("received a CONNECT message!");
            log.debug(connectMessage.toString());
            connectedClients.add(connectMessage.getChorusClientId());
        }

        @Override
        public void receivePublishStep(PublishStepMessage publishStep) {
            log.debug("received a PUBLISH_STEP message!");
            log.debug(publishStep.toString());

            WebSocketClientStepInvoker stepInvoker;
            try {
                stepInvoker = WebSocketClientStepInvoker.create(messageRouter, publishStep, DEFAULT_TIMEOUT_SECONDS);
                stepIdToInvoker.put(publishStep.getStepId(), stepInvoker);
            } catch (InvalidStepException e) {
                log.warn("Invalid step sent by client " + publishStep.getChorusClientId(), e);
            }
        }

        @Override
        public void receiveStepsAligned(StepsAlignedMessage stepsAlignedMessage) {
            log.debug("received a STEPS_ALIGNED message!");
            log.debug(stepsAlignedMessage.toString());
            alignedClients.add(stepsAlignedMessage.getChorusClientId());
        }

        @Override
        public void receiveStepSucceeded(StepSucceededMessage stepSuccessMessage) {
            log.debug("received a STEP_SUCCEEDED message!");
            log.debug(stepSuccessMessage.toString());

            WebSocketClientStepInvoker stepInvoker = stepIdToInvoker.get(stepSuccessMessage.getStepId());
            stepInvoker.stepSucceeded(stepSuccessMessage);
        }

        @Override
        public void receiveStepFailed(StepFailedMessage stepFailedMessage) {
            log.debug("received a STEP_FAILED message!");
            log.debug(stepFailedMessage.toString());

            WebSocketClientStepInvoker stepInvoker = stepIdToInvoker.get(stepFailedMessage.getStepId());
            stepInvoker.stepFailed(stepFailedMessage);
        }

        @Override
        public void clientDisconnected(String clientId) {
            log.debug("StepServer client " + clientId + " disconnected, removing client");
            removeClient(clientId);
        }

    }

    private void removeClient(String clientId) {
        connectedClients.remove(clientId);
        alignedClients.remove(clientId);

        Iterator<WebSocketClientStepInvoker> i = stepIdToInvoker.values().iterator();
        while(i.hasNext()) {
            WebSocketClientStepInvoker stepInvoker = i.next();
            if ( stepInvoker.getClientId().equals(clientId)) {
                i.remove();
            }
        }
    }

    public static void main(String[] args) {
        StdOutLogProvider.setLogLevel(LogLevel.DEBUG);
        StepServer stepServer = new StepServer();
        stepServer.startServer(DEFAULT_SERVER_NAME);
        log.info("Socket Server Started!");
    }

}
