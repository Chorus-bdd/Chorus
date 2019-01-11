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

import org.chorusbdd.chorus.annotations.ExecutionPriority;
import org.chorusbdd.chorus.config.InterpreterPropertyException;
import org.chorusbdd.chorus.executionlistener.ExecutionListenerAdapter;
import org.chorusbdd.chorus.logging.ChorusOut;
import org.chorusbdd.chorus.results.*;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;

/**
 * User: nick
 * Date: 27/08/13
 * Time: 08:36
 */
public class ChorusSuite extends ParentRunner<ChorusSuite.ChorusFeatureTest> {

    private Class clazz;
    private Chorus chorus;
    private JUnitSuiteExecutionListener executionListener = new JUnitSuiteExecutionListener();
    private Thread testThread;

    static {
        ChorusOut.initialize();
    }

    public ChorusSuite(Class clazz) throws InitializationError {
        super(clazz);
        this.clazz = clazz;
    }

    @Override
    protected List<ChorusFeatureTest> getChildren() {
        Method method;
        try {
            method = clazz.getMethod("getChorusArgs");
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Failed to find getChorusArgs method");
        }

        if (! Modifier.isStatic(method.getModifiers())) {
            throw new RuntimeException("The getChorusArgs method is not static");
        }

        if ( ! String.class.equals(method.getReturnType())) {
            throw new RuntimeException("The getChorusArgs method does not return a String");
        }

        String args;
        try {
            args = (String)method.invoke(null);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed during call to getChorusArgs", e);
        }

        try {
            chorus = new Chorus(args.split(" "));
        } catch (InterpreterPropertyException e) {
            throw new RuntimeException("Error with Chorus command line " + e.getMessage());
        }

        chorus.addJUnitExecutionListener(executionListener);

        ExecutionToken executionToken = chorus.createExecutionToken();
        List<ChorusFeatureTest> tests = getChorusFeatureTests(executionToken);
        return tests;
    }

