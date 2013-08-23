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
import org.chorusbdd.chorus.executionlistener.ExecutionListener;
import org.chorusbdd.chorus.executionlistener.ExecutionListenerSupport;
import org.chorusbdd.chorus.results.EndState;
import org.chorusbdd.chorus.results.ExecutionToken;
import org.chorusbdd.chorus.results.FeatureToken;
import org.chorusbdd.chorus.util.config.ChorusConfigProperty;
import org.chorusbdd.chorus.util.config.ConfigProperties;
import org.chorusbdd.chorus.util.config.ConfigReader;
import org.chorusbdd.chorus.util.config.InterpreterPropertyException;
import org.chorusbdd.chorus.util.logging.ChorusOut;
import org.chorusbdd.chorus.util.logging.StandardOutLogProvider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Created by: Steve Neal & Nick Ebbutt, ChorusBDD.org
 */
public class Chorus {

    private final ExecutionListenerSupport listenerSupport = new ExecutionListenerSupport();
    private InterpreterBuilder interpreterBuilder;

    private final ConfigReader configReader;
    private final FeatureListBuilder featureListBuilder;

    public static void main(String[] args) throws Exception {
        boolean success = false;
        try {
            Chorus chorus = new Chorus(args);
            success = chorus.run();
        } catch (InterpreterPropertyException e) {
            ChorusOut.err.println(e.getMessage());
            ChorusOut.err.print(ChorusConfigProperty.getHelpText());
        }

        //We should exit with a code between 0-255 since this is the valid range for unix exit statuses
        //(windows supports signed integer exit status, unix does not)
        //choosing the most obvious, 0 = success, 1 = failure, we could expand on this if needed
        int exitCode = success ? 0 : 1;
        System.exit(exitCode);
    }

    public Chorus(String[] args) throws InterpreterPropertyException {
        configReader = new ConfigReader(ChorusConfigProperty.getAll(), args);
        configReader.readConfiguration();

        setLoggingProvider();

        //configure logging first
        interpreterBuilder = new InterpreterBuilder(listenerSupport); 
        featureListBuilder = new FeatureListBuilder();

        List<ExecutionListener> listeners = new ExecutionListenerFactory().createExecutionListener(
            configReader
        );
        listenerSupport.addExecutionListener(listeners);
    }

    /**
     * Run interpreter using just the base configuration and the listeners provided
     * @return true, if all tests passed or were marked pending
     */
    public boolean run() throws Exception {
        boolean passed = false;
        try {
            ExecutionToken t = startTests();
            List<FeatureToken> features = run(t, ConfigMutator.NULL_MUTATOR);
            endTests(t, features);
            passed = t.getEndState() == EndState.PASSED || t.getEndState() == EndState.PENDING;
        } catch (InterpreterPropertyException e) {
            ChorusOut.err.println(e.getMessage());
            ChorusOut.err.print(ChorusConfigProperty.getHelpText());
        } catch (Throwable t) {
            ChorusOut.err.println(t.getMessage());
            t.printStackTrace(ChorusOut.err);
        }
        return passed;
    }

    /**
     * Set the log level of Chorus' built in logProvider to the logLevel provided
     */
    public void setLogLevel(String logLevel) {
        StandardOutLogProvider.setLogLevel(logLevel);
    }

    /**
     * Start tests, notifying executionListeners
     * @return an executionToken to collate results for this test run
     */
    public ExecutionToken startTests() {
        ExecutionToken t = new ExecutionToken(getSuiteName());
        listenerSupport.notifyStartTests(t);
        return t;
    }

    /**
     * End tests, notifying executionListeners
     * @param t the executionToken containing results for this test run
     * @param features the features which were run during this test run
     */
    public void endTests(ExecutionToken t, List<FeatureToken> features) {
        t.calculateTimeTaken();
        listenerSupport.notifyTestsCompleted(t, features);
    }

    /**
     * Run the interpreter once for each configMutator, adding executed features to the list of features
     * and collating results within the executionToken
     */
    public List<FeatureToken> run(ExecutionToken t, ConfigMutator configMutator) throws Exception {
        List<FeatureToken> features = new ArrayList<FeatureToken>();
        ConfigProperties p = configMutator.getNewConfig(configReader);

        //set log level here in case log level was a mutated property
        String logLevel = p.getValue(ChorusConfigProperty.LOG_LEVEL);
        setLogLevel(logLevel);

        List<FeatureToken> featuresThisPass = featureListBuilder.getFeatureList(t, p);
        ChorusInterpreter interpreter = interpreterBuilder.buildAndConfigure(p);
        interpreter.processFeatures(t, featuresThisPass);
        features.addAll(featuresThisPass);
        return features;
    }

    public List<String> getFeatureFilePaths() {
        return configReader.getValues(ChorusConfigProperty.FEATURE_PATHS);
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


    //to get the suite name we concatenate all the values provided for suite name switch
    public String getSuiteName() {
        return configReader.isSet(ChorusConfigProperty.SUITE_NAME) ?
                concatenateName(configReader.getValues(ChorusConfigProperty.SUITE_NAME)) :
                "";
    }

    private String concatenateName(List<String> name) {
        StringBuilder sb = new StringBuilder();
        if ( name.size() > 0 ) {
            Iterator<String> i = name.iterator();
            sb.append(i.next());
            while (i.hasNext()) {
                sb.append(" ");
                sb.append(i.next());
            }
        }
        return sb.toString();
    }

    private void setLoggingProvider() {
        //the logging factory checks the system property version chorusLogProvider when it
        //performs static initialization - the log provider must be set as a system property
        //even if provided as a switch
        ChorusConfigProperty p = ChorusConfigProperty.LOG_PROVIDER;
        if ( System.getProperty(p.getSystemProperty()) == null && configReader.isSet(p)) {
            System.setProperty(p.getSystemProperty(), configReader.getValue(p));
        }
    }
}