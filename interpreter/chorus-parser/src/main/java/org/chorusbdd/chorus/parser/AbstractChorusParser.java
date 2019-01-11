/**
 * MIT License
 *
 * Copyright (c) 2019 Chorus BDD Organisation.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.chorusbdd.chorus.parser;

import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;
import org.chorusbdd.chorus.results.ScenarioToken;
import org.chorusbdd.chorus.results.StepToken;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: nick
 * Date: 23/02/13
 * Time: 23:12
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractChorusParser<E> implements ChorusParser<E> {

    private ChorusLog log = ChorusLogFactory.getLog(AbstractChorusParser.class);

    protected StepToken createStepToken(String line) {
        int indexOfSpace = line.indexOf(' ');
        String type = line.substring(0, indexOfSpace).trim();
        String action = line.substring(indexOfSpace, line.length()).trim();
        return StepToken.createStep(type, action);
    }

    protected StepToken createStepToken(String type, String action) {
        return StepToken.createStep(type, action);
    }

    protected StepToken addStepToScenario(ScenarioToken scenarioToken, StepToken stepToken, List<StepMacro> stepMacros) {
        //we first see if the step matches any StepMacros which were defined during pre-parsing
        //and if so populate child steps

        //only accept the first valid matching step macro we find, log warnings about any duplicate matches
        boolean alreadyMatched = false;
        for (StepMacro s : stepMacros) {
            alreadyMatched = s.processStep(stepToken, stepMacros, alreadyMatched);
        }

        scenarioToken.addStep(stepToken);
        return stepToken;
    }

    public static String removeComments(String line) {
        //remove commented portion
        if ( line.length() > 0) {
          String[] tokens = line.split("(#[^!]|#$)");  //any # which is not followed by !, or any # which is at the end of a line
          line = tokens.length > 0 ? tokens[0] : "";
        }
        return line.trim();
    }
}
