package org.chorusbdd.chorus.parser;

import org.chorusbdd.chorus.results.ScenarioToken;
import org.chorusbdd.chorus.results.StepToken;

/**
 * Adapt a StepMacro to the StepConsumer interface
 */
public final class ScenarioTokenStepConsumer implements StepConsumer {
    private ScenarioToken scenarioToken;

    public ScenarioTokenStepConsumer(ScenarioToken scenarioToken) {
        this.scenarioToken = scenarioToken;
    }

    public void addStep(StepToken t) {
        this.scenarioToken.addStep(t);
    }
}
