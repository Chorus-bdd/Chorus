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
package org.chorusbdd.chorus.core.interpreter;

import org.chorusbdd.chorus.core.interpreter.results.FeatureToken;
import org.chorusbdd.chorus.core.interpreter.results.ScenarioToken;
import org.chorusbdd.chorus.core.interpreter.results.StepToken;
import org.chorusbdd.chorus.core.interpreter.results.TestExecutionToken;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: nick
 * Date: 16/05/12
 * Time: 22:03
 * To change this template use File | Settings | File Templates.
 */
public class ExecutionListenerSupport {

    private List<ChorusExecutionListener> listeners = new ArrayList<ChorusExecutionListener>();

    //
    // Execution event methods
    //
    public void addExecutionListener(ChorusExecutionListener... listeners) {
        this.listeners.addAll(Arrays.asList(listeners));
    }

    public boolean removeExecutionListener(ChorusExecutionListener... listeners) {
        return this.listeners.removeAll(Arrays.asList(listeners));
    }

    public void notifyStartTests(TestExecutionToken t) {
        for (ChorusExecutionListener listener : listeners) {
            listener.testsStarted(t);
        }
    }

    public void notifyStepStarted(TestExecutionToken t, StepToken step) {
        for (ChorusExecutionListener listener : listeners) {
            listener.stepStarted(t, step);
        }
    }

    public void notifyStepCompleted(TestExecutionToken t, StepToken step) {
        for (ChorusExecutionListener listener : listeners) {
            listener.stepCompleted(t, step);
        }
    }

    public void notifyFeatureStarted(TestExecutionToken t, FeatureToken feature) {
        for (ChorusExecutionListener listener : listeners) {
            listener.featureStarted(t, feature);
        }
    }

    public void notifyFeatureCompleted(TestExecutionToken t, FeatureToken feature) {
        for (ChorusExecutionListener listener : listeners) {
            listener.featureCompleted(t, feature);
        }
    }

    public void notifyScenarioStarted(TestExecutionToken t, ScenarioToken scenario) {
        for (ChorusExecutionListener listener : listeners) {
            listener.scenarioStarted(t, scenario);
        }
    }

    public void notifyScenarioCompleted(TestExecutionToken t, ScenarioToken scenario) {
        for (ChorusExecutionListener listener : listeners) {
            listener.scenarioCompleted(t, scenario);
        }
    }

    public void notifyTestsCompleted(TestExecutionToken t, List<FeatureToken> features) {
        for (ChorusExecutionListener listener : listeners) {
            listener.testsCompleted(t, features);
        }
    }

}
