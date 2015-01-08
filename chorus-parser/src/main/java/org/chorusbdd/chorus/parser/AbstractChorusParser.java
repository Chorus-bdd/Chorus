/**
 *  Copyright (C) 2000-2014 The Software Conservancy and Original Authors.
 *  All rights reserved.
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to
 *  deal in the Software without restriction, including without limitation the
 *  rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 *  sell copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 *  FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 *  IN THE SOFTWARE.
 *
 *  Nothing in this notice shall be deemed to grant any rights to trademarks,
 *  copyrights, patents, trade secrets or any other intellectual property of the
 *  licensor or any contributor except as expressly stated herein. No patent
 *  license is granted separate from the Software, for code that you delete from
 *  the Software, or for combinations of the Software with other software or
 *  hardware.
 */
package org.chorusbdd.chorus.parser;

import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;
import org.chorusbdd.chorus.results.ScenarioToken;
import org.chorusbdd.chorus.results.StepToken;

import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: nick
 * Date: 23/02/13
 * Time: 23:12
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractChorusParser<E> implements ChorusParser<E> {

    private static ChorusLog log = ChorusLogFactory.getLog(AbstractChorusParser.class);

    private List<StepToken> bufferedStepDirectives = new LinkedList<StepToken>();
    private List<StepToken> bufferedKeyWordDirectives = new LinkedList<StepToken>();

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

    protected void addStepDirectives(StepConsumer c, int lineNumber) throws ParseException {
        //the key word directive buffer should be clear, steps can only have step-level directives
        //which are appended to the step text
        if ( bufferedKeyWordDirectives.size() > 0) {
            throw new ParseException("Invalid location for directive " + bufferedKeyWordDirectives.get(0), lineNumber);
        }

        for(StepToken d : bufferedStepDirectives) {
            c.addStep(d);
        }
        clearDirectives();
    }

    protected void addKeyWordDirectives(StepConsumer c, int lineNumber) throws ParseException {
        //the step directive buffer should be clear
        //key words can only have directives on a dedicated line which precedes the key work
        if ( bufferedStepDirectives.size() > 0) {
            throw new ParseException("Invalid location for directive " + bufferedStepDirectives.get(0), lineNumber);
        }

        for(StepToken d : bufferedKeyWordDirectives) {
            c.addStep(d);
        }
        clearDirectives();
    }


    protected String removeAndBufferDirectives(String line) {
        String[] tokens = line.split(StepToken.DIRECTIVE_TYPE);

        String lineAfterDirectivesWereStripped = tokens[0];

        //if the line is empty after we parse the directives, this must be a keyword directive
        //rather than a step-level directive which appears at the end of a line
        List<StepToken> bufferedDirectives = isEmptyLineOrComment(lineAfterDirectivesWereStripped) ?
                bufferedKeyWordDirectives :
                bufferedStepDirectives;

        //the first token is the step type and action, followed by zero or more directives
        //we want to process the directives and return the step text with the directives stripped
        for ( int loop=1; loop < tokens.length; loop++) {
            addBufferedDirective(tokens[loop].trim(), bufferedDirectives);
        }
        return lineAfterDirectivesWereStripped;
    }

    protected boolean isEmptyLineOrComment(String line) {
        return line.startsWith("#") || line.length() == 0;
    }


    private void addBufferedDirective(String directive, List<StepToken> bufferedDirectives) {
        if ( directive.length() > 0) {
            StepToken s = StepToken.createDirective(directive);
            bufferedDirectives.add(s);
        }
    }

    protected void clearDirectives() {
        bufferedStepDirectives.clear();
        bufferedKeyWordDirectives.clear();
    }

    protected List<StepToken> getKeyWordDirectives() {
        return bufferedKeyWordDirectives;
    }
}