    private List<ChorusFeatureTest> getChorusFeatureTests(ExecutionToken executionToken) {
        List<ChorusFeatureTest> tests = null;
        try {
            List<FeatureToken> features = chorus.getFeatureList(executionToken);

            //we must ensure scenario string name is unique across all features
            //due to problems with the Descripition equality in junit
            HashSet<String> uniqueScenarioNames = new HashSet<>();
            tests = new ArrayList<>();
            for (FeatureToken f  : features) {
                try {
                    tests.add(new ChorusFeatureTest(f, uniqueScenarioNames, true));
                } catch (InitializationError initializationError) {
                    initializationError.printStackTrace();
                }
            }

            if (!tests.isEmpty()) {
                ChorusFeatureTest t = tests.remove(0);
                tests.add(0, new InitialFeature(t.featureToken, t, executionToken, features, uniqueScenarioNames));

                t = tests.remove(tests.size() - 1);
                tests.add(new FinalFeature(t.featureToken, t, uniqueScenarioNames));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tests;
    }

    protected Description describeChild(ChorusFeatureTest child) {
        return child.getDescription();
    }

    protected void runChild(ChorusFeatureTest child, RunNotifier notifier) {
        child.run(notifier);
    }

    public class InitialFeature extends ChorusFeatureTest {

        ChorusFeatureTest wrappedFeature;
        private ExecutionToken executionToken;
        private List<FeatureToken> features;

        public InitialFeature(FeatureToken featureToken, ChorusFeatureTest wrappedFeature, ExecutionToken executionToken, List<FeatureToken> features, Set<String> uniqueScenarioNames) throws InitializationError {
            super(featureToken, uniqueScenarioNames, false);
            this.wrappedFeature = wrappedFeature;
            this.executionToken = executionToken;
            this.features = features;
        }

        public void run(RunNotifier notifier) {
            testThread = new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        chorus.startTests(executionToken, features);
                        chorus.initializeInterpreter();
                        chorus.processFeatures(executionToken, features);
                        chorus.endTests(executionToken, features);
                        chorus.dispose();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            testThread.start();

            executionListener.awaitTestStart();
            wrappedFeature.run(notifier);
        }

        @Override
        public List<ChorusScenario> getChildren() {
            return wrappedFeature.getChildren();
        }

        @Override
        public Description getDescription() {
            return wrappedFeature.getDescription();
        }

        @Override
        public Description describeChild(ChorusScenario child) {
            return wrappedFeature.describeChild(child);
        }

        @Override
        public void runChild(ChorusScenario child, RunNotifier notifier) {
            wrappedFeature.runChild(child, notifier);
        }
    }

    public class FinalFeature extends ChorusFeatureTest {

        ChorusFeatureTest wrappedFeature;

        public FinalFeature(FeatureToken featureToken, ChorusFeatureTest wrappedFeature, Set<String> uniqueScenarioNames) throws InitializationError {
            super(featureToken, uniqueScenarioNames, false);
            this.wrappedFeature = wrappedFeature;
        }

        public void run(RunNotifier notifier) {
            wrappedFeature.run(notifier);

            //wait for tests to terminate
            try {
                testThread.join(30000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            pauseForJUnitOutput();
        }

        @Override
        public List<ChorusScenario> getChildren() {
            return wrappedFeature.getChildren();
        }

        @Override
        public Description getDescription() {
            return wrappedFeature.getDescription();
        }

        @Override
        public Description describeChild(ChorusScenario child) {
            return wrappedFeature.describeChild(child);
        }

        @Override
        public void runChild(ChorusScenario child, RunNotifier notifier) {
            wrappedFeature.runChild(child, notifier);
        }
    }


    /**
     * A race condition (in Intellij junit plugin?) causes std output to bleed into the next test unless we pause briefly
     */
    private void pauseForJUnitOutput()  {
        try {
            Thread.sleep(20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    public class ChorusFeatureTest extends ParentRunner<ChorusSuite.ChorusScenario> {
        private final FeatureToken featureToken;
        private final Set<String> uniqueScenarioNames;
        private List<ChorusScenario> children;
        private Description description;

        public ChorusFeatureTest(FeatureToken featureToken, Set<String> uniqueScenarioNames, boolean createChildren) throws InitializationError {
            super(ChorusFeatureTest.class);
            this.featureToken = featureToken;
            this.uniqueScenarioNames = uniqueScenarioNames;
            if ( createChildren) {
                this.children = createChildren();
                this.description = createDescription();
            }
        }

        private List<ChorusScenario> createChildren() {
            List<ChorusScenario> l = new ArrayList<>();
            for (ScenarioToken s : featureToken.getScenarios()) {
                l.add(new ChorusScenario(featureToken, s, uniqueScenarioNames));
            }
            return l;
        }

        private Description createDescription() {
            Description description = Description.createSuiteDescription(featureToken.getNameWithConfiguration());
            for (ChorusScenario s : children) {
                description.addChild(s.getDescription());
            }
            return description;
        }

        public void run(RunNotifier notifier) {
            executionListener.awaitFeatureStart();
            super.run(notifier);
            executionListener.awaitFeatureEnd();
        }

        @Override
        protected List<ChorusScenario> getChildren() {
            return children;
        }


        public Description getDescription() {
            return description;
        }

        @Override
        protected Description describeChild(ChorusScenario child) {
            return child.getDescription();
        }

        @Override
        protected void runChild(ChorusScenario child, RunNotifier notifier) {
            notifier.fireTestStarted(child.getDescription());
            try {
                child.run();
                if ( ! child.isSuccess()) {
                    notifier.fireTestFailure(new FailureWithNoException(child.getDescription(), child));
                }
            } catch (Exception e) {
                notifier.fireTestFailure(new Failure(child.getDescription(), e));
            }
            notifier.fireTestFinished(child.getDescription());
        }
    }

    private class FailureWithNoException extends Failure {

        public FailureWithNoException(Description description, ChorusScenario scenarioName) {
            super(description, new Exception("Chorus Test Failed " + scenarioName.getErrorDescription()));
        }
        
        public String toString() {
            return getTestHeader();    
        }
    }

    public static boolean isJUnitPass(PassPendingFailToken featureToken) {
        return featureToken.getEndState() == EndState.PASSED || featureToken.getEndState() == EndState.PENDING;
    }

    private class ChorusScenario {

        private final FeatureToken featureToken;
        private final ScenarioToken scenarioToken;
        private final String scenarioName;
        private final Description description;

        public ChorusScenario(FeatureToken featureToken, ScenarioToken scenarioToken, Set<String> scenarioNames) {
            this.featureToken = featureToken;
            this.scenarioToken = scenarioToken;

            scenarioName = calculateName(featureToken, scenarioToken, scenarioNames);
            description = Description.createTestDescription(clazz, scenarioName);
        }

        //The JUnit description seems to require a class to be globally unique - we don't have a class so get problems
        //when scenarios have the same name within our features. In this case detect the problem and prepend the feature name to the string.
        private String calculateName(FeatureToken featureToken, ScenarioToken scenarioToken, Set<String> scenarioNames) {
            String conf = featureToken.getConfigurationName().equals(FeatureToken.BASE_CONFIGURATION) ? "" : " [" + featureToken.getConfigurationName() + "]";
            String name = scenarioToken.getName() + conf;
            int instance = 2;
            while ( ! scenarioNames.add(name)) {
                name = scenarioToken.getName() + " (" + instance++ + ")";
            }
            return name;
        }

        public Description getDescription() {
            return description;
        }

        public void run() throws Exception {
            executionListener.awaitScenarioStart();
            executionListener.awaitScenarioEnd();
        }

        public boolean isSuccess() {
            return isJUnitPass(scenarioToken);
        }

        public String getException() { return scenarioToken.getException() == null ? "" : scenarioToken.getException(); }

        public String getErrorDescription() {
            String exception = scenarioToken.getException();
            return scenarioName + (exception == null ? "" : " " + exception);
        }
    }


    @ExecutionPriority(ExecutionPriority.JUNIT_SUITE_LISTENER_PRIORITY)
    private class JUnitSuiteExecutionListener extends ExecutionListenerAdapter {

        long waitForTestStartLimit = Long.parseLong(System.getProperty("chorusJUnitScenarioTimeout", "10"));   //10 seconds
        long waitForFeatureLimit = Long.parseLong(System.getProperty("chorusJUnitFeatureTimeout", "300"));    //5 mins
        long waitForScenarioLimit = Long.parseLong(System.getProperty("chorusJUnitScenarioTimeout", "600"));   //10 mins

        private CyclicBarrier startBarrier = new CyclicBarrier(2);
        private volatile List<FeatureToken> features;

        private CyclicBarrier featureBarrier = new CyclicBarrier(2);

        private CyclicBarrier scenarioBarrier = new CyclicBarrier(2);

        private Set<ScenarioToken> completedScenarios = Collections.synchronizedSet(new HashSet<ScenarioToken>());


        private volatile boolean timedOut = false;

        @Override
        public void testsStarted(ExecutionToken testExecutionToken, List<FeatureToken> features) {
            pauseForJUnitOutput();
            this.features = features;
            awaitTestStart();
            pauseForJUnitOutput();
        }

        @Override
        public void featureStarted(ExecutionToken testExecutionToken, FeatureToken feature) {
            pauseForJUnitOutput();
            awaitFeatureStart();
            pauseForJUnitOutput();
        }

        @Override
        public void featureCompleted(ExecutionToken testExecutionToken, FeatureToken feature) {
            pauseForJUnitOutput();
            for ( ScenarioToken s : feature.getScenarios()) {
               if ( ! completedScenarios.contains(s)) {
                   //The feature terminated early and this scenario did not get run
                   //usually this happens when we can't find feature-level resources such as a
                   //handler class.
                   // Since junit is waiting on the scenario we need to send a start and complete
                   scenarioStarted(testExecutionToken, s);
                   scenarioCompleted(testExecutionToken, s);
               }
            }
            awaitFeatureEnd();
            pauseForJUnitOutput();
        }

        @Override
        public void scenarioStarted(ExecutionToken testExecutionToken, ScenarioToken scenario) {
            pauseForJUnitOutput();
            awaitScenarioStart();
            pauseForJUnitOutput();
        }

        @Override
        public void scenarioCompleted(ExecutionToken testExecutionToken, ScenarioToken scenario) {
            pauseForJUnitOutput();
            completedScenarios.add(scenario);
            awaitScenarioEnd();
            pauseForJUnitOutput();
        }

        public boolean isTimedOut() {
            return timedOut;
        }

        private void await(CyclicBarrier cyclicBarrier, long length, String desc) {
            try {
                cyclicBarrier.await(length, TimeUnit.SECONDS);
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Timed out waiting for " + desc);
                System.out.println("Timed out waiting for " + desc);
                timedOut = true;
            }
        }

        public void awaitFeatureStart() {
            if ( ! isTimedOut() ) {
                await(featureBarrier, waitForFeatureLimit, "feature start");
            }
        }

        public void awaitFeatureEnd() {
            if ( ! isTimedOut() ) {
                await(featureBarrier, waitForFeatureLimit, "feature end");
            }
        }

        public void awaitScenarioStart() {
            if ( ! isTimedOut() ) {
                await(scenarioBarrier, waitForScenarioLimit, "scenario start");
            }
        }

        public void awaitScenarioEnd() {
            if ( ! isTimedOut() ) {
                await(scenarioBarrier, waitForScenarioLimit, "scenario end");
            }
        }

        public void awaitTestStart() {
            if ( ! isTimedOut() ) {
                await(startBarrier, waitForTestStartLimit, "suite start");
            }
        }

    }
}
