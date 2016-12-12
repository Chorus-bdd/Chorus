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

    public StepServer() {}

    public void startServer(String serverName) {
        if ( ! isRunning.getAndSet(true)) {
            //TODO implement configuration by server name
            log.info("Starting StepServer on port port");
            StepServerMessageProcessor stepServerMessageProcessor = new StepServerMessageProcessor();
            webSocketServer = new ChorusWebSocketServer(DEFAULT_PORT, stepServerMessageProcessor);
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

    private class StepServerMessageProcessor implements org.chorusbdd.chorus.stepserver.StepServerMessageProcessor {

        @Override
        public void receiveClientConnected(ConnectMessage connectMessage) {
            log.info("Yes!! - received a CONNECT message!");
            log.info(connectMessage.toString());
        }

        @Override
        public void receivePublishStep(PublishStep publishStep) {
            log.info("Yeeassss!! - received a PUBLISH_STEP message!");
            log.info(publishStep.toString());
        }

        @Override
        public void receiveStepsAligned(StepsAlignedMessage stepsAlignedMessage) {
            log.info("WhooHooo!! - received a PUBLISH_STEP message!");
            log.info(stepsAlignedMessage.toString());
        }

        @Override
        public void receiveStepSucceeded(StepSucceededMessage stepSuccessMessage) {
            log.info("Yeeeeaas!! - received a STEP_SUCCEEDED message!");
            log.info(stepSuccessMessage.toString());
        }

        @Override
        public void receiveStepFailed(StepFailedMessage stepFailedMessage) {
            log.info("Noooooo!! - received a STEP_FAILED message!");
            log.info(stepFailedMessage.toString());
        }
    }

    public static void main(String[] args) {
        StdOutLogProvider.setLogLevel(LogLevel.INFO);
        StepServer stepServer = new StepServer();
        stepServer.startServer(DEFAULT_SERVER_NAME);
        log.info("Socket Server Started!");
    }

}
