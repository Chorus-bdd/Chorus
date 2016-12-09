package org.chorusbdd.chorus.stepserver;

import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;
import org.chorusbdd.chorus.stepserver.message.ConnectMessage;

/**
 * Created by nick on 09/12/2016.
 */
public class StepServer {

    private static ChorusLog log = ChorusLogFactory.getLog(StepServer.class);

    public static void main(String[] args) {


        StepServerMessageProcessor stepServerMessageProcessor = new StepServerMessageProcessor() {
            @Override
            public void processClientConnected(ConnectMessage connectMessage) {
                log.info("Yea - received a connect message!");
                log.info(connectMessage.toString());
            }
        };

        new ChorusWebSocketServer(9080, stepServerMessageProcessor).start();

        log.info("Started!");
    }
}
