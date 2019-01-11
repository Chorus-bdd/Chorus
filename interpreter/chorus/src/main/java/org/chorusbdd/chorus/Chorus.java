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
package org.chorusbdd.chorus;

import org.chorusbdd.chorus.config.ConfigReader;
import org.chorusbdd.chorus.config.InterpreterPropertyException;
import org.chorusbdd.chorus.executionlistener.ExecutionListener;
import org.chorusbdd.chorus.executionlistener.ExecutionListenerSupport;
import org.chorusbdd.chorus.interpreter.interpreter.ChorusInterpreter;
import org.chorusbdd.chorus.interpreter.startup.ChorusConfigProperty;
import org.chorusbdd.chorus.interpreter.startup.ExecutionListenerFactory;
import org.chorusbdd.chorus.interpreter.startup.InterpreterBuilder;
import org.chorusbdd.chorus.interpreter.startup.OutputAndLoggingConfigurer;
import org.chorusbdd.chorus.interpreter.subsystem.SubsystemManager;
import org.chorusbdd.chorus.interpreter.subsystem.SubsystemManagerImpl;
import org.chorusbdd.chorus.logging.ChorusOut;
import org.chorusbdd.chorus.pathscanner.FeatureListBuilder;
import org.chorusbdd.chorus.results.EndState;
import org.chorusbdd.chorus.results.ExecutionToken;
import org.chorusbdd.chorus.results.FeatureToken;

import java.util.Iterator;
import java.util.List;

import static org.chorusbdd.chorus.SwitchPreprocessing.handleVersionOrHelpSwitches;

/**
 * Created by: Steve Neal and Nick Ebbutt, ChorusBDD.org
 */
public class Chorus {

    static {
        ChorusOut.initialize();
    }

    //there's a chicken and egg problem which means we can't use ChorusLog as a static in the main class since config properties
    //are used to set the log implementation in use and these need to be configured first.
    
    private final ExecutionListenerSupport listenerSupport = new ExecutionListenerSupport();
    private final InterpreterBuilder interpreterBuilder;
    private final OutputAndLoggingConfigurer outputAndLoggingConfigurer;
    private final FeatureListBuilder featureListBuilder;
    private final ConfigReader configReader;
    private final ChorusInterpreter interpreter;
    private final SubsystemManager subsystemManager;

    public static void main(String[] args) {
        boolean proceed = handleVersionOrHelpSwitches(args);
        if ( proceed ) {
            boolean success = false;
            try {
                Chorus chorus = new Chorus(args);
                success = chorus.run();
            } catch (InterpreterPropertyException e) {
                ChorusOut.err.println(e.getMessage());
                ChorusOut.err.print(ChorusConfigProperty.getHelpText());
            } catch (Throwable t) {
                ChorusOut.err.println("Chorus encountered an error and had to exit");
                ChorusOut.err.print(t.toString());
            }

            //We should exit with a code between 0-255 since this is the valid range for unix exit statuses
            //(windows supports signed integer exit status, unix does not)
            //choosing the most obvious, 0 = success, 1 = failure, we could expand on this if needed
            int exitCode = success ? 0 : 1;
            System.exit(exitCode);
        }
    }

    public Chorus(String[] args) throws InterpreterPropertyException {
        //*********  To set up config and logging / output
        configReader = new ConfigReader(ChorusConfigProperty.getAll(), args);
        configReader.readConfiguration();

        outputAndLoggingConfigurer = new OutputAndLoggingConfigurer();
        configureOutputAndLogging();

        //*********  After config and logging / output is set up
        subsystemManager = new SubsystemManagerImpl();

        //add custom execution listeners before subsystem listeners
        //guarantees user listener will have their callbacks before subsystems
        addCustomExecutionListeners();
        configureSubsystems();

        //configure logging first
        interpreterBuilder = new InterpreterBuilder(listenerSupport);
        interpreter = interpreterBuilder.buildAndConfigure(configReader, subsystemManager);
        featureListBuilder = new FeatureListBuilder();
    }

    private void configureSubsystems() {
        List<String> handlerClassBasePackages = configReader.getValues(ChorusConfigProperty.HANDLER_PACKAGES);
        subsystemManager.initializeSubsystems(handlerClassBasePackages);
        listenerSupport.addExecutionListeners(subsystemManager.getExecutionListeners());
    }

    private void configureOutputAndLogging() {
        outputAndLoggingConfigurer.configureOutputAndLogging(configReader);
        ExecutionListener l = outputAndLoggingConfigurer.getOutputExecutionListener();
        listenerSupport.addExecutionListeners(l);
    }

    private void addCustomExecutionListeners() {
        List<ExecutionListener> listeners = new ExecutionListenerFactory().createExecutionListeners(configReader);
        listenerSupport.addExecutionListeners(listeners);
    }

    /**
     * Run interpreter using just the base configuration and the listeners provided
     * @return true, if all tests passed or were marked pending
     */
    public boolean run() {
        boolean passed;
        ExecutionToken t = createExecutionToken();
        List<FeatureToken> features = getFeatureList(t);
        startTests(t, features);
        initializeInterpreter();
        processFeatures(t, features);
        endTests(t, features);
        passed = t.getEndState() == EndState.PASSED || t.getEndState() == EndState.PENDING;
        dispose();
        return passed;
    }

    ExecutionToken createExecutionToken() {
        ExecutionToken executionToken = new ExecutionToken(getSuiteName());
        executionToken.setProfile(configReader.getValue(ChorusConfigProperty.PROFILE));
        return executionToken;
    }

    void initializeInterpreter() {
        interpreter.initialize();
    }

    void processFeatures(ExecutionToken t, List<FeatureToken> features) {
        interpreter.runFeatures(t, features);
    }

    List<FeatureToken> getFeatureList(ExecutionToken executionToken)  {
        return featureListBuilder.getFeatureList(
            executionToken,
            configReader.getValues(ChorusConfigProperty.FEATURE_PATHS),
            configReader.getValues(ChorusConfigProperty.STEPMACRO_PATHS),
            configReader.getValues(ChorusConfigProperty.TAG_EXPRESSION)
        );
    }

    /**
     * Start tests, notifying executionListeners
     */
    public void startTests(ExecutionToken executionToken, List<FeatureToken> features) {
        listenerSupport.notifyTestsStarted(executionToken, features);
    }

    /**
     * End tests, notifying executionListeners
     * @param t the executionToken containing results for this test run
     * @param features the features which were run during this test run
     */
    public void endTests(ExecutionToken t, List<FeatureToken> features) {
        t.calculateTimeTaken();
        listenerSupport.notifyTestsCompleted(t, features, interpreter.getCataloguedSteps());
    }

    void addJUnitExecutionListener(ExecutionListener listener) {
        listenerSupport.addExecutionListeners(listener);
    }

    void dispose() {
        outputAndLoggingConfigurer.dispose();
    }

    //to get the suite name we concatenate all the values provided for suite name switch
    public String getSuiteName() {
        return configReader.isSet(ChorusConfigProperty.SUITE_NAME) ?
                concatenateName(configReader.getValues(ChorusConfigProperty.SUITE_NAME)) :
                "";
    }

    private String concatenateName(List<String> name) {
        StringBuilder sb = new StringBuilder();
        if (!name.isEmpty()) {
            Iterator<String> i = name.iterator();
            sb.append(i.next());
            while (i.hasNext()) {
                sb.append(" ");
                sb.append(i.next());
            }
        }
        return sb.toString();
    }

}