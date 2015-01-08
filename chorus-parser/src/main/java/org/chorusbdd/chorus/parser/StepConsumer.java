package org.chorusbdd.chorus.parser;

import org.chorusbdd.chorus.results.StepToken;

/**
 * Created by nick on 08/01/15.
 */
public interface StepConsumer {

    void addStep(StepToken t);
}
