package org.chorusbdd.chorus.core.interpreter;

import org.chorusbdd.chorus.results.ScenarioToken;
import org.chorusbdd.chorus.results.StepToken;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: nick
 * Date: 23/02/13
 * Time: 23:12
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractChorusParser<E> {

    public abstract List<E> parse(Reader reader) throws IOException, ParseException;

    protected StepToken createStepToken(String line) {
        int indexOfSpace = line.indexOf(' ');
        String type = line.substring(0, indexOfSpace).trim();
        String action = line.substring(indexOfSpace, line.length()).trim();
        return new StepToken(type, action);
    }

    protected StepToken addStep(ScenarioToken scenarioToken, String line, List<StepMacro> stepMacros) {
        StepToken s = createStepToken(line);
        return addStep(scenarioToken, s, stepMacros);
    }

    protected StepToken addStep(ScenarioToken scenarioToken, String type, String action, List<StepMacro> stepMacros) {
        StepToken s = new StepToken(type, action);
        return addStep(scenarioToken, s, stepMacros);
    }

    protected StepToken addStep(ScenarioToken scenarioToken, StepToken stepToken, List<StepMacro> stepMacros) {
        //we first see if the step matches any StepMacros which were defined during pre-parsing
        //and if so populate child steps
        for (StepMacro s : stepMacros) {
            s.processStep(stepToken, stepMacros);
        }

        scenarioToken.addStep(stepToken);
        return stepToken;
    }

    public static class ParseException extends Exception {

        public ParseException(String message, Throwable cause) {
            super(message, cause);
        }

        ParseException(String message, int lineNumber) {
            super(String.format("%s (at line:%s)", message, lineNumber));
        }
    }
}
