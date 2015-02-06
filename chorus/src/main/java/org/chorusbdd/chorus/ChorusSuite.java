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
package org.chorusbdd.chorus;

import org.chorusbdd.chorus.config.InterpreterPropertyException;
import org.chorusbdd.chorus.executionlistener.ExecutionListenerAdapter;
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

        String args = "";
        try {
            args = (String)method.invoke(null);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed during call to getChorusArgs", e);
        }


        try {
            chorus = new Chorus(args.split(" "));
            chorus.addJUnitExecutionListener(executionListener);
        } catch (InterpreterPropertyException e) {
            throw new RuntimeException("Unsupported property", e);
        }

        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    chorus.run();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        executionListener.awaitTestStart();
        List<FeatureToken> features = executionListener.features;
        List<ChorusFeatureTest> tests = new ArrayList<>();
        for (FeatureToken f  : features) {
            try {
                tests.add(new ChorusFeatureTest(f));
            } catch (InitializationError initializationError) {
                initializationError.printStackTrace();
            }
        }
        return tests;
    }
    
    protected Description describeChild(ChorusFeatureTest child) {
        return child.getDescription();
    }

    protected void runChild(ChorusFeatureTest child, RunNotifier notifier) {
        child.run(notifier);
    }
    
    public class ChorusFeatureTest extends ParentRunner<ChorusSuite.ChorusScenario> {
        private final FeatureToken featureToken;
        private final List<ChorusScenario> children;

        public ChorusFeatureTest(FeatureToken featureToken) throws InitializationError {
            super(ChorusFeatureTest.class);
            //To change body of created methods use File | Settings | File Templates.
            this.featureToken = featureToken;
            this.children = createChildren();
        }

        public void run(RunNotifier notifier) {
            super.run(notifier);
            executionListener.awaitFeatureEnd();
        }

        @Override
        protected List<ChorusScenario> getChildren() {
            return children;
        }

        private List<ChorusScenario> createChildren() {
            List<ChorusScenario> l = new ArrayList<>();
            HashSet<String> scenarioNames = new HashSet<>();  //used to guarantee unique scenario names
            for (ScenarioToken s : featureToken.getScenarios()) {
                l.add(new ChorusScenario(featureToken, s, scenarioNames));
            }
            return l;
        }

        public Description getDescription() {
            Description d = Description.createSuiteDescription(featureToken.getNameWithConfiguration());
            for (ChorusScenario s : children) {
                d.addChild(s.getDescription());
            }
            return d;
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
                    notifier.fireTestFailure(new FailureWithNoException(child.getDescription(), child.getScenarioName()));
                }
            } catch (Exception e) {
                notifier.fireTestFailure(new Failure(child.getDescription(), e));
            }
            notifier.fireTestFinished(child.getDescription());
        }
    }

    private class FailureWithNoException extends Failure {
        
        public FailureWithNoException(Description description, String featureName) {
            super(description, new Exception("Chorus Test Failed " + featureName));
        }
        
        public String toString() {
            return getTestHeader();    
        }
    }

    public static boolean isJUnitPass(PassPendingFailToken featureToken) {
        return featureToken.getEndState() == EndState.PASSED || featureToken.getEndState() == EndState.PENDING;
    }

    private class ChorusScenario {

        private FeatureToken featureToken;
        private ScenarioToken scenarioToken;
        private String scenarioName;

        public ChorusScenario(FeatureToken featureToken, ScenarioToken scenarioToken, Set<String> scenarioNames) {
            this.featureToken = featureToken;
            this.scenarioToken = scenarioToken;

            calculateName(featureToken, scenarioToken, scenarioNames);
        }

        //The JUnit description seems to require a class to be globally unique - we don't have a class so get problems
        //when scenarios have the same name within our features. In this case detect the problem and prepend the feature name to the string.
        private void calculateName(FeatureToken featureToken, ScenarioToken scenarioToken, Set<String> scenarioNames) {
            String conf = featureToken.getConfigurationName().equals(FeatureToken.BASE_CONFIGURATION) ? "" : " [" + featureToken.getConfigurationName() + "]";
            String name = scenarioToken.getName() + conf;
            if ( ! scenarioNames.add(name)) {
                name = featureToken.getNameWithConfiguration() + " " + scenarioToken.getName();
            }
            this.scenarioName = name;
        }

        public Description getDescription() {
            return Description.createTestDescription(clazz, scenarioName);
        }

        public void run() throws Exception {
            if ( ! executionListener.isCompleted(featureToken)) {
                executionListener.awaitScenarioEnd();
            }
        }

        public boolean isSuccess() {
            return isJUnitPass(scenarioToken);
        }

        public String getScenarioName()  {
            return scenarioToken.getName();
        }
    }


    private class JUnitSuiteExecutionListener extends ExecutionListenerAdapter {

        long waitForTestStartLimit = Long.parseLong(System.getProperty("chorusJUnitScenarioTimeout", "10"));   //10 seconds
        long waitForFeatureLimit = Long.parseLong(System.getProperty("chorusJUnitFeatureTimeout", "1200"));    //20 mins
        long waitForScenarioLimit = Long.parseLong(System.getProperty("chorusJUnitScenarioTimeout", "300"));   //5 mins

        private CyclicBarrier startBarrier = new CyclicBarrier(2);
        private volatile List<FeatureToken> features;

        private CyclicBarrier featureBarrier = new CyclicBarrier(2);

        private CyclicBarrier scenarioBarrier = new CyclicBarrier(2);

        private Set<FeatureToken> completedFeatures = Collections.synchronizedSet(new HashSet<FeatureToken>());

        private volatile boolean timedOut = false;

        public void testsStarted(ExecutionToken testExecutionToken, List<FeatureToken> features) {
            this.features = features;
            awaitTestStart();
        }

        public void featureCompleted(ExecutionToken testExecutionToken, FeatureToken feature) {
            completedFeatures.add(feature);
            if ( scenarioBarrier.getNumberWaiting() > 1) {
                //this feature terminated early, the secenario will not run,
                //usually this happens when we can't find feature-level resources such as a handler class
                await(scenarioBarrier, 1000, "interrupt scenario");
            }
            awaitFeatureEnd();
            pauseForJUnitOutput();
        }

        public void scenarioCompleted(ExecutionToken testExecutionToken, ScenarioToken scenario) {
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

        public boolean isCompleted(FeatureToken featureToken) {
            return completedFeatures.contains(featureToken);
        }

        public void awaitFeatureEnd() {
            if ( ! isTimedOut() ) {
                await(featureBarrier, waitForFeatureLimit, "feature end");
            }
        }

        private void awaitScenarioEnd() {
            if ( ! isTimedOut() ) {
                await(scenarioBarrier, waitForScenarioLimit, "scenario start");
            }
        }

        public void awaitTestStart() {
            if ( ! isTimedOut() ) {
                await(startBarrier, waitForTestStartLimit, "suite start");
            }
        }

    }
}
