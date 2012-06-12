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

import org.chorusbdd.chorus.core.interpreter.results.ExecutionToken;
import org.chorusbdd.chorus.core.interpreter.results.FeatureToken;
import org.chorusbdd.chorus.core.interpreter.results.ScenarioToken;
import org.chorusbdd.chorus.core.interpreter.results.StepToken;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: nick
 * Date: 16/05/12
 * Time: 22:03
 * To change this template use File | Settings | File Templates.
 */
public class ExecutionListenerSupport {

    private List<ExecutionListener> listeners = new ArrayList<ExecutionListener>();

    //
    // Execution event methods
    //
    public void addExecutionListener(ExecutionListener... listeners) {
        this.listeners.addAll(Arrays.asList(listeners));
    }

    public boolean removeExecutionListener(ExecutionListener... listeners) {
        return this.listeners.removeAll(Arrays.asList(listeners));
    }

    public void addExecutionListeners(Collection<ExecutionListener> listeners) {
        this.listeners.addAll(listeners);
    }

    public void removeExecutionListeners(List<ExecutionListener> listeners) {
        this.listeners.removeAll(listeners);
    }

    public void notifyStartTests(ExecutionToken t) {
        for (ExecutionListener listener : listeners) {
            listener.testsStarted(t);
        }
    }

    public void notifyStepStarted(ExecutionToken t, StepToken step) {
        for (ExecutionListener listener : listeners) {
            listener.stepStarted(t, step);
        }
    }

    public void notifyStepCompleted(ExecutionToken t, StepToken step) {
        for (ExecutionListener listener : listeners) {
            listener.stepCompleted(t, step);
        }
    }

    public void notifyFeatureStarted(ExecutionToken t, FeatureToken feature) {
        for (ExecutionListener listener : listeners) {
            listener.featureStarted(t, feature);
        }
    }

    public void notifyFeatureCompleted(ExecutionToken t, FeatureToken feature) {
        for (ExecutionListener listener : listeners) {
            listener.featureCompleted(t, feature);
        }
    }

    public void notifyScenarioStarted(ExecutionToken t, ScenarioToken scenario) {
        for (ExecutionListener listener : listeners) {
            listener.scenarioStarted(t, scenario);
        }
    }

    public void notifyScenarioCompleted(ExecutionToken t, ScenarioToken scenario) {
        for (ExecutionListener listener : listeners) {
            listener.scenarioCompleted(t, scenario);
        }
    }

    public void notifyTestsCompleted(ExecutionToken t, List<FeatureToken> features) {
        for (ExecutionListener listener : listeners) {
            listener.testsCompleted(t, features);
        }
    }

}
