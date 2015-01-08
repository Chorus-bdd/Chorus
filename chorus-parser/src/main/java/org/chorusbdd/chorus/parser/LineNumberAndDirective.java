package org.chorusbdd.chorus.parser;

import org.chorusbdd.chorus.results.StepToken;

/**
* Created by GA2EBBU on 08/01/2015.
*/
public class LineNumberAndDirective {

    private int line;
    private StepToken directive;

    public LineNumberAndDirective(int lineNumber, StepToken directive) {
        this.line = lineNumber;
        this.directive = directive;
    }

    public int getLine() {
        return line;
    }

    public StepToken getDirective() {
        return directive;
    }
}
