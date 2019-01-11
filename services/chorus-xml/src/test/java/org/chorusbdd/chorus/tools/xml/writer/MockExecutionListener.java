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
package org.chorusbdd.chorus.tools.xml.writer;

import org.chorusbdd.chorus.executionlistener.ExecutionListener;
import org.chorusbdd.chorus.results.*;

import java.util.List;
import java.util.Set;

/**
* Created by Nick E on 19/02/2015.
*/
public class MockExecutionListener implements ExecutionListener {

    static ExecutionToken testExecutionToken;
    static List<FeatureToken> featureTokens;

    public void testsStarted(ExecutionToken testExecutionToken, List<FeatureToken> features) {
    }

    public void testsCompleted(ExecutionToken testExecutionToken, List<FeatureToken> features, Set<CataloguedStep> cataloguedSteps) {
        this.testExecutionToken = testExecutionToken;
        this.featureTokens = features;
    }

    public void featureStarted(ExecutionToken testExecutionToken, FeatureToken feature) {
    }

    public void featureCompleted(ExecutionToken testExecutionToken, FeatureToken feature) {
    }

    public void scenarioStarted(ExecutionToken testExecutionToken, ScenarioToken scenario) {
    }

    public void scenarioCompleted(ExecutionToken testExecutionToken, ScenarioToken scenario) {
    }

    public void stepStarted(ExecutionToken testExecutionToken, StepToken step) {
    }

    public void stepCompleted(ExecutionToken testExecutionToken, StepToken step) {
    }


}
