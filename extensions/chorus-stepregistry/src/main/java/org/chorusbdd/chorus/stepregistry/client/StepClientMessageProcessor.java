package org.chorusbdd.chorus.stepregistry.client;

import org.chorusbdd.chorus.stepregistry.message.ExecuteStepMessage;

/**
 * Created by Nick E on 13/12/2016.
 */
public interface StepClientMessageProcessor {

    void executeStep(ExecuteStepMessage executeStepMessage);

}
