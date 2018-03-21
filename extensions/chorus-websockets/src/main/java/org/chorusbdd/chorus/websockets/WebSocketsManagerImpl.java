package org.chorusbdd.chorus.websockets;

import org.chorusbdd.chorus.annotations.ExecutionPriority;
import org.chorusbdd.chorus.annotations.Scope;
import org.chorusbdd.chorus.executionlistener.ExecutionListener;
import org.chorusbdd.chorus.executionlistener.ExecutionListenerAdapter;
import org.chorusbdd.chorus.logging.*;
import org.chorusbdd.chorus.results.ExecutionToken;
import org.chorusbdd.chorus.results.FeatureToken;
import org.chorusbdd.chorus.results.ScenarioToken;
import org.chorusbdd.chorus.stepinvoker.StepInvoker;
import org.chorusbdd.chorus.websockets.config.WebSocketsConfig;
import org.chorusbdd.chorus.websockets.config.WebSocketsConfigBuilderFactory;
import org.chorusbdd.chorus.websockets.config.WebSocketsConfigBeanValidator;
import org.chorusbdd.chorus.websockets.config.WebSocketsConfigBuilder;
import org.chorusbdd.chorus.websockets.message.*;
import org.chorusbdd.chorus.util.ChorusException;
import org.chorusbdd.chorus.util.PolledAssertion;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.chorusbdd.chorus.util.assertion.ChorusAssert.fail;

/**
 * Created by nick on 09/12/2016.
 */
public class WebSocketsManagerImpl implements WebSocketsManager {

    private static ChorusLog log = ChorusLogFactory.getLog(WebSocketsManagerImpl.class);

    private final WebSocketsConfigBuilderFactory webSocketsConfigBeanFactory = new WebSocketsConfigBuilderFactory();
    private final WebSocketsConfigBeanValidator webSocketsConfigBeanValidator = new WebSocketsConfigBeanValidator();


    private ChorusWebSocketServer webSocketServer;
    private final AtomicBoolean isRunning = new AtomicBoolean();

    private final Map<String, WebSocketClientStepInvoker> stepIdToInvoker = new ConcurrentHashMap<>();

    private final Set<String> connectedClients = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private final Set<String> alignedClients = Collections.newSetFromMap(new ConcurrentHashMap<>());

    private CleanupShutdownHook cleanupShutdownHook = new CleanupShutdownHook();
    
    /**
     * At present we only support one step server instance, the 'default', and this is the config
     */
    private WebSocketsConfig webSocketsConfig;

    public WebSocketsManagerImpl() {
        addShutdownHook();
    }

    private void addShutdownHook() {
        log.trace("Adding shutdown hook for ProcessHandler " + this);
        Runtime.getRuntime().addShutdownHook(cleanupShutdownHook);
    }

    public void startWebSocketServer(Properties properties) {
        if ( ! isRunning.getAndSet(true)) {

            webSocketsConfig = getWebSocketsConfig(DEFAULT_WEB_SOCKET_SERVER_NAME, properties);
            checkConfig(webSocketsConfig);

            int port = webSocketsConfig.getPort();
            log.info("Starting Web Socket server on port " + port);
            webSocketServer = new ChorusWebSocketServer(port);

            MessageProcessor messageProcessor = new MessageProcessor(webSocketServer);
            webSocketServer.setWebSocketMessageProcessor(messageProcessor);
            webSocketServer.start();
        } else {
            //when multiple configurations are supported this will change
            log.error("Only one step server can be started");
        }
    }

    private void checkConfig(WebSocketsConfig config) {
        boolean validConfig = webSocketsConfigBeanValidator.isValid(config);
        if ( ! validConfig) {
            log.warn(webSocketsConfigBeanValidator.getErrorDescription());
            fail("Remoting config must be valid for " + config.getConfigName());
        }
    }

    @Override
    public boolean waitForClientConnection(String clientName) {
        if ( isRunning.get() ) {
            PolledAssertion polledAssertion = new PolledAssertion() {
                @Override
                protected void validate() throws Exception {
                if ( ! alignedClients.contains(clientName)) {
                    boolean connected = connectedClients.contains(clientName);
                    String message = connected ? "connect" : "finish publishing steps (send steps aligned)";
                    throw new ChorusException("Client " + clientName + " did not " + message);
                }
                }
            };

            boolean result = true;
            try {
                polledAssertion.await(TimeUnit.SECONDS, webSocketsConfig.getClientConnectTimeoutSeconds());
            } catch (AssertionError assertionError) {
                result = false;
            }
            return result;
        } else {
            throw new ChorusException("Web Socket Server is not running");
        }
    }

    @Override
    public boolean isClientConnected(String clientName) {
       return alignedClients.contains(clientName);
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
    public void stopWebSocketServer() {
        if ( isRunning.getAndSet(false) ) {
            log.debug("Stopping Web Sockets server");
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

        @ExecutionPriority(ExecutionPriority.WEB_SOCKETS_MANAGER_PRIORITY)
        class WebSocketsExecutionListener extends ExecutionListenerAdapter {

            @Override
            public void featureCompleted(ExecutionToken testExecutionToken, FeatureToken feature) {
                if ( isRunning.get() && webSocketsConfig.getScope() == Scope.FEATURE) {
                    stopWebSocketServer();
                }
            }

            @Override
            public void scenarioCompleted(ExecutionToken testExecutionToken, ScenarioToken scenarioToken) {
                if ( isRunning.get() && webSocketsConfig.getScope() == Scope.SCENARIO) {
                    stopWebSocketServer();
                }
            }
        };
        return new WebSocketsExecutionListener();
    }

    private class MessageProcessor implements WebSocketMessageProcessor {


        private WebSocketMessageRouter messageRouter;

        public MessageProcessor(WebSocketMessageRouter messageRouter) {
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
                    messageRouter, publishStep, webSocketsConfig.getStepTimeoutSeconds()
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
            log.debug("WebSocketsManagerImpl client " + clientId + " disconnected, removing client");
            removeClient(clientId);
        }

    }

    private void removeClient(String clientId) {
        connectedClients.remove(clientId);
        alignedClients.remove(clientId);
        stepIdToInvoker.values().removeIf(stepInvoker -> stepInvoker.getClientId().equals(clientId));
    }

    private WebSocketsConfig getWebSocketsConfig(String configName, Properties webSocketsProperties) {
        WebSocketsConfigBuilder config = webSocketsConfigBeanFactory.createConfigBuilder(webSocketsProperties, configName);
        return config.build();
    }

    /**
     * If shut down before a scenario completes, try as hard as we can to cleanly close down any open web socket servers
     */
    private class CleanupShutdownHook extends Thread {
        public void run() {
            log.debug("Running Cleanup on shutdown for WebSocketsManager");
            try {
                stopWebSocketServer();
            } catch (Throwable t) {
                log.debug("Failed during cleanup", t);
            }
        }
    }


    public static void main(String[] args) {
        StdOutLogProvider.setLogLevel(LogLevel.DEBUG);
        WebSocketsManagerImpl webSocketsManager = new WebSocketsManagerImpl();
        Properties properties = new Properties();
        properties.put("port", "9080");
        properties.put("stepTimeoutSeconds", 30);
        webSocketsManager.startWebSocketServer(properties);

        log.info("Socket Server Started!");
    }

}
