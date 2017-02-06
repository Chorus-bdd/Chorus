package org.chorusbdd.chorus.stepserver;

import org.chorusbdd.chorus.executionlistener.ExecutionListener;
import org.chorusbdd.chorus.executionlistener.ExecutionListenerAdapter;
import org.chorusbdd.chorus.logging.*;
import org.chorusbdd.chorus.results.ExecutionToken;
import org.chorusbdd.chorus.results.FeatureToken;
import org.chorusbdd.chorus.stepinvoker.StepInvoker;
import org.chorusbdd.chorus.stepserver.config.StepServerConfig;
import org.chorusbdd.chorus.stepserver.config.StepServerConfigBeanFactory;
import org.chorusbdd.chorus.stepserver.config.StepServerConfigBeanValidator;
import org.chorusbdd.chorus.stepserver.config.StepServerConfigBuilder;
import org.chorusbdd.chorus.stepserver.message.*;
import org.chorusbdd.chorus.util.ChorusException;
import org.chorusbdd.chorus.util.PolledAssertion;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by nick on 09/12/2016.
 */
public class StepServer implements StepServerManager {

    private static ChorusLog log = ChorusLogFactory.getLog(StepServer.class);

    private final StepServerConfigBeanFactory stepServerConfigBeanFactory = new StepServerConfigBeanFactory();
    private final StepServerConfigBeanValidator stepServerConfigBeanValidator = new StepServerConfigBeanValidator();


    private ChorusWebSocketServer webSocketServer;
    private final AtomicBoolean isRunning = new AtomicBoolean();

    private final Map<String, WebSocketClientStepInvoker> stepIdToInvoker = new ConcurrentHashMap<>();

    private final Set<String> connectedClients = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private final Set<String> alignedClients = Collections.newSetFromMap(new ConcurrentHashMap<>());

    /**
     * At present we only support one step server instance, the 'default', and this is the config
     */
    private StepServerConfig stepServerConfig;

    public StepServer() {}

    public void startServer(Properties properties) {
        if ( ! isRunning.getAndSet(true)) {

            stepServerConfig = getStepServerConfig(DEFAULT_SERVER_NAME, properties);

            int port = stepServerConfig.getPort();
            log.info("Starting StepServer on port " + port);
            webSocketServer = new ChorusWebSocketServer(port);

            MessageProcessor messageProcessor = new MessageProcessor(webSocketServer);
            webSocketServer.setStepServerMessageProcessor(messageProcessor);
            webSocketServer.start();
        } else {
            //when multiple configurations are supported this will change
            log.error("Only one step server can be started");
        }
    }

    @Override
    public boolean waitForClientConnection(String clientName) throws ClientConnectionException {
        if ( isRunning.get() ) {
            PolledAssertion polledAssertion = new PolledAssertion() {
                @Override
                protected void validate() throws Exception {
                    if ( ! alignedClients.contains(clientName)) {
                        throw new ChorusException("Client " + clientName + " did not connect");
                    }
                }
            };

            boolean result = true;
            try {
                polledAssertion.await(TimeUnit.SECONDS, stepServerConfig.getClientConnectTimeoutSeconds());
            } catch (AssertionError assertionError) {
                result = false;
            }
            return result;
        } else {
            throw new ChorusException("Step Server is not running");
        }
    }

    @Override
    public List<StepInvoker> getStepInvokers() {
        return new ArrayList<>(stepIdToInvoker.values());
    }

    @Override
    public void showAllSteps() {
        ChorusOut.out.println("Steps:");
         stepIdToInvoker.values().stream()
            .map(i -> i.getStepPattern().toString())
            .sorted()
            .map(s -> " " + s + "\n")
            .forEach(ChorusOut.out::print);

    }

    @Override
    public void stopServer() {
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
                stopServer();
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
                stepInvoker = WebSocketClientStepInvoker.create(
                    messageRouter, publishStep, stepServerConfig.getStepTimeoutSeconds()
                );
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

    private StepServerConfig getStepServerConfig(String configName, Properties stepServerProperties) {
        StepServerConfigBuilder config = stepServerConfigBeanFactory.createConfig(stepServerProperties, configName);
        return config.build();
    }

    public static void main(String[] args) {
        StdOutLogProvider.setLogLevel(LogLevel.DEBUG);
        StepServer stepServer = new StepServer();
        Properties properties = new Properties();
        properties.put("port", "9080");
        properties.put("stepTimeoutSeconds", 30);
        stepServer.startServer(properties);

        log.info("Socket Server Started!");
    }

}
