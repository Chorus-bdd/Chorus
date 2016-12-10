package org.chorusbdd.chorus.stepserver;

import org.chorusbdd.chorus.stepserver.message.ConnectMessage;
import org.chorusbdd.chorus.stepserver.message.PublishStep;

/**
 * Created by nick on 09/12/2016.
 */
public interface StepServerMessageProcessor {

    void processClientConnected(ConnectMessage connectMessage);

    void processPublishStep(PublishStep publishStep);
}
