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
package org.chorusbdd.chorus.selftest.executionlistener;

import org.chorusbdd.chorus.executionlistener.ExecutionListener;
import org.chorusbdd.chorus.results.*;

import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * User: nick
 * Date: 03/12/13
 * Time: 18:43
 */
public class ExecutionListenerTwo implements ExecutionListener {
    
    public static final AtomicBoolean isTestsStartedCalled = new AtomicBoolean();
    public static final AtomicBoolean isFeatureStartedCalled = new AtomicBoolean();
    public static final AtomicBoolean isScenarioStartedCalled = new AtomicBoolean();

    /**
     * @param testExecutionToken a token representing the current suite of tests starting execution
     * @param features
     */
    @Override
    public void testsStarted(ExecutionToken testExecutionToken, List<FeatureToken> features) {
        isTestsStartedCalled.getAndSet(true);
    }

    /**
     * @param testExecutionToken a token representing the current suite of tests
     * @param features           a List of features executed
     */
    @Override
    public void testsCompleted(ExecutionToken testExecutionToken, List<FeatureToken> features, Set<CataloguedStep> cataloguedSteps) {
    }

    /**
     * @param testExecutionToken a token representing the current suite of tests running
     * @param feature            a token representing the feature which is starting
     */
    @Override
    public void featureStarted(ExecutionToken testExecutionToken, FeatureToken feature) {
        isFeatureStartedCalled.getAndSet(true);
    }

    /**
     * @param testExecutionToken a token representing the current suite of tests running
     * @param feature            a token representing the feature which has just completed
     */
    @Override
    public void featureCompleted(ExecutionToken testExecutionToken, FeatureToken feature) {
    }

    /**
     * @param testExecutionToken a token representing the current suite of tests running
     * @param scenario           a token representing the scenario which is starting
     */
    @Override
    public void scenarioStarted(ExecutionToken testExecutionToken, ScenarioToken scenario) {
        isScenarioStartedCalled.getAndSet(true);
    }

    /**
     * @param testExecutionToken a token representing the current suite of tests running
     * @param scenario           a token representing the scenario which has just completed
     */
    @Override
    public void scenarioCompleted(ExecutionToken testExecutionToken, ScenarioToken scenario) {
    }

    /**
     * @param testExecutionToken a token representing the current suite of tests running
     * @param step               a token representing the test stop which has just started execution
     */
    @Override
    public void stepStarted(ExecutionToken testExecutionToken, StepToken step) {
    }

    /**
     * @param testExecutionToken a token representing the current suite of tests running
     * @param step               a token representing the test stop which has just completed execution
     */
    @Override
    public void stepCompleted(ExecutionToken testExecutionToken, StepToken step) {
    }
}
