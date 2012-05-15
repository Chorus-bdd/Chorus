package org.chorusbdd.chorus.core.interpreter;

import org.chorusbdd.chorus.core.interpreter.token.FeatureToken;
import org.chorusbdd.chorus.core.interpreter.token.ScenarioToken;
import org.chorusbdd.chorus.core.interpreter.token.StepToken;

/**
 * Implementors can register with a ChorusInterpreter to recieve callbacks during test execution.
 *
 * Created by: Steve Neal
 * Date: 11/01/12
 */
public interface ChorusInterpreterExecutionListener {
    public void startFeature(FeatureToken feature);
    public void startScenario(ScenarioToken scenario);
    public void stepExecuted(StepToken step);
}
