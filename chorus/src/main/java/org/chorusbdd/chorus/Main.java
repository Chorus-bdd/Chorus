/**
 *  Copyright (C) 2000-2012 The Software Conservancy and Original Authors.
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

import org.chorusbdd.chorus.core.interpreter.ChorusInterpreter;
import org.chorusbdd.chorus.core.interpreter.ExecutionListener;
import org.chorusbdd.chorus.core.interpreter.ExecutionListenerSupport;
import org.chorusbdd.chorus.core.interpreter.results.ExecutionToken;
import org.chorusbdd.chorus.core.interpreter.results.FeatureToken;
import org.chorusbdd.chorus.core.interpreter.scanner.FeatureScanner;
import org.chorusbdd.chorus.util.config.ChorusConfig;
import org.chorusbdd.chorus.util.config.InterpreterProperty;
import org.chorusbdd.chorus.util.config.InterpreterPropertyException;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by: Steve Neal & Nick Ebbutt, ChorusBDD.org
 */
public class Main {

    private final ExecutionListenerSupport listenerSupport = new ExecutionListenerSupport();
    private final ExecutionListenerFactory factory = new ExecutionListenerFactory();
    private final ChorusConfig baseConfig;

    public static void main(String[] args) throws Exception {

        boolean success = false;
        try {
            Main main = new Main(args);
            success = main.run();
        } catch (InterpreterPropertyException e) {
            System.err.println(e.getMessage());
            ChorusConfig.logHelp();
        }

        //We should exit with a code between 0-255 since this is the valid range for unix exit statuses
        //(windows supports signed integer exit status, unix does not)
        //choosing the most obvious, 0 = success, 1 = failure, we could expand on this if needed
        int exitCode = success ? 0 : 1;
        System.exit(exitCode);
    }


    public Main(String[] args) throws InterpreterPropertyException {
        baseConfig = new ChorusConfig(args);
        baseConfig.readConfiguration();
        List<ExecutionListener> listeners = factory.createExecutionListener(baseConfig);
        listenerSupport.addExecutionListener(listeners);
    }

    /**
     * Run interpreter using just the base configuration and the listeners provided
     * @return true, if all tests were fully implemented and passed
     */
    public boolean run() throws Exception {
        ExecutionToken t = startTests();
        List<FeatureToken> features = run(t, ConfigMutator.NULL_MUTATOR);
        endTests(t, features);
        return t.isPassedAndFullyImplemented();
    }

    /**
     * Start tests, notifying executionListeners
     * @return an executionToken to collate results for this test run
     */
    public ExecutionToken startTests() {
        ExecutionToken t = new ExecutionToken(baseConfig.getSuiteName());
        listenerSupport.notifyStartTests(t);
        return t;
    }

    /**
     * End tests, notifying executionListeners
     * @param t, the executionToken containing results for this test run
     * @param features, the features which were run during this test run
     */
    public void endTests(ExecutionToken t, List<FeatureToken> features) {
        listenerSupport.notifyTestsCompleted(t, features);
    }

    /**
     * Run the interpreter once for each configMutator, adding executed features to the list of features
     * and collating results within the executionToken
     */
    public List<FeatureToken> run(ExecutionToken t, ConfigMutator... configMutators) throws Exception {
        List<FeatureToken> features = new ArrayList<FeatureToken>();
        for ( ConfigMutator c : configMutators) {
            ChorusConfig childConfig = c.getNewConfig(baseConfig);
            List<FeatureToken> featuresThisPass = run(t, childConfig);
            features.addAll(featuresThisPass);
        }
        return features;
    }

    /**
     * Run the interpreter, collating results into the executionToken and notifying
     */
    private List<FeatureToken> run(ExecutionToken executionToken, ChorusConfig config) throws Exception {
        //prepare the interpreter
        ChorusInterpreter chorusInterpreter = new ChorusInterpreter();
        List<String> handlerPackages = config.getValues(InterpreterProperty.HANDLER_PACKAGES);
        if (handlerPackages != null) {
            chorusInterpreter.setBasePackages(handlerPackages.toArray(new String[handlerPackages.size()]));
        }

        chorusInterpreter.setDryRun(config.isTrue(InterpreterProperty.DRY_RUN));

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

        chorusInterpreter.addExecutionListeners(listenerSupport.getListeners());
        List<FeatureToken> features = chorusInterpreter.processFeatures(executionToken, featureFiles);
        return features;
    }

    public String getSuiteName() {
        return baseConfig.getSuiteName();
    }

    public List<String> getFeatureFilePaths() {
        return baseConfig.getValues(InterpreterProperty.FEATURE_PATHS);
    }

    public void addExecutionListener(ExecutionListener... listeners) {
        listenerSupport.addExecutionListener(listeners);
    }

    public boolean removeExecutionListener(ExecutionListener... listeners) {
        return listenerSupport.removeExecutionListener(listeners);
    }

    public void addExecutionListener(Collection<ExecutionListener> listeners) {
        listenerSupport.addExecutionListener(listeners);
    }

    public void removeExecutionListeners(List<ExecutionListener> listeners) {
        listenerSupport.removeExecutionListeners(listeners);
    }

    public void setExecutionListener(ExecutionListener... listener) {
        listenerSupport.setExecutionListener(listener);
    }

    public List<ExecutionListener> getListeners() {
        return listenerSupport.getListeners();
    }
}