/**
 *  Copyright (C) 2000-2012 The Software Conservancy as Trustee.
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
package org.chorusbdd.chorus;

import org.chorusbdd.chorus.core.interpreter.ChorusExecutionListener;
import org.chorusbdd.chorus.core.interpreter.ChorusInterpreter;
import org.chorusbdd.chorus.core.interpreter.results.TestExecutionToken;
import org.chorusbdd.chorus.core.interpreter.scanner.FeatureScanner;
import org.chorusbdd.chorus.util.config.CommandLineParser;
import org.chorusbdd.chorus.util.config.InterpreterConfiguration;
import org.chorusbdd.chorus.util.config.InterpreterProperty;
import org.chorusbdd.chorus.util.config.InterpreterPropertyException;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by: Steve Neal & Nick Ebbutt, ChorusBDD.org
 */
public class Main {

    public static void main(String[] args) throws Exception {
        boolean failed = true;
        try {
            failed = run(args);
        } catch (InterpreterPropertyException e) {
            System.err.println(e.getMessage());
            InterpreterConfiguration.logHelp();
        }

        System.out.println("Exiting with:" + (failed ? -1 : 0));
        System.exit(failed ? -1 : 0);
    }

    public static boolean run(String[] args) throws Exception {
        InterpreterConfiguration c = new InterpreterConfiguration(args).readConfiguration();
        List<ChorusExecutionListener> l = new ExecutionListenerFactory().createExecutionListener(c);
        return run(c, l.toArray(new ChorusExecutionListener[l.size()]));
    }

    /**
     * Run without exiting on test completion, useful from tests
     *
     * @return true, if all tests were fully implemented and all tests passed
     */
    public static boolean run(InterpreterConfiguration config, ChorusExecutionListener... executionListeners) throws Exception {

        //prepare the interpreter
        ChorusInterpreter chorusInterpreter = new ChorusInterpreter();
        List<String> handlerPackages = config.getValues(InterpreterProperty.HANDLER_PACKAGES);
        if (handlerPackages != null) {
            chorusInterpreter.setBasePackages(handlerPackages.toArray(new String[handlerPackages.size()]));
        }

        chorusInterpreter.setDryRun(config.isSet(InterpreterProperty.DRY_RUN));

        //set a filter tags expression if provided
        if (config.isSet(InterpreterProperty.TAG_EXPRESSION)) {
            List<String> tagExpressionParts = config.getValues(InterpreterProperty.TAG_EXPRESSION);
            StringBuilder builder = new StringBuilder();
            for (String tagExpressionPart : tagExpressionParts) {
                builder.append(tagExpressionPart);
                builder.append(" ");
            }
            chorusInterpreter.setFilterExpression(builder.toString());
        }

        //identify the feature files
        List<String> featureFileNames = config.getValues(InterpreterProperty.FEATURE_PATHS);
        List<File> featureFiles = new FeatureScanner().getFeatureFiles(featureFileNames);

        chorusInterpreter.addExecutionListener(executionListeners);

        String testSuiteName = config.getSuiteName();

        TestExecutionToken executionResultsToken = new TestExecutionToken(testSuiteName);
        chorusInterpreter.processFeatures(executionResultsToken, featureFiles);

        return executionResultsToken.isPassedAndFullyImplemented();
    }

}