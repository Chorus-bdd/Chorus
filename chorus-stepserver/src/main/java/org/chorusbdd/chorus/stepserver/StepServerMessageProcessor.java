package org.chorusbdd.chorus.stepserver;

import org.chorusbdd.chorus.stepserver.message.*;

/**
 * Created by nick on 09/12/2016.
 */
public interface StepServerMessageProcessor {

    void receiveClientConnected(ConnectMessage connectMessage);

    void receivePublishStep(PublishStep publishStep);

    void receiveStepsAligned(StepsAlignedMessage stepsAlignedMessage);

    void receiveStepSucceeded(StepSucceededMessage stepSucceededMessage);

    void receiveStepFailed(StepFailedMessage stepsAlignedMessage);
}
