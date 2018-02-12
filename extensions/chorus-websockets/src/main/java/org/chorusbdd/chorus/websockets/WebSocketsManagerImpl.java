package org.chorusbdd.chorus.websockets;

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


    private ChorusWebSocketRegistry webSocketServer;
    private final AtomicBoolean isRunning = new AtomicBoolean();

    private final Map<String, WebSocketClientStepInvoker> stepIdToInvoker = new ConcurrentHashMap<>();

    private final Set<String> connectedClients = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private final Set<String> alignedClients = Collections.newSetFromMap(new ConcurrentHashMap<>());

    /**
     * At present we only support one step server instance, the 'default', and this is the config
     */
    private WebSocketsConfig webSocketsConfig;

    public WebSocketsManagerImpl() {}

    public void startWebSocketServer(Properties properties) {
        if ( ! isRunning.getAndSet(true)) {

            webSocketsConfig = getWebSocketsConfig(DEFAULT_WEB_SOCKET_SERVER_NAME, properties);
            checkConfig(webSocketsConfig);

            int port = webSocketsConfig.getPort();
            log.info("Starting Web Socket server on port " + port);
            webSocketServer = new ChorusWebSocketRegistry(port);

            MessageProcessor messageProcessor = new MessageProcessor(webSocketServer);
            webSocketServer.setStepRegistryMessageProcessor(messageProcessor);
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
                    throw new ChorusException("Client " + clientName + " did not connect");
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
            throw new ChorusException("Step Server is not running");
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
        return new ExecutionListenerAdapter() {
            
            public void featureCompleted(ExecutionToken testExecutionToken, FeatureToken feature) {
                if ( isRunning.get() && webSocketsConfig.getScope() == Scope.FEATURE) {
                    stopWebSocketServer();
                }
            }

            public void scenarioCompleted(ExecutionToken testExecutionToken, ScenarioToken scenarioToken) {
                if ( isRunning.get() && webSocketsConfig.getScope() == Scope.SCENARIO) {
                    stopWebSocketServer();
                }
            }
        };
    }

    private class MessageProcessor implements StepRegistryMessageProcessor {


        private StepRegistryMessageRouter messageRouter;

        public MessageProcessor(StepRegistryMessageRouter messageRouter) {
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

        Iterator<WebSocketClientStepInvoker> i = stepIdToInvoker.values().iterator();
        while(i.hasNext()) {
            WebSocketClientStepInvoker stepInvoker = i.next();
            if ( stepInvoker.getClientId().equals(clientId)) {
                i.remove();
            }
        }
    }

    private WebSocketsConfig getWebSocketsConfig(String configName, Properties webSocketsProperties) {
        WebSocketsConfigBuilder config = webSocketsConfigBeanFactory.createConfigBuilder(webSocketsProperties, configName);
        return config.build();
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
