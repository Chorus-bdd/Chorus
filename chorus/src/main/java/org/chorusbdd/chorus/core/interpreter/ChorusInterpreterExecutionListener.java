package org.chorusbdd.chorus.core.interpreter;

import org.chorusbdd.chorus.core.interpreter.results.FeatureToken;
import org.chorusbdd.chorus.core.interpreter.results.ResultsSummary;
import org.chorusbdd.chorus.core.interpreter.results.ScenarioToken;
import org.chorusbdd.chorus.core.interpreter.results.StepToken;

/**
 * Implementors can register with a ChorusInterpreter to recieve callbacks during test execution.
 *
 * Created by: Steve Neal
 * Date: 11/01/12
 */
public interface ChorusInterpreterExecutionListener {

    public void featureStarted(FeatureToken feature);
    public void scenarioStarted(ScenarioToken scenario);
    public void stepExecuted(StepToken step);
    public void testsCompleted(ResultsSummary results);
}
