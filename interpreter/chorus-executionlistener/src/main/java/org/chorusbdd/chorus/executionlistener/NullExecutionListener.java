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
package org.chorusbdd.chorus.executionlistener;

import org.chorusbdd.chorus.results.*;

import java.util.List;
import java.util.Set;

/**
 * Created by nick on 15/10/2014.
 */
public class NullExecutionListener {

    public static final ExecutionListener NULL_LISTENER = new ExecutionListener() {

        @Override
        public void testsStarted(ExecutionToken testExecutionToken, List<FeatureToken> features) {}

        @Override
        public void testsCompleted(ExecutionToken testExecutionToken, List<FeatureToken> features, Set<CataloguedStep> cataloguedSteps) {}

        @Override
        public void featureStarted(ExecutionToken testExecutionToken, FeatureToken feature) {}

        @Override
        public void featureCompleted(ExecutionToken testExecutionToken, FeatureToken feature) {}

        @Override
        public void scenarioStarted(ExecutionToken testExecutionToken, ScenarioToken scenario) {}

        @Override
        public void scenarioCompleted(ExecutionToken testExecutionToken, ScenarioToken scenario) {}

        @Override
        public void stepStarted(ExecutionToken testExecutionToken, StepToken step) {}

        @Override
        public void stepCompleted(ExecutionToken testExecutionToken, StepToken step) {}
    };

}
