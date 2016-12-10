package org.chorusbdd.chorus.stepserver;

import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;
import org.chorusbdd.chorus.logging.LogLevel;
import org.chorusbdd.chorus.logging.StdOutLogProvider;
import org.chorusbdd.chorus.stepserver.message.ConnectMessage;
import org.chorusbdd.chorus.stepserver.message.PublishStep;

/**
 * Created by nick on 09/12/2016.
 */
public class StepServer {

    private static ChorusLog log = ChorusLogFactory.getLog(StepServer.class);

    public static void main(String[] args) {

        StdOutLogProvider.setLogLevel(LogLevel.INFO);

        StepServerMessageProcessor stepServerMessageProcessor = new StepServerMessageProcessor() {

            @Override
            public void processClientConnected(ConnectMessage connectMessage) {
                log.info("Yes!! - received a CONNECT message!");
                log.info(connectMessage.toString());
            }

            @Override
            public void processPublishStep(PublishStep publishStep) {
                log.info("Yeeassss!! - received a PUBLISH_STEP message!");
                log.info(publishStep.toString());
            }
        };

        new ChorusWebSocketServer(9080, stepServerMessageProcessor).start();

        log.info("Socket Server Started!");
    }
}
