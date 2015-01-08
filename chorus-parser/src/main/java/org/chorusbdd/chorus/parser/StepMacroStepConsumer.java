package org.chorusbdd.chorus.parser;

import org.chorusbdd.chorus.results.StepToken;

/**
 * Adapt a StepMacro to the StepConsumer interface
 */
public final class StepMacroStepConsumer implements StepConsumer {
    private StepMacro stepMacro;

    public StepMacroStepConsumer(StepMacro stepMacro) {
        this.stepMacro = stepMacro;
    }

    public void addStep(StepToken t) {
        this.stepMacro.addStep(t);
    }
}
