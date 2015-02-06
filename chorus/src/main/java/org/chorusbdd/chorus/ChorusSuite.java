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
import java.util.ArrayList;
import java.util.List;
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
    private ExecutionToken executionToken;
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
            chorus.addExecutionListener(executionListener);
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
                tests.add(new ChorusFeatureTest(chorus, executionToken, f));
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
        private final Chorus chorus;
        private final ExecutionToken t;
        private final FeatureToken featureToken;
        private final List<ChorusScenario> children;

        public ChorusFeatureTest(Chorus chorus, ExecutionToken t, FeatureToken featureToken) throws InitializationError {
            super(ChorusFeatureTest.class);
            this.chorus = chorus;
            this.t = t;
            //To change body of created methods use File | Settings | File Templates.
            this.featureToken = featureToken;
            this.children = createChildren();
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

        private List<ChorusScenario> createChildren() {
            List<ChorusScenario> l = new ArrayList<>();
            for (ScenarioToken s : featureToken.getScenarios()) {
                l.add(new ChorusScenario(featureToken, s));
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

        public ChorusScenario(FeatureToken featureToken, ScenarioToken scenarioToken) {
            this.featureToken = featureToken;
            this.scenarioToken = scenarioToken;
        }

        public Description getDescription() {
            String conf = featureToken.getConfigurationName().equals(FeatureToken.BASE_CONFIGURATION) ? "" : " [" + featureToken.getConfigurationName() + "]";
            return Description.createTestDescription(clazz, scenarioToken.getName() + conf);
        }

        public void run() throws Exception {

        }

        public boolean isSuccess() {
            return isJUnitPass(scenarioToken);
        }

        public String getScenarioName()  {
            return scenarioToken.getName();
        }
    }

    private class JUnitSuiteExecutionListener extends ExecutionListenerAdapter {

        private CyclicBarrier startBarrier = new CyclicBarrier(2);
        private volatile List<FeatureToken> features;

        private CyclicBarrier featureStartBarrier = new CyclicBarrier(2);

        private CyclicBarrier featureEndBarrier = new CyclicBarrier(2);


        public void testsStarted(ExecutionToken testExecutionToken, List<FeatureToken> features) {
            this.features = features;
            awaitTestStart();
        }

        public void featureStarted(ExecutionToken testExecutionToken, FeatureToken feature) {
            awaitFeatureStart();
        }

        public void featureCompleted(ExecutionToken testExecutionToken, FeatureToken feature) {
            awaitFeatureEnd();
        }

        private void await(CyclicBarrier cyclicBarrier, long length) {
            try {
                cyclicBarrier.await(length, TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void awaitFeatureStart() {
            await(featureStartBarrier, 10000);
        }

        public void awaitFeatureEnd() {
            await(featureEndBarrier, 600000);
        }

        public void awaitTestStart() {
            await(startBarrier, 10000);
        }
    }
}
