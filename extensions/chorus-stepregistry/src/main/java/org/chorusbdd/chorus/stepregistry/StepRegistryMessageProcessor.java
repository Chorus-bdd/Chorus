package org.chorusbdd.chorus.stepregistry;

import org.chorusbdd.chorus.stepregistry.message.*;

/**
 * Created by nick on 09/12/2016.
 */
public interface StepRegistryMessageProcessor {

    void receiveClientConnected(ConnectMessage connectMessage);

    void receivePublishStep(PublishStepMessage publishStep);

    void receiveStepsAligned(StepsAlignedMessage stepsAlignedMessage);

    void receiveStepSucceeded(StepSucceededMessage stepSucceededMessage);

    void receiveStepFailed(StepFailedMessage stepFailedMessage);

    void clientDisconnected(String clientId);
}
