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

    protected StepToken addStep(ScenarioToken scenarioToken, String line) {
        StepToken s = createStepToken(line);
        return scenarioToken.addStep(s);
    }

    protected StepToken addStep(ScenarioToken scenarioToken, String type, String action) {
        StepToken s = new StepToken(type, action);
        scenarioToken.addStep(s);
        return s;
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
