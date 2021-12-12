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

import org.chorusbdd.chorus.results.StepToken;

import java.util.*;

/**
 * Created by Nick E on 08/01/2015.
 *
 * <pre>
 * This class contains the logic to parse and process directives
 *
 * A directive is prefixed with #! and can appear in two forms:
 *
 * a) keyword-directive
 *
 * 'Keyword' directives can appear before some keywords, such as Scenario:
 * See the KeyWord enumeration which makes clear which keywords permit directives.
 * These appear on their own line, as below:
 *
 * #! My Directive One
 * #! My Directive Two  #! My Directive Three
 * Scenario: My Scenario
 *
 *
 * b) step-directive
 *
 * One or more 'Step' directives can be appended to a step, e.g.
 *
 * Given I run a step      #! My Directive One
 * When I run a step       #! My Directive One   #! My Directive Two
 *
 * </pre>
 */
public class DirectiveParser {

    private List<LineNumberAndDirective> bufferedStepDirectives = new LinkedList<>();
    private List<LineNumberAndDirective> bufferedKeyWordDirectives = new LinkedList<>();

    public String parseDirectives(String line, int lineNumber) {
        String[] tokens = line.split(StepToken.DIRECTIVE_TYPE);

        String lineAfterDirectivesWereStripped = tokens[0];

        //if the line is empty after we parse the directives, this must be a keyword directive
        //rather than a step-level directive which appears at the end of a line
        List<LineNumberAndDirective> bufferedDirectives = isEmptyLineOrComment(lineAfterDirectivesWereStripped) ?
                bufferedKeyWordDirectives :
                bufferedStepDirectives;

        //the first token is the step type and action, followed by zero or more directives
        //we want to process the directives and return the step text with the directives stripped
        for ( int loop=1; loop < tokens.length; loop++) {
            addBufferedDirective(tokens[loop].trim(), bufferedDirectives, lineNumber);
        }
        return lineAfterDirectivesWereStripped;
    }

    public void addStepDirectives(StepConsumer c) throws ParseException {
        //the key word directive buffer should be clear, steps can only have step-level directives
        //which are appended to the step text
        logErrorIfDirectivesExist(bufferedKeyWordDirectives);
        addAndClearDirectives(bufferedStepDirectives, c);
    }

    public void addKeyWordDirectives(StepConsumer c) throws ParseException {
        //the step directive buffer should be clear
        //key words can only have directives on a dedicated line which precedes the key work
        logErrorIfDirectivesExist(bufferedStepDirectives);
        addAndClearDirectives(bufferedKeyWordDirectives, c);
    }

    //We can never have step directives associated with a keyword
    //If a keyword supports directives, then we can have one more more 'keyword' directives but no 'step' directives before a keyword
    public void checkDirectivesForKeyword(DirectiveParser directiveParser, KeyWord keyWord) throws ParseException {
        checkNoStepDirectiveBeforeKeyword(directiveParser, keyWord);
        checkForInvalidKeywordDirective(directiveParser, keyWord);
    }

    public void clearDirectives() {
        bufferedStepDirectives.clear();
        bufferedKeyWordDirectives.clear();
    }

    private void checkForInvalidKeywordDirective(DirectiveParser directiveParser, KeyWord keyWord) throws ParseException {
        if (directiveParser.keywordDirectivesExist() && ! keyWord.isSupportsDirectives()) {
            LineNumberAndDirective d = directiveParser.getFirstKeywordDirective();
            throw new ParseException("Cannot add directive [" + d.getDirective() + "] before the keyword " + keyWord, d.getLine());
        }
    }

    private void checkNoStepDirectiveBeforeKeyword(DirectiveParser directiveParser, KeyWord keyWord) throws ParseException {
        if (directiveParser.stepDirectivesExist()) {
            LineNumberAndDirective d = directiveParser.getFirstStepDirective();
            throw new ParseException("Cannot add step directive [" + d.getDirective() + "] before keyword" + keyWord, d.getLine());
        }
    }

    private void logErrorIfDirectivesExist(List<LineNumberAndDirective> directives) throws ParseException {
        if (!directives.isEmpty()) {
            LineNumberAndDirective firstInvalidDirective = directives.get(0);
            throw new ParseException("Invalid location for directive " + firstInvalidDirective.getDirective(), firstInvalidDirective.getLine());
        }
    }

    private void addAndClearDirectives(List<LineNumberAndDirective> directives, StepConsumer c) {
        for(LineNumberAndDirective l : directives) {
            c.addStep(l.getDirective());
        }
        clearDirectives();
    }


    private boolean isEmptyLineOrComment(String line) {
        return line.startsWith("#") || line.length() == 0;
    }


    private void addBufferedDirective(String directive, List<LineNumberAndDirective> bufferedDirectives, int lineNumber) {
        if ( directive.length() > 0) {
            StepToken s = StepToken.createDirective(directive);
            bufferedDirectives.add(new LineNumberAndDirective(lineNumber, s));
        }
    }

    private boolean stepDirectivesExist() {
        return !bufferedStepDirectives.isEmpty();
    }

    private boolean keywordDirectivesExist() {
        return !bufferedKeyWordDirectives.isEmpty();
    }

    private LineNumberAndDirective getFirstKeywordDirective() {
        return bufferedKeyWordDirectives.get(0);
    }

    private LineNumberAndDirective getFirstStepDirective() {
        return bufferedStepDirectives.get(0);
    }

    //this catches for any unprocessed directives at the end of parsing
    public void checkForUnprocessedDirectives() throws ParseException {
        List<LineNumberAndDirective> remaining = new LinkedList<>();
        remaining.addAll(bufferedKeyWordDirectives);
        remaining.addAll(bufferedStepDirectives);
        if (!remaining.isEmpty()) {
            LineNumberAndDirective exampleError = remaining.get(0);
            throw new ParseException("Invalid trailing directive [" + exampleError.getDirective() + "]", exampleError.getLine());
        }
    }
}
