package org.chorusbdd.chorus.stepserver.client;

import org.chorusbdd.chorus.stepserver.message.ExecuteStepMessage;

/**
 * Created by GA2EBBU on 13/12/2016.
 */
public interface StepClientMessageProcessor {

    void executeStep(ExecuteStepMessage executeStepMessage);

}
