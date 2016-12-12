package org.chorusbdd.chorus.stepserver;

import org.chorusbdd.chorus.stepserver.message.*;

/**
 * Created by nick on 09/12/2016.
 */
public interface StepServerMessageProcessor {

    void receiveClientConnected(ClientDetails clientDetails, ConnectMessage connectMessage);

    void receivePublishStep(ClientDetails clientDetails, PublishStepMessage publishStep);

    void receiveStepsAligned(ClientDetails clientDetails, StepsAlignedMessage stepsAlignedMessage);

    void receiveStepSucceeded(ClientDetails clientDetails, StepSucceededMessage stepSucceededMessage);

    void receiveStepFailed(ClientDetails clientDetails, StepFailedMessage stepFailedMessage);
}
