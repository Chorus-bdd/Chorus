package org.chorusbdd.chorus.websockets.client;

import org.chorusbdd.chorus.websockets.message.ExecuteStepMessage;

/**
 * Created by Nick E on 13/12/2016.
 */
public interface StepClientMessageProcessor {

    void executeStep(ExecuteStepMessage executeStepMessage);

}
